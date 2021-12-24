package com.pcs.ztqtj.view.activity.set;

import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.pcs.lib_ztqfj_v2.model.pack.net.SuggestListInfo
import com.pcs.ztqtj.MyApplication
import com.pcs.ztqtj.R
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.OkHttpUtil
import com.pcs.ztqtj.view.activity.set.abs_feed_tu.AbsActivityFeekTu
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * 设置-您的建议
 */
class AcitvityFeedBack : AbsActivityFeekTu() {

    override fun proInitData() {
        val bundle = intent.extras
        titleText = bundle.getString("title")
    }


    private var arrsuggestListInfo: ArrayList<SuggestListInfo> = ArrayList()

    override fun reqComment() {
        okHttpList()
    }

    private fun okHttpList() {
        showProgressDialog()
        Thread {
            val param = JSONObject()
            param.put("token", MyApplication.TOKEN)
            val info = JSONObject()
            info.put("type", "1")//type：1（意见反馈），2（苗情速报）
            param.put("paramInfo", info)
            val json: String = param.toString()
            Log.e("feedBackList", json)
            val url = "${CONST.BASE_URL}feed_back/feedBackList"
            Log.e("feedBackList", url)
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = RequestBody.create(mediaType, json)
            OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    runOnUiThread {
                        dismissProgressDialog()
                        Log.e("feedBackList", result)
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                val obj = JSONObject(result)
                                if (!obj.isNull("result")) {
                                    arrsuggestListInfo.clear()
                                    val data = obj.get("result")
                                    if (!TextUtils.isEmpty(data.toString())) {
                                        val array = obj.getJSONArray("result")
                                        for (i in 0 until array.length()) {
                                            val dto = SuggestListInfo()
                                            val itemObj = array.getJSONObject(i)
                                            if (!itemObj.isNull("createTime")) {
                                                dto.create_time = itemObj.getString("createTime")
                                            }
                                            if (!itemObj.isNull("content")) {
                                                dto.msg = itemObj.getString("content")
                                            }
                                            arrsuggestListInfo.add(dto)
                                        }
                                        reflushListView(arrsuggestListInfo)
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            })
        }.start()
    }

    override fun receiverBack(nameStr: String?, errorStr: String?) {

    }

    override fun commitInformation(upContent: String?, phoneNumber: String?) {
        okHttpPostContent(upContent)
    }

    /**
     * 意见反馈
     */
    private fun okHttpPostContent(content: String?) {
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, getString(R.string.feedback_eidtdesc), Toast.LENGTH_SHORT).show()
            return
        }
        showProgressDialog()
        val url = "${CONST.BASE_URL}feed_back/save"
        val param  = JSONObject()
        param.put("token",MyApplication.TOKEN)
        val info = JSONObject()
        info.put("userId", MyApplication.UID)
        info.put("type", "1")//type：1（意见反馈），2（苗情速报）
        info.put("content", content)
        param.put("paramInfo", info)
        val json : String = param.toString()
        Log.e("submit-content", json)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType, json)
        Thread {
            OkHttpUtil.enqueue(Request.Builder().post(body).url(url).addHeader("Authorization", MyApplication.TOKEN).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@AcitvityFeedBack, e.message, Toast.LENGTH_SHORT).show()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    runOnUiThread {
                        dismissProgressDialog()
                        Log.e("submit-content", result)
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                val obj = JSONObject(result)
                                if (!obj.isNull("status")) {
                                    val status = obj.getString("status")
                                    if (TextUtils.equals(status, "success")) {
                                        Toast.makeText(this@AcitvityFeedBack, "提交成功！", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            })
        }.start()
    }

}
