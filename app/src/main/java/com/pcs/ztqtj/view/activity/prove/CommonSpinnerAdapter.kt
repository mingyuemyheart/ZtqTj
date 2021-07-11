package com.pcs.ztqtj.view.activity.prove

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pcs.ztqtj.R

/**
 * 通用spinner适配器
 */
class CommonSpinnerAdapter(context: Context?, private val mArrayList: ArrayList<ProveDto>?) : BaseAdapter() {

    private val mInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private class ViewHolder {
        var tvTitle: TextView? = null
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
            convertView = mInflater!!.inflate(R.layout.adapter_common_spinner, null)
            mHolder = ViewHolder()
            mHolder.tvTitle = convertView.findViewById(R.id.tvTitle)
            convertView.tag = mHolder
        } else {
            mHolder = convertView.tag as ViewHolder
        }

        val dto = mArrayList!![position]
        if (dto.companyName != null) {
            mHolder.tvTitle!!.text = dto.companyName
        }
        return convertView
    }

}
