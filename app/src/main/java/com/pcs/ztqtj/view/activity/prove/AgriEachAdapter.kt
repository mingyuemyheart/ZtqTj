package com.pcs.ztqtj.view.activity.prove

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.pcs.ztqtj.R
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.CommonUtil
import com.squareup.picasso.Picasso

/**
 * 苗情农情互动
 */
class AgriEachAdapter(private val context: Context?, private val mArrayList: ArrayList<ProveDto>?) : BaseAdapter(){

	private val mInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

	class ViewHolder {
		var tvTime: TextView? = null
		var tvAsk: TextView? = null
		var tvAnwser: TextView? = null
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
			convertView = mInflater!!.inflate(R.layout.adapter_agri_each, null)
			mHolder = ViewHolder()
			mHolder.tvTime = convertView.findViewById(R.id.tvTime)
			mHolder.tvAsk = convertView.findViewById(R.id.tvAsk)
			mHolder.tvAnwser = convertView.findViewById(R.id.tvAnwser)
			mHolder.llContainer = convertView.findViewById(R.id.llContainer);
			convertView.tag = mHolder
		} else {
			mHolder = convertView.tag as ViewHolder
		}

		val dto = mArrayList!![position]
		if (dto.createTime != null) {
			mHolder.tvTime!!.text = dto.createTime
		}
		if (dto.ask != null) {
			mHolder.tvAsk!!.text = dto.ask
		}
		if (dto.anwser != null) {
			mHolder.tvAnwser!!.text = dto.anwser
		}

		mHolder.llContainer!!.removeAllViews()
		if (!TextUtils.isEmpty(dto.imgUrl)) {
			val param: LinearLayout.LayoutParams = LinearLayout.LayoutParams(CommonUtil.dip2px(context, 50f).toInt(), CommonUtil.dip2px(context, 50f).toInt())
			param.leftMargin = CommonUtil.dip2px(context, 10f).toInt()
			if (dto.imgUrl.contains("|")) {
				val array = dto.imgUrl.split("|")
				for (i in array.indices) {
					if (array[i] != null) {
						val imgUrl = context!!.getString(R.string.msyb)+"/"+array[i]
						val imageView = ImageView(context)
						imageView.adjustViewBounds = true
						Picasso.get().load(imgUrl).error(R.drawable.no_pic).into(imageView)
						mHolder.llContainer!!.addView(imageView)
						imageView.layoutParams = param

						imageView.setOnClickListener {
							val intent = Intent(context, DisplayPictureActivity::class.java)
							intent.putExtra(CONST.WEB_URL, imgUrl)
							context.startActivity(intent)
						}
					}
				}
			}
		}

		return convertView
	}

}
