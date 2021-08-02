package com.pcs.ztqtj.control.adapter.photo;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoSingle;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.utils.TextUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 设置-我的
 */
public class AdapterPhotoCenter extends BaseAdapter {

    private Context mContext;
    private ImageFetcher imageFetcher;
    private List<PackPhotoSingle> photoList;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy", Locale.CHINA);
    private SimpleDateFormat sdf3 = new SimpleDateFormat("MM", Locale.CHINA);
    private SimpleDateFormat sdf4 = new SimpleDateFormat("dd", Locale.CHINA);

    private ImageListener mImageListener;
    private RemoveListener mRemoveListener;

    // 当前年份
    private int currentYear = -1;

    public AdapterPhotoCenter(Context context, ImageFetcher imageFetcher, List<PackPhotoSingle> photoList) {
        mContext = context;
        this.imageFetcher = imageFetcher;
        this.photoList = photoList;
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public Object getItem(int position) {
        return photoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_photo_center_2, parent, false);
            holder.tvToday = (TextView) convertView.findViewById(R.id.tv_today);
            holder.llDate = (LinearLayout) convertView.findViewById(R.id.ll_date);
            holder.tvMonth = (TextView) convertView.findViewById(R.id.time_month);
            holder.tvDay = (TextView) convertView.findViewById(R.id.time_day);
            holder.tvYear = (TextView) convertView.findViewById(R.id.tv_year);
            holder.ivImage = (ImageView) convertView.findViewById(R.id.image_photo);
            holder.tvDelete = (TextView) convertView.findViewById(R.id.btn_delete);
            holder.layoutContent = (RelativeLayout) convertView.findViewById(R.id.content_layout);
            holder.tvContent = (TextView) convertView.findViewById(R.id.des_tv);
            holder.tvAddress = (TextView) convertView.findViewById(R.id.address_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImageListener != null) {
                    mImageListener.onClick(position);
                }
            }
        });

        final PackPhotoSingle info = photoList.get(position);

        // 当显示照相机时
        if (TextUtils.isEmpty(info.itemId)) {
            holder.ivImage.setImageResource(R.drawable.icon_camera); // 显示照相机
            holder.tvToday.setVisibility(View.VISIBLE); // 显示今天圆圈
            holder.llDate.setVisibility(View.GONE); // 隐藏之前日期圆圈
            holder.layoutContent.setVisibility(View.GONE); // 隐藏评论
            holder.tvDelete.setVisibility(View.GONE); // 隐藏删除按钮

            // 今天的年份
            currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if(photoList.size() > 0) {
                setYear(0, holder.tvYear);
            }
        } else {
            holder.tvToday.setVisibility(View.GONE); // 隐藏今天圆圈
            holder.llDate.setVisibility(View.VISIBLE); // 显示之前日期圆圈
            holder.layoutContent.setVisibility(View.VISIBLE); // 显示评论
            holder.tvDelete.setVisibility(View.VISIBLE); // 显示删除按钮

            try {
                if (!TextUtils.isEmpty(info.date_time)) {
                    holder.tvMonth.setText(sdf3.format(sdf1.parse(info.date_time)));
                    holder.tvDay.setText(sdf4.format(sdf1.parse(info.date_time)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            setYear(position, holder.tvYear);

            if(!TextUtils.isEmpty(info.des)) {
                holder.tvContent.setVisibility(View.VISIBLE);
                holder.tvContent.setText(info.des);
            } else {
                holder.tvContent.setVisibility(View.GONE);
            }

            if (!TextUtil.isEmpty(info.thumbnailUrl)) {
                imageFetcher.loadImage(mContext.getString(R.string.sjkp) + info.thumbnailUrl, holder.ivImage, ImageConstant.ImageShowType.SRC);
            } else {
                holder.ivImage.setImageResource(R.drawable.no_pic);
            }

            if (info.address != null) {
                holder.tvAddress.setText(info.address);
            }

            holder.tvDelete.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        mRemoveListener.removeItem(info, position);
                        photoList.remove(position);
                    }
                });
        }

        return convertView;
    }

    /**
     * 获取下一条图片的年份信息
     * @param position
     * @return
     */
    private int getYear(int position) {
        if(photoList != null && photoList.size() > position) {
            PackPhotoSingle info = photoList.get(position);
            if (info != null && !TextUtils.isEmpty(info.date_time)) {
                int year = currentYear;
                try {
                    String y = sdf2.format(sdf1.parse(info.date_time));
                    year = Integer.parseInt(y);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return year;
            }
        }
        return -1;
    }

    /**
     * 设置年份
     * @param position
     * @param tv
     */
    private void setYear(int position, TextView tv) {
        int year = getYear(position);
        if(year != -1 && currentYear > year) {
            tv.setVisibility(View.VISIBLE);
            tv.setText(year + "年");
        } else {
            tv.setVisibility(View.INVISIBLE);
        }
        currentYear = year;
    }

    /**
     * 设置图片点击监听
     * @param listener
     */
    public void setImageListener(ImageListener listener) {
        mImageListener = listener;
    }

    /**
     * 删除监听
     * @param listener
     */
    public void setDeleteListener(RemoveListener listener) {
        mRemoveListener = listener;
    }

    /**
     * 图片点击监听
     */
    public interface ImageListener {
        void onClick(int position);
    }

    /**
     * 删除监听
     */
    public interface RemoveListener {
        public void removeItem(PackPhotoSingle info, int position);
    }

    private static class ViewHolder {
        public TextView tvToday; // 今天的圆圈
        public LinearLayout llDate; // 其他时间的圆圈
        public TextView tvMonth; // 左侧月份
        public TextView tvDay; // 左侧日期
        public TextView tvYear; // 底部年份
        public ImageView ivImage; // 图片
        public TextView tvDelete; // 删除按钮
        public RelativeLayout layoutContent; // 评论布局
        public TextView tvContent; // 评论
        public TextView tvAddress; // 地址
    }

}
