package com.meong.podoandroid.ui.map

import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.meong.podoandroid.R
import com.meong.podoandroid.ui.menu.MainActivity
import kotlinx.android.synthetic.main.activity_map_search.*

class MapSearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_search)

        setDrawer()
        setOnBtnClickListener()
        onDrawerItemClickListener()
    }

    private fun onDrawerItemClickListener() {
        // 홈이 눌렸을 때
        val txtHome = findViewById<View>(R.id.txt_nav_main_home) as TextView
        txtHome.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)

            finish()
        }

    }

    private fun setDrawer() {
        drawer_map_search_act.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun setOnBtnClickListener() {
        map_hamburger.setOnClickListener { drawer_map_search_act.openDrawer(Gravity.LEFT) }
    }
}
