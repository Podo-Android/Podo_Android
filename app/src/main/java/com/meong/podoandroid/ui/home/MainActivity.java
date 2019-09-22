package com.meong.podoandroid.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.meong.podoandroid.R;
import com.meong.podoandroid.bluetooth.BluetoothService;
import com.meong.podoandroid.data.FeedData;
import com.meong.podoandroid.helper.DatabaseHelper;
import com.meong.podoandroid.helper.DogDBHelper;
import com.meong.podoandroid.ui.feed.FeedRecommendActivity;
import com.meong.podoandroid.ui.map.MapSearchActivity;


import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

    LineChart lineChart;
    LineDataSet set1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;

        lineChart= (LineChart)findViewById(R.id.line_chart);

        front_right_leg=(ImageView)findViewById(R.id.front_right_leg);
        front_left_leg=(ImageView)findViewById(R.id.front_left_leg);
        end_right_leg=(ImageView)findViewById(R.id.end_right_leg);
        end_left_leg=(ImageView)findViewById(R.id.end_left_leg);
        leg_controll_txt=(TextView)findViewById(R.id.leg_controll_txt);


        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);

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


        insertLegData("dog_leg","20","15","30","40");
        insertLegData("dog_leg","10","20","15","5");
        leg_compare("dog_leg");

        insertWeightData("dog_weight","10","16");
        insertWeightData("dog_weight","20","17");
        insertWeightData("dog_weight","10","18");
        insertWeightData("dog_weight","30","19");
        insertWeightData("dog_weight","40","20");
        insertWeightData("dog_weight","15","21");
        insertWeightData("dog_weight","50","22");
        DrawLineChart("dog_weight");


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
                    //몸무게를 잰 것은 오늘 날짜도 처리해서 같이 넘겨줘야함
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
    public void insertLegData(String tableName, String front_left, String front_right, String end_left, String end_right) {
        Log.d("msg2", "insert leg Data");

        if(database!= null) {
            String sql = "insert or replace into "+tableName+"(front_left, front_right, end_left, end_right) values (?, ?, ?, ?)";
            Object[] params = {front_left, front_right, end_left,end_right};
            database.execSQL(sql, params);
        } else{
            Log.e("msg2","데이터 베이스를 오픈하세요");
        }

    }

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


    public void DrawLineChart(String tableName) {

        ArrayList<Entry> values = new ArrayList<>();
        String sql_select = "select weight, date from "+tableName;
        Cursor cursor=database.rawQuery(sql_select,null );


        for(int i=0; i<cursor.getCount(); i++) {
            cursor.moveToNext();
            float weight=Float.parseFloat(cursor.getString(0));
            float date=Float.parseFloat(cursor.getString(1));
            values.add(new Entry(date, weight));
        }
       // cursor.close();

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "몸무게 그래프");
            set1.setDrawIcons(false);
//            set1.enableDashedLine(10f, 5f, 0f);
//            set1.enableDashedHighlightLine(10f, 5f, 0f);
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            YAxis yAxisRight = lineChart.getAxisRight();
            yAxisRight.setDrawLabels(false);

            set1.setColor(Color.DKGRAY);
            set1.setCircleColor(Color.DKGRAY);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(6f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);

            lineChart.setDescription(null);

            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.DKGRAY);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            lineChart.setData(data);
        }
    }



}
