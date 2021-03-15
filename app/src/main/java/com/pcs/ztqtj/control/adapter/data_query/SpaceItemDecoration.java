package com.pcs.ztqtj.control.adapter.data_query;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by tyaathome on 2017/10/12.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int left;
    private int bottom;
    private int column = 0;
    private int padding;

    /**
     *
     * @param left
     * @param bottom
     * @param column 每行个数
     */
    public SpaceItemDecoration(int left, int bottom, int column) {
        this.left = left;
        this.bottom = bottom;
        this.column = column;
    }

    public SpaceItemDecoration(int padding) {
        this.padding = padding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if(column == 0) {
            int index = parent.getChildAdapterPosition(view);
            if(index == 0) {
                outRect.top = padding;
                outRect.left = padding;
                outRect.right = padding;
                outRect.bottom = padding;
            } else {
                outRect.bottom = padding;
                outRect.left = padding;
                outRect.right = padding;
            }
        } else {
            int index = parent.getChildAdapterPosition(view);
            int remainder = index % column;
//        if(remainder != 0 && remainder < column) {
//            outRect.left = left;
//        }
//        outRect.bottom = bottom;
            if (column == 2) {
                int columnWidth = (int) (left / 2.0f);
                if (remainder == 0) {
                    outRect.right = columnWidth;
                } else if (remainder == 1) {
                    outRect.left = columnWidth;
                }
            } else if (column > 2) {
                int columnWidth = (int) (left / 3.0f);
                if (remainder == 0) {
                    outRect.right = columnWidth * 2;
                } else if (remainder == column - 1) {
                    outRect.left = columnWidth * 2;
                } else {
                    outRect.left = columnWidth;
                    outRect.right = columnWidth;
                }
            }
            outRect.top = bottom/2;
            outRect.bottom = bottom/2;
        }
    }
}
