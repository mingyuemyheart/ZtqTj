package com.pcs.ztqtj.control.tool.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.pcs.ztqtj.control.tool.LoginInformation;
import com.pcs.ztqtj.wxapi.wxtools.DesUtil;
import com.pcs.ztqtj.wxapi.wxtools.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tyaathome on 2017/11/3.
 */

public class AlipayTool {

    private Context context;

    public AlipayTool(Context context) {
        this.context = context;
    }

    private static final int SDK_PAY_FLAG = 1;

    private String appid = "";
    private String orderid = "";
    private String logUrl = "";

    /**
     * 支付宝支付
     * @param appid
     * @param orderid 订单id
     * @param subject
     * @param body
     * @param amount 费用
     */
    public void pay(String appid, String orderid, String subject, String body, String amount, String time, String key, String logUrl, String notifyUrl) {

        String privateKey = null;
        try {
            privateKey = DesUtil.decrypt(key, "pcs**key");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(TextUtils.isEmpty(privateKey)) {
            return;
        }
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(appid, false, orderid, subject, body, "0.01", time, notifyUrl);
        this.appid = appid;
        this.orderid = orderid;
        this.logUrl = logUrl;
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, false);
        final String orderInfo = orderParam + "&" + sign;

        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) context);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Runnable postLogRunnable = new Runnable() {

            @Override
            public void run() {
                postLog(orderInfo);
                Thread payThread = new Thread(payRunnable);
                payThread.start();
            }
        };

        // 异步提交日志信息
        Thread postLogThread = new Thread(postLogRunnable);
        postLogThread.start();
    }

    /**
     * 提交日志信息
     * @param payInfo
     */
    private void postLog(String payInfo) {
        // 提交日志信息
        String myEntity = "";
        myEntity = "<![CDATA[" + payInfo + "]]>";
        List<NameValuePair> info = new ArrayList<NameValuePair>();
        info.add(new BasicNameValuePair("appid",
                appid));
        info.add(new BasicNameValuePair("usrid", LoginInformation
                .getInstance().getUserId()));
        info.add(new BasicNameValuePair("out_trade_no",
                orderid));
        info.add(new BasicNameValuePair("q_xml", myEntity));
        String logXML = toLog(System.currentTimeMillis(), 10001, info);
        byte[] logBuf = Util.httpPost(logUrl,
                logXML, true);
        String logContent = new String(logBuf);
        Log.e("orion", logContent);
    }

    /**
     * 生成日志请求文件
     *
     * @param time
     *            请求时间
     * @param funcid
     *            功能id
     * @param params
     *            请求内容
     * @return
     */
    private String toLog(long time, int funcid, List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<operation_in>");
        sb.append("<request_time>" + String.valueOf(time) + "</request_time>");
        sb.append("<sysfunc_id>" + String.valueOf(funcid) + "</sysfunc_id>");
        sb.append("<content>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");
            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</content>");
        sb.append("</operation_in>");
        return sb.toString();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

}
