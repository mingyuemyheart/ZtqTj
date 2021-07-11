package com.pcs.ztqtj.view.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.inter.ItemClick;
import com.pcs.ztqtj.control.tool.youmeng.ShareUtil;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;

import java.util.List;

/**
 * 知天气基础Activity
 *
 * @author JiangZy
 */
public class FragmentActivityZtqBase extends FragmentActivityBase {
    // 枚举：Activity退出方向
    protected enum BackDirection {
        BACK_LEFT, BACK_RIGHT, BACK_UP, BACK_DOWN
    }


    // Activity退出方向
    private BackDirection mBackDirection = BackDirection.BACK_RIGHT;

    /**
     * 回退目标
     *
     * @author JiangZY
     */
    public enum BackTarget {
        /* 正常 */NORMAL, /* 首页 */MAIN, /* 气象产品 */PRODUCT, /* 气象服务 */SERVICE, /* 气象生活 */LIVE, /* 预警中心 */WARN
    }

    private int mIntBackTarget = BackTarget.NORMAL.ordinal();

    public RelativeLayout headLayout;
    protected Button textButton;
    private ImageButton btnBack;
    private LinearLayout lay_back;
    // 等待对话框
    private ProgressDialog mProgress;

    public boolean hasMeasured = false;// 只计算一次
    public int headHeight = 0; // 顶部布局的数值，需计算
    public int mScreenHeight = 0;

    public LinearLayout all_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(R.layout.activity_base);
        // 回退目标
        mIntBackTarget = getIntent().getIntExtra("BackTarget",
                BackTarget.NORMAL.ordinal());

        try {
            // 没有物理键的手机显示menu键
            Object obj = null;
            getWindow().addFlags(
                    WindowManager.LayoutParams.class.getField(
                            "FLAG_NEEDS_MENU_KEY").getInt(obj));
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }

        headLayout = (RelativeLayout) findViewById(R.id.head_layout);
        all_view = (LinearLayout) findViewById(R.id.all_view);
        textButton = (Button) findViewById(R.id.rightbtn);
        // 回退按钮
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBack();
            }
        });
        lay_back=findViewById(R.id.lay_back);
        lay_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBack();
            }
        });
        // 打开动画
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    /**
     * 点击回退
     */
    protected void clickBack() {
        if (mIntBackTarget == BackTarget.MAIN.ordinal()
                || mIntBackTarget == BackTarget.SERVICE.ordinal()
                || mIntBackTarget == BackTarget.PRODUCT.ordinal()
                || mIntBackTarget == BackTarget.WARN.ordinal()
                || mIntBackTarget == BackTarget.LIVE.ordinal()) {
            // 首页/气象服务/气象产品/气象生活/预警中心
            Intent it = getIntent();
            it.setClass(this, ActivityMain.class);
            it.putExtra("BackTarget", mIntBackTarget);
            startActivity(it);
        }
        this.finish();
    }

    @Override
    public void finish() {
        super.finish();
        // 退出动画
        backAnimation();
    }

    /**
     * 退出动画
     */
    private void backAnimation() {
        if (mBackDirection == BackDirection.BACK_LEFT) {
            overridePendingTransition(R.anim.slide_right_in,
                    R.anim.slide_left_out);
        } else if (mBackDirection == BackDirection.BACK_RIGHT) {
            overridePendingTransition(R.anim.slide_left_in,
                    R.anim.slide_right_out);
        } else if (mBackDirection == BackDirection.BACK_UP) {
            overridePendingTransition(R.anim.slide_down_in, R.anim.slide_up_out);
        } else if (mBackDirection == BackDirection.BACK_DOWN) {
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        }
    }

    /**
     * 设置退出方向
     *
     * @param direction
     */
    protected void setBackDirection(BackDirection direction) {
        mBackDirection = direction;
    }

    @Override
    public void onBackPressed() {
        clickBack();
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    public void setBackListener(OnClickListener listener) {
        btnBack.setOnClickListener(listener);
    }

    public void setContentView(int resID) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        View v = LayoutInflater.from(getApplicationContext()).inflate(resID,
                null);
        ViewGroup layout = (ViewGroup) findViewById(R.id.layout_content);
        layout.addView(v, lp);
    }

    /**
     * 设置回退图片按钮
     *
     * @param imageID
     */
    protected void setBtnBack(int imageID) {
        ImageButton btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnBack.setImageResource(imageID);
    }

    /**
     * 设置标题文本
     *
     * @param title
     */
    protected void setTitleText(String title) {
        TextView text = (TextView) findViewById(R.id.text_title);
        text.setText(title);
    }

    protected TextView getTitleTextView() {
        TextView text = (TextView) findViewById(R.id.text_title);
        return text;
    }

    /**
     * 设置标题文本
     *
     * @param rId
     */
    protected void setTitleText(int rId) {
        TextView text = (TextView) findViewById(R.id.text_title);
        text.setText(rId);
    }

    /**
     * 获取当前标题
     * @return
     */
    protected String getTitleText() {
        TextView text = (TextView) findViewById(R.id.text_title);
        return text.getText().toString();
    }

    protected ImageButton btnRight;

    /**
     * 设置右边图片按钮
     *
     * @param ImageID
     * @param listener
     */
    protected void setBtnRight(int ImageID, OnClickListener listener) {
        btnRight = (ImageButton) findViewById(R.id.btn_right);
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setBackgroundResource(ImageID);
        btnRight.setOnClickListener(listener);
    }

    /**
     * 设置右边按钮点击回调
     *
     * @param listener
     */
    protected void setBtnRightListener(OnClickListener listener) {
        btnRight = (ImageButton) findViewById(R.id.btn_right);
        btnRight.setOnClickListener(listener);
    }

    /**
     * 设置右边第二个图片按钮
     *
     * @param ImageID
     * @param listener
     */
    protected void setBtnRight2(int ImageID, OnClickListener listener) {
        ImageButton btnRight2 = (ImageButton) findViewById(R.id.btn_right2);
        btnRight2.setVisibility(View.VISIBLE);
        btnRight2.setBackgroundResource(ImageID);
        btnRight2.setOnClickListener(listener);
    }

    /**
     * 右边按钮处理
     */
    public void setRightTextButton(int image, String btnName,
                                   OnClickListener listener) {
        textButton.setVisibility(View.VISIBLE);
        textButton.setBackgroundResource(image);
        textButton.setText(btnName);
        textButton.setOnClickListener(listener);
    }

    ;

    /**
     * 右边按钮设置文字和图片
     */
    public void setRightButtonText(int image, String btnName) {
        textButton.setBackgroundResource(image);
        textButton.setText(btnName);
    }

    public void setRightButtonText(int image, String btnName, int width) {
        textButton.setBackgroundResource(image);
        textButton.setText(btnName);
        ViewGroup.LayoutParams params = textButton.getLayoutParams();
        params.width = width;
        textButton.setLayoutParams(params);
    }

    private Toast toast;

    public void showToast(String str) {
        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT);
        } else {
            toast.setText(str);
        }
        toast.show();
    }

    /**
     * 显示等待对话框
     */
    public void showProgressDialog(String keyWord) {
        if (mProgress == null) {
            mProgress = new ProgressDialog(this);
            mProgress.setCancelable(true);
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.setOnCancelListener(mProgressOnCancel);
        }
        if (mProgress.isShowing()) {
            mProgress.setMessage(keyWord);
        } else {
            mProgress.show();
            mProgress.setMessage(keyWord);
        }
    }

    /**
     * 取消等待对话框
     */
    public void dismissProgressDialog() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    public void showProgressDialog() {
        showProgressDialog(getResources().getString(R.string.please_wait));
    }

    /**
     * 进度框OnCancel
     */
    private OnCancelListener mProgressOnCancel = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            FragmentActivityZtqBase.this.finish();
        }
    };

    private PopupWindow popupWindow;

    /**
     * 显示下来选择列表
     */
    public void showPopupWindow(Context context, final TextView tvView, final List<String> listData, final ItemClick listener) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_list_layout, null);
        // 设置按钮的点击事件
        ListView chose_listview = (ListView) contentView.findViewById(R.id.mylistviw);
        AdapterData adapter = new AdapterData(context, listData);
        chose_listview.setAdapter(adapter);
        chose_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dimissPop();
                tvView.setText(listData.get(position));
                if (listener != null) {
                    listener.itemClick(position, listData.get(position));
                }
            }
        });
        popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar
                .LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.alpha100));
        int width = tvView.getWidth();
        // 设置好参数之后再show
        int off = 0;
//        int off = tvView.getWidth() / 4;
        popupWindow.setWidth(width);
        popupWindow.showAsDropDown(tvView, -off, 0);
    }

    /**
     * 显示指定行数的下拉框
     *
     * @param context
     * @param tvView
     * @param listData
     * @param showLineCount 指定行数
     * @param listener
     */
    public void showPopupWindow(Context context, final TextView tvView, final List<String> listData, int showLineCount, final ItemClick listener) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_list_layout, null);
        // 设置按钮的点击事件
        ListView chose_listview = (ListView) contentView.findViewById(R.id.mylistviw);
        AdapterData adapter = new AdapterData(context, listData);
        chose_listview.setAdapter(adapter);
        chose_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dimissPop();
                tvView.setText(listData.get(position));
                if (listener != null) {
                    listener.itemClick(position, listData.get(position));
                }
            }
        });
        int height = 0;
        if (adapter.getCount() > 0) {
            View childView = adapter.getView(0, null, chose_listview);
            childView.measure(0, View.MeasureSpec.UNSPECIFIED);
            height = childView.getMeasuredHeight();
        }
        if (height != 0) {
            popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT, height * 5, true);
        } else {
            popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar
                    .LayoutParams.WRAP_CONTENT, true);
        }
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.alpha100));
        int width = tvView.getWidth();
        // 设置好参数之后再show
        int off = 0;
//        int off = tvView.getWidth() / 4;
        popupWindow.setWidth(width);
        popupWindow.showAsDropDown(tvView, -off, 0);
    }

    /**
     * 关闭popup
     */
    private void dimissPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();

        }
    }

    /**
     * 分享
     */
    public void share(final Activity activity) {
        ShareUtil.share(activity);
    }

    /**
     * 设置背景颜色
     *
     * @param color
     */
    protected void setBackground(int color) {
        ViewGroup layout = (ViewGroup) findViewById(R.id.layout_content);
        layout.setBackgroundColor(color);
    }

    /* 判断是否是处于wifi状态下*/
    protected boolean isWiFiNewWord() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else {
            return false;
        }
    }


    /* 判断是否是有网络*/
    public boolean isOpenNet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            return true;
        } else {
            return false;
        }
    }

    private  DialogTwoButton myDialog;

    /**
     * 检查通知栏权限设置
     */
    public  void checkNotificationPermission(final Context context) {
        if(!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            if(myDialog == null) {
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
                TextView tv = (TextView) view.findViewById(R.id.dialogmessage);
                tv.setText("您的通知接收功能尚未开通，请到本机\"设置->通知管理->天津气象\"的管理界面开启相应服务。");
                tv.setGravity(Gravity.CENTER);
                myDialog = new DialogTwoButton(context, view, "知道了", "去设置", new DialogFactory.DialogListener() {
                    @Override
                    public void click(String str) {
                        switch (str) {
                            case "知道了":
                                myDialog.dismiss();
                                break;
                            case "去设置":
                                gotoNotificaiontSettings(context);
                                break;
                        }
                        myDialog.dismiss();
                    }
                });
                myDialog.show();
            }else{
                myDialog.show();
            }
        }

    }

    /**
     * 跳转通知栏设置页面
     */
    private  void gotoNotificaiontSettings(Context context) {
        Intent intent = new Intent();
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }

}
