package com.meong.podoandroid.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {
    TextView mTvReceiveData;
    TextView mTvSendData;
    Button mBtnBluetoothOn;
    Button mBtnBluetoothOff;
    Button mBtnConnect;
    Button mBtnSendData;

    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mPairedDevices;
    List<String> mListPairedDevices;

    Handler mBluetoothHandler;
    ConnectedBluetoothThread mThreadConnectedBluetooth;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;

    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    Activity mActivity;

    public BluetoothService (Activity activity) {
        this.mActivity =activity;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void bluetoothOn() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(mActivity, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                Toast.makeText(mActivity, "블루투스가 이미 활성화 되어 있습니다.", Toast.LENGTH_LONG).show();
            } else { //블루투스 비활성화 상태

                //ACTION_REQUEST_ENABLE 로 지정하여 Intent를 발생시키면 사용자에게 블루투스 활성 여부를 묻는 다이얼로그가 뜸
                Intent intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //활성화 창을 띄워 onActivityResult에서 결과를 처리
                mActivity.startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE);

            }
        }
    }

    public void bluetoothOff() { //블루투스 비활성화 메소드
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        } else {
            Toast.makeText(mActivity, "블루투스가 이미 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }



    //블루투스 페어링 장치 목록 가져오는 메소드
    public void listPairedDevices() {
        if (mBluetoothAdapter.isEnabled()) { //블루투스 활성화 상태인지 확인
            mPairedDevices = mBluetoothAdapter.getBondedDevices();

            if (mPairedDevices.size() > 0) { //페어링 된 장치가 존재한다면
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("장치 선택"); //알람창에 '장치선택' 타이틀 표시

                //페어링 된 장치명들을 추가
                mListPairedDevices = new ArrayList<String>();
                for (BluetoothDevice device : mPairedDevices) {
                    mListPairedDevices.add(device.getName());
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }


                //페어링된 장치 수를 얻어와서 각 장치를 누르면 장치 명을 매게변수로 사용하여 connectSelectedDevice 메소드로 전달
                final CharSequence[] items = mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);
                mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        connectSelectedDevice(items[item].toString());
                    }
                });

                //위에서 리스트로 추가된 알람창을 실제로 띄움
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Toast.makeText(mActivity, "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(mActivity, "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //블루투스 연결하는 메소드
    void connectSelectedDevice(String selectedDeviceName) {
        //페어링 된 모든 장치르 검색하면서 매개변수로 받은 장치 값과 같다면 그 장치의 주소 값을 얻어옴
        for(BluetoothDevice tempDevice : mPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                mBluetoothDevice = tempDevice;
                break;
            }
        }
        try {
            //블루투스 소켓을 가져옴
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);
            mBluetoothSocket.connect();//연결

            //데이터는 언제 수신받을 지 몰라 데이터 수신을 위한 쓰레드를 따로 만들어서 처리
            //쓰레드 생성
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mBluetoothSocket);
            mThreadConnectedBluetooth.start();
            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
        } catch (IOException e) {
            Toast.makeText(mActivity, "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //쓰레드 초기화 과정. getInputStream() 와 getOutputStream() 을 사용하여 소켓을 통한 전송을 처리하는
        //inputStream 및 OutputStream()을 가져옴
        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(mActivity, "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            //수신받은 데이터는 언제 들어올 지 모르니 항상 확인 while문 사용
            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        //데이터 전송을 위한 메소드
        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(mActivity, "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }

        //블루투스 소켓을 닫는 메소드
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(mActivity, "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }
}

