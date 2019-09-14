package com.meong.podoandroid.ui.feed;

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

import com.meong.podoandroid.helper.DatabaseHelper;
import com.meong.podoandroid.R;
import com.meong.podoandroid.data.FeedData;
import com.meong.podoandroid.ui.deco.HorizontalSpaceItemDecoration;

import java.util.ArrayList;

public class FeedRecommendActivity extends AppCompatActivity {

    Button feed_btn1, feed_btn2, feed_btn3;
    private ArrayList<FeedData> mArrayList;
    private FeedRecommendAdapter adapter;
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

    insertData("feed_leg","NATURAL 0","http://shop1.phinf.naver.net/20170313_32/googlelsi_1489403381484kVw95_JPEG/13668205842604657_-1246674263.jpg","유기농으로 만든 강아지 사료 정말 맛있어요~~~");
    insertData("feed_leg","PURE","http://shop1.phinf.naver.net/20190116_280/banrymall_1547610017951sRiYW_JPEG/30241197607536529_697648285.jpg","퓨어한 강아지 사료 맛있어요~~");
    insertData("feed_leg","NATURAL","http://blogfiles.naver.net/20140930_270/dhkswjssovus_1412077848772ISpJP_JPEG/%B3%BB%C3%DF%B7%B2_%B9%DF%B6%F5%BD%BA_%BF%C0%B0%A1%B4%D0_-_%BA%B9%BB%E7%BA%BB.jpg","강아지 사료로 최고입니다");

    insertData("feed_calory","Dr.Mamma","https://phinf.pstatic.net/shop/20180207_222/dr-mamma2_1517979171001KjNb9_JPEG/698029708095745_1019992530.jpg","닥터맘마 사료를 추천합니다");
    insertData("feed_calory","NUTRA GOLD","http://blogfiles.naver.net/20140930_194/dhkswjssovus_1412075690636Fjf7I_JPEG/%B4%BA%C6%AE%B6%F3_%B0%F1%B5%E5.jpg","뉴트라 골드 강아지 사료입니다~~");

    insertData("feed_wellbing","CANAGAN","http://shop1.phinf.naver.net/20171208_238/jsl2439_1512736251421xhhkj_JPEG/36043431063492999_-443674421.jpg","미국산 최고급 강아지 사료");
    insertData("feed_wellbing","WISE","http://shop1.phinf.naver.net/20190624_42/mankuna_15613654438475gyOS_JPEG/44084302690641001_678454637.jpg","강아지 고양이 다 이용 가능한 사료");


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

    //리사이클러뷰 간격
    HorizontalSpaceItemDecoration itemDecoration =new HorizontalSpaceItemDecoration(this,80,100);
    mRecyclerview.addItemDecoration(itemDecoration);

    //리사이클러뷰 슬라이딩
    snapHelper.attachToRecyclerView(mRecyclerview);

    adapter = new FeedRecommendAdapter(mContext, mArrayList);
    mRecyclerview.setAdapter(adapter);


    feed_btn1.setOnClickListener(click);
    feed_btn2.setOnClickListener(click);
    feed_btn3.setOnClickListener(click);


    }

    //버튼 클릭 이벤트 처리
    private Button.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.feed_btn1:
                    InitialButton();
                    feed_btn1.setSelected(true);
                    feed_btn1.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGrayWhite));
                    mArrayList.clear();
                    view_data("feed_leg");
                    break;

                case R.id.feed_btn2:
                    InitialButton();
                    feed_btn2.setSelected(true);
                    feed_btn2.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGrayWhite));
                    mArrayList.clear();
                    view_data("feed_calory");
                    break;
                case R.id.feed_btn3:
                    InitialButton();
                    feed_btn3.setSelected(true);
                    //feed_btn3.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.buttonpress));
                    feed_btn3.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGrayWhite));
                    mArrayList.clear();
                    view_data("feed_wellbing");
                    break;
            }
        }
    };

    //버튼 초기화
   public void InitialButton() {
       feed_btn1.setSelected(false);
       feed_btn1.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGray));
       feed_btn2.setSelected(false);
       feed_btn2.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGray));
       feed_btn3.setSelected(false);
       feed_btn3.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGray));

    }


   //db에 데이타 넣는 함수
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
    //data 불러와서 recyclerview에 갱신
    public void view_data(String tableName) {
        String sql_select = "select title, url, content from "+tableName;
        Cursor cursor=database.rawQuery(sql_select,null );


        for(int i=0; i<cursor.getCount(); i++) {
            cursor.moveToNext();
            String title= cursor.getString(0);
            String url=cursor.getString(1);
            String content=cursor.getString(2);

            mArrayList.add(new FeedData(title, url, content));
            adapter.notifyDataSetChanged();
//    mArrayList.add(new FeedData("사료2","https://img.theqoo.net/img/Ixosq.jpg","굿"));
//    mArrayList.add(new FeedData("사료3","https://img.theqoo.net/img/Ixosq.jpg","꺅"));
//    mArrayList.add(new FeedData("사료4","https://img.theqoo.net/img/Ixosq.jpg","맛나요"));
//    mArrayList.add(new FeedData("사료5","https://img.theqoo.net/img/Ixosq.jpg","맛나요"));
        }
        cursor.close();


    }

}

