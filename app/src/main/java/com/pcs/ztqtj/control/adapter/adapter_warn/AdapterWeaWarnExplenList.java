package com.pcs.ztqtj.control.adapter.adapter_warn;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnCenterYJXXGridBean;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterWeaWarnExplenList extends BaseExpandableListAdapter {
	private Context context;
	private List<String> parentData;
	private List<List<WarnCenterYJXXGridBean>> childData;

	public AdapterWeaWarnExplenList(Context context, List<String> parentData, List<List<WarnCenterYJXXGridBean>> chilData) {
		this.context = context;
		this.parentData = parentData;
		this.childData = chilData;
	}

	public  class ListViewChildHolder {
		public TextView cTitle;
		public TextView cInformation,tv_warn_info_flag;
		public ImageView classImage;
	}

	public  class ListViewGroupHolder {
		public TextView content;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childData.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		ListViewChildHolder lvchildholder;
		if (null == convertView) {
			lvchildholder = new ListViewChildHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_weather_warn_child, parent, false);
			lvchildholder.cTitle = (TextView) convertView.findViewById(R.id.warn_title);
			lvchildholder.cInformation = (TextView) convertView.findViewById(R.id.warn_info);
			lvchildholder.classImage = (ImageView) convertView.findViewById(R.id.warn_image);
			lvchildholder.tv_warn_info_flag= (TextView) convertView.findViewById(R.id.tv_warn_info_flag);
			convertView.setTag(lvchildholder);
		} else {
			lvchildholder = (ListViewChildHolder) convertView.getTag();
		}

        String date2 = childData .get(groupPosition).get(childPosition).put_time + ":00";
        date2 = date2.replace("年", "-");
        date2 = date2.replace("月", "-");
        date2 = date2.replace("日", " ");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int days = 0;
        try {
            Date date3 = format.parse(date2);
            String date = format.format(new Date());
            Date date4 = format.parse(date);
            days = differentDaysByMillisecond(date3, date4);
        } catch (ParseException e) {
            e.printStackTrace();
        }
		String id= SharedPreferencesUtil.getData(childData.get(groupPosition).get(childPosition).id,"");
		if (days<1) {
            if (!TextUtils.isEmpty(id)) {
                lvchildholder.tv_warn_info_flag.setText("");
            } else {
                lvchildholder.tv_warn_info_flag.setText("未读");
            }
        }else {
            lvchildholder.tv_warn_info_flag.setText("");
        }
		lvchildholder.cTitle.setText(childData.get(groupPosition).get(childPosition).level);
		lvchildholder.cInformation.setText(childData.get(groupPosition).get(childPosition).put_str);
//		try {
//			if (childData.get(groupPosition).get(childPosition).level.contains("黄色")) {
//				lvchildholder.cTitle.setTextColor(context.getResources().getColor(R.color.warn_yellow));
//			} else if (childData.get(groupPosition).get(childPosition).level.contains("红色")) {
//				lvchildholder.cTitle.setTextColor(context.getResources().getColor(R.color.warn_red));
//			} else if (childData.get(groupPosition).get(childPosition).level.contains("橙色")) {
//				lvchildholder.cTitle.setTextColor(context.getResources().getColor(R.color.warn_orange));
//			} else if (childData.get(groupPosition).get(childPosition).level.contains("蓝色")) {
//				lvchildholder.cTitle.setTextColor(context.getResources().getColor(R.color.warn_blue));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		
//		lvchildholder.classImage.setImageResource(Integer.parseInt(childData.get(groupPosition).get(childPosition).get("i")));
		if(childData.get(groupPosition).get(childPosition).ico.equals("")){
			lvchildholder.classImage.setVisibility(View.GONE);
		}else{
		InputStream is = null;
		try {
			is=context.getResources().getAssets().open("img_warn/"+childData.get(groupPosition).get(childPosition).ico+".png");
			Bitmap bm=BitmapFactory.decodeStream(is);
			is.close();
//			bm=BitmapUtil.scaleBitmip(bm, 0.6f, 0.6f);
			lvchildholder.classImage.setImageBitmap(bm);
		} catch (IOException e) {
			e.printStackTrace();
				try {
					if(is!=null){
					is.close();
				}
				} catch (IOException e1) {
				
			}
		}
		}
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childData.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return parentData.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return parentData.size();

	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		ListViewGroupHolder parentHolder;
		if (null == convertView) {
			parentHolder = new ListViewGroupHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_weather_warn_parent, parent, false);
			parentHolder.content = (TextView) convertView.findViewById(R.id.weatherwarn_parent);
			convertView.setTag(parentHolder);
		} else {
			parentHolder = (ListViewGroupHolder) convertView.getTag();
		}
		parentHolder.content.setText(parentData.get(groupPosition));
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        return Math.abs(days);
    }
}