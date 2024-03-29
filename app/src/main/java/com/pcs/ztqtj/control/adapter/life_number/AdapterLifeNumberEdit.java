package com.pcs.ztqtj.control.adapter.life_number;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalLife;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackLifeNumberDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackLifeNumberDown.LifeNumber;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackLifeNumberUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 适配器：生活指数编辑
 * 
 * @author JiangZy
 * 
 */
public class AdapterLifeNumberEdit extends BaseAdapter {

	private Context mContext;
	// 网络指数列表
	private PackLifeNumberDown mDownPack = new PackLifeNumberDown();

	private List<LifeNumber> localNum = new ArrayList<LifeNumber>();

	public AdapterLifeNumberEdit(Context context) {
		mContext = context;
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        String area = cityMain.ID;
		localNum = PackLocalLife.getInstance().getLifeNumber(mContext);
		PackLifeNumberUp mUp = new PackLifeNumberUp();
		mDownPack = (PackLifeNumberDown) PcsDataManager.getInstance().getNetPack(PackLifeNumberUp.NAME+"#"+area);
		if(mDownPack==null){
			mDownPack = new PackLifeNumberDown();
		}
	}

	@Override
	public int getCount() {
		return mDownPack.dataList.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_life_number_edit, null);
			// 图标
			holder.icon = (ImageView) convertView.findViewById(R.id.image_icon);
			// 标题
			holder.content = (TextView) convertView
					.findViewById(R.id.text_title);
			holder.checkbox = (CheckBox) convertView
					.findViewById(R.id.check_box);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		LifeNumber lifeNumber = mDownPack.dataList.get(position);
		holder.content.setText(lifeNumber.index_name + "指数");
		String url = mContext.getString(R.string.file_download_url) + lifeNumber.ico2_path;
		Picasso.get().load(url).into(holder.icon);
		boolean isCheck = false;
		for (int i = 0; i < localNum.size(); i++) {
			if (localNum.get(i).id.equals(lifeNumber.id)) {
				isCheck = true;
			}
		}
		if (isCheck) {
			holder.checkbox.setChecked(true);
		} else {
			holder.checkbox.setChecked(false);
		}
		return convertView;
	}

	public void checkItem(int position) {
		LifeNumber lifeNumber = mDownPack.dataList.get(position);
		// Toast.makeText(mContext,
		// "click:"+position+"  "+lifeNumber.index_name+"  "+lifeNumber.id,
		// 0).show();
		boolean insert = true;
		for (int i = 0; i < localNum.size(); i++) {
			if (lifeNumber.id.equals(localNum.get(i).id)) {
				insert = false;
				localNum.remove(i);
				break;
			}
		}
		if (insert) {
			localNum.add(lifeNumber);

			changeList();
		}
		// 更新数据记录
		PackLocalLife.getInstance().setLifeNumber(mContext, localNum);
		this.notifyDataSetChanged();
	}

	/** 更新顺序 */
	public void changeList() {
		List<LifeNumber> localNumNew = new ArrayList<LifeNumber>();
		for (int i = 0; i < mDownPack.dataList.size(); i++) {
			for (int j = 0; j < localNum.size(); j++) {
				if (mDownPack.dataList.get(i).id.equals(localNum.get(j).id)) {
					localNumNew.add(mDownPack.dataList.get(i));
				}
			}
		}
		localNum.clear();
		for (int i = 0; i < localNumNew.size(); i++) {
			localNum.add(localNumNew.get(i));
		}
	}

	class Holder {
		ImageView icon;
		TextView content;
		CheckBox checkbox;
	}
}
