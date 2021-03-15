package com.pcs.ztqtj.control.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.ProductSetMealInfo;

/**
 * 套餐类型数据适配器
 * @author E.Sun
 * 2015年11月13日
 */
public class AdapterSetMeal extends BaseAdapter {

    private Context mContext;
    private List<ProductSetMealInfo> mList;

    public AdapterSetMeal(Context context, List<ProductSetMealInfo> list) {
        mContext = context;
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
            converView = LayoutInflater.from(mContext).inflate(R.layout.item_livequery_text, null);
            holder = new Holder();
            holder.tvSetMeal = (TextView) converView.findViewById(R.id.text_string);
            converView.setTag(holder);
        } else {
            holder = (Holder) converView.getTag();
        }

        // 填充内容
        ProductSetMealInfo info = mList.get(position);
        holder.tvSetMeal.setText(info.name);
        
        // 返回指定position的选项视图
        return converView;
    }

    /**
     * 替换初始数据
     * @param list
     */
    public void setData(List<ProductSetMealInfo> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public class Holder {
        public TextView tvSetMeal;
    }

}
