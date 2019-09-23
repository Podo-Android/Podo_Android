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
import android.media.Image;
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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView front_right_leg, front_left_leg, end_right_leg,end_left_leg, arrow_left,arrow_right;
    TextView leg_controll_txt,today_weight,today_weight_obesity, month_txt, month_aver_txt;
    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    BluetoothService bluetooth;
    Handler mBluetoothHandler;
    SQLiteDatabase database;
    DogDBHelper databaseHelper;


    ImageView imgMenu, sign_start, sign_center, sign_end;
    DrawerLayout drawer;

    private Context mContext;

    LineChart lineChart;
    LineDataSet set1;

    int num=9;



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
        DrawLineChart("dog_weight");

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

    public void DrawLineChart(String tableName) {
        lineChart.invalidate();
        lineChart.clear();

        database=databaseHelper.getReadableDatabase();


        List<Entry> values = new ArrayList<>();
//        String sql_select = "select weight, date from "+tableName;
//        Cursor cursor=database.rawQuery(sql_select,null );

        String sql_select = "SELECT * FROM "+tableName+" Where date >= date('now','weekday 0', '-7 days', 'localtime') AND date <= date('now','weekday 0', '-1 days', 'localtime');";
        Cursor cursor=database.rawQuery(sql_select,null);

        while(cursor.moveToNext()) {

            float weight=Float.parseFloat(cursor.getString(0));
            long date=Long.parseLong(cursor.getString(1));
            values.add(new Entry(date, weight));
        }
        cursor.close();

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
//            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
//            set1.setValues(values);
//            lineChart.getData().notifyDataChanged();
//            lineChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "weight");
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setHighlightEnabled(true);
            set1.setLineWidth(2);
            set1.setColor(R.color.point_pink);
            set1.setCircleColor(R.color.buttonpress);
            set1.setCircleRadius(4);
            set1.setCircleHoleRadius(2);
            set1.setDrawHighlightIndicators(true);
            set1.setHighLightColor(Color.RED);
            set1.setValueTextSize(7);
            set1.setValueTextColor(Color.DKGRAY);
            YAxis yAxisRight = lineChart.getAxisRight(); //Y축의 오른쪽면 설정
            yAxisRight.setDrawLabels(false);
            yAxisRight.setDrawAxisLine(false);
            yAxisRight.setDrawGridLines(false);
            XAxis xAxis = lineChart.getXAxis(); // x 축 설정
            //  xAxis.setPosition(XAxis.XAxisPosition.TOP); //x 축 표시에 대한 위치 설정
            //xAxis.setValueFormatter(new ChartXValueFormatter()); //X축의 데이터를 제 가공함. new ChartXValueFormatter은 Custom한 소스
//            xAxis.setLabelCount(7, true); //X축의 데이터를 최대 몇개 까지 나타낼지에 대한 설정 5개 force가 true 이면 반드시 보여줌
//
//
//            xAxis.setValueFormatter(new IAxisValueFormatter() {
//                final String[] labels=new String[]{"MON","TUE","WED","THU","FRI","SAT","SUN"};
//                @Override
//                public String getFormattedValue(float value, AxisBase axis) {
//
//                        return labels[(int)value%labels.length];
//
//                }
//            });

            LineData lineData = new LineData(set1);
            lineChart.getDescription().setText("");
            lineChart.getDescription().setTextSize(10);
            lineChart.setDrawMarkers(true);
            lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            lineChart.animateY(1000);
            lineChart.getXAxis().setGranularityEnabled(true);
            lineChart.getXAxis().setGranularity(1.0f);
            lineChart.getXAxis().setLabelCount(set1.getEntryCount());
            lineChart.setData(lineData);


        }
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

}
