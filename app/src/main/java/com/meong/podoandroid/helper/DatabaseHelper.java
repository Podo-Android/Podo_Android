package com.meong.podoandroid.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "feed.db",null,7);

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        feedSql(sqLiteDatabase);
    }

    private void feedSql(SQLiteDatabase sqLiteDatabase) {
        String tableName1 = "feed_leg";
        String tableName2="feed_calory";
        String tableName3="feed_wellbing";

        //table 삭제 할 때만 주석 해제
//        sqLiteDatabase.execSQL("drop table if exists "+tableName1);
//        sqLiteDatabase.execSQL("drop table if exists "+tableName2);
//        sqLiteDatabase.execSQL("drop table if exists "+tableName3);

        String sql1="create table if not exists "  + tableName1+"(_id integer PRIMARY KEY autoincrement, title text, img_url text, content text, url text)";
        String sql2="create table if not exists "  + tableName2+"(_id integer PRIMARY KEY autoincrement, title text, img_url text, content text, url text)";
        String sql3="create table if not exists "  + tableName3+"(_id integer PRIMARY KEY autoincrement, title text, img_url text, content text, url text)";

        sqLiteDatabase.execSQL(sql1);
        sqLiteDatabase.execSQL(sql2);
        sqLiteDatabase.execSQL(sql3);

        Log.e("msg","table create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS feed_leg");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS feed_calory");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS feed_wellbing");
        onCreate(sqLiteDatabase);

    }
}
