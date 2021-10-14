package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackTravelWeekDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 旅游气象城市收藏夹适配器
 *
 * @author Administrator
 */
public class AdapterTravelBookmarks extends BaseAdapter {

    private Context context;
    private List<PackTravelWeekDown> dataList = new ArrayList<PackTravelWeekDown>();
    private OnClickDeleteButtonListener lDelete = null;
    private OnClickItemListener lItemClick = null;

    public AdapterTravelBookmarks(Context context, List<PackTravelWeekDown> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        if (dataList.size() >= 8) {
            return 8;
        }
        return dataList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_scenic_spots_favorite, null);
            holder.tvScenicSpotsName = (TextView) convertView
                    .findViewById(R.id.tv_scenic_spots_name);
            holder.tvTemp = (TextView) convertView.findViewById(R.id.tv_temp);
            holder.tvCity = (TextView) convertView.findViewById(R.id.tv_city);
            holder.ivWeatherIcon = (ImageView) convertView
                    .findViewById(R.id.iv_weather_icon);
            holder.btnFavoriteDelete = (Button) convertView
                    .findViewById(R.id.btn_favorite_delete);
            holder.llAdd = (LinearLayout) convertView.findViewById(R.id.ll_add);
            holder.llDefault = (LinearLayout) convertView
                    .findViewById(R.id.ll_default);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position >= 0 && position < 8) {
            if ((dataList.size() == 0)
                    || (dataList.size() > 0 && dataList.size() <= 7 && position == getCount() - 1)) {
                holder.llAdd.setVisibility(View.VISIBLE);
                holder.llDefault.setVisibility(View.GONE);
            } else if ((dataList.size() > 0 && dataList.size() <= 7 && position < getCount() - 1)
                    || dataList.size() == 8) {
                holder.llAdd.setVisibility(View.GONE);
                holder.llDefault.setVisibility(View.VISIBLE);
            }

            if (position >= 0 && position < dataList.size()) {
                PackTravelWeekDown pack = dataList.get(position);
                WeekWeatherInfo info = pack.getToday();
                // 取第一天
                if (info == null) {
                    return convertView;
                }
                holder.tvScenicSpotsName.setText(pack.cityName);
                holder.tvTemp.setText(info.higt + "~" + info.lowt + "℃");
                holder.tvCity.setText(pack.p_name);
                Bitmap bitmap = CommonUtil.getImageFromAssetsFile(context, pack.getIconPath(0));
                if (bitmap != null) {
                    holder.ivWeatherIcon.setImageBitmap(bitmap);
                }

                holder.btnFavoriteDelete
                        .setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (lDelete != null) {
                                    lDelete.onDelete(position);
                                }
                            }
                        });

            }

            final boolean isAdd = holder.llAdd.getVisibility() == View.VISIBLE;

            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (lItemClick != null) {

                        lItemClick.onItemClick(position, isAdd);
                    }
                }
            });

            // holder.itemText.setText(dataList.get(position).tour_name);
            // String url = context.getResources().getString(
            // R.string.file_download_url)
            // + dataList.get(position).img_url;
            // imageFetcher.loadImage(url, holder.itemImage, ImageShowType.SRC);
            // if(dataList.size() != 9 && position == dataList.size()+1) {
            // holder.itemText.setVisibility(View.GONE);
            // holder.itemImage.setImageResource(R.drawable.ic_launcher);
            // }
        }
        return convertView;
    }

    /**
     * 设置删除按钮点击事件监听
     *
     * @param listener
     */
    public void setOnClickDeleteButtonListener(
            OnClickDeleteButtonListener listener) {
        this.lDelete = listener;
    }

    /**
     * 设置整个布局点击事件监听
     *
     * @param listener
     */
    public void setOnClickItemListener(OnClickItemListener listener) {
        this.lItemClick = listener;
    }

    class ViewHolder {
        public TextView tvScenicSpotsName;
        public TextView tvTemp;
        public TextView tvCity;
        public ImageView ivWeatherIcon;
        public Button btnFavoriteDelete;
        public LinearLayout llDefault;
        public LinearLayout llAdd;
    }

    public interface OnClickDeleteButtonListener {
        void onDelete(int position);
    }

    public interface OnClickItemListener {
        /**
         * @param position
         * @param isAdd    是否为添加城市按钮
         */
        void onItemClick(int position, boolean isAdd);
    }

}
