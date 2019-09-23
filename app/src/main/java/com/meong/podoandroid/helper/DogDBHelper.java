package com.meong.podoandroid.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DogDBHelper extends SQLiteOpenHelper {
    public DogDBHelper( Context context) {
        super(context,"dog.db",null,15);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        dogSql(sqLiteDatabase);
    }

    private void dogSql(SQLiteDatabase sqLiteDatabase) {

        String tableName1="dog_leg";
        String tableName2="dog_weight";
        String tableName3="month_data";

        String sql1="create table if not exists "  + tableName1+"(_id integer PRIMARY KEY autoincrement, front_left text, front_right text, end_left text, end_right text)";
        String sql2="create table if not exists "  + tableName2+"(_id integer PRIMARY KEY autoincrement, weight text, date date(10))";
        String sql3="create table if not exists "  + tableName3+"(_id integer PRIMARY KEY autoincrement, month text, weight_aver text)";

        sqLiteDatabase.execSQL(sql1);
        sqLiteDatabase.execSQL(sql2);
        sqLiteDatabase.execSQL(sql3);

        Log.d("msg2","table create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS dog_leg");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS dog_weight");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS month_data");
        onCreate(sqLiteDatabase);
    }
}
