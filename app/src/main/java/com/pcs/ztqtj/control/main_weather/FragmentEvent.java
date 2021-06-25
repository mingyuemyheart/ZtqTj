package com.pcs.ztqtj.control.main_weather;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.web.webview.ActivityWebView;

public class FragmentEvent extends Fragment {

    private ImageFetcher mImageFetcher;

    public FragmentEvent() {
    }

    public FragmentEvent(ImageFetcher imageFetcher) {
        mImageFetcher = imageFetcher;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget(view);
    }

    private void initWidget(View view) {
        final String imgUrl = getArguments().getString("imgUrl");
        final String name = getArguments().getString("name");
        final String dataUrl = getArguments().getString("dataUrl");
        ImageView imageView = view.findViewById(R.id.imageView);
        if (mImageFetcher != null) {
            mImageFetcher.loadImage(imgUrl, imageView, ImageConstant.ImageShowType.SRC);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), ActivityWebView.class);
                it.putExtra("title", name);
                it.putExtra("url", dataUrl);
                it.putExtra("shareContent", name);
                startActivity(it);
            }
        });
    }

}
