package com.meong.podoandroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    Context context;
    private final int space;
    private final int first_left_space;

    public HorizontalSpaceItemDecoration(Context context, int space, int first_left_space) {
        this.space=space;
        this.first_left_space=first_left_space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left=space/2;
        outRect.right=space/2;

        if(parent.getChildAdapterPosition(view)==0) {
            outRect.left=first_left_space;
        }
    }
}
