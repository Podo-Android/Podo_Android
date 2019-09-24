package com.meong.podoandroid.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;


import com.meong.podoandroid.R;
import com.meong.podoandroid.data.FeedData;
import com.meong.podoandroid.helper.DatabaseHelper;
import com.meong.podoandroid.helper.DogDBHelper;
import com.meong.podoandroid.ui.feed.FeedRecommendActivity;
import com.meong.podoandroid.ui.map.MapSearchActivity;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ImageView front_right_leg, front_left_leg, end_right_leg,end_left_leg, arrow_left,arrow_right;
    TextView leg_controll_txt,today_weight,today_weight_obesity, month_txt, month_aver_txt;

    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mPairedDevices;
    List<String> mListPairedDevices;

    public Handler mBluetoothHandler;
    ConnectedBluetoothThread mThreadConnectedBluetooth;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;

    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    SQLiteDatabase database;
    DogDBHelper databaseHelper;


    ImageView imgMenu, sign_start, sign_center, sign_end;
    DrawerLayout drawer;

    private Context mContext;

    LineChart lineChart;
    LineDataSet set1;

    int num=9;

    String readMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;

        lineChart= (LineChart)findViewById(R.id.line_chart);

        sign_start=(ImageView)findViewById(R.id.sign_start);
        sign_center=(ImageView)findViewById(R.id.sign_center);
        sign_end=(ImageView)findViewById(R.id.sign_end);
        arrow_left=(ImageView)findViewById(R.id.arrow_left);
        arrow_right=(ImageView)findViewById(R.id.arrow_right);
        front_right_leg=(ImageView)findViewById(R.id.front_right_leg);
        front_left_leg=(ImageView)findViewById(R.id.front_left_leg);
        end_right_leg=(ImageView)findViewById(R.id.end_right_leg);
        end_left_leg=(ImageView)findViewById(R.id.end_left_leg);
        leg_controll_txt=(TextView)findViewById(R.id.leg_controll_txt);
        today_weight=(TextView)findViewById(R.id.today_weight);
        today_weight_obesity=(TextView)findViewById(R.id.today_weight_obesity);
        month_txt=(TextView)findViewById(R.id.month_txt);
        month_aver_txt=(TextView)findViewById(R.id.month_aver_txt);

        //getDefaultAdapter메소드-> 해당 장치가 블루투스 기능을 지원하는지 알아오는 메소드
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);


        Switch sw= (Switch)findViewById(R.id.bluetooth_on_off_switch);

        databaseHelper= new DogDBHelper(mContext);
        database=databaseHelper.getWritableDatabase();

        sw.setTextOff("OFF");
        sw.setTextOn("ON");
        sw.setChecked(false);

        setDrawer();
        setOnBtnClickListener();
        onDrawerItemClickListener();



//
//        insertLegData("dog_leg","20","15","30","40");
//        insertLegData("dog_leg","10","20","15","5");
//        leg_compare("dog_leg");

        insertWeightData("dog_weight","10","2019-09-01");
        insertWeightData("dog_weight","10","2019-09-11");
        insertWeightData("dog_weight","10","2019-09-15");
        insertWeightData("dog_weight","10","2019-09-13");
        insertWeightData("dog_weight","10","2019-09-07");
        insertWeightData("dog_weight","10","2019-09-16");
        insertWeightData("dog_weight","20","2019-09-17");
        insertWeightData("dog_weight","10","2019-09-18");
        insertWeightData("dog_weight","30","2019-09-19");
        insertWeightData("dog_weight","40","2019-09-20");
        insertWeightData("dog_weight","15","2019-09-21");
        insertWeightData("dog_weight","4","2019-09-22");
        DrawLineChart();

       //delete_table("dog_weight");

        today_weight("dog_weight");

        insertMonthWeightData("9","4");
        insertMonthWeightData("8","3");
        insertMonthWeightData("7","6");
        insertMonthWeightData("6","9");

        up_down_month();



        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    bluetoothOn();
                }
                else{
                    bluetoothOff();
                }
            }
        });

        //블루투스 핸들러로 블루투스 연결 뒤 수신된 데이터를 읽어와 ReceiveData 텍스트 뷰에 표시
        mBluetoothHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == BT_MESSAGE_READ){
                    Log.d("bt","블루투스 연결됨");
                     readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                   // mTvReceiveData.setText(readMessage);
                    String Message= String.valueOf(readMessage.charAt(0));

                  //  Toast.makeText(getApplicationContext(),Message, Toast.LENGTH_LONG).show();
                    //new Thread(new Runnable() {
                      //  @Override
                      //  public void run() {
                            Log.d("bt",Message);
                            leg_compare(Message);

                    //    }
                //    }).start();

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


    //db에 데이타 넣는 함수
   /* public void insertLegData(String tableName, String front_left, String front_right, String end_left, String end_right) {
        Log.d("msg2", "insert leg Data");

        if(database!= null) {
            String sql = "insert or replace into "+tableName+"(front_left, front_right, end_left, end_right) values (?, ?, ?, ?)";
            Object[] params = {front_left, front_right, end_left,end_right};
            database.execSQL(sql, params);
        } else{
            Log.e("msg2","데이터 베이스를 오픈하세요");
        }

    }*/

    public void insertWeightData(String tableName, String weight, String date) {
        Log.d("msg2","insert weight data");

                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");


                if(database!=null) {
                    String sql= "insert or replace into "+tableName+"(weight, date) values (?, ?)";
                    Object[] params = {weight, date};
                    database.execSQL(sql, params);
                } else{
                    Log.e("msg2","데이터 베이스를 오픈하세요");
                }
    }

    public void leg_compare(String leg_name) {
      /*  String sql_select = "select front_left, front_right, end_left, end_right from "+tableName;
        Cursor cursor=database.rawQuery(sql_select,null );

        cursor.moveToLast();
        int front_left= Integer.parseInt(cursor.getString(0));
        int front_right=Integer.parseInt(cursor.getString(1));
        int end_left=Integer.parseInt(cursor.getString(2));
        int end_right=Integer.parseInt(cursor.getString(3));*/

      switch (leg_name){
        case "3":
            front_left_leg.setVisibility(View.VISIBLE);
            front_right_leg.setVisibility(View.GONE);
            end_right_leg.setVisibility(View.GONE);
            end_left_leg.setVisibility(View.GONE);
            leg_controll_txt.setText(getApplicationContext().getResources().getString(R.string.front_left_txt));
            break;
          case "1":
            front_right_leg.setVisibility(View.VISIBLE);
            front_left_leg.setVisibility(View.GONE);
            end_left_leg.setVisibility(View.GONE);
            end_right_leg.setVisibility(View.GONE);
            leg_controll_txt.setText(getApplicationContext().getResources().getString(R.string.front_right_txt));
            break;
          case "4":
            end_left_leg.setVisibility(View.VISIBLE);
            end_right_leg.setVisibility(View.GONE);
            front_left_leg.setVisibility(View.GONE);
            front_right_leg.setVisibility(View.GONE);
            leg_controll_txt.setText(getApplicationContext().getResources().getString(R.string.end_left_txt));
            break;
          case "2":
            end_right_leg.setVisibility(View.VISIBLE);
            end_left_leg.setVisibility(View.GONE);
            front_right_leg.setVisibility(View.GONE);
            front_left_leg.setVisibility(View.GONE);
            leg_controll_txt.setText(getApplicationContext().getResources().getString(R.string.end_right_txt));
            break;
        }

    }

    public void today_weight(String tableName) {
        String sql_select = "select weight from "+tableName;
        Cursor cursor=database.rawQuery(sql_select,null );

        cursor.moveToLast();
        today_weight.setText(cursor.getString(0)+"kg");
        int weight = Integer.parseInt(cursor.getString(0));

        if(weight<4) {
            today_weight_obesity.setText("저체중");
        }
        else if(weight>=4 && weight<6) {
            today_weight_obesity.setText("보통");

        } else{
            today_weight_obesity.setText("과체중");
        }


    }

    public void DrawLineChart() {
        lineChart.invalidate();
        lineChart.clear();

     //   database=databaseHelper.getReadableDatabase();


        List<Entry> values = new ArrayList<>();
        values.add(new Entry(16,4));
        values.add(new Entry(17,2));
        values.add(new Entry(18,7));
        values.add(new Entry(19,6));
        values.add(new Entry(20,5));
        values.add(new Entry(21,7));
        values.add(new Entry(22,2));

        LineDataSet lineDataSet = new LineDataSet(values,"weight"); //LineDataSet 선언
        lineDataSet.setColor(getApplicationContext().getResources().getColor(R.color.line_circle)); //LineChart에서 Line Color 설정
        lineDataSet.setCircleColor(getApplicationContext().getResources().getColor(R.color.line_circle)); // LineChart에서 Line Circle Color 설정

        LineData lineData = new LineData(); //LineDataSet을 담는 그릇 여러개의 라인 데이터가 들어갈 수 있습니다.
        lineData.addDataSet(lineDataSet);

        lineData.setValueTextColor(getApplicationContext().getResources().getColor(R.color.line_circle)); //라인 데이터의 텍스트 컬러 설정
        lineData.setValueTextSize(9);

        XAxis xAxis = lineChart.getXAxis(); // x 축 설정
        final String[] labels=new String[]{"MON","TUE","WED","THU","FRI","SAT","SUN"};
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //x 축 표시에 대한 위치 설정

            //X축의 데이터를 제 가공함. new ChartXValueFormatter은 Custom한 소스
        xAxis.setLabelCount(7, true); //X축의 데이터를 최대 몇개 까지 나타낼지에 대한 설정 5개 force가 true 이면 반드시 보여줌
        xAxis.setTextColor(getApplicationContext().getResources().getColor(R.color.colorTextGray)); // X축 텍스트컬러설정
        xAxis.setGridColor(getApplicationContext().getResources().getColor(R.color.line_circle)); // X축 줄의 컬러 설정
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        YAxis yAxisLeft = lineChart.getAxisLeft(); //Y축의 왼쪽면 설정
        yAxisLeft.setTextColor(getApplicationContext().getResources().getColor(R.color.colorTextGray)); //Y축 텍스트 컬러 설정
        yAxisLeft.setGridColor(getApplicationContext().getResources().getColor(R.color.line_circle)); // Y축 줄의 컬러 설정

        YAxis yAxisRight = lineChart.getAxisRight(); //Y축의 오른쪽면 설정
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);
        //y축의 활성화를 제거함

       // lineChart.setVisibleXRangeMinimum(60 * 60 * 24 * 1000 * 5); //라인차트에서 최대로 보여질 X축의 데이터 설정
        lineChart.setDescription(null); //차트에서 Description 설정 저는 따로 안했습니다.

        Legend legend = lineChart.getLegend(); //레전드 설정 (차트 밑에 색과 라벨을 나타내는 설정)
     //   legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);//하단 왼쪽에 설정
        legend.setTextColor(getApplicationContext().getResources().getColor(R.color.line_circle)); // 레전드 컬러 설정

        lineChart.setData(lineData);


//        String sql_select = "select weight, date from "+tableName;
//        Cursor cursor=database.rawQuery(sql_select,null );

//        String sql_select = "SELECT * FROM "+tableName+" Where date >= date('now','weekday 0', '-7 days', 'localtime') AND date <= date('now','weekday 0', '-1 days', 'localtime');";
//        Cursor cursor=database.rawQuery(sql_select,null);
//
//        while(cursor.moveToNext()) {
//
//            float weight=Float.parseFloat(cursor.getString(0));
//            long date=Long.parseLong(cursor.getString(1));
//            values.add(new Entry(date, weight));
//        }
//        cursor.close();


          //  XAxis xAxis = lineChart.getXAxis(); // x 축 설정
            //  xAxis.setPosition(XAxis.XAxisPosition.TOP); //x 축 표시에 대한 위치 설정
            //xAxis.setValueFormatter(new ChartXValueFormatter()); //X축의 데이터를 제 가공함. new ChartXValueFormatter은 Custom한 소스
//            xAxis.setLabelCount(7, true); //X축의 데이터를 최대 몇개 까지 나타낼지에 대한 설정 5개 force가 true 이면 반드시 보여줌
//
//
          /*  xAxis.setValueFormatter(new IAxisValueFormatter() {
                final String[] labels=new String[]{"MON","TUE","WED","THU","FRI","SAT","SUN"};
                @Override
                public String getFormattedValue(float value, AxisBase axis) {

                        return labels[(int)value%labels.length];

                }
            });
*/


        }


//    public class MyXAxisValueFormatter implements IAxisValueFormatter{
//        private String[] mValues;
//
//        public MyXAxisValueFormatter(String[] values) {
//            this.mValues= values;
//        }
//        @Override
//        public String getFormattedValue(float value, AxisBase axis) {
//           return mValues[(int) value];
//        }
//
//
//    }


    public void up_down_month() {


        arrow_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                num--;
               if(num>0) {
                   String month = Integer.toString(num);
                   month_txt.setText(month+"월");
                   month_aver_compare("month_data",month);
                   month_aver_txt.setTextSize(40);
               } else {
                   num=12;
                   String month = Integer.toString(num);
                   month_txt.setText(month+"월");
                   month_aver_compare("month_data",month);
                   month_aver_txt.setTextSize(40);
               }
            }
        });

        arrow_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                num++;
                if(num<10) {
                    String month=Integer.toString(num);
                    month_txt.setText(month+"월");
                    month_aver_compare("month_data",month);
                    month_aver_txt.setTextSize(40);
                } else if(10<=num&& num<13) {
                    String month=Integer.toString(num);
                    month_txt.setText(month+"월");
                    month_aver_txt.setText("아직 데이터가 없습니다");
                    month_aver_txt.setTextSize(12);
                    sign_center.setVisibility(View.GONE);
                }else {
                    num=1;
                    String month=Integer.toString(num);
                    month_txt.setText(month+"월");
                    month_aver_txt.setText("아직 데이터가 없습니다");
                    month_aver_txt.setTextSize(12);
                    sign_center.setVisibility(View.GONE);
                }
            }
        });
    }

    public void insertMonthWeightData(String month, String weight_aver) {

        if(database!= null) {
            String sql = "insert or replace into month_data(month, weight_aver) values (?, ?)";
            Object[] params = {month, weight_aver};
            database.execSQL(sql, params);
        } else{
            Log.e("msg2","데이터 베이스를 오픈하세요");
        }
    }

    public void month_aver_compare(String tableName, String mon) {


        String sql_select = "select weight_aver from "+tableName+" where month = '"+mon+"'";
        Cursor cursor=database.rawQuery(sql_select,null );

       // if(cursor!=null && cursor.getCount()>0) {
            cursor.moveToFirst();
            month_aver_txt.setText(cursor.getString(0) + "kg");
        //}
        int weight = Integer.parseInt(cursor.getString(0));

        if(weight<4) {
            sign_start.setVisibility(View.VISIBLE);
            sign_center.setVisibility(View.GONE);
            sign_end.setVisibility(View.GONE);
        }
        else if(weight>=4 && weight<6) {
            sign_center.setVisibility(View.VISIBLE);
            sign_end.setVisibility(View.GONE);
            sign_start.setVisibility(View.GONE);

        } else{
            sign_end.setVisibility(View.VISIBLE);
            sign_start.setVisibility(View.GONE);
            sign_center.setVisibility(View.GONE);
        }


    }



    public void delete_table(String tableName) {
        String sql_select = "delete from "+tableName;
        database.execSQL(sql_select);

    }

    public class IndexAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues= new String[] {};
        private int mValueCount = 0;
        public IndexAxisValueFormatter(String[] values) {
            if(values != null) {
                setValues(values);
            }
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
           int index= Math.round(value);

           if(index < 0 || index >= mValueCount|| index != (int)value)
               return "";

            return mValues[index];
        }

        public String[] getValues()
        {
            return mValues;
        }
        public void setValues(String[] values)
        {
            if(values==null)
                values=new String[] {};

            this.mValues=values;
            this.mValueCount=values.length;
        }
    }

    public void bluetoothOn() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "블루투스가 이미 활성화 되어 있습니다.", Toast.LENGTH_LONG).show();
            } else { //블루투스 비활성화 상태

                //ACTION_REQUEST_ENABLE 로 지정하여 Intent를 발생시키면 사용자에게 블루투스 활성 여부를 묻는 다이얼로그가 뜸
                Intent intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //활성화 창을 띄워 onActivityResult에서 결과를 처리
                startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE);

            }
        }
    }

    public void bluetoothOff() { //블루투스 비활성화 메소드
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        } else {
            Toast.makeText(getApplicationContext(), "블루투스가 이미 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //블루투스 활성화 결과를 위한 메소드드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_REQUEST_ENABLE:
                if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                    Toast.makeText(getApplicationContext(), "블루투스 활성화", Toast.LENGTH_LONG).show();
                    listPairedDevices();
                } else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                    Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    //블루투스 페어링 장치 목록 가져오는 메소드
    public void listPairedDevices() {
        if (mBluetoothAdapter.isEnabled()) { //블루투스 활성화 상태인지 확인
            mPairedDevices = mBluetoothAdapter.getBondedDevices();

            if (mPairedDevices.size() > 0) { //페어링 된 장치가 존재한다면
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
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
                 //   bytes = mmInStream.available();
                 //   if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer);
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                   // }
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
                Toast.makeText(getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }

        //블루투스 소켓을 닫는 메소드
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }
}


