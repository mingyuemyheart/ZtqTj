package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfuMyproV2Down.ClassList;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfuMyproV2Down.DesServer;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfuMyproV2Down.SubClassList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterServerMyServer extends BaseExpandableListAdapter {
	private Context context;
	public List<ClassList> classList;
	public List<SubClassList> channels;
	private String showSubtitle;
    private MoreClickListener listener;
	public AdapterServerMyServer(Context context, String showSubtitle) {
		this.context = context;
		this.showSubtitle = showSubtitle;
		classList = new ArrayList<ClassList>();
		channels = new ArrayList<SubClassList>();
		typePosition = new ArrayList<IntType>();
	}
	class IntType {
		public int typePosition;
		public int inPosition;
	}

	private List<IntType> typePosition;

	public void setData(List<ClassList> classList) {
		this.classList = classList;
		this.channels.clear();
		typePosition.clear();
		for (int i = 0; i < classList.size(); i++) {
			IntType in = new IntType();
			in.typePosition = i;
			in.inPosition = this.channels.size();
			typePosition.add(in);
			for (int j = 0; j < classList.get(i).channels.size(); j++) {
				this.channels.add(classList.get(i).channels.get(j));
			}
		}
		this.notifyDataSetChanged();
	}

    @Override
	public int getGroupCount() {

		return channels.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
	    if (channels.get(groupPosition).pro_list.size()>0){
            return 1;
        }else{
            return channels.get(groupPosition).pro_list.size();
        }

	}

	@Override
	public Object getGroup(int groupPosition) {
		return channels.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		DesServer bena=channels.get(groupPosition).pro_list.get(childPosition);
		return bena;
	}

	@Override
	public long getGroupId(int groupPosition) {

		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		HolderGroup holder = null;
		if (convertView == null) {
			holder = new HolderGroup();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_myserver_group, null);
			holder.myserver_group_item = (TextView) convertView.findViewById(R.id.myserver_group_item);
			holder.myserver_group_channel = (TextView) convertView.findViewById(R.id.myserver_group_channel);
			holder.more = (Button) convertView.findViewById(R.id.more);
            holder.layout_subtitle = (RelativeLayout) convertView.findViewById(R.id.layout_myserver_group_channel);
			convertView.setTag(holder);
		} else {
			holder = (HolderGroup) convertView.getTag();
		}
		try {

			boolean showtype = false;
			int typePo = 0;
			for (int i = 0; i < typePosition.size(); i++) {
				if (groupPosition == typePosition.get(i).inPosition) {
					showtype = true;
					typePo = i;
				}
			}
			if (showtype) {
				String org_channel_name = "-" + classList.get(typePosition.get(typePo).typePosition).channel_name + "-";
				if (!showSubtitle.equals("0")) {
                    holder.layout_subtitle.setVisibility(View.VISIBLE);
					holder.myserver_group_channel.setText(org_channel_name);
				} else {
                    holder.layout_subtitle.setVisibility(View.GONE);
				}
			} else {
                holder.layout_subtitle.setVisibility(View.GONE);
			}
			holder.myserver_group_item.setText(channels.get(groupPosition).org_name+" "+channels.get(groupPosition).channel_name);
		} catch (Exception e) {
			e.printStackTrace();
		}

		holder.more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                if(listener != null) {
                    listener.onClick(channels.get(groupPosition));
                }
			}
		});
		return convertView;
	}

    public void setMoreListener(MoreClickListener listener) {
        this.listener = listener;
    }

	class HolderGroup {
		TextView myserver_group_item;
		TextView myserver_group_channel;
		Button more;
        RelativeLayout layout_subtitle;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.item_myserver_child, null);
		TextView myserver_title = (TextView) convertView.findViewById(R.id.myserver_title);
		TextView myserver_time = (TextView) convertView.findViewById(R.id.myserver_time);
        TextView tv_unit_flag= (TextView) convertView.findViewById(R.id.tv_unit_flag);
        String date2=channels.get(groupPosition).pro_list.get(childPosition).create_time+":00";
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

        String id= SharedPreferencesUtil.getData(channels.get(groupPosition).pro_list.get(childPosition).html_url,"");
        if (day<7) {
            if (!TextUtils.isEmpty(id)) {
                tv_unit_flag.setText("");
            } else {
                tv_unit_flag.setText("未读");
            }
        }else {
            tv_unit_flag.setText("");
        }


		myserver_title.setText(channels.get(groupPosition).pro_list.get(childPosition).title);
		myserver_time.setText(channels.get(groupPosition).pro_list.get(childPosition).create_time);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

    public interface MoreClickListener {
        void onClick(SubClassList bean);
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