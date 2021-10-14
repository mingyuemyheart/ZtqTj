package com.pcs.ztqtj.view.activity.product.dataquery;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.OrderMealInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryOrderDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryOrderUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryPayDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryPayOrderDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryPayOrderUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryPayUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryServiceDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryServiceUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PayOrderInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.inter.ItemClickListener;
import com.pcs.ztqtj.control.tool.LoginInformation;
import com.pcs.ztqtj.control.tool.alipay.AlipayTool;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.wxapi.wxtools.MD5;
import com.pcs.ztqtj.wxapi.wxtools.OrderPayInfo;
import com.pcs.ztqtj.wxapi.wxtools.WXInfo;
import com.pcs.ztqtj.wxapi.wxtools.WXPay;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2017/11/2.
 */

public class ActivityDataQueryService extends FragmentActivityZtqBase {

    private View layoutNoOrder, layoutOrder, layoutPay;
    private MyReceiver receiver = new MyReceiver();
    private List<OrderMealInfo> mealInfoList = new ArrayList<>();
    private TextView tvMeal, tvTariff;
    private OrderMealInfo currentMealInfo = new OrderMealInfo();
    private Button btnPay;
    private String userId = "";
    private PayType currentPayType = PayType.NO_ORDER;
    private String orderId = "";
    private String payType = "2"; // 2、微信支付 4、支付宝
    private RadioGroup rgPay;
    private TextView tvStartTime, tvEndTime;
    private CheckBox btnRenew;
    private TextView tvWelcome;
    private ImageView ivAvator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_query_service);
        setTitleText(R.string.data_query);
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestUserOrder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    private void initView() {
        layoutNoOrder = findViewById(R.id.layout_no_order);
        layoutOrder = findViewById(R.id.layout_order);
        layoutPay = findViewById(R.id.layout_pay);
        tvMeal = (TextView) findViewById(R.id.btn_set_meal);
        tvTariff = (TextView) findViewById(R.id.tv_tariff);
        btnPay = (Button) findViewById(R.id.btn_confirm);
        rgPay = (RadioGroup) findViewById(R.id.rg);
        tvStartTime = (TextView) findViewById(R.id.tv_start_time);
        tvEndTime = (TextView) findViewById(R.id.tv_end_time);
        btnRenew = (CheckBox) findViewById(R.id.btn_continue);
        tvWelcome = (TextView) findViewById(R.id.tv_welcome);
        ivAvator = (ImageView) findViewById(R.id.image_user);
    }

    private void initEvent() {
        tvMeal.setOnClickListener(onClickListener);
        btnPay.setOnClickListener(onClickListener);
        rgPay.setOnCheckedChangeListener(onCheckedChangeListener);
        btnRenew.setOnCheckedChangeListener(checkboxChangeListener);
    }

    private void initData() {
        userId = LoginInformation.getInstance().getUserId();
        tvWelcome.setText("亲爱的" + LoginInformation.getInstance().getUsername() + ",欢迎您！");
        Picasso.get().load(LoginInformation.getInstance().getUserIconUrl()).into(ivAvator);
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        //requestUserOrder();
        requestPayOrder();
    }

    private void refreshLayout(PayType type, PackDataQueryServiceDown down) {
        currentPayType = type;
        switch (type) {
            case NO_ORDER:
                layoutNoOrder.setVisibility(View.VISIBLE);
                layoutOrder.setVisibility(View.GONE);
                layoutPay.setVisibility(View.VISIBLE);
                break;
            case ORDER:
                layoutNoOrder.setVisibility(View.GONE);
                layoutOrder.setVisibility(View.VISIBLE);
                layoutPay.setVisibility(View.GONE);
                if(down != null) {
                    tvStartTime.setText("订购时间：" + down.s_time);
                    tvEndTime.setText("到期时间：" + down.e_time);
                }
                btnRenew.setText(getString(R.string.renew));
                break;
            case RENEW:
                layoutNoOrder.setVisibility(View.GONE);
                layoutOrder.setVisibility(View.VISIBLE);
                layoutPay.setVisibility(View.VISIBLE);
                btnRenew.setText(getString(R.string.cancel_renew));
                break;
        }
    }

    /** 创建城市下拉选择列表 */
    public PopupWindow createPopupWindow(final TextView dropDownView,
                                         final List<String> dataeaum,
                                         final ItemClickListener listener) {
        AdapterData dataAdapter = new AdapterData(this, dataeaum);
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
                listener.itemClick(position, dataeaum.get(position));
            }
        });
        return pop;
    }

    /**
     * 请求用户订单信息
     */
    private void requestUserOrder() {
        PackDataQueryServiceUp up = new PackDataQueryServiceUp();
        up.user_id = userId;
        PcsDataDownload.addDownload(up);
    }

    /**
     * 请求支付套餐
     */
    private void requestPayOrder() {
        PackDataQueryPayOrderUp up = new PackDataQueryPayOrderUp();
        up.product_type = "5";
        PcsDataDownload.addDownload(up);
    }

    /**
     * 请求支付订单
     */
    private void requestOrder() {
        showProgressDialog();
        PackDataQueryOrderUp up = new PackDataQueryOrderUp();
        up.product_id = currentMealInfo.id;
        up.user_id = userId;
        if(currentPayType == PayType.NO_ORDER) {
            up.act_type = "0";
        } else {
            up.act_type = "1";
            up.order_id = orderId;
        }
        PcsDataDownload.addDownload(up);
    }

    /**
     * 请求支付信息
     */
    private void requestPayInfo(PackDataQueryOrderDown down) {
        PackDataQueryPayUp up = new PackDataQueryPayUp();
        up.order_id = down.order_id;
        up.user_id = down.user_id;
        up.pay_type = payType;
        up.product_id = down.product_id;
        String sign = down.order_id + "-" + down.user_id + "-pcsztq";
        up.sign = MD5.getMessageDigest(sign.getBytes());
        PcsDataDownload.addDownload(up);
    }

    public enum PayType {
        NO_ORDER, // 没有订单
        ORDER, // 有订单
        RENEW // 续费
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_set_meal:
                    List<String> strList = new ArrayList<>();
                    for(OrderMealInfo info : mealInfoList) {
                        strList.add(info.m_name);
                    }
                    createPopupWindow((TextView) v, strList, new ItemClickListener() {
                        @Override
                        public void itemClick(int position, Object... objects) {
                            if(mealInfoList.size() > position) {
                                currentMealInfo = mealInfoList.get(position);
                                tvTariff.setText(currentMealInfo.total_amount + "元");
                            }
                        }
                    }).showAsDropDown(v);
                    break;
                case R.id.btn_confirm:
                    requestOrder();
                    break;
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_weixin :
                    payType = "2";
                    break;
                case R.id.rb_alipay :
                    payType = "4";
                    break;
            }
        }
    };
    
    private CompoundButton.OnCheckedChangeListener checkboxChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.btn_continue:
                    if(isChecked) {
                        refreshLayout(PayType.RENEW, null);
                    } else {
                        refreshLayout(PayType.ORDER, null);
                    }
                    break;
            }
        }
    };

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (PackDataQueryServiceUp.NAME.equals(nameStr)) {
                PackDataQueryServiceDown down = (PackDataQueryServiceDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                if(TextUtils.isEmpty(down.order_id)) {
                    refreshLayout(PayType.NO_ORDER, down);
                } else {
                    if(btnRenew.isChecked()) {
                        btnRenew.setChecked(false);
                    } else {
                        refreshLayout(PayType.ORDER, down);
                    }
                    orderId = down.order_id;
                }
            } else if (PackDataQueryPayOrderUp.NAME.equals(nameStr)) {
                PackDataQueryPayOrderDown down = (PackDataQueryPayOrderDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                for(PayOrderInfo info : down.product_list) {
                    if(info.name.equals("资料查询")) {
                        mealInfoList = info.set_meal;
                        break;
                    }
                }
                if(mealInfoList != null && mealInfoList.size() > 0) {
                    OrderMealInfo info = mealInfoList.get(0);
                    tvMeal.setText(info.m_name);
                    tvTariff.setText(info.total_amount + "元");
                    currentMealInfo = info;
                }
            } else if (PackDataQueryOrderUp.NAME.equals(nameStr)) {
                PackDataQueryOrderDown down = (PackDataQueryOrderDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    dismissProgressDialog();
                    return;
                }
                if(down.result.equals("1")) {
                    requestPayInfo(down);
                } else {
                    dismissProgressDialog();
                    showToast("支付失败");
                }
            } else if (PackDataQueryPayUp.NAME.equals(nameStr)) {
                PackDataQueryPayDown down = (PackDataQueryPayDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    dismissProgressDialog();
                    return;
                }
                if(down.result.equals("1")) {
                    String userID = LoginInformation.getInstance().getUserId();
                    String format = "%s-%s-%s-%s-%s-%s-%s-%s-%s";
                    String value = "";
                    if(down.pay_type.equals("2")) { // 微信
                        value = String.format(format, down.total_amount,
                                down.pay_type, down.weixin_pay_info.mch_id,
                                down.weixin_pay_info.notify_url,
                                down.weixin_pay_info.key,
                                down.weixin_pay_info.log_url,
                                down.weixin_pay_info.appid,
                                down.order_detail_id, userID);
                    } else if(down.pay_type.equals("4")) { // 支付宝
                        value = String.format(format, down.total_amount,
                                down.pay_type, down.ali_pay_info.mch_id,
                                down.ali_pay_info.notify_url,
                                down.ali_pay_info.key,
                                down.ali_pay_info.log_url,
                                down.ali_pay_info.appid,
                                down.order_detail_id, userID);
                    }
                    String reSign = MD5.getMessageDigest(value.getBytes());
                    if (!reSign.endsWith(down.re_sign)) {
                        showToast("解析错误！");
                        return ;
                    }

                    if(down.pay_type.equals("2")) { // 微信支付
                        // 微信支付入口
                        WXInfo wxInfo = new WXInfo();
                        wxInfo.appid = down.weixin_pay_info.appid;
                        wxInfo.mch_id = down.weixin_pay_info.mch_id;
                        wxInfo.notify_url = down.weixin_pay_info.notify_url;
                        wxInfo.key = down.weixin_pay_info.key;
                        wxInfo.log_url = down.weixin_pay_info.log_url;

                        OrderPayInfo payInfo = new OrderPayInfo();
                        payInfo.order_detail_id = down.order_detail_id;
                        payInfo.product_name = down.product_name;
                        payInfo.weixin_pay_info = wxInfo;
                        payInfo.total_amount = String.valueOf(down.total_amount);

                        float amount = Float.parseFloat(down.total_amount);

                        WXPay pay = new WXPay(ActivityDataQueryService.this, payInfo, (int) amount);// 订单实际金额
                        pay.start();
                    } else if(down.pay_type.equals("4")) { // 支付宝
                        AlipayTool alipayTool = new AlipayTool(ActivityDataQueryService.this);
                        alipayTool.pay(down.ali_pay_info.appid,
                                down.order_detail_id,
                                down.product_detail,
                                down.product_name,
                                down.total_amount,
                                down.ali_pay_info.it_b_pay,
                                down.ali_pay_info.key,
                                down.ali_pay_info.log_url,
                                down.ali_pay_info.notify_url
                        );
                    }
                } else {
                    showToast("支付失败");
                }
                dismissProgressDialog();
            }
        }
    }
}
