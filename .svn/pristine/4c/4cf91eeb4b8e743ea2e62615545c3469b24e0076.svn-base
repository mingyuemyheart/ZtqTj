package com.pcs.ztqtj.control.adapter.location_warning;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.listener.OnOrderDeleteListener;
import com.pcs.ztqtj.control.tool.LayoutTool;
import com.pcs.ztqtj.view.activity.product.locationwarning.ActivityWarningManage;
import com.pcs.ztqtj.view.myview.MyDialog;
import com.pcs.ztqtj.view.myview.MyDialog.DialogListener;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.WarningOrderInfo;

/**
 * 订单列表数据适配器
 * @author E.Sun
 * 2015年11月12日
 */
@SuppressLint({ "InflateParams", "SimpleDateFormat" })
public class AdapterWarningOrder extends BaseAdapter {

    private ActivityWarningManage mActivity;
    private List<WarningOrderInfo> mList;
    
    private SimpleDateFormat valueDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
    private SimpleDateFormat textDateFormat = new SimpleDateFormat("yyyy年M月d日");
    
    public AdapterWarningOrder(ActivityWarningManage activity, List<WarningOrderInfo> list) {
    	mActivity = activity;
        mList = list;
    }

    @Override
    public int getCount() {
        if(mList == null) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        if(mList == null || position < 0 || position >= mList.size()) {
            return null;
        }
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        Holder holder;
        if(converView == null) {
            converView = LayoutInflater.from(mActivity).inflate(R.layout.item_warning_order, null);
            holder = new Holder();
            holder.layoutMain = (LinearLayout) converView.findViewById(R.id.layout_main);
            holder.tvName = (TextView) converView.findViewById(R.id.tv_name);
            holder.tvExpireDate = (TextView) converView.findViewById(R.id.tv_expire_date);
            holder.btnPay = (Button) converView.findViewById(R.id.btn_pay);
            holder.btnContinue = (Button) converView.findViewById(R.id.btn_continue);
            holder.btnRevise = (Button) converView.findViewById(R.id.btn_revise);
            holder.btnDelete = (Button) converView.findViewById(R.id.btn_delete);
            holder.layoutContent = (LinearLayout) converView.findViewById(R.id.layout_content);
            holder.tvEffectDate = (TextView) converView.findViewById(R.id.tv_effect_date);
            holder.tvServiceCycle = (TextView) converView.findViewById(R.id.tv_service_cycle);
            holder.lvArea = (ListView) converView.findViewById(R.id.listview_area);
            holder.image = (ImageView) converView.findViewById(R.id.image);
            converView.setTag(holder);
        } else {
            holder = (Holder) converView.getTag();
        }

        // 填充内容
        WarningOrderInfo info = mList.get(position);
        holder.tvName.setText(info.name);
        holder.tvExpireDate.setText(formatDate(info.expiredDate));
        MyOnClickListener listener = new MyOnClickListener(info);
        holder.btnPay.setOnClickListener(listener);
		holder.btnContinue.setOnClickListener(listener);
		holder.btnRevise.setOnClickListener(listener);
		holder.btnDelete.setOnClickListener(listener);
		holder.tvEffectDate.setText(formatDate(info.effectDate));
		holder.tvServiceCycle.setText((TextUtils.isEmpty(info.cycle) ? "" : info.cycle + "个月"));
		holder.lvArea.setAdapter(new AdapterServiceArea(mActivity, info.areaList));
		LayoutTool.changeLayoutParams(holder.lvArea);
		
		// 调整列表项状态
		if(info.isPaid) {
			holder.btnPay.setVisibility(View.GONE);
			if(info.isRenew) {
				holder.btnContinue.setVisibility(View.VISIBLE);
			} else {
				holder.btnContinue.setVisibility(View.GONE);
			}
			holder.btnRevise.setVisibility(View.VISIBLE);
			holder.btnDelete.setVisibility(View.GONE);
			holder.layoutContent.setVisibility(View.VISIBLE);
		} else {
			holder.tvExpireDate.setVisibility(View.GONE);
			holder.btnPay.setVisibility(View.VISIBLE);
			holder.btnContinue.setVisibility(View.GONE);
			holder.btnRevise.setVisibility(View.VISIBLE);
			holder.btnDelete.setVisibility(View.VISIBLE);
			holder.layoutContent.setVisibility(View.GONE);
		}

        // 返回指定position的选项视图
        return converView;
    }
    
    /**
     * 获取已定制预警类型编号列表
     * @return
     */
    public ArrayList<String> getTypeIdList() {
    	ArrayList<String> typeIdList = new ArrayList<String>();
    	if(mList != null) {
    		for(WarningOrderInfo info : mList) {
    			typeIdList.add(info.typeID);
    		}
    	}
    	return typeIdList;
    }
    
    /**
     * 替换数据
     * @param list
     */
    public void setData(List<WarningOrderInfo> list) {
        mList = list;
        notifyDataSetChanged();
        mActivity.resetView();
    }
    
    /**
     * 移除指定数据
     * @param orderInfo
     */
    public void removeData(WarningOrderInfo orderInfo) {
    	mList.remove(orderInfo);
		notifyDataSetChanged();
		mActivity.resetView();
    }
    
    /**
     * 格式化时间
     * @param date
     * @return
     */
    private String formatDate(String date) {
    	String dateString = "";
    	if(TextUtils.isEmpty(date)) {
    		return dateString;
    	}
    	try {
    		dateString = textDateFormat.format(valueDateFormat.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return dateString;
    }
    
    class Holder {
    	LinearLayout layoutMain;
        TextView tvName;
        TextView tvExpireDate;
        Button btnPay;
        Button btnContinue;
        Button btnRevise;
        Button btnDelete;
        LinearLayout layoutContent;
        TextView tvEffectDate;
        TextView tvServiceCycle;
        ListView lvArea;
        ImageView image;
    }

    /**
     * 操作按钮点击事件监听器
     * @author E.Sun
     * 2015年11月14日
     */
    private class MyOnClickListener implements OnClickListener {

    	private WarningOrderInfo orderInfo;
    	private MyDialog deleteDialog;
    	
    	public MyOnClickListener(WarningOrderInfo info) {
    		orderInfo = info;
    		initDeleteDialog();
    	}
    	
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_pay:
			case R.id.btn_continue:
				mActivity.startPayActivity(orderInfo);
				break;
			case R.id.btn_revise:
				mActivity.startCustomizeActivity(orderInfo);
				break;
			case R.id.btn_delete:
				showDeleteDialog();
				break;
			}
		}
		
		/**
		 * 初始化取消支付对话框
		 */
		private void initDeleteDialog() {
			TextView tv = getTextDialog();
			tv.setText("你要删除订单吗？");
			deleteDialog = new MyDialog(mActivity, tv, "删除", "取消", new DialogListener() {
				@Override
				public void click(String str) {
					if (str.equals("删除")) {
						deleteDialog.dismiss();
						mActivity.requestDeleteOrder(orderInfo.orderID, new OnOrderDeleteListener() {
							
							@Override
							public void onDeleteSucceed() {
								removeData(orderInfo);
								mActivity.showToast(mActivity.getString(R.string.succeed_delete_order));
							}
						});
					} else if (str.equals("取消")) {
						deleteDialog.dismiss();
					}
				}
			});
		}
		
		/**
		 * 构造对话框提示字段
		 * @return
		 */
		private TextView getTextDialog() {
			TextView tv = (TextView) LayoutInflater.from(mActivity).inflate(
					R.layout.dialog_message, null);
			tv.setGravity(Gravity.CENTER);
			return tv;
		}
		
		/**
		 * 显示删除提示对话框
		 */
		private void showDeleteDialog() {
			deleteDialog.show();
		}
    }
    
}