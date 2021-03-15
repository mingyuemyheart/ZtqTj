package com.pcs.ztqtj.view.activity.product.dataquery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.inter.ItemClickListener;
import com.pcs.ztqtj.control.tool.LoginInformation;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.ztqtj.view.fragment.dataquery.FragmentDataCompare;
import com.pcs.ztqtj.view.fragment.dataquery.FragmentDisasterType;
import com.pcs.ztqtj.view.fragment.dataquery.FragmentWeatherElementsDayQuery;
import com.pcs.ztqtj.view.fragment.dataquery.FragmentWeatherValueQuery;
import com.pcs.lib.lib_pcs_v3.control.file.PcsMD5;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackAppaisalDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackAppaisalUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryServiceDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryServiceUp;

import java.util.List;

import static com.pcs.ztqtj.R.id.rb_temp;

/**
 * 资料查询
 */
public class ActivityDataQuery extends FragmentActivityZtqBase {

    private RadioGroup rgCategory, rgColumn;
    private String category = "2", column = "";
    private Fragment currentFragment;
    private View layoutMask;
    private MyReceiver receiver = new MyReceiver();
    public boolean isBought = false;
    private String strPwd = "";
    private DialogTwoButton dialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyConfigure.REQUEST_DATA_QUERY_LOGIN:
                    //checkPermissions();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_query);
        setTitleText(R.string.data_query);
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    private void initView() {
        rgCategory = (RadioGroup) findViewById(R.id.rg_category);
        rgColumn = (RadioGroup) findViewById(R.id.rg_column);
        layoutMask = findViewById(R.id.layout_mask);
        //setRightTextButton(R.drawable.btn_personal_warning, "我的服务", onClickListener);
        //checkPermissions();
    }

    private void initEvent() {
        rgCategory.setOnCheckedChangeListener(categoryListener);
        rgColumn.setOnCheckedChangeListener(columnListener);
        layoutMask.setOnClickListener(onClickListener);
//        int[] btnList = {R.id.rb_rain, R.id.rb_wind, R.id.rb_sunshine};
//        for (int aBtnList : btnList) {
//            RadioButton btn = (RadioButton) findViewById(aBtnList);
//            btn.setOnTouchListener(ignoreButtonTouchListener);
//        }
    }

    private void initData() {
        receiver = new MyReceiver();
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        //rgCategory.check(R.id.rb_day);
        rgColumn.check(R.id.rb_disaster);
        textButton.setVisibility(View.GONE);
    }

    private void checkPermissions() {
        if (ZtqCityDB.getInstance().isLoginService()) {
            textButton.setVisibility(View.GONE);
            setBoughtState(true);
        }
//        else if (LoginInformation.getInstance().hasLogin()) {
//            textButton.setVisibility(View.VISIBLE);
//            gotoService();
//            requestUserOrder();
//        }
    }

    private void checkLogin(boolean flag) {
//        if(LoginInformation.getInstance().hasLogin()) {
//            gotoService();
//            if(flag) {
//                showToast("还未购买");
//            }
//        } else {
//            checkLogin();
//        }
        checkLogin();
    }

    /**
     * 点击蒙板
     */
    private void clickMask() {
        checkLogin();
    }

    /**
     * 点击我的服务
     */
    private void clickService() {
//        if (LoginInformation.getInstance().hasLogin()) {
//            gotoService();
//        } else {
//            checkLogin();
//        }
        if(!ZtqCityDB.getInstance().isLoginService()) {
            checkLogin();
        }
    }

    private void gotoService() {
        Intent intent = new Intent(this, ActivityDataQueryService.class);
        startActivity(intent);
    }

    public void checkLogin() {
//        Intent intent = new Intent(this, ActivityDataQueryLogin.class);
//        startActivityForResult(intent, MyConfigure.REQUEST_DATA_QUERY_LOGIN);
//        Intent intent = new Intent(this, AcitvityServeLogin.class);
//        startActivityForResult(intent, MyConfigure.REQUEST_DATA_QUERY_LOGIN);
        PackAppaisalDown down = (PackAppaisalDown) PcsDataManager.getInstance().getNetPack(PackAppaisalUp.NAME);
        if(down != null && down.result.equals("1")) {
            setBoughtState(true);
        } else {
            showAppraisalDailog();
        }
    }

    private void showAppraisalDailog() {

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_appraisel, null);
        final TextView tv = (TextView) view.findViewById(R.id.tv_pwd);
        dialog = new DialogTwoButton(this, view, "取消", "提交", new DialogFactory
                .DialogListener() {
            @Override
            public void click(String str) {
                if (str.equals("提交")) {
                    if (!isOpenNet()) {
                        showToast("网络连接失败，请检查网络连接");
                    } else {
                        strPwd = tv.getText().toString().trim();
                        if (TextUtils.isEmpty(strPwd)) {
                            showToast("请输入口令");
                        } else {
//                        dialog.dismiss();
                            showProgressDialog();
                            commidPwd();
                        }
                    }
                } else {
                    dialog.dismiss();
                }
            }
        });
        dialog.setTitle("口令输入框");
        dialog.show();
    }

    /**
     * 种类rg监听
     */
    private RadioGroup.OnCheckedChangeListener categoryListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            RadioButton btn = (RadioButton) findViewById(R.id.rb_disaster);
            switch (checkedId) {
                case R.id.rb_day:
                    //category = "气象要素日数查询";
                    category = "1";
                    btn.setText("同期比较");
                    break;
                case R.id.rb_value:
                    //category = "气候值分析与查询";
                    category = "2";
                    btn.setText("气候事件");
                    break;
            }
            rgColumn.clearCheck();
            rgColumn.check(R.id.rb_disaster);
        }
    };

    /**
     * 栏目rg监听
     */
    private RadioGroup.OnCheckedChangeListener columnListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            RadioButton button = (RadioButton) findViewById(checkedId);
            if (button != null && !button.isChecked()) {
                return;
            }
            switch (checkedId) {
                case rb_temp:
                    //column = "气温";
                    column = "1";
                    break;
                case R.id.rb_rain:
                    //column = "降水";
                    column = "2";
                    break;
                case R.id.rb_wind:
                    //column = "风况";
                    column = "3";
                    break;
                case R.id.rb_sunshine:
                    //column = "日照";
                    column = "4";
                    break;
                case R.id.rb_disaster:
                    //column = "灾害类型";
                    column = "5";
                    break;
            }
            if (checkedId != View.NO_ID) {
                refreshFragment();
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_mask:
                    //checkLogin(true);
                    clickMask();
                    break;
                case R.id.rightbtn:
                    //checkLogin(false);
                    clickService();
                    break;
            }
        }
    };

    private View.OnTouchListener ignoreButtonTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (category.equals("1")) {
                showToast("建设中");
                return true;
            } else {
                return false;
            }
        }
    };

    /**
     * 刷新fragment
     */
    private void refreshFragment() {
        Bundle bundle = new Bundle();
        if (category.equals("1")) {
            if(column.equals("5")) {
                currentFragment = new FragmentDataCompare();
            } else {
                currentFragment = new FragmentWeatherElementsDayQuery();
            }
        } else {
            if(column.equals("5")) {
                currentFragment = new FragmentDisasterType();
            } else {
                currentFragment = new FragmentWeatherValueQuery();
            }
        }
        bundle.putString("category", category);
        bundle.putString("column", column);
        currentFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, currentFragment).commit();
    }

    /**
     * 创建城市下拉选择列表
     */
    public PopupWindow createPopupWindow(final TextView dropDownView,
                                         final List<String> dataeaum,
                                         final ItemClickListener listener) {
        AdapterData dataAdapter = new AdapterData(this, dataeaum);
        dataAdapter.setTextViewSize(14);
        View popcontent = LayoutInflater.from(this).inflate(
                R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(this);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth(dropDownView.getWidth());
        // 调整下拉框长度
        View mView = dataAdapter.getView(0, null, lv);
        mView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int height = mView.getMeasuredHeight();
        height += lv.getDividerHeight();
        height *= 6; // item的高度*个数
        if (dataeaum.size() < 6) {
            pop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight(height);
        }
        pop.setFocusable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                pop.dismiss();
                dropDownView.setText(dataeaum.get(position));
                if (listener != null) {
                    listener.itemClick(position, dataeaum.get(position));
                }
            }
        });
        return pop;
    }

    /**
     * 设置购买状态
     *
     * @param b
     */
    private void setBoughtState(boolean b) {
        isBought = b;
        if (b) {
            layoutMask.setVisibility(View.GONE);
        } else {
            layoutMask.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 请求用户订单信息
     */
    private void requestUserOrder() {
        PackDataQueryServiceUp up = new PackDataQueryServiceUp();
        up.user_id = LoginInformation.getInstance().getUserId();
        PcsDataDownload.addDownload(up);
    }

    private void commidPwd() {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        String str = strPwd.trim();
        PackAppaisalUp up = new PackAppaisalUp();
        up.pwd = PcsMD5.Md5(str);
        PcsDataDownload.addDownload(up);

    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (PackDataQueryServiceUp.NAME.equals(nameStr)) {
                PackDataQueryServiceDown down = (PackDataQueryServiceDown) PcsDataManager.getInstance().getNetPack
                        (nameStr);
                if (down == null) {
                    return;
                }
                setBoughtState(!TextUtils.isEmpty(down.order_id));
            } else if (PackAppaisalUp.NAME.equals(nameStr)) {
                dismissProgressDialog();
                PackAppaisalDown down = (PackAppaisalDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (down == null) {
                    showToast("提交失败，请稍后再试");
                } else if (down.result.equals("1")) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    setBoughtState(true);
                    showToast("提交成功");
                } else {
                    showToast("口令错误");
                }
            }
        }
    }
}