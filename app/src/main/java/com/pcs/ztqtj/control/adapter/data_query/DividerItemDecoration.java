package com.pcs.ztqtj.control.adapter.data_query;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by tyaathome on 2018/1/15.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private int padding;

    public DividerItemDecoration(int padding) {
        this.padding = padding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.right = padding;
    }
}
