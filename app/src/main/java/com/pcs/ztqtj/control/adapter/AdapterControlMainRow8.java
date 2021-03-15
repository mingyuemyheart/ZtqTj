package com.pcs.ztqtj.control.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.pcs.ztqtj.control.inter.ImageClick;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;

import java.util.List;

/**
 * @author Z 首页广告栏适配器
 */
public class AdapterControlMainRow8 extends PagerAdapter {
    private List<String> imageViews;
    private ImageClick listener;
    private ImageFetcher imageFetcher;

    public AdapterControlMainRow8(List<String> imageViews, ImageClick listener, ImageFetcher imageFetcher) {
        this.imageViews = imageViews;
        this.listener = listener;
        this.imageFetcher = imageFetcher;
    }

    @Override
    public int getCount() {
        if (imageViews.size() > 1) {
            return 10000;
        } else {
            return imageViews.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (View) object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imgview = new ImageView(container.getContext());
        imgview.setScaleType(ScaleType.FIT_XY);
        if (imageViews.size() == 0) {
            return null;
        }
        String path = imageViews.get(position % imageViews.size());
        imageFetcher.loadImage(path, imgview, ImageConstant.ImageShowType.SRC);
        imgview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.itemClick(imageViews.get(position % imageViews.size()));
                }
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        container.addView(imgview, lp);
        return imgview;
    }

}