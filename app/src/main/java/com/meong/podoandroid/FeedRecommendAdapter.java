package com.meong.podoandroid;

import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FeedRecommendAdapter extends RecyclerView.Adapter<FeedRecommendAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FeedData> listData = new ArrayList<>();
    public String[] mColors= {"#dee9ff", "#fcd2ce","#f8cfa9"};

    public FeedRecommendAdapter(Context context, ArrayList<FeedData> listData) {
        this.context=context;
        this.listData=listData;
    }
    @NonNull
    @Override
    public FeedRecommendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_feed_recommend_card,parent,false);
       ViewHolder holder= new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedRecommendAdapter.ViewHolder holder, int position) {
        holder.container.setBackgroundColor(Color.parseColor(mColors[position % 3]));

        FeedData feedData=listData.get(position);
        holder.txt_title.setText(feedData.getTitle());
        Glide.with(holder.itemView.getContext())
                .load(feedData.getImg_url())
                .into(holder.img_feed);

        holder.txt_content.setText(feedData.getContent());
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



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_title=itemView.findViewById(R.id.txt_rv_item_feed_name);
            img_feed=itemView.findViewById(R.id.img_rv_item_feed_recommend);
            txt_content=itemView.findViewById(R.id.txt_rv_item_feed_content);
            container=itemView.findViewById(R.id.container_rv_item_archive_card);


        }
    }
}
