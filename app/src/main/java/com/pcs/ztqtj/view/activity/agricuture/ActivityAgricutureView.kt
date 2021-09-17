package com.pcs.ztqtj.view.activity.agricuture

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.pcs.ztqtj.MyApplication
import com.pcs.ztqtj.R
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.OkHttpUtil
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase
import com.pcs.ztqtj.view.activity.prove.DisplayPictureActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_agricuture_view.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * 专项服务-行业气象-农业气象-农作物发育情况-农田实景
 */
class ActivityAgricutureView: FragmentActivityZtqBase() {

    private var mAdapter: AdapterAgricutureView? = null
    private val dataList: ArrayList<AgriDto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agricuture_view)
        initWidget()
        initListView()
    }

    private fun initWidget() {
        if (intent.hasExtra("title")) {
            val title = intent.getStringExtra("title")
            if (title != null) {
                titleText = title
            }
        }
        okHttpList()
    }

    private fun initListView() {
        mAdapter = AdapterAgricutureView(this)
        gridView.adapter = mAdapter
    }

    private inner class AdapterAgricutureView(private val mContext: Context) : BaseAdapter() {

        private inner class Holder {
            var imageView: ImageView? = null
            var tvStationName: TextView? = null
            var tvTime: TextView? = null
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
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_agricuture_view, null)
                holder.tvStationName = view.findViewById<View>(R.id.tvStationName) as TextView
                holder.imageView = view.findViewById<View>(R.id.imageView) as ImageView
                holder.tvTime = view.findViewById<View>(R.id.tvTime) as TextView
                view.tag = holder
            } else {
                holder = view.tag as Holder
            }

            val data: AgriDto = dataList[position]
            if (!TextUtils.isEmpty(data.filepath)) {
                val imgUrl = getString(R.string.file_download_url)+data.filepath
                Picasso.get().load(imgUrl).error(R.drawable.no_pic).into(holder.imageView)
                holder.imageView!!.setOnClickListener {
                    val intent = Intent(this@ActivityAgricutureView, DisplayPictureActivity::class.java)
                    intent.putExtra(CONST.WEB_URL, imgUrl)
                    startActivity(intent)
                }
            } else {
                holder.imageView!!.setImageResource(R.drawable.no_pic)
            }

            if (data.stationname != null) {
                holder.tvStationName!!.text = data.stationname
            }
            if (data.datatime != null) {
                holder.tvTime!!.text = data.datatime
            }
            return view!!
        }
    }

    private fun okHttpList() {
        showProgressDialog()
        Thread {
            try {
                val param = JSONObject()
                param.put("token", MyApplication.TOKEN)
                val info = JSONObject()
                info.put("stationId", "A2201")
                param.put("paramInfo", info)
                val json = param.toString()
                Log.e("agmepic_list", json)
                val url = CONST.BASE_URL + "agmepic_list"
                Log.e("agmepic_list", url)
                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val body = RequestBody.create(mediaType, json)
                OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            return
                        }
                        val result = response.body!!.string()
                        runOnUiThread {
                            dismissProgressDialog()
                            Log.e("agmepic_list", result)
                            if (!TextUtils.isEmpty(result)) {
                                try {
                                    dataList.clear()
                                    val array = JSONArray(result)
                                    for (i in 0 until array.length()) {
                                        val dto = AgriDto()
                                        val itemObj = array.getJSONObject(i)
                                        if (!itemObj.isNull("code_id")) {
                                            dto.stationid = itemObj.getString("code_id")
                                        }
                                        if (!itemObj.isNull("station_name")) {
                                            dto.stationname = itemObj.getString("station_name")
                                        }
                                        if (!itemObj.isNull("filepath")) {
                                            dto.filepath = itemObj.getString("filepath")
                                        }
                                        if (!itemObj.isNull("datatime")) {
                                            dto.datatime = itemObj.getString("datatime")
                                        }
                                        dataList.add(dto)
                                    }
                                    if (mAdapter != null) {
                                        mAdapter!!.notifyDataSetChanged()
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                })
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }.start()
    }

}