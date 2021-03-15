package com.pcs.ztqtj.control.adapter.photo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoCommentListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoCommentListDown.Data;

/**
 * 适配器：实景评论
 *
 * @author JiangZy
 */
public class AdapterPhotoComment extends BaseAdapter {

    private Context mContext;
    private PackPhotoCommentListDown mPack = new PackPhotoCommentListDown();

    public AdapterPhotoComment(Context context) {
        mContext = context;
    }

    /**
     * 设置JSON数据
     *
     * @param key
     */
    public void setJsonData(String key) {
        mPack = (PackPhotoCommentListDown) PcsDataManager.getInstance().getNetPack(key);
        if (mPack == null) {
            mPack = new PackPhotoCommentListDown();
        }
    }

    @Override
    public int getCount() {
        return mPack.mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPack.mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_photo_comment, parent, false);
        }

        TextView textView;
        Data data = (Data) getItem(position);
        // 时间
        textView = (TextView) convertView.findViewById(R.id.text_time);
        textView.setText(data.time_format);
        // 内容
        textView = (TextView) convertView.findViewById(R.id.text_comment);
        textView.setText(data.des);
        // 昵称
        textView = (TextView) convertView.findViewById(R.id.text_name);
        textView.setText(data.nickName);

        return convertView;
    }
}
