package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.pcs.ztqtj.R;

/**
 * 雨量图例控件
 * Created by tyaathome on 2017/4/16.
 */

public class RainLegend extends LinearLayout implements View.OnTouchListener {
    public RainLegend(Context context) {
        super(context);
        init(context);
    }

    public RainLegend(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RainLegend(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_rain_legend, this, true);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}
