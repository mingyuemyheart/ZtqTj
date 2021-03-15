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
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfuMyproV2Down.DesServer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterMyserverMore extends BaseAdapter{

	private Context context;
	private List<DesServer> pro_list;
	public AdapterMyserverMore(Context context,List<DesServer> pro_list){
		this.context=context;
		this.pro_list=pro_list;
	}
	@Override
	public int getCount() {
		return pro_list.size();
	}

	@Override
	public Object getItem(int position) {
		return pro_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder=null;
		if(convertView==null){
			holder=new Holder();
			convertView=LayoutInflater.from(context).inflate(R.layout.item_myserver_child, null);
			holder.title=(TextView) convertView.findViewById(R.id.myserver_title);
			holder.time=(TextView) convertView.findViewById(R.id.myserver_time);
			holder.tv_unit_flag= (TextView) convertView.findViewById(R.id.tv_unit_flag);
			convertView.setTag(holder);
		}else{
			holder=(Holder) convertView.getTag();
		}
        String date2=pro_list.get(position).create_time+":00";
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

		String id= SharedPreferencesUtil.getData(pro_list.get(position).html_url,"");
        if (day<7) {
            if (!TextUtils.isEmpty(id)) {
                holder.tv_unit_flag.setText("");
            } else {
                holder.tv_unit_flag.setText("未读");
            }
        }else {
            holder.tv_unit_flag.setText("");
        }
		holder.title.setText(pro_list.get(position).title);
		holder.time.setText(pro_list.get(position).create_time);
		
		return convertView;
	}
	class Holder{
		public TextView title;
		public TextView time,tv_unit_flag;
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
