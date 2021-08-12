package com.pcs.ztqtj.view.activity.photoshow

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import com.pcs.ztqtj.MyApplication
import com.pcs.ztqtj.R
import com.pcs.ztqtj.control.tool.CommUtils
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.OkHttpUtil
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase
import com.pcs.ztqtj.view.activity.user.ActivityUserInformation
import kotlinx.android.synthetic.main.activity_bind.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

/**
 * 绑定手机号
 */
class ActivityBind : FragmentActivityZtqBase(), OnClickListener {

    private var seconds = 60
    private var timer : Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind)
        initWidget()
    }

    private fun initWidget() {
        titleText = "绑定手机号"
        layout_main.setOnClickListener(this)
        ivClearPhone.setOnClickListener(this)
        ivClearPwd.setOnClickListener(this)
        tvSend.setOnClickListener(this)
        tvLogin.setOnClickListener(this)
    }

    /**
     * 获取验证码
     */
    private fun okHttpCode() {
        Thread {
            val param = JSONObject()
            param.put("phonenumber", etPhone.text.toString())
            val json = param.toString()
            val url = CONST.BASE_URL + "user/phonelogin"
            Log.e("phonelogin", url)
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = RequestBody.create(mediaType, json)
            OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        resetTimer()
                        Toast.makeText(this@ActivityBind, "登录失败，重新登录试试", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    runOnUiThread {
                        Log.e("phonelogin", result)
                        if (!TextUtils.isEmpty(result)) {
                            val obj = JSONObject(result)
                            if (!obj.isNull("result")) {
                                val status = obj.getBoolean("result")
                                if (status) {//成功发送验证码
                                    //发送验证码成功
                                    etPwd.isFocusable = true
                                    etPwd.isFocusableInTouchMode = true
                                    etPwd.requestFocus()
                                } else {//发送验证码失败
                                    if (!obj.isNull("errorMessage")) {
                                        resetTimer()
                                        Toast.makeText(this@ActivityBind, obj.getString("errorMessage"), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            })
        }.start()
    }

    @SuppressLint("HandlerLeak")
    private val handler : Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when(msg!!.what) {
                101 -> {
                    if (seconds <= 0) {
                        resetTimer()
                    } else {
                        tvSend.text = seconds--.toString() + "s"
                    }
                }
            }
        }
    }

    private fun checkInfo(): Boolean {
        if (TextUtils.isEmpty(etPhone.text.toString())) {
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(etPwd.text.toString())) {
            Toast.makeText(this, "请输入手机验证码", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * 重置计时器
     */
    private fun resetTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
        seconds = 60
        tvSend.text = "获取验证码"
    }

    override fun onDestroy() {
        super.onDestroy()
        resetTimer()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.layout_main -> CommUtils.closeKeyboard(this, v)
            R.id.ivClearPhone -> etPhone.setText("")
            R.id.ivClearPwd -> etPwd.setText("")
            R.id.tvSend -> {
                if (timer == null) {
                    if (TextUtils.isEmpty(etPhone.text.toString())) {
                        Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show()
                        return
                    }
                    timer = Timer()
                    timer!!.schedule(object : TimerTask() {
                        override fun run() {
                            handler.sendEmptyMessage(101)
                        }
                    }, 0, 1000)
                    okHttpCode()
                }
            }
            R.id.tvLogin -> {
                if (checkInfo()) {
                    val intent = Intent()
                    intent.putExtra("phonenumber", etPhone.text.toString())
                    intent.putExtra("content", etPwd.text.toString())
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

}
