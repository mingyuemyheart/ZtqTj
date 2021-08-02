package com.pcs.ztqtj.view.activity.life.health

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.pcs.ztqtj.R
import com.pcs.ztqtj.util.CommonUtil
import com.pcs.ztqtj.view.activity.life.HealthDto
import com.squareup.picasso.Picasso

/**
 * 健康气象详情
 */
class HealthDetailAdapter(private val context: Context?, private val mArrayList: ArrayList<HealthDto>?) : BaseAdapter(){

	private val mInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

	class ViewHolder {
		var imageView: ImageView? = null
		var tvName: TextView? = null
		var llContainer: LinearLayout? = null
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
			convertView = mInflater!!.inflate(R.layout.adapter_health_detail, null)
			mHolder = ViewHolder()
			mHolder.tvName = convertView.findViewById(R.id.tvName)
			mHolder.llContainer = convertView.findViewById(R.id.llContainer)
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			convertView.tag = mHolder
		} else {
			mHolder = convertView.tag as ViewHolder
		}

		val dto = mArrayList!![position]

		if (dto.isFirst) {
			mHolder.imageView!!.visibility = View.INVISIBLE
		} else {
			mHolder.imageView!!.visibility = View.VISIBLE
		}

		if (dto.indexName != null) {
			mHolder.tvName!!.text = dto.indexName
		}

		if (!TextUtils.isEmpty(dto.indexIcon)) {
			val imgUrl = context!!.getString(R.string.msyb)+dto.indexIcon
			Picasso.get().load(imgUrl).error(R.drawable.no_pic).into(mHolder.imageView)
		} else {
			mHolder.imageView!!.setImageResource(R.drawable.no_pic)
		}

		mHolder.llContainer!!.removeAllViews()
		val itemWidth = (CommonUtil.widthPixels(context)-CommonUtil.dip2px(context, 75f).toInt())/12
		val param = LinearLayout.LayoutParams(itemWidth, CommonUtil.dip2px(context, 40f).toInt())
		for (i in 1 until 13) {
			val tv = TextView(context)
			tv.setTextColor(ContextCompat.getColor(context!!, R.color.text_color4))
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
			tv.gravity = Gravity.CENTER
			if (dto.isFirst) {
//				if (i == 12) {
//					tv.text = "${i}月"
//				} else {
					tv.text = i.toString()
//				}
			} else {
				tv.text = ""
				if (dto.indexMonth.contains(",")) {
					val monthArrayList = dto.indexMonth.split(",")
					for (j in monthArrayList.indices) {
						if (TextUtils.equals(monthArrayList[j], i.toString())) {
							tv.setBackgroundColor(0x403180d1)
							break
						}
					}
				} else {
					if (TextUtils.equals(dto.indexMonth, i.toString())) {
						tv.setBackgroundColor(0x403180d1)
					}
				}
			}
			tv.layoutParams = param
			mHolder.llContainer!!.addView(tv)
		}

		return convertView
	}

}
