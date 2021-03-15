package com.pcs.ztqtj.control.tool;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by tyaathome on 2017/10/12.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int left;
    private int bottom;
    private int column;

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

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int index = parent.getChildAdapterPosition(view);
        int remainder = index%column;
        if(column == 2) {
            int columnWidth = (int) (left/2.0f);
            if(remainder == 0) {
                outRect.right = columnWidth;
            } else if(remainder == 1) {
                outRect.left = columnWidth;
            }
        } else if(column > 2) {
            int columnWidth = (int) (left/3.0f);
            if(remainder == 0) {
                outRect.right = columnWidth*2;
            } else if(remainder == column-1) {
                outRect.left = columnWidth*2;
            } else {
                outRect.left = columnWidth;
                outRect.right = columnWidth;
            }
        }
        outRect.bottom = bottom;
    }
}
