package com.meong.podoandroid.ui.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.meong.podoandroid.bluetooth.BluetoothService;
import com.meong.podoandroid.ui.feed.FeedRecommendActivity;
import com.meong.podoandroid.ui.map.MapSearchActivity;
import com.meong.podoandroid.R;

public class MainActivity extends AppCompatActivity {

    final static int BT_REQUEST_ENABLE = 1;
    BluetoothService bluetooth;

    ImageView imgMenu;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetooth= new BluetoothService(this);

        Switch sw= (Switch)findViewById(R.id.bluetooth_on_off_switch);

        sw.setTextOff("OFF");
        sw.setTextOn("ON");
        sw.setChecked(false);

        setDrawer();
        setOnBtnClickListener();
        onDrawerItemClickListener();

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
}
