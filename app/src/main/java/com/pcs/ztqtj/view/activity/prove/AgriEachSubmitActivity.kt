package com.pcs.ztqtj.view.activity.prove

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.pcs.ztqtj.MyApplication
import com.pcs.ztqtj.R
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.OkHttpUtil
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase
import kotlinx.android.synthetic.main.activity_agri_each_submit.*
import kotlinx.android.synthetic.main.fragment_agri_each.*
import kotlinx.android.synthetic.main.layout_date.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * 苗情农情互动提交
 */
class AgriEachSubmitActivity: FragmentActivityZtqBase(), View.OnClickListener {

    private var disAdapter: ShowPictureAdapter? = null
    private val disList: ArrayList<ProveDto> = ArrayList()
    private var disPics = ""
    private val maxCount2 = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agri_each_submit)
        initWidget()
    }

    private fun initWidget() {
        if (intent.hasExtra("title")) {
            val title = intent.getStringExtra("title")
            if (title != null) {
                titleText = title
            }
        }
        tvSubmit.setOnClickListener(this)

        initGridViewDis()
    }

    /**
     * 添加图片按钮
     */
    private fun initAddPicButton(list: ArrayList<ProveDto>, maxCount: Int) {
        for (i in 0 until list.size) {
            if (TextUtils.isEmpty(list[i].imgUrl)) {
                list.removeAt(i)
            }
        }
        if (list.size < maxCount-1) {
            val dto = ProveDto()
            dto.drawable = R.drawable.icon_add_pic
            dto.imgUrl = ""
            list.add(dto)
        }
    }

    /**
     * 受灾照片
     */
    private fun initGridViewDis() {
        initAddPicButton(disList, maxCount2)
        disAdapter = ShowPictureAdapter(this, disList)
        gridViewDis.adapter = disAdapter
        gridViewDis.setOnItemClickListener { parent, view, position, id ->
            val data = disList[position]
            if (TextUtils.isEmpty(data.imgUrl)) {//点击了添加按钮
                val intent = Intent(this, SelectPictureActivity::class.java)
                intent.putExtra("count", disList.size)
                intent.putExtra("maxCount", maxCount2)
                startActivityForResult(intent, 1003)
            } else {//预览
                val intent = Intent(this, DisplayPictureActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("data", data)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

    /**
     * 退出对话框
     */
    private fun dialogExit(context: Context, title: String?, content: String?) {
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_exit, null)
        val tipTitle = view.findViewById<TextView>(R.id.tipTitle)
        val tipContent = view.findViewById<TextView>(R.id.tipContent)
        val tvCancel = view.findViewById<TextView>(R.id.tvCancel)
        val tvSure = view.findViewById<TextView>(R.id.tvSure)
        if (TextUtils.isEmpty(title)) {
            tipTitle.text = "提示"
        } else {
            tipTitle.text = title
        }
        tipContent.text = content
        val dialog = Dialog(context, R.style.CustomProgressDialog)
        dialog.setContentView(view)
        dialog.show()
        tvCancel.setOnClickListener { dialog.dismiss() }
        tvSure.setOnClickListener {
            dialog.dismiss()
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialogExit(this, "提示", "确定退出当前编辑页面？")
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when(requestCode) {
                1003 -> {
                    if (data != null) {
                        val bundle = data.extras
                        if (bundle != null) {
                            val list: ArrayList<ProveDto> = bundle.getParcelableArrayList("dataList")
                            for (i in list.size-1 downTo 0) {
                                disList.add(0, list[i])
                            }
                            initAddPicButton(disList, maxCount2)
                            if (disAdapter != null) {
                                disAdapter!!.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.tvSubmit -> {
                okHttpPostFiles()
            }
        }
    }

    /**
     * 校验信息
     */
    private fun checkPost() : Boolean {
        if (TextUtils.isEmpty(etDesc.text.toString())) {
            Toast.makeText(this, "请简述问题...", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * 上传图片
     */
    private fun okHttpPostFiles() {
        if (!checkPost()) {
            return
        }
        showProgressDialog()
        val url = "${CONST.BASE_URL}feed_back/upload"
        val builder = MultipartBody.Builder()
        builder.addFormDataPart("token", MyApplication.TOKEN)
        for (i in 0 until disList.size) {
            val data = disList[i]
            if (!TextUtils.isEmpty(data.imgUrl)) {
                val file = File(data.imgUrl)
                if (file.exists()) {
                    builder.addFormDataPart("files", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
                }
            }
        }
        val body: RequestBody = builder.build()
        Thread {
            OkHttpUtil.enqueue(Request.Builder().post(body).url(url).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        dismissProgressDialog()
                        Toast.makeText(this@AgriEachSubmitActivity, e.message, Toast.LENGTH_SHORT).show()
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
                        Log.e("submit-file", result)
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                val obj = JSONObject(result)
                                if (!obj.isNull("result")) {
                                    val array = obj.getJSONArray("result")
                                    for (i in 0 until array.length()) {
                                        val pic = array.get(i).toString()
                                        disPics += if (i == array.length()-1) {
                                            pic
                                        } else {
                                            "$pic|"
                                        }
                                    }
                                    okHttpPostContent()
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

    /**
     * 报送内容
     */
    private fun okHttpPostContent() {
        if (!checkPost()) {
            return
        }
        showProgressDialog()
        val url = "${CONST.BASE_URL}feed_back/save"
        val param  = JSONObject()
        param.put("token",MyApplication.TOKEN)
        val info = JSONObject()
        info.put("userId", MyApplication.UID)
        info.put("type", "2")//type：1（意见反馈），2（苗情速报）
        info.put("content", etDesc.text.toString())
        info.put("picture", disPics)
        param.put("paramInfo", info)
        val json : String = param.toString()
        Log.e("submit-content", json)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType, json)
        Thread {
            OkHttpUtil.enqueue(Request.Builder().post(body).url(url).addHeader("Authorization", MyApplication.TOKEN).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@AgriEachSubmitActivity, e.message, Toast.LENGTH_SHORT).show()
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
                                        Toast.makeText(this@AgriEachSubmitActivity, "提交成功！", Toast.LENGTH_SHORT).show()

                                        //刷新气象证明广播
                                        val i = Intent()
                                        i.action = CONST.BROADCAST_REFRESH_EACH
                                        sendBroadcast(i)

                                        setResult(RESULT_OK)
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