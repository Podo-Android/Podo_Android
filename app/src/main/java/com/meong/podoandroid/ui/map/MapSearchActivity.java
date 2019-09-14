package com.meong.podoandroid.ui.map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.meong.podoandroid.R;
import com.meong.podoandroid.ui.menu.MainActivity;

public class MapSearchActivity extends AppCompatActivity {

    DrawerLayout drawer;
    ImageView imgMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);

        setDrawer();
        setOnBtnClickListener();
        onDrawerItemClickListener();
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

    }

    private void setDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_map_search_act);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void setOnBtnClickListener() {
        imgMenu = (ImageView) findViewById(R.id.map_hamburger);
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
    }
}
