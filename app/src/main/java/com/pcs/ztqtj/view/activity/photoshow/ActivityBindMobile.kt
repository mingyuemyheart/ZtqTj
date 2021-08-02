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
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUserInfo
import com.pcs.ztqtj.MyApplication
import com.pcs.ztqtj.R
import com.pcs.ztqtj.control.tool.CommUtils
import com.pcs.ztqtj.model.ZtqCityDB
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.OkHttpUtil
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase
import com.pcs.ztqtj.view.activity.web.webview.ActivityWebView
import kotlinx.android.synthetic.main.activity_bind_mobile.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

/**
 * 绑定手机号
 */
class ActivityBindMobile : FragmentActivityZtqBase(), OnClickListener {

    private var seconds = 60
    private var timer : Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind_mobile)
        initWidget()
    }

    private fun initWidget() {
        titleText = "绑定手机号码"
        layout_main.setOnClickListener(this)
        ivClearPhone.setOnClickListener(this)
        ivClearPwd.setOnClickListener(this)
        tvProtocal.setOnClickListener(this)
        tvPrivacy.setOnClickListener(this)
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
                        Toast.makeText(this@ActivityBindMobile, "登录失败，重新登录试试", Toast.LENGTH_SHORT).show()
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
                                        Toast.makeText(this@ActivityBindMobile, obj.getString("errorMessage"), Toast.LENGTH_SHORT).show()
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

    /**
     * 登录接口
     */
    private fun okHttpLogin() {
        if (TextUtils.isEmpty(etPhone.text.toString())) {
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(etPwd.text.toString())) {
            Toast.makeText(this, "请输入手机验证码", Toast.LENGTH_SHORT).show()
            return
        }
        if (!checkbox.isChecked) {
            Toast.makeText(this, "请阅读并同意用户协议和隐私政策", Toast.LENGTH_SHORT).show()
            return
        }
        showProgressDialog()
        Thread {
            val param = JSONObject()
            param.put("phonenumber", etPhone.text.toString())
            param.put("content", etPwd.text.toString())
            param.put("userid", "")
            param.put("mobileType", "1")//1是android、2是iOS
            val json = param.toString()
            Log.e("loginVerify", json)
            val url = CONST.BASE_URL + "user/loginVerify"
            Log.e("loginVerify", url)
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = RequestBody.create(mediaType, json)
            OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@ActivityBindMobile, "登录失败", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    runOnUiThread {
                        dismissProgressDialog()
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                val obj = JSONObject(result)
                                if (!obj.isNull("token")) {
                                    MyApplication.TOKEN = obj.getString("token")
                                    Log.e("token", MyApplication.TOKEN)
                                }
                                if (!obj.isNull("limitInfo")) {
                                    MyApplication.LIMITINFO = obj.getString("limitInfo")
                                }
                                if (!obj.isNull("userInfo")) {
                                    val userInfo = obj.getJSONObject("userInfo")
                                    if (!userInfo.isNull("userId")) {
                                        MyApplication.UID = userInfo.getString("userId")
                                    }
                                    if (!userInfo.isNull("loginName")) {
                                        MyApplication.USERNAME = userInfo.getString("loginName")
                                    }
                                    if (!userInfo.isNull("password")) {
                                        MyApplication.PASSWORD = userInfo.getString("password")
                                    }
                                    if (!userInfo.isNull("userName")) {
                                        MyApplication.NAME = userInfo.getString("userName")
                                    }
                                    if (!userInfo.isNull("phonenumber")) {
                                        MyApplication.MOBILE = userInfo.getString("phonenumber")
                                    }
                                    if (!userInfo.isNull("avatar")) {
                                        MyApplication.PORTRAIT = userInfo.getString("avatar")
                                    }
                                    MyApplication.saveUserInfo(this@ActivityBindMobile)

                                    //存储用户数据
                                    val myUserInfo = PackLocalUser()
                                    myUserInfo.user_id = MyApplication.UID
                                    myUserInfo.sys_user_id = MyApplication.UID
                                    myUserInfo.sys_nick_name = MyApplication.NAME
                                    myUserInfo.sys_head_url = MyApplication.PORTRAIT
                                    myUserInfo.mobile = MyApplication.MOBILE
                                    val packLocalUserInfo = PackLocalUserInfo()
                                    packLocalUserInfo.currUserInfo = myUserInfo
                                    ZtqCityDB.getInstance().setMyInfo(packLocalUserInfo)

                                    //刷新栏目数据
                                    val bdIntent = Intent()
                                    bdIntent.action = CONST.BROADCAST_REFRESH_COLUMNN
                                    sendBroadcast(bdIntent)
                                    Toast.makeText(this@ActivityBindMobile, getString(R.string.login_succ), Toast.LENGTH_SHORT).show()
                                    resetTimer()
                                    setResult(RESULT_OK)
                                    finish()
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
            R.id.ivClearPhone -> etPhone.setText("")
            R.id.ivClearPwd -> etPwd.setText("")
            R.id.tvProtocal -> {
                val intent = Intent(this@ActivityBindMobile, ActivityWebView::class.java)
                intent.putExtra("title", "天津惠民软件许可及服务协议")
                intent.putExtra("url", CONST.PROTOCAL)
                intent.putExtra("shareContent", "天津惠民软件许可及服务协议")
                startActivity(intent)
            }
            R.id.tvPrivacy -> {
                val intent = Intent(this@ActivityBindMobile, ActivityWebView::class.java)
                intent.putExtra("title", "天津惠民软件用户隐私政策")
                intent.putExtra("url", CONST.PRIVACY)
                intent.putExtra("shareContent", "天津惠民软件用户隐私政策")
                startActivity(intent)
            }
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
            R.id.tvLogin -> okHttpLogin()
            R.id.tvUserLogin -> startActivityForResult(Intent(this, UserLoginActivity::class.java), 1001)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when(requestCode) {
                1001 -> {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

}
