package com.meong.podoandroid.ui.feed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meong.podoandroid.helper.DatabaseHelper;
import com.meong.podoandroid.R;
import com.meong.podoandroid.data.FeedData;
import com.meong.podoandroid.ui.deco.HorizontalSpaceItemDecoration;
import com.meong.podoandroid.ui.map.MapActivity;
import com.meong.podoandroid.ui.map.MapSearchActivity;
import com.meong.podoandroid.ui.home.MainActivity;

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
    DrawerLayout drawer;
    ImageView imgMenu, backButton;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_recommend);
        mContext=this;

        mArrayList= new ArrayList<>();
        snapHelper= new LinearSnapHelper();
        dbhelper= new DatabaseHelper(mContext);
        database=dbhelper.getWritableDatabase();

        setDrawer();
        setOnBtnClickListener();
        onDrawerItemClickListener();


    feed_btn1=(Button)findViewById(R.id.feed_btn1);
    feed_btn2=(Button)findViewById(R.id.feed_btn2);
    feed_btn3=(Button)findViewById(R.id.feed_btn3);

    mRecyclerview= (RecyclerView)findViewById(R.id.feed_recommend_recyclerview);

    //처음 사료 추천 화면으로 갔을 때는 다리 건강 사료 추천 버튼이 눌려있도록
    feed_btn1.setSelected(true);
    feed_btn1.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGrayWhite));

    insertData("feed_leg","NATURAL 0","http://shop1.phinf.naver.net/20170313_32/googlelsi_1489403381484kVw95_JPEG/13668205842604657_-1246674263.jpg","유기농으로 만든 강아지 사료 정말 맛있어요~~~","https://smartstore.naver.com/catbab/products/680158993?&frm=NVSCIMG");
    insertData("feed_leg","PURE","http://shop1.phinf.naver.net/20190116_280/banrymall_1547610017951sRiYW_JPEG/30241197607536529_697648285.jpg","퓨어한 강아지 사료 맛있어요~~","https://smartstore.naver.com/banryomall/products/4079368654?&frm=NVSCIMG");
    insertData("feed_leg","PUREOVITA","http://shop1.phinf.naver.net/20190507_251/momoba_1557189023287GB3NI_JPEG/43431652196122989_722995650.jpg","강아지 사료로 최고입니다","https://smartstore.naver.com/momoba/products/4491897982?NaPm=ct%3Dk0jbi048%7Cci%3Dc6763bc25d79f04708a39027e52e3e26db0b434a%7Ctr%3Dimg%7Csn%3D284553%7Chk%3D8847592300ae89a0f21ac7dfe2e4de34b15ddb15");

    insertData("feed_calory","Dr.Mamma","https://phinf.pstatic.net/shop/20180207_222/dr-mamma2_1517979171001KjNb9_JPEG/698029708095745_1019992530.jpg","닥터맘마 사료를 추천합니다","https://shopping.naver.com/pet/stores/100128254/products/2496459017?NaPm=ct%3Dk0jbk2yo%7Cci%3D2e5ad3101b0590de8f1f97fa7e90652dbf0eb6ad%7Ctr%3Dimg%7Csn%3D529446%7Chk%3D03814acbb7fe656b84d6ad33ac81d2d2db02f073");
    insertData("feed_calory","NATURAL CORE","http://shop1.phinf.naver.net/20170606_204/phc1153_1496714370625VHxv0_JPEG/20020335251473449_1899306817.jpg","뉴트라 골드 강아지 사료입니다~~","https://smartstore.naver.com/petskingdoms_com/products/2012016981?NaPm=ct%3Dk0jbl7uw%7Cci%3D4b20753cfe5bc610292157086de377dfb4256666%7Ctr%3Dimg%7Csn%3D482946%7Chk%3Dffb191435c109836491e7d05710c7f3b52996106");

    insertData("feed_wellbing","SOFT LAMB","http://shop1.phinf.naver.net/20180628_189/nubitel1@domemart.co.kr_1530155715555DXsO8_JPEG/53462016177122315_572480971.jpg","미국산 최고급 강아지 사료","https://smartstore.naver.com/nubitel7/products/4293779440?NaPm=ct%3Dk0jbmixc%7Cci%3De9fe1c86231f5a26c4a7bfc88d8da027ee068b8b%7Ctr%3Dimg%7Csn%3D176316%7Chk%3D132ee46e2657e7ca0af9e5d643588e71826ad7eb");
    insertData("feed_wellbing","WISE","http://shop1.phinf.naver.net/20190624_42/mankuna_15613654438475gyOS_JPEG/44084302690641001_678454637.jpg","강아지 고양이 다 이용 가능한 사료","https://smartstore.naver.com/mankuna/products/4567153064?NaPm=ct%3Dk0jbgbxk%7Cci%3Ddeb4c52cf39152f6171a1fbdbb2d9f9bf0d01c98%7Ctr%3Dimg%7Csn%3D275210%7Chk%3Df69e26f2e2631183f888eecc62abb4c57b385144");


    String sql_select = "select title, img_url, content from feed_leg";
    Cursor cursor=database.rawQuery(sql_select,null );
   FeedRecommendAdapter table= new FeedRecommendAdapter(mContext, mArrayList,"feed_leg");
   setUpAdapter(table);

    for(int i=0; i<cursor.getCount(); i++) {
        cursor.moveToNext();
        String title= cursor.getString(0);
        String img_url=cursor.getString(1);
        String content=cursor.getString(2);

        mArrayList.add(new FeedData(title, img_url, content));
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

   adapter = new FeedRecommendAdapter(mContext, mArrayList,"feed_leg");
//    mRecyclerview.setAdapter(adapter);
        setUpAdapter(adapter);


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
                    FeedRecommendAdapter table1 = new FeedRecommendAdapter(mContext, mArrayList,"feed_leg");
                    setUpAdapter(table1);
                    break;

                case R.id.feed_btn2:
                    InitialButton();
                    feed_btn2.setSelected(true);
                    feed_btn2.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGrayWhite));
                    mArrayList.clear();
                    view_data("feed_calory");
                    FeedRecommendAdapter table2 = new FeedRecommendAdapter(mContext, mArrayList,"feed_calory");
                    setUpAdapter(table2);
                    break;
                case R.id.feed_btn3:
                    InitialButton();
                    feed_btn3.setSelected(true);
                    //feed_btn3.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.buttonpress));
                    feed_btn3.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGrayWhite));
                    mArrayList.clear();
                    view_data("feed_wellbing");
                    FeedRecommendAdapter table3 = new FeedRecommendAdapter(mContext, mArrayList,"feed_wellbing");
                    setUpAdapter(table3);
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
    public void insertData(String tableName, String title, String img_url, String content, String url) {
        Log.e("msg", "inserData");

        if(database!= null) {
            String sql = "insert or replace into "+tableName+"(title, img_url, content, url) values (?, ?, ?, ?)";
            Object[] params = {title, img_url, content,url};
            database.execSQL(sql, params);
        } else{
            Log.e("msg","데이터 베이스를 오픈하세요");
        }

    }

    //data 불러와서 recyclerview에 갱신
    public void view_data(String tableName) {
        String sql_select = "select title, img_url, content from "+tableName;
        Cursor cursor=database.rawQuery(sql_select,null );


        for(int i=0; i<cursor.getCount(); i++) {
            cursor.moveToNext();
            String title= cursor.getString(0);
            String img_url=cursor.getString(1);
            String content=cursor.getString(2);

            mArrayList.add(new FeedData(title, img_url, content));
            adapter.notifyDataSetChanged();
//    mArrayList.add(new FeedData("사료2","https://img.theqoo.net/img/Ixosq.jpg","굿"));
//    mArrayList.add(new FeedData("사료3","https://img.theqoo.net/img/Ixosq.jpg","꺅"));
//    mArrayList.add(new FeedData("사료4","https://img.theqoo.net/img/Ixosq.jpg","맛나요"));
//    mArrayList.add(new FeedData("사료5","https://img.theqoo.net/img/Ixosq.jpg","맛나요"));
        }
        cursor.close();


    }

    //menu 코드
    private void setDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_feed_act);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void setOnBtnClickListener() {
        imgMenu = (ImageView) findViewById(R.id.feed_recommend_menu_icon);
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        //뒤로가기 버튼 눌렀을 때
        backButton=(ImageView)findViewById(R.id.feed_recommend_back_icon);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
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

    private void setUpAdapter(FeedRecommendAdapter adapter) {

        mRecyclerview.setAdapter(adapter);
    }




}

