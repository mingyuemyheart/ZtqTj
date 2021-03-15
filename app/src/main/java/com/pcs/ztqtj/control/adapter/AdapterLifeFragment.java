package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.life.ActivityChannelList;
import com.pcs.ztqtj.view.activity.life.ActivityMeteorologicalScience;
import com.pcs.ztqtj.view.activity.life.expert_interpretation.ActivityExpertList;
import com.pcs.ztqtj.view.activity.life.travel.ActivityTravelView;
import com.pcs.ztqtj.view.activity.product.media.ActivityMediaList;

import java.util.List;
import java.util.Map;


/**
 * Created by tyaathome on 2017/9/21.
 */

public class AdapterLifeFragment extends RecyclerView.Adapter<AdapterLifeFragment.ViewHolder> {

    private Context context;
    private List<Map<String, Object>> data;

    public AdapterLifeFragment(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.live_gridview_item, parent,
                false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemImage.setImageResource((Integer) data.get(position).get("i"));
        holder.itemText.setText("" + data.get(position).get("t"));
        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                switch (position) {
                    // <!-- 气象生活 栏目顺序 0、旅游气象；1、气象影视;2、天气新闻；3、专家解读 ;4、灾害防御;5、气象科普.
                    // -->
                    case 3:
                        //气象生活
                        intent.setClass(context, ActivityExpertList.class);
                        context.startActivity(intent);
                        break;
                    case 0:
                        // 旅游气象
                        intent.setClass(context, ActivityTravelView.class);
                        context.startActivity(intent);

                        break;
                    case 1:
                        // 气象影视
//					intent.setClass(context, ActivityFamilyCity.class);
                        intent.setClass(context, ActivityMediaList.class);
                        context.startActivity(intent);

                        break;
                    case 2:
                        intent.setClass(context, ActivityChannelList.class);
                        intent.putExtra("title", "天气新闻");
                        intent.putExtra("channel_id", "100005");
                        context.startActivity(intent);
//                        intent.setClass(context, ActivityExpertList.class);
//                        context.startActivity(intent);
                        break;
                    case 4:
//					灾害防御
                        intent.setClass(context, ActivityChannelList.class);
                        intent.putExtra("title", "灾害防御");
                        intent.putExtra("channel_id", "100007");
                        context.startActivity(intent);
                        break;
                    case 5:
                        // 气象科普
                        intent.setClass(context, ActivityMeteorologicalScience.class);
                        context.startActivity(intent);

                        break;
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemText;
        public ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            itemText = (TextView) itemView.findViewById(R.id.item_text);
            itemImage = (ImageView) itemView.findViewById(R.id.item_image);
        }
    }

}
