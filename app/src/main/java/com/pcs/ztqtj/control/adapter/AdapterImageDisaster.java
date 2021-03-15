package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackKnowWarnDetailDown.KnowWarnDetailBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Z 识图防灾适配器
 */
public class AdapterImageDisaster extends BaseAdapter {
    private Context context;
    private List<KnowWarnDetailBean> datalist;
    private ImageFetcher imageFetcher;

    public AdapterImageDisaster(Context context, List<KnowWarnDetailBean> datalist, ImageFetcher imageFetcher) {
        this.context = context;
        this.datalist = datalist;
        this.imageFetcher = imageFetcher;
    }

    @Override
    public int getCount() {
        return datalist.size();
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
    public View getView(int position, View view, ViewGroup parent) {
        Handler handler;
        if (view == null) {
            handler = new Handler();
            view = View.inflate(context, R.layout.item_iamgedisaster, null);
            handler.icon = (ImageView) view.findViewById(R.id.warn_icon);
            handler.title = (TextView) view.findViewById(R.id.warn_title);
            handler.style_desc = (TextView) view.findViewById(R.id.warn_desc);
            handler.protect_desc = (TextView) view.findViewById(R.id.protect_desc);
            handler.style_content = (TextView) view.findViewById(R.id.warn_context);
            handler.protect_content = (TextView) view.findViewById(R.id.protect_content);
            view.setTag(handler);
        } else {
            handler = (Handler) view.getTag();
        }
        KnowWarnDetailBean bean = datalist.get(position);
        handler.title.setText(bean.title);
        handler.style_content.setText(bean.des);
        handler.protect_content.setText(bean.content);
        if (bean.img.equals("")) {
            handler.icon.setVisibility(View.GONE);
        } else {
            Bitmap bm = getWarnBm(context, "img_warn/" + bean.img + ".png");
            handler.icon.setImageBitmap(bm);
        }
        return view;
    }


    private Bitmap getWarnBm(Context context, String path) {
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(path);
            Bitmap bm = BitmapFactory.decodeStream(is);
            bm = BitmapUtil.scaleBitmip(bm, 0.8f, 0.8f);
            is.close();
            return bm;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e1) {
            }
        }
        return null;
    }


    private class Handler {
        public ImageView icon;
        public TextView title;
        public TextView style_desc;
        public TextView protect_desc;
        public TextView style_content;
        public TextView protect_content;
    }

}
