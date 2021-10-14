package com.pcs.ztqtj.control.main_weather;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.view.activity.web.webview.ActivityWebView;
import com.squareup.picasso.Picasso;

/**
 * 广告
 */
public class FragmentAd extends Fragment {

    public FragmentAd() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ad, null);
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
        Picasso.get().load(imgUrl).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtil.isEmpty(dataUrl) && dataUrl.startsWith("http")) {
                    Intent it = new Intent(getActivity(), ActivityWebView.class);
                    it.putExtra("title", name);
                    it.putExtra("url", dataUrl);
                    it.putExtra("shareContent", name);
                    startActivity(it);
                }
            }
        });
    }

}
