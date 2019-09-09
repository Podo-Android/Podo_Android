package com.meong.podoandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class FeedRecommendActivity extends AppCompatActivity {

    Button feed_btn1, feed_btn2, feed_btn3;
    private ArrayList<FeedData> mArrayList;
    private  FeedRecommendAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context mContext;
    RecyclerView mRecyclerview;
    SnapHelper snapHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_recommend);
        mContext=this;

        mArrayList= new ArrayList<>();
        snapHelper= new LinearSnapHelper();

    feed_btn1=(Button)findViewById(R.id.feed_btn1);
    feed_btn2=(Button)findViewById(R.id.feed_btn2);
    feed_btn3=(Button)findViewById(R.id.feed_btn3);

    mRecyclerview= (RecyclerView)findViewById(R.id.feed_recommend_recyclerview);

    //처음 사료 추천 화면으로 갔을 때는 다리 건강 사료 추천 버튼이 눌려있도록
    feed_btn1.setSelected(true);
    feed_btn1.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGrayWhite));

    mArrayList.add(new FeedData("사료1","https://img.theqoo.net/img/xRxVm.jpg","맛나요"));
    mArrayList.add(new FeedData("사료2","https://img.theqoo.net/img/Ixosq.jpg","굿"));
    mArrayList.add(new FeedData("사료3","https://img.theqoo.net/img/Ixosq.jpg","꺅"));
    mArrayList.add(new FeedData("사료4","https://img.theqoo.net/img/Ixosq.jpg","맛나요"));
    mArrayList.add(new FeedData("사료5","https://img.theqoo.net/img/Ixosq.jpg","맛나요"));

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
                    break;

                case R.id.feed_btn2:
                    InitialButton();
                    feed_btn2.setSelected(true);
                    feed_btn2.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGrayWhite));
                    break;
                case R.id.feed_btn3:
                    InitialButton();
                    feed_btn3.setSelected(true);
                    //feed_btn3.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.buttonpress));
                    feed_btn3.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGrayWhite));
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

}

