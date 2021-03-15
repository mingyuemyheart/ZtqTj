package com.pcs.ztqtj.control.adapter.location_warning;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.AreaInfo;

/**
 * 精确定点服务--服务区域数据适配器
 * @author E.Sun
 * 2015年11月14日
 */
public class AdapterServiceArea extends BaseAdapter {

    private Context mContext;
    private List<AreaInfo> mList;
    private String[] codeStrings;

    public AdapterServiceArea(Context context, List<AreaInfo> list) {
        mContext = context;
        mList = list;
        codeStrings = mContext.getResources().getStringArray(R.array.CodeStringArray);
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

    @SuppressLint("InflateParams")
	@Override
    public View getView(int position, View converView, ViewGroup parent) {
        Holder holder;
        if(converView == null) {
            converView = LayoutInflater.from(mContext).inflate(R.layout.item_service_area, null);
            holder = new Holder();
            holder.tvID = (TextView) converView.findViewById(R.id.tv_id);
            holder.tvCity = (TextView) converView.findViewById(R.id.tv_city);
            holder.tvArea = (TextView) converView.findViewById(R.id.tv_area);
            converView.setTag(holder);
        } else {
            holder = (Holder) converView.getTag();
        }

        // 填充内容
        AreaInfo info = mList.get(position);
        holder.tvID.setText(codeStrings[position]);
        holder.tvCity.setText(info.cityName);
        holder.tvArea.setText(info.areaName);

        // 返回指定position的选项视图
        return converView;
    }

    /**
     * 替换初始数据
     */
    public void setData(List<AreaInfo> list) {
        mList = list;
        notifyDataSetChanged();
    }

    class Holder {
    	TextView tvID;
        TextView tvCity;
        TextView tvArea;
    }

}
