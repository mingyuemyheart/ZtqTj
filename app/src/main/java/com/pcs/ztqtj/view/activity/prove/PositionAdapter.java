package com.pcs.ztqtj.view.activity.prove;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;

import java.util.List;

/**
 * 我上传的灾情反馈
 */
public class PositionAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<ProveDto> mArrayList;

	private final class ViewHolder{
		TextView tvTitle;
	}

	public PositionAdapter(Context context, List<ProveDto> mArrayList) {
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mArrayList.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_position, null);
			mHolder = new ViewHolder();
			mHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		ProveDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.disPosition)) {
			mHolder.tvTitle.setText(dto.disPosition);
		}

		return convertView;
	}

}
