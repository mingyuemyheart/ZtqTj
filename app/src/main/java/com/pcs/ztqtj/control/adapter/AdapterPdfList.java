package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.ServiceProductInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * pdf列表
 */
public class AdapterPdfList extends BaseAdapter {

	private Context context;
	private List<ServiceProductInfo> dataList;

	public AdapterPdfList(Context context, List<ServiceProductInfo> dataList) {
		this.context = context;
		this.dataList = dataList;
	}

	public void setData(List<ServiceProductInfo> dataList) {
		this.dataList = dataList;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Holder holder;
		if (view == null) {
			holder = new Holder();
			view = LayoutInflater.from(context).inflate(R.layout.adapter_pdf_list, null);
			holder.texttitle = (TextView) view.findViewById(R.id.item_title);
			holder.textdata = (TextView) view.findViewById(R.id.item_data);
			holder.tv_warn_info_flag= (TextView) view.findViewById(R.id.tv_warn_info_flag);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		ServiceProductInfo info = dataList.get(position);
        String date2=info.create_time+":00";
        date2=date2.replace("年","-");
        date2=date2.replace("月","-");
        date2=date2.replace("日","");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int day=0;
        try {
            Date date3=format.parse(date2);
            String date=format.format(new Date());
            Date date4=format.parse(date);
            day=differentDaysByMillisecond(date3,date4);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String id= SharedPreferencesUtil.getData(info.html_url,"");
        if (day<7){
            if (!TextUtils.isEmpty(id)){
                holder.tv_warn_info_flag.setText("");
            }else {
                holder.tv_warn_info_flag.setText("未读");
            }
        }else{
            holder.tv_warn_info_flag.setText("");
        }

		holder.texttitle.setText(info.title);
		holder.textdata.setText(info.create_time);

		return view;
	}

	class Holder {
		public TextView texttitle;
		public TextView textdata,tv_warn_info_flag;
	}

   /**
     * 通过时间秒毫秒数判断两个时间的间隔
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1,Date date2)
    {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }
}
