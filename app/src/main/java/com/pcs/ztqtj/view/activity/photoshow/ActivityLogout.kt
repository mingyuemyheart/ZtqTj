package com.pcs.ztqtj.view.activity.photoshow

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.activity_logout.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

/**
 * 注销账号
 */
class ActivityLogout : FragmentActivityZtqBase(), OnClickListener {

    private var seconds = 60
    private var timer : Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)
        initWidget()
    }

    private fun initWidget() {
        titleText = "注销账号"
        layout_main.setOnClickListener(this)
        ivClearPwd.setOnClickListener(this)
        tvSend.setOnClickListener(this)
        tvLogin.setOnClickListener(this)

        tvPhone.text = MyApplication.MOBILE
    }

    /**
     * 获取验证码
     */
    private fun okHttpCode() {
        Thread {
            val param = JSONObject()
            param.put("phonenumber", tvPhone.text.toString())
            val json = param.toString()
            val url = CONST.BASE_URL + "user/phonelogin"
            Log.e("phonelogin", url)
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = RequestBody.create(mediaType, json)
            OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        resetTimer()
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
                                        Toast.makeText(this@ActivityLogout, obj.getString("errorMessage"), Toast.LENGTH_SHORT).show()
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
        if (TextUtils.isEmpty(etPwd.text.toString())) {
            Toast.makeText(this, "请输入手机验证码", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * 注销账号
     */
    private fun okHttpLogout() {
        if (!checkInfo()) {
            return
        }
        showProgressDialog()
        Thread {
            val param = JSONObject()
            param.put("phonenumber", tvPhone.text.toString())
            param.put("content", etPwd.text.toString())
            param.put("userid", MyApplication.UID)
            val json = param.toString()
            Log.e("userlogout", json)
            val url = CONST.BASE_URL + "user/userlogout"
            Log.e("userlogout", url)
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = RequestBody.create(mediaType, json)
            OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@ActivityLogout, "注销失败", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    Log.e("userlogout", result)
                    runOnUiThread {
                        dismissProgressDialog()
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                val obj = JSONObject(result)

                                if (!obj.isNull("result")) {
                                    val status = obj.getBoolean("result")
                                    if (status) {
                                        Toast.makeText(this@ActivityLogout, "注销成功", Toast.LENGTH_SHORT).show()
                                        resetTimer()
                                        setResult(RESULT_OK)
                                        finish()
                                    } else {//发送验证码失败
                                        if (!obj.isNull("errorMessage")) {
                                            resetTimer()
                                            Toast.makeText(this@ActivityLogout, obj.getString("errorMessage"), Toast.LENGTH_SHORT).show()
                                        }
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
            R.id.ivClearPwd -> etPwd.setText("")
            R.id.tvSend -> {
                if (timer == null) {
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
                okHttpLogout()
            }
        }
    }

}
