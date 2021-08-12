package com.pcs.ztqtj.view.activity.service

import android.app.Activity
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
import com.pcs.ztqtj.model.ZtqCityDB
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.ColumnDto
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase
import com.pcs.ztqtj.view.activity.agricuture.ActivityAgricutureFact
import com.pcs.ztqtj.view.activity.agricuture.ActivityAgricutureView
import com.pcs.ztqtj.view.activity.photoshow.ActivityLogin
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoShow
import com.pcs.ztqtj.view.activity.product.agriculture.ActivityAgricultureWeather
import com.pcs.ztqtj.view.activity.product.traffic.ActivityTraffic
import com.pcs.ztqtj.view.activity.prove.AgriEachActivity
import com.pcs.ztqtj.view.activity.web.webview.ActivityWebView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_server_hyqx.*

/**
 * 专项服务-行业气象
 */
class ActivityServerHyqx : FragmentActivityZtqBase() {

    private val dataList: ArrayList<ColumnDto> = ArrayList()
    private var mAdapter: MyGridViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_hyqx)
        initGridView()
    }

    private fun initGridView() {
        if (intent.hasExtra("title")) {
            val title = intent.getStringExtra("title")
            if (title != null) {
                titleText = title
            }
        }

        if (intent.hasExtra("dataList")) {
            dataList.addAll(intent.getParcelableArrayListExtra("dataList"))
        }
        mAdapter = MyGridViewAdapter(this)
        gridview.adapter = mAdapter
    }

    private inner class MyGridViewAdapter(private val mContext: Context) : BaseAdapter() {

        private inner class Holder {
            var itemImageView: ImageView? = null
            var itemimageview_top: ImageView? = null
            var itemName: TextView? = null
        }

        override fun getCount(): Int {
            return dataList.size
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, itemView: View?, parent: ViewGroup): View {
            var view = itemView
            val holder: Holder
            if (view == null) {
                holder = Holder()
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_special_service, null)
                holder.itemImageView = view.findViewById<View>(R.id.itemImageView) as ImageView
                holder.itemimageview_top = view.findViewById<View>(R.id.itemimageview_top) as ImageView
                holder.itemName = view.findViewById<View>(R.id.itemName) as TextView
                view.tag = holder
            } else {
                holder = view.tag as Holder
            }

            val data: ColumnDto = dataList[position]

            if (!TextUtils.isEmpty(data.icon)) {
                val url = mContext.getString(R.string.msyb) + data.icon
                Picasso.get().load(url).error(R.drawable.no_pic).into(holder.itemImageView)
            } else {
                holder.itemImageView!!.setImageResource(R.drawable.no_pic)
            }
            holder.itemImageView!!.setOnClickListener {
                when(data.dataCode) {
                    "10103030201" -> {//交通气象
                        val intent = Intent(mContext, ActivityTraffic::class.java)
                        intent.putExtra("title", data.dataName)
                        intent.putExtra("dataCode", data.dataCode)
                        startActivity(intent)
                    }
                    "101030302020101" -> {//农情实况数据
                        val intent = Intent(mContext, ActivityAgricutureFact::class.java)
                        intent.putExtra("title", data.dataName)
                        intent.putExtra("dataCode", data.dataCode)
                        startActivity(intent)
                    }
                    "101030302020102" -> {//农田实景
                        val intent = Intent(mContext, ActivityAgricutureView::class.java)
                        intent.putExtra("title", data.dataName)
                        intent.putExtra("dataCode", data.dataCode)
                        startActivity(intent)
                    }
                    "101030302020201" -> {//旬报
                        val intent = Intent(mContext, ActivityAgricultureWeather::class.java)
                        intent.putExtra("title", data.dataName)
                        intent.putExtra("dataCode", data.dataCode)
                        intent.putExtra("channel_id", "10103030202")
                        intent.putExtra("type", "xb")
                        startActivity(intent)
                    }
                    "101030302020202" -> {//月报
                        val intent = Intent(mContext, ActivityAgricultureWeather::class.java)
                        intent.putExtra("title", data.dataName)
                        intent.putExtra("dataCode", data.dataCode)
                        intent.putExtra("channel_id", "10103030202")
                        intent.putExtra("type", "yb")
                        startActivity(intent)
                    }
                    "101030302020301" -> {//农情苗情速报
                        if (!ZtqCityDB.getInstance().isLoginService()) {
                            intent = Intent(mContext, ActivityLogin::class.java)
                            startActivityForResult(intent, CONST.RESULT_LOGIN)
                        } else {
                            val intent = Intent(mContext, ActivityPhotoShow::class.java)
                            intent.putExtra("title", data.dataName)
                            intent.putExtra("imgType", "2")//imgType:图片类型，1（实景开拍），2（农业开拍分类）必须传，区分哪个业务
                            startActivity(intent)
                        }
                    }
                    "101030302020302" -> {//农情苗情互动
                        val intent = Intent(mContext, AgriEachActivity::class.java)
                        intent.putExtra("title", data.dataName)
                        startActivity(intent)
                    }
                    "1010303020204","1010303020205","1010303020206","1010303020207","1010303020208","1010303020209" -> {
                        val intent = Intent(mContext, ActivityWebView::class.java)
                        intent.putExtra("title", data.dataName)
                        intent.putExtra("url", data.url)
                        intent.putExtra("shareContent", data.dataName)
                        startActivity(intent)
                    }
                    else -> {//农业气象
                        if (data.childList.size > 0) {
                            val intent = Intent(mContext, ActivityServerHyqx::class.java)
                            intent.putExtra("title", data.dataName)
                            val bundle = Bundle()
                            bundle.putParcelableArrayList("dataList", data.childList)
                            intent.putExtras(bundle)
                            startActivity(intent)
                        }
                    }
                }
            }

            if (data.dataName != null) {
                holder.itemName!!.text = data.dataName
            }
            holder.itemimageview_top!!.visibility = View.GONE
            return view!!
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CONST.RESULT_LOGIN -> {
                    val intent = Intent(this, ActivityPhotoShow::class.java)
                    intent.putExtra("title", "农情苗情速报")
                    intent.putExtra("imgType", "2")//imgType:图片类型，1（实景开拍），2（农业开拍分类）必须传，区分哪个业务
                    startActivity(intent)
                }
            }
        }
    }

}
