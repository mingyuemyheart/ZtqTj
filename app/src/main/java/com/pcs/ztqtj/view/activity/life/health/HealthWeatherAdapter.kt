package com.pcs.ztqtj.view.activity.life.health

import android.content.Context
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.pcs.ztqtj.R
import com.pcs.ztqtj.view.activity.life.HealthDto
import com.squareup.picasso.Picasso

/**
 * 健康气象-健康与生活气象
 */
class HealthWeatherAdapter(private val context: Context?, private val mArrayList: ArrayList<HealthDto>?) : BaseAdapter(){

	private val mInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

	class ViewHolder {
		var divider1: TextView? = null
		var imageView: ImageView? = null
		var tvName: TextView? = null
		var tvLevel: TextView? = null
		var tvTips: TextView? = null
	}

	override fun getCount(): Int {
		return mArrayList!!.size
	}

	override fun getItem(position: Int): Any? {
		return position
	}

	override fun getItemId(position: Int): Long {
		return position.toLong()
	}

	override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
		var convertView = view
		val mHolder: ViewHolder
		if (convertView == null) {
			convertView = mInflater!!.inflate(R.layout.adapter_health_weather, null)
			mHolder = ViewHolder()
			mHolder.divider1 = convertView.findViewById(R.id.divider1)
			mHolder.tvName = convertView.findViewById(R.id.tvName)
			mHolder.tvLevel = convertView.findViewById(R.id.tvLevel)
			mHolder.tvTips = convertView.findViewById(R.id.tvTips)
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			convertView.tag = mHolder
		} else {
			mHolder = convertView.tag as ViewHolder
		}

		val dto = mArrayList!![position]
		if (position % 2 == 0) {
			mHolder.divider1!!.visibility = View.VISIBLE
		} else {
			mHolder.divider1!!.visibility = View.INVISIBLE
		}
		if (dto.name != null) {
			mHolder.tvName!!.text = dto.name
		}
		if (dto.level != null) {
			mHolder.tvLevel!!.text = dto.level+"级"
		}
		if (dto.tips != null) {
			mHolder.tvTips!!.text = dto.tips
			if (dto.tips.length >= 16) {
				mHolder.tvTips!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
			} else if (dto.tips.length >= 12) {
				mHolder.tvTips!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
			} else {
				mHolder.tvTips!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
			}
		}
		if (!TextUtils.isEmpty(dto.indexIcon)) {
			val imgUrl = context!!.getString(R.string.msyb)+dto.indexIcon
			Picasso.get().load(imgUrl).error(R.drawable.no_pic).into(mHolder.imageView)
		} else {
			mHolder.imageView!!.setImageResource(R.drawable.no_pic)
		}

		return convertView
	}

}
