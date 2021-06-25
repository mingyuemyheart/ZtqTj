package com.pcs.ztqtj.control.adapter.life_number;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackLifeNumberDown.LifeNumber;
import com.pcs.ztqtj.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页-生活指数
 */
public class AdapterLifeNumberGridView extends BaseAdapter {

    private Activity activity;
    private ImageFetcher mImageFetcher;
    private List<LifeNumber> localNum;

    public AdapterLifeNumberGridView(Activity activity, ImageFetcher mImageFetcher, List<LifeNumber> localNum) {
        this.localNum = localNum;
        this.activity = activity;
        this.mImageFetcher = mImageFetcher;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (localNum == null) {
            return 0;
        }
        return localNum.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_home_life_number, parent, false);
            holder.icon = (ImageView) convertView.findViewById(R.id.image);
            holder.title = (TextView) convertView.findViewById(R.id.text_title);
            holder.content = (TextView) convertView.findViewById(R.id.text_content);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        try {
            LifeNumber packLocal = localNum.get(position);
            mImageFetcher.loadImage(activity.getString(R.string.shzs)+ packLocal.ico_path, holder.icon,ImageConstant.ImageShowType.SRC);
            holder.title.setText(packLocal.index_name);
            holder.content.setText(packLocal.simple_des);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    public LifeNumber getItemPosition(int position) {
        LifeNumber packLocal = localNum.get(position);
        return packLocal;
    }

    class Holder {
        public ImageView icon;
        public TextView title;
        public TextView content;
    }

}
