package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.ScreenUtil;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirTrendDown;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.ClickPositionListener;
import com.pcs.ztqtj.control.tool.DrawViewTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Z on 2016/8/4.
 */
public class AirQualityView extends View {
    protected Bitmap mBitmap;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    private int mode = NONE;
    /**
     * 就算两点间的距离
     */
    int startX;

    /**
     * 就算两点间的距离
     */
    int startY;

    int stopX;

    int stopY;

    float oldDist;

    private float mScale = 1.0f;

    private float mTop;

    private float mBottom;

    private float mLeft;

    private float mRight;

    private float disX;

    private float disY;

    private int whatX;

    private int whatY;

    protected Rect srcRect;

    protected RectF dstRect;
    private boolean reinvalidate = true;

    private ClickBitmap cutClickBitmap = new ClickBitmap();

    public AirQualityView(Context context) {
        super(context);
        this.setPadding(0, 0, 0, 0);
    }


    public AirQualityView(Context context, AttributeSet a) {
        super(context, a);
        this.setPadding(0, 0, 0, 0);
        initPointValue();
    }

    private float moviceStart;
    private Paint ptLineWidth;
    private Paint ptLine;
    private Paint ptYLine;
    private Paint ptLineNull;
    private Paint ptPre24;
    private Paint ptNext24;
    private Paint ptTextButtom;
    private Paint ptTextButtomFloag;
    private Paint ptTextTop;
    private Paint ptTextYValue;
    private float margLeft, margButton, margRight, margTop;
    private int stationX = 48;
    private int ringR = 0;


    private void initPointValue() {

        margTop = ScreenUtil.dip2px(getContext(), 15);
        margRight = ScreenUtil.dip2px(getContext(), 15);
        sizeText = ScreenUtil.dip2px(getContext(), 12);
        ringR = ScreenUtil.dip2px(getContext(), 4);
        ptTextButtom =
                DrawViewTool.getInstance().getTextPaint(getContext().getResources().getColor(R.color.text_white));
        ptTextTop = DrawViewTool.getInstance().getTextPaint(getContext().getResources().getColor(R.color.text_white));
        ptTextButtomFloag =
                DrawViewTool.getInstance().getTextPaint(getContext().getResources().getColor(R.color.text_white));
        ptTextYValue =
                DrawViewTool.getInstance().getTextPaint(getContext().getResources().getColor(R.color.text_white));
        ptTextButtom.setTextSize(sizeText);
        ptTextTop.setTextSize(sizeText);
        ptTextYValue.setTextSize(sizeText);

        ptTextButtomFloag.setTextSize(sizeText);

        ptLineWidth = DrawViewTool.getInstance().getMyLine(Color.argb(0, 0, 0, 0));
        ptLine = DrawViewTool.getInstance().getMyLine(Color.argb(255, 0, 0, 0));
        ptLine.setStrokeWidth(15);

        ptYLine = DrawViewTool.getInstance().getMyLine(getContext().getResources().getColor(R.color.bg_white));
        ptYLine.setStrokeWidth(2);


        ptLineNull = DrawViewTool.getInstance().getMyLine(getContext().getResources().getColor(R.color.bg_white));
        ptLineNull.setStrokeWidth(1);
        ptLineNull.setStyle(Paint.Style.FILL_AND_STROKE);
        PathEffect effects = new DashPathEffect(new float[]{6, 6, 6, 6}, 1);
        ptLineNull.setPathEffect(effects);

        ptNext24 = DrawViewTool.getInstance().getMyLine(getContext().getResources().getColor(R.color.text_white));
        ptPre24 = DrawViewTool.getInstance().getMyLine(getContext().getResources().getColor(R.color.text_white));
        ptNext24.setStrokeWidth(ringR / 3);
        ptPre24.setStrokeWidth(ringR / 3);
    }

    private boolean isMove = true;

    /**
     * 处理触碰事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                moviceStart = (int) event.getX();
                startX = (int) event.getX();
                startY = (int) event.getY();
                mode = DRAG;
                compleValue(startX, startY);
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(moviceStart - event.getX()) > 4) {
                    if (clicklistener != null) {
                        cutClickBitmap.bm = null;
                        invalidate();
                    }
                }
                mode = NONE;
                checkOutScreen();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    stopX = (int) event.getX();
                    stopY = (int) event.getY();
                    this.disX = stopX - startX;
                    this.disY = stopY - startY;
                    startX = stopX;
                    startY = stopY;
                    invalidate();

                }

                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 50f) {
                    mode = ZOOM;
                }
                break;
        }
        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private String strValue = "";
    private String strUnit = "mm";

    public String getUnit() {
        return strUnit;
    }

    public void setItemName(String strUnit, IsDrawRectangele cutDraw) {
        this.strUnit = strUnit;
        this.cutDraw = cutDraw;
    }

    private boolean isDoubleWidth = false;

    private void compleValue(int touchX, int touchY) {
        float viewYHight = getHeight() - margButton;
        for (int i = 0; i < rectangleAcHight.size(); i++) {
            Point pointCut = rectangleAcHight.get(i);
            float pY = viewYHight - pointCut.y;
            float pX = pointCut.x - getWidth() + whatX;
            if (touchX > pX - xSaction && touchX < pX + xSaction) {
                if (pointCut.y == 0) {
                    if (touchY >= viewYHight - ringR && touchY < viewYHight + ringR) {
                        setActView(i, pointCut);
                        return;
                    }
                } else {
                    if (touchY >= pY && touchY < viewYHight) {
                        setActView(i, pointCut);
                        return;
                    }
                }

                if (touchY >= (pY - ringR) && touchY <= (pY + ringR)) {
                    setActView(i, pointCut);
                    return;
                }

            }
        }
    }

    private void setPreCompView(int i, Point p) {
        if (cutClickBitmap.bm != null && cutClickBitmap.bitmapPosition == i && cutClickBitmap.isYbBitmap) {
            cutClickBitmap.bm = null;
        } else {
            cutClickBitmap.isYbBitmap = true;
            cutClickBitmap.bitmapPosition = i;
            String value = "--";
            if (!TextUtils.isEmpty(skList.get(i).val)) {
                value = skList.get(i).val;
            }
            drawClickImageView(p, strValue + value, true);
        }
        return;
    }

    /***
     * 升成实况的图片
     * @param i 图片的位置为第几
     * @param p 生成图片的位置参数
     */
    private void setActView(int i, Point p) {
        if (cutClickBitmap.bm != null && cutClickBitmap.bitmapPosition == i && !cutClickBitmap.isYbBitmap) {
            cutClickBitmap.bm = null;
        } else {
            cutClickBitmap.bitmapPosition = i;
            cutClickBitmap.isYbBitmap = false;

            String value = "--";
            if (!TextUtils.isEmpty(skList.get(i).val)) {
                value = skList.get(i).val;
            }
            drawClickImageView(p, strValue + value, false);
        }
    }


    private void drawClickImageView(Point p, String value, boolean isYb) {
        reinvalidate = true;

        cutClickBitmap.p.x = p.x;
        cutClickBitmap.p.y = (int) (p.y + margButton);
        if (p.y + getResources().getDimensionPixelSize(R.dimen.tvPopH) + margTop + margButton < getHeight()) {
            cutClickBitmap.bm = getClickBitmap(value, isYb, true);
            cutClickBitmap.isDrawTop = true;
        } else {
            cutClickBitmap.bm = getClickBitmap(value, isYb, false);
            cutClickBitmap.isDrawTop = false;
        }
        invalidate();
    }

    private ClickPositionListener clicklistener;

    public void setClickPositionListener(ClickPositionListener clicklistener) {
        this.clicklistener = clicklistener;
    }

    /**
     * 处理图片溢出屏幕
     */
    private void checkOutScreen() {
        float left = mLeft + whatX;
        float right = mRight + whatX;
        float top = mTop + whatY;
        float bottom = mBottom + whatY;

        float wight = right - left;
        float height = bottom - top;

        float screenW = getWidth();
        float screenH = getHeight();

        float disX = 0;
        float disY = 0;

        if (height < screenH) {
            if (top < 0) {
                disY = 0 - top;
            } else if (bottom > (getBottom() - getTop())) {
                disY = (getBottom() - getTop()) - bottom;
            }
        } else {
            if (top > 0) {
                disY = 0 - top;
            } else if (bottom < (getBottom() - getTop())) {
                disY = (getBottom() - getTop()) - bottom;
            }
        }

        if (wight < screenW) {
            if (left < getLeft()) {
                disX = getLeft() - left;
            } else if (right > getRight()) {
                disX = getRight() - right;
            }
        } else {
            if (left > getLeft()) {
                disX = getLeft() - left;
            } else if (right < getRight()) {
                disX = getRight() - right;
            }
        }
        this.disX = disX;
        this.disY = disY;
    }


    private void setBitmap(Bitmap bm) {
        float mScale = (float) getHeight() / (float) bm.getHeight();
        this.mScale = mScale;
        imageStartRight(bm, mScale);
    }


    /**
     * 初始化状态
     */
    private void reDefaultValue() {
        this.mLeft = 0;
        this.whatX = 0;
        this.mRight = 0;
        this.mTop = 0;
        this.whatY = 0;
        this.mBottom = 0;
        this.srcRect = null;
        this.dstRect = null;
        this.mBitmap = null;
    }


    /**
     * 居中 所有的都居中
     *
     * @param bm
     * @param mScale
     */
    private void imageStartCenter(Bitmap bm, float mScale) {
        float imageWidth = bm.getWidth() * mScale;
        float imageHight = bm.getHeight() * mScale;
        mLeft = (getWidth() - imageWidth) / 2;
        mTop = (getHeight() - imageHight) / 2;
        mRight = mLeft + imageWidth;
        mBottom = mTop + imageHight;
    }

    /**
     * 居中 所有的都居中
     *
     * @param bm
     * @param mScale
     */
    private void imageStartLeft(Bitmap bm, float mScale) {
        float imageWidth = bm.getWidth() * mScale;
        float imageHight = bm.getHeight() * mScale;
        mLeft = 0;
        mTop = 0;
        mRight = mLeft + imageWidth;
        mBottom = mTop + imageHight;
    }

    /**
     * 起始位置在右侧
     *
     * @param bm
     * @param scale
     */
    private void imageStartRight(Bitmap bm, float scale) {
        float imageWidth = bm.getWidth() * mScale;
        float imageHight = bm.getHeight() * mScale;
        mLeft = getWidth() - imageWidth;
        mTop = getHeight() - imageHight;
        mRight = mLeft + imageWidth;
        mBottom = mTop + imageHight;
    }


    public void moveToPro(boolean isTrue) {
        mLeft = 0;
        whatX = 0;
        cutClickBitmap.bm = null;
        if (isTrue) {
            disX = -(getWidth() * widthScale) / 2;
        } else {
            disX = 0;
        }
        invalidate();
    }


    private List<Point> rectangleAcHight = new ArrayList<>();
    private List<String> yValue = new ArrayList<>();
    private List<String> xValueButtom = new ArrayList<>();
    private List<String> xValueTop = new ArrayList<>();
    private List<String> yValue_conetent = new ArrayList<>();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (reinvalidate) {
            mBitmap = drawImage();
            setBitmap(mBitmap);
            reinvalidate = false;
            checkOutScreen();
        }
        if (null == mBitmap || mBitmap.isRecycled()) {
            canvas.drawARGB(0, 255, 255, 255);
            return;
        }
        float left = mLeft + whatX;
        float right = mRight + whatX;
        float top = mTop + whatY;
        float bottom = mBottom + whatY;
        if (whatX + disX < 0) {
            disX = 0;
        } else if (whatX + disX > getWidth()) {
            disX = 0;
        }


        if (disY != 0 || disX != 0) {
            whatX += disX;
            whatY += disY;
            left += disX;
            right += disX;
            top += disY;
            bottom += disY;
        }
        srcRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        dstRect = new RectF(left, mTop, right, mBottom);
        canvas.save();
        canvas.clipRect(margLeft, 0, getMeasuredWidth(), getMeasuredHeight());
        canvas.drawBitmap(mBitmap, srcRect, dstRect, null);
        canvas.restore();

        this.disY = 0;
        this.disX = 0;

        Path mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(margLeft, 0);
        mPath.lineTo(margLeft, getHeight());
        mPath.lineTo(0, getHeight());
        mPath.close();
        canvas.drawPath(mPath, ptLineWidth);
        ptLine.setColor(getResources().getColor(R.color.green));
        canvas.drawLine(margLeft, margTop, margLeft, getHeight() - margButton, ptLine);
        float h_len = (getHeight() - margButton - margTop) / 6;
        if (type.equals("0")){
            ptLine.setARGB(255, 153, 51, 51);
            canvas.drawLine(margLeft, margTop, margLeft, margTop + h_len, ptLine);
            ptLine.setARGB(255, 204, 51, 153);
            canvas.drawLine(margLeft, margTop + h_len, margLeft, margTop + h_len * 2, ptLine);
            ptLine.setARGB(255, 255, 0, 0);
            canvas.drawLine(margLeft, margTop + h_len * 2, margLeft, margTop + h_len * 3, ptLine);
            ptLine.setARGB(255, 255, 103, 0);
            canvas.drawLine(margLeft, margTop + h_len * 3, margLeft, margTop + h_len * 4, ptLine);
            ptLine.setARGB(255, 255, 255, 102);
            canvas.drawLine(margLeft, margTop + h_len * 4, margLeft, margTop + h_len * 5, ptLine);
            ptLine.setARGB(255, 100, 227, 100);
            canvas.drawLine(margLeft, margTop + h_len * 5, margLeft, margTop + h_len * 6, ptLine);
        }else if (type.equals("1")||type.equals("3")){
            ptLine.setARGB(255, 255, 255, 102);
            canvas.drawLine(margLeft, margTop, margLeft, margTop + h_len, ptLine);
            canvas.drawLine(margLeft, margTop + h_len, margLeft, margTop + h_len * 2, ptLine);
            canvas.drawLine(margLeft, margTop + h_len * 2, margLeft, margTop + h_len * 3, ptLine);
            ptLine.setARGB(255, 100, 227, 100);
            canvas.drawLine(margLeft, margTop + h_len * 3, margLeft, margTop + h_len * 4, ptLine);
            canvas.drawLine(margLeft, margTop + h_len * 4, margLeft, margTop + h_len * 5, ptLine);
            canvas.drawLine(margLeft, margTop + h_len * 5, margLeft, margTop + h_len * 6, ptLine);
        }else if (type.equals("2")){
            ptLine.setARGB(255, 255, 103, 0);
            canvas.drawLine(margLeft, margTop, margLeft, margTop + h_len, ptLine);
            canvas.drawLine(margLeft, margTop + h_len, margLeft, margTop + h_len * 2, ptLine);
            ptLine.setARGB(255, 255, 255, 102);
            canvas.drawLine(margLeft, margTop + h_len * 2, margLeft, margTop + h_len * 3, ptLine);
            canvas.drawLine(margLeft, margTop + h_len * 3, margLeft, margTop + h_len * 4, ptLine);
            ptLine.setARGB(255, 100, 227, 100);
            canvas.drawLine(margLeft, margTop + h_len * 4, margLeft, margTop + h_len * 5, ptLine);
            canvas.drawLine(margLeft, margTop + h_len * 5, margLeft, margTop + h_len * 6, ptLine);
        }else if (type.equals("4")){
            ptLine.setARGB(255, 255, 0, 0);
            canvas.drawLine(margLeft, margTop, margLeft, margTop + h_len, ptLine);
            ptLine.setARGB(255, 255, 103, 0);
            canvas.drawLine(margLeft, margTop + h_len, margLeft, margTop + h_len * 2, ptLine);
            canvas.drawLine(margLeft, margTop + h_len * 2, margLeft, margTop + h_len * 3, ptLine);
            ptLine.setARGB(255, 255, 255, 102);
            canvas.drawLine(margLeft, margTop + h_len * 3, margLeft, margTop + h_len * 4, ptLine);
            canvas.drawLine(margLeft, margTop + h_len * 4, margLeft, margTop + h_len * 5, ptLine);
            ptLine.setARGB(255, 100, 227, 100);
            canvas.drawLine(margLeft, margTop + h_len * 5, margLeft, margTop + h_len * 6, ptLine);
        }

        float acturalYhight = getHeight() - margButton - margTop;
        float actionYalue = acturalYhight / ySize;
        Paint.FontMetrics fontMetrics = ptTextButtom.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;

        for (int i = 0; i < yValue.size(); i++) {
            float yValueHight = actionYalue * i + margTop + fontHeight / 3;
            canvas.drawText(yValue.get(i), margLeft / 2, yValueHight, ptTextYValue);
        }
    }

    private int widthScale = 2;
    private int sizeText;

    private float xSaction;

    /*  创建一个画布，在这个画笔上绘制图，然后用这张图放大缩小；*/
    private Bitmap drawImage() {
        int AllViewWidth = getWidth() * widthScale;
        Bitmap bitmapAltered = Bitmap.createBitmap(AllViewWidth, getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvasBm = new Canvas(bitmapAltered);
        float viewXWidth, viewYHight, acturalYhight;

        Paint.FontMetrics fontMetrics = ptTextButtomFloag.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        margButton = (int) (4 * fontHeight);
        margLeft = 4 * fontHeight;
        if (isDoubleWidth) {
            margRight = 2 * fontHeight;
        } else {
            margRight = fontHeight;
        }
        viewXWidth = AllViewWidth - margLeft - margRight;
        viewYHight = getHeight() - margButton;
        acturalYhight = getHeight() - margButton - margTop;
        canvasBm.drawLine(margLeft, viewYHight, AllViewWidth - margRight, viewYHight, ptYLine);

        xSaction = viewXWidth / stationX;
        countValue((int) acturalYhight, xSaction);
        float actionYalue = acturalYhight / ySize;
        for (int i = 0; i < ySize; i++) {
            float yValueHight = actionYalue * i + margTop;
            Path mPath = new Path();
            mPath.moveTo(margLeft, yValueHight);
            mPath.lineTo(AllViewWidth - margRight, yValueHight);
            canvasBm.drawPath(mPath, ptLineNull);
        }

        for (int i = 0; i < xValueTop.size(); i++) {
            Bitmap timeBmTemp = Bitmap.createBitmap((int) xSaction / 2 * 3, (int) margButton, Bitmap.Config.ARGB_8888);
            Canvas canvasBmTemp = new Canvas(timeBmTemp);
            if (xValueTop.get(i).length() < 4) {
                canvasBmTemp.drawText(xValueTop.get(i).replace("时", ""), timeBmTemp.getWidth() / 2,
                        timeBmTemp.getHeight() / 2 - fontHeight / 2, ptTextTop);
                canvasBmTemp.drawText("时", timeBmTemp.getWidth() / 2, timeBmTemp.getHeight() / 2 + fontHeight / 2,
                        ptTextTop);
            } else {
                String tiem = xValueTop.get(i);
                canvasBmTemp.drawText(tiem.substring(0, tiem.indexOf("日")), timeBmTemp.getWidth() / 2,
                        timeBmTemp.getHeight() / 2 - fontHeight / 2, ptTextTop);
                canvasBmTemp.drawText("日", timeBmTemp.getWidth() / 2, timeBmTemp.getHeight() / 2 + fontHeight / 2,
                        ptTextTop);
            }
            float withXPosition = (int) (xSaction * ((i + 24) * 2 + 1) + margLeft - xSaction / 2);//每一个值横坐标的位置
            canvasBm.drawBitmap(timeBmTemp, withXPosition, getHeight() - margButton, new Paint());
        }
        for (int i = 0; i < xValueButtom.size(); i++) {
            Bitmap timeBmTemp = Bitmap.createBitmap((int) xSaction / 2 * 3, (int) margButton, Bitmap.Config.ARGB_8888);
            Canvas canvasBmTemp = new Canvas(timeBmTemp);

            if (xValueButtom.get(i).contains("时")) {
                canvasBmTemp.drawText(xValueButtom.get(i).replace("时", ""), timeBmTemp.getWidth() / 2,
                        timeBmTemp.getHeight() / 2 - fontHeight / 2, ptTextButtom);
                canvasBmTemp.drawText("时", timeBmTemp.getWidth() / 2, timeBmTemp.getHeight() / 2 + fontHeight / 2,
                        ptTextButtom);
            } else {
                String tiem = xValueButtom.get(i);
                canvasBmTemp.drawText(tiem.substring(0, tiem.indexOf("日")), timeBmTemp.getWidth() / 2,
                        timeBmTemp.getHeight() / 2 - fontHeight / 2, ptTextButtom);
                canvasBmTemp.drawText("日", timeBmTemp.getWidth() / 2, timeBmTemp.getHeight() / 2 + fontHeight / 2,
                        ptTextButtom);
            }
            float withXPosition = (int) (xSaction * (i * 2 + 1) + margLeft - xSaction / 2);//每一个值横坐标的位置
            canvasBm.drawBitmap(timeBmTemp, withXPosition, getHeight() - margButton, new Paint());
        }

        if (cutDraw == IsDrawRectangele.RECTANGLE) {
            drawRectangle(canvasBm, viewYHight);
        } else if (cutDraw == IsDrawRectangele.BROKENLINE) {
            drawBrokenLine(canvasBm, viewYHight);
        }


        if (cutClickBitmap.bm != null) {
            int bmx, bmy;
            bmx = cutClickBitmap.bm.getWidth();
            bmy = cutClickBitmap.bm.getHeight();
            int x = cutClickBitmap.p.x - bmx / 2;
            int tempY = (getHeight() - cutClickBitmap.p.y);
            int y;
            if (cutClickBitmap.isDrawTop) {
                y = tempY - bmy;
            } else {
                y = tempY;
            }
            canvasBm.drawBitmap(cutClickBitmap.bm, x, y, new Paint());
        }


        return bitmapAltered;
    }


    private IsDrawRectangele cutDraw = IsDrawRectangele.RECTANGLE;

    public enum IsDrawRectangele {
        RECTANGLE, BROKENLINE, DIRECTIONLINE;
    }

    /***
     * 绘制矩形图
     */
    private void drawRectangle(Canvas canvasBm, float viewYHight) {
        ptPre24.setStrokeWidth(xSaction);
        ptNext24.setStrokeWidth(xSaction);
        for (int i = 0; i < rectangleAcHight.size(); i++) {
            Point p = rectangleAcHight.get(i);
            if (p.y == DataNull) {
            } else {
                if (p.y == 0) {
                    canvasBm.drawCircle(p.x, viewYHight, ringR, ptPre24);
                } else {
                    canvasBm.drawLine(p.x, viewYHight - p.y, p.x, viewYHight, ptPre24);
                }
            }
        }
    }


    /***
     * 绘制折线图
     */
    private void drawBrokenLine(Canvas canvasBm, float viewYHight) {
        ptNext24.setStrokeWidth(ringR / 3);
        ptPre24.setStrokeWidth(ringR / 3);

        for (int i = 0; i < rectangleAcHight.size(); i++) {
            Point p = rectangleAcHight.get(i);
            if (p.y != DataNull) {
                canvasBm.drawCircle(p.x, viewYHight - p.y, ringR, ptPre24);
                if (i > 0) {
                    Point pp = rectangleAcHight.get(i - 1);
                    if (pp.y == DataNull) {
                        for (int j = i - 1; j >= 0; j--) {
                            if (rectangleAcHight.get(j).y != DataNull) {
                                pp = rectangleAcHight.get(j);
                                break;
                            }
                        }
                    }
                    if (pp.y != DataNull) {
                        canvasBm.drawLine(pp.x, viewYHight - pp.y, p.x, viewYHight - p.y, ptPre24);
                    }
                }
            }
        }
    }


    public String reverse(String s) {
        return new StringBuffer(s).reverse().toString();
    }

    private int ySize = 6;
    private float DataNull = -200000;

    private List<PackAirTrendDown.AirMapBean> skList = new ArrayList<>();

    private String[] str_level;
    private String type="0";

    /**
     * 设置数据
     */
    public void setNewData(List<PackAirTrendDown.AirMapBean> skList, String[] str, String tyype) {
        this.skList.clear();
        this.skList.addAll(skList);
        cutClickBitmap.bm = null;
        reinvalidate = true;
        this.str_level = str;
        this.type = tyype;
        reDefaultValue();
        invalidate();
    }

    /**
     * 计算柱状图位置，高度值
     *
     * @param acturalYhight
     * @param xSaction
     */
    private void countValue(int acturalYhight, float xSaction) {
        xValueButtom.clear();
        xValueTop.clear();
        rectangleAcHight.clear();
        yValue.clear();
        List<Float> listData = new ArrayList<>();
        List<Float> listDataAc = new ArrayList<>();
        List<Float> listDataPre = new ArrayList<>();
        for (int i = 0; i < skList.size(); i++) {
            xValueButtom.add(skList.get(i).time);
            if (!TextUtils.isEmpty(skList.get(i).val)) {
                listData.add(Float.parseFloat(skList.get(i).val));
                listDataAc.add(Float.parseFloat(skList.get(i).val));
            } else {
                listDataAc.add(DataNull);
            }
        }
        if (listData.size() == 0) {
            return;
        }

        float max = Collections.max(listData);
        float min = Collections.min(listData);

        float ySeactionTemp = 300;
        float tempMin = min % ySeactionTemp;
        if (min > 0) {
            min = min - tempMin;
        } else if (tempMin == 0) {
            min -= ySeactionTemp;
        } else {
            min = min - tempMin - ySeactionTemp;
        }

        if (min < 0) {
            min = 0;
        }

        float tempMax = max % ySeactionTemp;
        max += ySeactionTemp - tempMax;

        float ySection = (max - min) / ySize;
        float yProportion;
        if (max == 0 && max == min) {
            yProportion = 0;
        } else {
            yProportion = acturalYhight / (max - min);
        }
        setYValue();
        setAllXYValue(yProportion, xSaction, listDataAc, listDataPre, min);

    }

    private void setAllXYValue(float yProportion, float xSaction, List<Float> listDataAc, List<Float> listDataPre,
                               float min) {
        float h_len = (getHeight() - margButton - margTop) / 6;
        for (int i = 0; i < listDataAc.size(); i++) {
            Point point = new Point();
            if (DataNull == listDataAc.get(i)) {
                point.y = (int) DataNull;
                point.x = (int) (xSaction * (i * 2 + 1) + margLeft);
            } else {
                if (yValue_conetent.size() > 0) {
                    if (listDataAc.get(i) < Float.valueOf(yValue_conetent.get(1))) {
                        point.y = (int) ((listDataAc.get(i) / Float.valueOf(yValue_conetent.get(1))) * h_len);
                    } else if (listDataAc.get(i) < Float.valueOf(yValue_conetent.get(2))) {
                        point.y = (int) ((listDataAc.get(i) / Float.valueOf(yValue_conetent.get(2))) * h_len * 2);
                    } else if (listDataAc.get(i) < Float.valueOf(yValue_conetent.get(3))) {
                        point.y = (int) ((listDataAc.get(i) / Float.valueOf(yValue_conetent.get(3))) * h_len * 3);
                    } else if (listDataAc.get(i) < Float.valueOf(yValue_conetent.get(4))) {
                        point.y = (int) ((listDataAc.get(i) / Float.valueOf(yValue_conetent.get(4))) * h_len * 4);
                    } else if (listDataAc.get(i) < Float.valueOf(yValue_conetent.get(5))) {
                        point.y = (int) (((listDataAc.get(i)) / Float.valueOf(yValue_conetent.get(5))) * h_len + h_len * 5);
                    } else {
                        point.y = (int) (((listDataAc.get(i)) / Float.valueOf(yValue_conetent.get(6))) * h_len + h_len * 6);
                    }
                }
                point.x = (int) (xSaction * (i * 2 + 1) + margLeft);
            }
            rectangleAcHight.add(point);
        }
    }


    /**
     * #############y轴值##########
     */
    private void setYValue() {
        if (type.equals("0")) {
            if (str_level.length > 0) {
                yValue.add(str_level[0]);
                yValue.add(str_level[1]);
                yValue.add(str_level[2]);
                yValue.add(str_level[3]);
                yValue.add(str_level[4]);
                yValue.add(str_level[5]);
                yValue.add(str_level[6]);
            }
        } else if (type.equals("1")) {
            yValue.add("0");
            yValue.add("1");
            yValue.add("3");
            yValue.add("5");
            yValue.add("7");
            yValue.add("9");
            yValue.add("10");
        } else if (type.equals("2")) {
            yValue.add("0");
            yValue.add("50");
            yValue.add("100");
            yValue.add("150");
            yValue.add("200");
            yValue.add("300");
            yValue.add("400");
        } else if (type.equals("3")) {
            yValue.add("0");
            yValue.add("50");
            yValue.add("100");
            yValue.add("150");
            yValue.add("170");
            yValue.add("190");
            yValue.add("200");
        } else if (type.equals("4")) {
            yValue.add("0");
            yValue.add("80");
            yValue.add("160");
            yValue.add("200");
            yValue.add("250");
            yValue.add("300");
            yValue.add("400");
        }
        yValue_conetent.clear();
        yValue_conetent.addAll(yValue);

        Collections.reverse(yValue);
    }
    public Bitmap getClickBitmap(String value, boolean isYb, boolean isOnTop) {
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.pop_livequery, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_value);
        textView.setText(value);
        view.setBackgroundResource(R.drawable.icon_airquality_sk);
        if (isOnTop) {
            textView.setPadding(getResources().getDimensionPixelSize(R.dimen.dimen4)
                    , 0
                    , getResources().getDimensionPixelSize(R.dimen.dimen4)
                    , getResources().getDimensionPixelSize(R.dimen.dimen10));
        } else {
            textView.setPadding(getResources().getDimensionPixelSize(R.dimen.dimen4)
                    , getResources().getDimensionPixelSize(R.dimen.dimen10)
                    , getResources().getDimensionPixelSize(R.dimen.dimen4)
                    , 0);
        }
        view.setDrawingCacheEnabled(true);
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public class ClickBitmap {
        Point p = new Point();
        Bitmap bm = null;
        boolean isDrawTop = true;
        boolean isYbBitmap = true;
        int bitmapPosition = 0;
    }
}
