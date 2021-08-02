package com.tianyi.shawn.tjdecision.wxapi.wxtools;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.pcs.ztqtj.control.tool.LoginInformation;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WXPay {
	private static final String TAG = "MicroMsg.SDKSample.PayActivity";
	private IWXAPI msgApi;
	private Context mContext = null;
	private OrderPayInfo orderInfo = null;
	private int nTotal = 0;

	/**
	 * 
	 * @param context 上下文
	 * @param orderInfo 订单信息
	 * @param total 支付金额(以分为单位，例如一元为100)
	 * FamilyOrderPayInfo 必填参数：WXInfo,product_name,order_detail_id
	 */
	public WXPay(Context context, OrderPayInfo orderInfo, int total) {
		this.mContext = context;
		this.orderInfo = orderInfo;
		//this.nTotal = total*100;
        this.nTotal = 1;
	}
	
	/**
	 * 拉起微信支付
	 */
	public void start() {

		// 拉起微信支付
        msgApi = WXAPIFactory.createWXAPI(mContext, orderInfo.weixin_pay_info.appid);
		//msgApi.registerApp(orderInfo.weixin_pay_info.appid);
		// 是否安装微信
		if (msgApi.isWXAppInstalled()) {
            WXPayTask getPrepayId = new WXPayTask();
            getPrepayId.execute();
		} else {
			Toast.makeText(mContext,
					"没有安装微信",
					Toast.LENGTH_LONG).show();
		}
	}
	
	private class WXPayTask extends AsyncTask<Void, Void, Map<String, String>> {

		@Override
		protected void onPreExecute() {
			// dialog = ProgressDialog.show(ActivityFamilyServicePay.this, "提示",
			// "提示");
			// showProgressDialog();
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {

//			Intent intent = new Intent(mContext,
//			WXPayEntryActivity.class);
//			intent.putExtra("AppID", orderInfo.weixin_pay_info.appid);
//			Activity activity = (Activity) mContext;
//			activity.startActivityForResult(intent,
//					requestCode);
//			if(l != null) {
//				l.onFinish();
//			}
			// 拉起微信支付
			PayReq req = genPayReq(result);
			boolean b = sendPayReq(req);
			System.out.println(b);

		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String, String> doInBackground(Void... params) {

			String url = String
					.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			// 请求报文
			String result = genProductArgs();
			String entity = "";
			try {
				entity = new String(result.toString().getBytes(), "ISO8859-1");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			Log.e("orion", entity);

			byte[] buf = Util.httpPost(url, entity, false);

			String content = new String(buf);
			Log.e("orion", content);

			// 提交日志信息
			String myEntity = "";
//			try {
//				myEntity = new String(result.toString().getBytes(), "utf-8");
//				myEntity = "<![CDATA[" + myEntity + "]]>";
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
			myEntity = "<![CDATA[" + result + "]]>";
			List<NameValuePair> info = new ArrayList<NameValuePair>();
			info.add(new BasicNameValuePair("appid",
					orderInfo.weixin_pay_info.appid));
			info.add(new BasicNameValuePair("usrid", LoginInformation
					.getInstance().getUserId()));
			info.add(new BasicNameValuePair("out_trade_no",
					orderInfo.order_detail_id));
			info.add(new BasicNameValuePair("q_xml", myEntity));
			String logXML = toLog(System.currentTimeMillis(), 10001, info);
			byte[] logBuf = Util.httpPost(orderInfo.weixin_pay_info.log_url,
					logXML, true);
			String logContent = new String(logBuf);
			Log.e("orion", logContent);

			Map<String, String> xml = decodeXml(content);
			return xml;
		}
	}
	
	// 生成交易请求
	private PayReq genPayReq(Map<String, String> resultunifiedorder) {
		PayReq payReq = new PayReq();
		payReq.appId = orderInfo.weixin_pay_info.appid;
		payReq.partnerId = orderInfo.weixin_pay_info.mch_id;
		payReq.prepayId = resultunifiedorder.get("prepay_id");
		payReq.packageValue = "Sign=WXPay";
		payReq.nonceStr = genNonceStr();
		payReq.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", payReq.appId));
		signParams.add(new BasicNameValuePair("noncestr", payReq.nonceStr));
		signParams.add(new BasicNameValuePair("package", payReq.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", payReq.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", payReq.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", payReq.timeStamp));

		payReq.sign = genSign(signParams);

		Log.e("orion", signParams.toString());
		return payReq;

	}
	
	private boolean sendPayReq(PayReq payReq) {
		//msgApi.registerApp(orderInfo.weixin_pay_info.appid);
		return msgApi.sendReq(payReq);
	}
	
	// 生成预交易单号
	private String genProductArgs() {

		try {
			String nonceStr = genNonceStr();
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			// packageParams.add(new BasicNameValuePair("appid",
			// Constants.APP_ID));
			// packageParams.add(new BasicNameValuePair("body", "weixin"));
			// packageParams.add(new BasicNameValuePair("mch_id",
			// Constants.MCH_ID));
			// packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			// packageParams.add(new BasicNameValuePair("notify_url",
			// "http://121.40.35.3/test"));
			// packageParams.add(new
			// BasicNameValuePair("out_trade_no",genOutTradNo()));
			// packageParams.add(new
			// BasicNameValuePair("spbill_create_ip","127.0.0.1"));
			// packageParams.add(new BasicNameValuePair("total_fee", "1"));
			// packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			packageParams.add(new BasicNameValuePair("appid",
					orderInfo.weixin_pay_info.appid));
			packageParams.add(new BasicNameValuePair("body",
					orderInfo.product_name));
			// packageParams.add(new BasicNameValuePair("body", "weixin"));
			packageParams.add(new BasicNameValuePair("mch_id",
					orderInfo.weixin_pay_info.mch_id));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url",
					orderInfo.weixin_pay_info.notify_url));
			packageParams.add(new BasicNameValuePair("out_trade_no",
					orderInfo.order_detail_id));
			// packageParams.add(new
			// BasicNameValuePair("out_trade_no",genOutTradNo()));
			packageParams.add(new BasicNameValuePair("spbill_create_ip",
					"127.0.0.1"));
			packageParams.add(new BasicNameValuePair("total_fee", String
					.valueOf(nTotal)));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			// packageParams.add(new BasicNameValuePair("detail",
			// orderInfo.product_detail));

			String sign = genSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			return toXml(packageParams);

		} catch (Exception e) {
//			Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}

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
	
	public Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if ("xml".equals(nodeName) == false) {
						// 实例化student对象
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion", e.toString());
		}
		return null;

	}
	
	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}
	
	/**
	 * 生成签名
	 */

	private String genSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		String key = "";
		try {
			key = DesUtil.decrypt(orderInfo.weixin_pay_info.key, "pcs**key");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		sb.append(key);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();
		Log.e("orion", packageSign);
		return packageSign;
	}
	
	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		Log.e("orion", sb.toString());
		return sb.toString();
	}
}
