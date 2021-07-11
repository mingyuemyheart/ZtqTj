package com.pcs.ztqtj.view.activity.prove

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.pcs.ztqtj.R
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*

/**
 * 气象证明-图片展示
 */
class ShowPictureAdapter(private val context: Context?, private val mArrayList: ArrayList<ProveDto>?) : BaseAdapter() {

    private var mInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private class ViewHolder {
        var imageView: ImageView? = null
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
            convertView = mInflater!!.inflate(R.layout.adapter_show_picture, null)
            mHolder = ViewHolder()
            mHolder.imageView = convertView.findViewById(R.id.imageView)
            convertView.tag = mHolder
        } else {
            mHolder = convertView.tag as ViewHolder
        }

        val dto = mArrayList!![position]
        if (TextUtils.isEmpty(dto.imgUrl)) {
            mHolder.imageView!!.setImageResource(R.drawable.icon_add_pic)
        } else {
            if (dto.imgUrl.startsWith("http")) {
                Picasso.get().load(dto.imgUrl).centerCrop().resize(200, 200).into(mHolder.imageView)
            } else {
                val file = File(dto.imgUrl)
                if (file.exists()) {
                    Picasso.get().load(file).centerCrop().resize(200, 200).into(mHolder.imageView)
                }
            }
        }

        return convertView
    }

}
