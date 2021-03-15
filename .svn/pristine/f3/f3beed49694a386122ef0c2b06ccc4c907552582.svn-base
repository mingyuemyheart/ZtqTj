package com.pcs.ztqtj.wxapi.wxtools;

import java.io.Serializable;

public class OrderPayInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String result 			= "";
	public String order_detail_id 	= "";
	public String product_name 		= "";
	public String product_detail 	= "";
	public String total_amount 		= "";
	public String pay_type 			= "";
	public WXInfo weixin_pay_info	= new WXInfo();
	public String re_sign			= "";
	
	public OrderPayInfo() {
		result 			= "";
		order_detail_id = "";
		product_name 	= "";
		product_detail 	= "";
		total_amount 	= "";
		pay_type 		= "";
		weixin_pay_info	= new WXInfo();
		re_sign			= "";
	}
	
	public OrderPayInfo(OrderPayInfo info) {
		this.result				= info.result;
		this.order_detail_id 	= info.order_detail_id;
		this.product_name 		= info.product_name;
		this.product_detail 	= info.product_detail;
		this.total_amount		= info.total_amount;
		this.pay_type 			= info.pay_type;
		this.weixin_pay_info	= info.weixin_pay_info;
		this.re_sign			= info.re_sign;
	}
                                    
}
