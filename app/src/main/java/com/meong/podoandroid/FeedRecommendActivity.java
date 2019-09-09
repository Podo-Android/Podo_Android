package com.meong.podoandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class FeedRecommendActivity extends AppCompatActivity {

    Button feed_btn1, feed_btn2, feed_btn3;
    private ArrayList<FeedData> mArrayList;
    private  FeedRecommendAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context mContext;
    RecyclerView mRecyclerview;
    SnapHelper snapHelper;
    DatabaseHelper dbhelper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_recommend);
        mContext=this;

        mArrayList= new ArrayList<>();
        snapHelper= new LinearSnapHelper();
        dbhelper= new DatabaseHelper(mContext);
        database=dbhelper.getWritableDatabase();


    feed_btn1=(Button)findViewById(R.id.feed_btn1);
    feed_btn2=(Button)findViewById(R.id.feed_btn2);
    feed_btn3=(Button)findViewById(R.id.feed_btn3);

    mRecyclerview= (RecyclerView)findViewById(R.id.feed_recommend_recyclerview);

    //처음 사료 추천 화면으로 갔을 때는 다리 건강 사료 추천 버튼이 눌려있도록
    feed_btn1.setSelected(true);
    feed_btn1.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGrayWhite));

    insertData("feed_leg","사료1","https://img.theqoo.net/img/xRxVm.jpg","맛나요");
    insertData("feed_leg","사료2","https://img.theqoo.net/img/xRxVm.jpg","최고");

    insertData("feed_calory","사료3","https://img.theqoo.net/img/xRxVm.jpg","맛나요");
    insertData("feed_calory","사료4","https://img.theqoo.net/img/xRxVm.jpg","최고");

    insertData("feed_wellbing","사료5","https://img.theqoo.net/img/xRxVm.jpg","맛나요");
    insertData("feed_wellbing","사료6","https://img.theqoo.net/img/xRxVm.jpg","최고");


    String sql_select = "select title, url, content from feed_leg";
    Cursor cursor=database.rawQuery(sql_select,null );

    for(int i=0; i<cursor.getCount(); i++) {
        cursor.moveToNext();
        String title= cursor.getString(0);
        String url=cursor.getString(1);
        String content=cursor.getString(2);

        mArrayList.add(new FeedData(title, url, content));
//    mArrayList.add(new FeedData("사료2","https://img.theqoo.net/img/Ixosq.jpg","굿"));
//    mArrayList.add(new FeedData("사료3","https://img.theqoo.net/img/Ixosq.jpg","꺅"));
//    mArrayList.add(new FeedData("사료4","https://img.theqoo.net/img/Ixosq.jpg","맛나요"));
//    mArrayList.add(new FeedData("사료5","https://img.theqoo.net/img/Ixosq.jpg","맛나요"));
    }
    cursor.close();

    mLayoutManager = new LinearLayoutManager(this, LinearLayout.HORIZONTAL,false);
    mRecyclerview.setLayoutManager(mLayoutManager);

    HorizontalSpaceItemDecoration itemDecoration =new HorizontalSpaceItemDecoration(this,80,100);
    mRecyclerview.addItemDecoration(itemDecoration);

    snapHelper.attachToRecyclerView(mRecyclerview);
    adapter = new FeedRecommendAdapter(mContext, mArrayList);
    mRecyclerview.setAdapter(adapter);


    feed_btn1.setOnClickListener(click);
    feed_btn2.setOnClickListener(click);
    feed_btn3.setOnClickListener(click);


    }

    private Button.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.feed_btn1:
                    InitialButton();
                    feed_btn1.setSelected(true);
                    feed_btn1.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGrayWhite));
                    view_data("feed_leg");
                    break;

                case R.id.feed_btn2:
                    InitialButton();
                    feed_btn2.setSelected(true);
                    feed_btn2.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGrayWhite));
                    view_data("feed_calory");
                    break;
                case R.id.feed_btn3:
                    InitialButton();
                    feed_btn3.setSelected(true);
                    //feed_btn3.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.buttonpress));
                    feed_btn3.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGrayWhite));
                    view_data("feed_wellbing");
                    break;
            }
        }
    };

   public void InitialButton() {
       feed_btn1.setSelected(false);
       feed_btn1.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGray));
       feed_btn2.setSelected(false);
       feed_btn2.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGray));
       feed_btn3.setSelected(false);
       feed_btn3.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGray));

    }



    public void insertData(String tableName, String title, String url, String content) {
        Log.e("msg", "inserData");

        if(database!= null) {
            String sql = "insert into "+tableName+"(title, url, content) values (?, ?, ?)";
            Object[] params = {title, url, content};
            database.execSQL(sql, params);
        } else{
            Log.e("msg","데이터 베이스를 오픈하세요");
        }

    }

    public void view_data(String tableName) {
        String sql_select = "select title, url, content from "+tableName;
        Cursor cursor=database.rawQuery(sql_select,null );

        for(int i=0; i<cursor.getCount(); i++) {
            cursor.moveToNext();
            String title= cursor.getString(0);
            String url=cursor.getString(1);
            String content=cursor.getString(2);

            mArrayList.add(new FeedData(title, url, content));
//    mArrayList.add(new FeedData("사료2","https://img.theqoo.net/img/Ixosq.jpg","굿"));
//    mArrayList.add(new FeedData("사료3","https://img.theqoo.net/img/Ixosq.jpg","꺅"));
//    mArrayList.add(new FeedData("사료4","https://img.theqoo.net/img/Ixosq.jpg","맛나요"));
//    mArrayList.add(new FeedData("사료5","https://img.theqoo.net/img/Ixosq.jpg","맛나요"));
        }
        cursor.close();
    }

}

