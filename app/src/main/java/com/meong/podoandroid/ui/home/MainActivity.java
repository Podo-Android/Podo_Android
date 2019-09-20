package com.meong.podoandroid.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.meong.podoandroid.bluetooth.BluetoothService;
import com.meong.podoandroid.helper.DatabaseHelper;
import com.meong.podoandroid.helper.DogDBHelper;
import com.meong.podoandroid.ui.feed.FeedRecommendActivity;
import com.meong.podoandroid.ui.map.MapSearchActivity;
import com.meong.podoandroid.R;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    ImageView front_right_leg, front_left_leg, end_right_leg,end_left_leg;
    TextView leg_controll_txt;
    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    BluetoothService bluetooth;
    Handler mBluetoothHandler;
    SQLiteDatabase database;
    DogDBHelper databaseHelper;

    ImageView imgMenu;
    DrawerLayout drawer;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;

        front_right_leg=(ImageView)findViewById(R.id.front_right_leg);
        front_left_leg=(ImageView)findViewById(R.id.front_left_leg);
        end_right_leg=(ImageView)findViewById(R.id.end_right_leg);
        end_left_leg=(ImageView)findViewById(R.id.end_left_leg);
        leg_controll_txt=(TextView)findViewById(R.id.leg_controll_txt);

        bluetooth= new BluetoothService(this);

        Switch sw= (Switch)findViewById(R.id.bluetooth_on_off_switch);

        databaseHelper= new DogDBHelper(mContext);
        database=databaseHelper.getWritableDatabase();

        sw.setTextOff("OFF");
        sw.setTextOn("ON");
        sw.setChecked(false);

        setDrawer();
        setOnBtnClickListener();
        onDrawerItemClickListener();

        insertData("dog_leg","20","15","30","40");
        insertData("dog_leg","10","20","15","5");
        leg_compare("dog_leg");

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    bluetooth.bluetoothOn();
                }
                else{
                    bluetooth.bluetoothOff();
                }
            }
        });

        //블루투스 핸들러로 블루투스 연결 뒤 수신된 데이터를 읽어와 ReceiveData 텍스트 뷰에 표시
        mBluetoothHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == BT_MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                   // mTvReceiveData.setText(readMessage);
                    //여기다 읽어온 값을 처리해 db에 넣는 코드 작성
                }
            }
        };
    }

    private void onDrawerItemClickListener() {
        // 홈이 눌렸을 때
        TextView txtHome = (TextView) findViewById(R.id.txt_nav_main_home);
        txtHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                finish();
            }
        });

        //병원 위치가 눌렸을 때
        TextView txtHospital = findViewById(R.id.txt_nav_main_hospital);
        txtHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapSearchActivity.class);
                startActivity(intent);

                finish();
            }
        });

        //사료 추천이 눌렸을 때
        TextView txtRecommend = findViewById(R.id.txt_nav_main_recommend);
        txtRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FeedRecommendActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }

    private void setDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_main_act);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void setOnBtnClickListener() {
        imgMenu = (ImageView) findViewById(R.id.img_main_act_menu);
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
    }

    //블루투스 활성화 결과를 위한 메소드드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_REQUEST_ENABLE:
                if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                    Toast.makeText(getApplicationContext(), "블루투스 활성화", Toast.LENGTH_LONG).show();
                    bluetooth.listPairedDevices();
                } else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                    Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //db에 데이타 넣는 함수
    public void insertData(String tableName, String front_left, String front_right, String end_left, String end_right) {
        Log.e("msg2", "inserData");

        if(database!= null) {
            String sql = "insert or replace into "+tableName+"(front_left, front_right, end_left, end_right) values (?, ?, ?, ?)";
            Object[] params = {front_left, front_right, end_left,end_right};
            database.execSQL(sql, params);
        } else{
            Log.e("msg2","데이터 베이스를 오픈하세요");
        }

    }

    public void leg_compare(String tableName) {
        String sql_select = "select front_left, front_right, end_left, end_right from "+tableName;
        Cursor cursor=database.rawQuery(sql_select,null );

        cursor.moveToLast();
        int front_left= Integer.parseInt(cursor.getString(0));
        int front_right=Integer.parseInt(cursor.getString(1));
        int end_left=Integer.parseInt(cursor.getString(2));
        int end_right=Integer.parseInt(cursor.getString(3));

        if(front_left<front_right && front_left<end_left&& front_left<end_right) {
            front_left_leg.setVisibility(View.VISIBLE);
            leg_controll_txt.setText(getApplicationContext().getResources().getString(R.string.front_left_txt));
        }
        else if (front_right<front_left&& front_right<end_left && front_right<end_right) {
            front_right_leg.setVisibility(View.VISIBLE);
            leg_controll_txt.setText(getApplicationContext().getResources().getString(R.string.front_right_txt));
        }
        else if( end_left<front_left && end_left<front_right && end_left<end_right) {
            end_left_leg.setVisibility(View.VISIBLE);
            leg_controll_txt.setText(getApplicationContext().getResources().getString(R.string.end_left_txt));
        }
        else {
            end_right_leg.setVisibility(View.VISIBLE);
            leg_controll_txt.setText(getApplicationContext().getResources().getString(R.string.end_right_txt));
        }

    }
}
