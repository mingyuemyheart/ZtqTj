package com.pcs.ztqtj.view.myview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;

/**
 * Created by tyaathome on 2019/06/14.
 */
@SuppressLint("AppCompatCustomView")
public class UnderLineRadioButton extends RadioButton {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int lineWidth;
    private int lineHeight;
    private int marginBottom;
    private int lineColor;
    private boolean currentChecked = false;
    private int left = 0, top = 0, right = 0, bottom = 0;

    {
        lineWidth = CommUtils.dp2px(30);
        lineHeight = CommUtils.dp2px(2);
        marginBottom = CommUtils.dp2px(5);
        lineColor = Color.WHITE;
    }

    public UnderLineRadioButton(Context context) {
        super(context);
        init(context, null);
    }

    public UnderLineRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UnderLineRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if(attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.UnderLineRadioButton, 0, 0);
            lineWidth = typedArray.getDimensionPixelSize(R.styleable.UnderLineRadioButton_line_width, lineWidth);
            lineHeight = typedArray.getDimensionPixelSize(R.styleable.UnderLineRadioButton_line_height, lineHeight);
            marginBottom = typedArray.getDimensionPixelSize(R.styleable.UnderLineRadioButton_line_marginBottom, marginBottom);
            lineColor = typedArray.getColor(R.styleable.UnderLineRadioButton_line_color, lineColor);
        }
        paint.setColor(lineColor);
    }

//    @Override
//    protected void dispatchDraw(Canvas canvas) {
////        if(currentChecked != isChecked()) {
////            if (isChecked()) {
////                int left = (int) ((getWidth() - lineWidth) / 2f);
////                int right = left + lineWidth;
////                int bottom = getHeight() - marginBottom;
////                int top = bottom + lineHeight;
////                canvas.drawRect(left, top, right, bottom, paint);
////                Log.e("UnderLineRadioButton", "id:" + getId() + " true");
////            } else {
////                Log.e("UnderLineRadioButton", "id:" + getId() + " false");
////            }
////        }
////        currentChecked = isChecked();
//        super.dispatchDraw(canvas);
//        canvas.drawRect(left, top, right, bottom, paint);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(currentChecked) {
            left = (int) ((getWidth() - lineWidth) / 2f);
            right = left + lineWidth;
            bottom = getHeight() - marginBottom;
            top = bottom + lineHeight;
        } else {
            left = right = bottom = top = 0;
        }
        canvas.drawRect(left, top, right, bottom, paint);
    }

    @Override
    public void setChecked(boolean checked) {
        currentChecked = checked;
        super.setChecked(checked);
        invalidate();
    }

}
