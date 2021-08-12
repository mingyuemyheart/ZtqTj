package com.pcs.ztqtj.control.adapter.media;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.media.MediaInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/18 0018.
 * chen_jx
 */

public class AdapterMediaList extends BaseAdapter {
    private Context mcontext;
    private ArrayList<MediaInfo> list;
    private ImageFetcher mImageFetcher = null;

    public AdapterMediaList(Context context, ArrayList<MediaInfo> mlist, ImageFetcher mImageFetcher) {
        this.mcontext = context;
        this.list = mlist;
        this.mImageFetcher = mImageFetcher;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Handler handler = null;
        if (view == null) {
            handler = new Handler();
            view = LayoutInflater.from(mcontext).inflate(R.layout.item_media_list, null);
            handler.item_image = (ImageView) view.findViewById(R.id.item_list_image);
            handler.tv_time = (TextView) view.findViewById(R.id.item_list_time);
            handler.tv_title = (TextView) view.findViewById(R.id.item_list_title);
            handler.tv_content = (TextView) view.findViewById(R.id.item_list_content);
            view.setTag(handler);
        } else {
            handler = (Handler) view.getTag();
        }
        handler.tv_title.setText(list.get(i).title);
        handler.tv_content.setText(list.get(i).desc);
        handler.tv_time.setText(list.get(i).time);
        String url = mcontext.getString(R.string.msyb) + list.get(i).imageurl;
        mImageFetcher.loadImage(url, handler.item_image, ImageConstant.ImageShowType.SRC);
        return view;
    }

    private static class Handler {
        private ImageView item_image;
        private TextView tv_time, tv_title, tv_content;
    }
}
