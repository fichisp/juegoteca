package com.juegoteca.util;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Suleiman on 26-07-2015.
 */
public class RightSpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int mSpace;

    public RightSpacesItemDecoration(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = mSpace;
    }
}