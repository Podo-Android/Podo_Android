package com.meong.podoandroid.ui.feed;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.meong.podoandroid.R;
import com.meong.podoandroid.data.FeedData;
import com.meong.podoandroid.helper.DatabaseHelper;

import java.util.ArrayList;

public class FeedRecommendAdapter extends RecyclerView.Adapter<FeedRecommendAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FeedData> listData = new ArrayList<>();
    DatabaseHelper dbhelper;
    SQLiteDatabase database;
    Cursor cursor;
    String tableName,sql_select;


    public String[] mColors= {"#dee9ff", "#fcd2ce","#f8cfa9"};

    public FeedRecommendAdapter(Context context, ArrayList<FeedData> listData,String tableName) {
        this.context=context;
        this.listData=listData;
        this.tableName=tableName;
    }



    @NonNull
    @Override
    public FeedRecommendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_feed_recommend_card,parent,false);
       ViewHolder holder= new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedRecommendAdapter.ViewHolder holder, final int position) {
        dbhelper= new DatabaseHelper(context);
        database=dbhelper.getWritableDatabase();
        sql_select = "select title, img_url, content, url from "+tableName;
        cursor=database.rawQuery(sql_select,null );

        holder.container.setBackgroundColor(Color.parseColor(mColors[position % 3]));

        FeedData feedData=listData.get(position);
        holder.txt_title.setText(feedData.getTitle());
        Glide.with(holder.itemView.getContext())
                .load(feedData.getImg_url())
                .into(holder.img_feed);

        holder.txt_content.setText(feedData.getContent());

        holder.purchase_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position);
               Uri uri=Uri.parse(cursor.getString(3));

                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void setItems(ArrayList<FeedData> items) {
        this.listData=items;
    }

   public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txt_title;
        private ImageView img_feed;
        private TextView txt_content;
        private ConstraintLayout container;
        private TextView purchase_btn;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_title=itemView.findViewById(R.id.txt_rv_item_feed_name);
            img_feed=itemView.findViewById(R.id.img_rv_item_feed_recommend);
            txt_content=itemView.findViewById(R.id.txt_rv_item_feed_content);
            container=itemView.findViewById(R.id.container_rv_item_archive_card);
            purchase_btn=itemView.findViewById(R.id.purchase_btn);


        }
    }
}
