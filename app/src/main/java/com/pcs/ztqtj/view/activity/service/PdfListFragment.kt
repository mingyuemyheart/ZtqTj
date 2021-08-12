package com.pcs.ztqtj.view.activity.service

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceMyProductDown
import com.pcs.lib_ztqfj_v2.model.pack.net.service.ServiceProductInfo
import com.pcs.ztqtj.MyApplication
import com.pcs.ztqtj.R
import com.pcs.ztqtj.control.adapter.AdapterPdfList
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil
import com.pcs.ztqtj.control.tool.utils.TextUtil
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.ColumnDto
import com.pcs.ztqtj.util.OkHttpUtil
import com.pcs.ztqtj.view.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_pdf_list.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * 专项服务-专项服务-gridview点击-pdf列表
 */
class PdfListFragment: BaseFragment() {

    private val dataList: ArrayList<ServiceProductInfo> = ArrayList()
    private var mAdapter: AdapterPdfList? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pdf_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGridView()
    }

    private fun initGridView() {
        val data: ColumnDto = arguments!!.getParcelable("data")
        if (data != null) {
            if (data.dataCode != null) {
                okHttpInfoList(data.dataCode)
            }
        }
        mAdapter = AdapterPdfList(activity, dataList)
        mylistviw.adapter = mAdapter
        mylistviw.setOnItemClickListener { parent, view, position, id ->
            val info: ServiceProductInfo = dataList[position]
            //存储点击的已读跟未读的状态
            SharedPreferencesUtil.putData(info.html_url, info.html_url)
            val intent = Intent(activity, ActivityServeDetails::class.java)
            intent.putExtra("title", data.dataName)
            intent.putExtra("url", getString(R.string.file_download_url) + info.html_url)
            intent.putExtra("style", info.style)
            startActivity(intent)
        }
    }

    /**
     * 获取详情数据
     */
    private fun okHttpInfoList(stationId: String) {
        progressBar.visibility = View.VISIBLE
        Thread {
            try {
                val param = JSONObject()
                param.put("token", MyApplication.TOKEN)
                val info = JSONObject()
                info.put("stationId", stationId)
                info.put("extra", "")
                param.put("paramInfo", info)
                val json = param.toString()
                Log.e("info_list", json)
                val url = CONST.BASE_URL + "info_list"
                Log.e("info_list", url)
                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val body = RequestBody.create(mediaType, json)
                OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            return
                        }
                        if (!isAdded) {
                            return
                        }
                        val result = response.body!!.string()
                        activity!!.runOnUiThread(Runnable {
                            Log.e("info_list", result)
                            progressBar.visibility = View.GONE
                            if (!TextUtil.isEmpty(result)) {
                                try {
                                    val obj = JSONObject(result)
                                    if (!obj.isNull("b")) {
                                        val bobj = obj.getJSONObject("b")
                                        if (!bobj.isNull("qxfw_product")) {
                                            val qxfw_product = bobj.getJSONObject("qxfw_product")
                                            if (!TextUtil.isEmpty(qxfw_product.toString())) {
                                                val packServiceMyProductDown = PackServiceMyProductDown()
                                                packServiceMyProductDown.fillData(qxfw_product.toString())
//                                                if (!TextUtils.isEmpty(packServiceMyProductDown.total_page)) {
//                                                    total_page = Integer.valueOf(packServiceMyProductDown.total_page)
//                                                }
//                                                if (!TextUtils.isEmpty(packServiceMyProductDown.total_count)) {
//                                                    total_count = Integer.valueOf(packServiceMyProductDown.total_count)
//                                                }
//                                                if (!TextUtils.isEmpty(packServiceMyProductDown.page_size)) {
//                                                    page_size = Integer.valueOf(packServiceMyProductDown.page_size)
//                                                }
//                                                if (!TextUtils.isEmpty(packServiceMyProductDown.page_num)) {
//                                                    page_num = Integer.valueOf(packServiceMyProductDown.page_num)
//                                                }

                                                dataList.clear()
                                                dataList.addAll(packServiceMyProductDown.myServiceProductList)
                                                if (mAdapter != null) {
                                                    mAdapter!!.notifyDataSetChanged()
                                                }
                                                if (packServiceMyProductDown.myServiceProductList.size > 0) {
                                                    tip_title_tv.visibility = View.GONE
//                                                    if (serviceProductList.size < page_size) {
//                                                        page_num = 1
//                                                    } else {
//                                                        page_num++
//                                                    }
//                                                    updateListData(serviceProductList)
                                                } else {
                                                    tip_title_tv.visibility = View.VISIBLE
                                                }
                                            }
                                        }
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        })
                    }
                })
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }.start()
    }

}