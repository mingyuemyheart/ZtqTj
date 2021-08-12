package com.pcs.ztqtj.view.activity.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.pcs.ztqtj.R
import com.pcs.ztqtj.util.ColumnDto
import com.pcs.ztqtj.view.fragment.BaseFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_special_service.*

/**
 * 专项服务-地区点击
 */
class SpecialServiceFragment: BaseFragment() {

    private val dataList: ArrayList<ColumnDto> = ArrayList()
    private var mAdapter: SpecialServiceAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_special_service, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGridView()
    }

    private fun initGridView() {
        val data: ColumnDto = arguments!!.getParcelable("data")
        if (data != null) {
            var desc = "${data.dataName}简介"
            if (data.desc != null) {
                desc = "${desc}\n${data.desc}"
            }
            tvDesc.text = desc

            dataList.clear()
            dataList.addAll(data.childList)
        }
        mAdapter = SpecialServiceAdapter(activity)
        gridView.adapter = mAdapter
    }

    private inner class SpecialServiceAdapter(private val mContext: Context?) : BaseAdapter() {

        override fun getCount(): Int {
            return dataList.size
        }

        override fun getItem(position: Int): Any? {
            return if (dataList == null) {
                null
            } else position
        }

        override fun getItemId(position: Int): Long {
            return if (dataList.size == null) {
                0
            } else position.toLong()
        }

        override fun getView(position: Int, itemView: View?, parent: ViewGroup?): View {
            var view = itemView
            val holder: Holder
            if (view == null) {
                holder = Holder()
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_special_service, null)
                holder.itemImageView = view.findViewById<View>(R.id.itemImageView) as ImageView
                holder.itemimageviewTop = view.findViewById<View>(R.id.itemimageview_top) as ImageView
                holder.itemName = view.findViewById<View>(R.id.itemName) as TextView
                view.tag = holder
            } else {
                holder = view.tag as Holder
            }

            val info: ColumnDto = dataList[position]
            if (!TextUtils.isEmpty(info.icon)) {
                val url = mContext!!.getString(R.string.msyb).toString() + info.icon
                Picasso.get().load(url).error(R.drawable.no_pic).into(holder.itemImageView)
            } else {
                holder.itemImageView!!.setImageResource(R.drawable.no_pic)
            }

            holder.itemimageviewTop!!.setOnClickListener {
                val intent = Intent(activity, ActivityPdfList::class.java)
                val bundle = Bundle()
                bundle.putParcelable("data", info)
                intent.putExtras(bundle)
                mContext!!.startActivity(intent)
            }

            if (info.dataName != null) {
                holder.itemName!!.text = info.dataName
            }
            return view!!
        }

        private inner class Holder {
            var itemImageView: ImageView? = null
            var itemimageviewTop: ImageView? = null
            var itemName: TextView? = null
        }
    }

}