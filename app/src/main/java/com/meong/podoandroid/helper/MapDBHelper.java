package com.meong.podoandroid.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.meong.podoandroid.data.StoreItem;

import java.util.ArrayList;

import static com.meong.podoandroid.helper.Sql.createTableStoreSql;
import static com.meong.podoandroid.helper.Sql.insertStoreSql;
import static com.meong.podoandroid.helper.Sql.selectStoreSql;

public class MapDBHelper {

    public static Cursor cursor;
    public static String sql;

    private static final String TAG = "DBHelper";

    private static SQLiteDatabase database;

    /**
     * DB open
     */
    public static void openDatabase(Context context, String databaseName) {
        Log.d(TAG, "opentDatabase() 호출 됨.");

        try {
            database = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);

            if (database != null) {
                Log.d(TAG, "데이터 베이스 " + databaseName + " 오픈됨.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * create Table
     */
    public static void createTable(String tableName) {
        /*
        만약 테이블을 삭제하고 싶을 때
         */
        //database.execSQL("drop table if exists "+tableName);

        Log.d(TAG, "createTable() 호출 됨." + tableName);

        if (database != null) {
            switch (tableName) {
                default:
                case "store":
                    database.execSQL(createTableStoreSql);
            }
        }
    }

    /**
     * MapActivity 실행 시에
     * data를 넣습니다.
     */
    public static void insertStoreData(ArrayList<StoreItem> items) {
        for (int i = 0; i < items.size(); i++) {
            String sql = insertStoreSql;
            Object[] params = {items.get(i).getName(), items.get(i).getLatitude(), items.get(i).getLongtitude(), items.get(i).getAddress()};

            if (database != null) {
                database.execSQL(sql, params);
            }
        }
        Log.d(TAG,"store 데이터를 넣었습니다.");
    }

    /**
     * MapAcrivity 실행 시,
     * Location Search 이후에
     *
     * 데이터를 불러옵니다.
     */

    public static  ArrayList<StoreItem> storeSelect(String tableName){
        sql = selectStoreSql + tableName;

        cursor = database.rawQuery(sql,null);

        Log.d(TAG, "store 조회된 데이터 개수 : " + cursor.getCount());

        ArrayList<StoreItem> storeItems = new ArrayList<>();

        int i=0;
        while(cursor.moveToNext()) {
            storeItems.add(getStoreItemFromCursor(cursor,i));
            i++;
        }
        Log.d(TAG, "#"+ i + "->" + storeItems.toString());

        cursor.close();

        return storeItems;
    }

    private static StoreItem getStoreItemFromCursor(Cursor cursor, int i) {
        String name = cursor.getString(0);
        Float latitude = cursor.getFloat(1);
        Float longtitude = cursor.getFloat(2);
        String address = cursor.getString(3);

        StoreItem storeItem = new StoreItem(name, latitude, longtitude, address);

        return storeItem;
    }

}
