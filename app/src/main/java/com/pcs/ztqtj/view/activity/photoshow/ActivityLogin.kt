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
import com.pcs.ztqtj.control.tool.AppTool
import com.pcs.ztqtj.control.tool.CommUtils
import com.pcs.ztqtj.control.tool.youmeng.LoginAnther
import com.pcs.ztqtj.control.tool.youmeng.ToolQQPlatform
import com.pcs.ztqtj.model.ZtqCityDB
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.OkHttpUtil
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase
import com.pcs.ztqtj.view.activity.web.webview.ActivityWebView
import com.tencent.tauth.IUiListener
import com.tencent.tauth.UiError
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

/**
 * 登录
 */
class ActivityLogin : FragmentActivityZtqBase(), OnClickListener {

    private var seconds = 60
    private var timer : Timer? = null
    private var isRead = false

    private var loginAnther: LoginAnther? = null
    private var toolQQLogin: ToolQQPlatform? = null
    private var currentPathFrom: SHARE_MEDIA? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initWidget()
    }

    private fun initWidget() {
        titleText = "登录"
        layout_main.setOnClickListener(this)
        ivClearPhone.setOnClickListener(this)
        ivClearPwd.setOnClickListener(this)
        checkbox.setOnClickListener(this)
        tvProtocal.setOnClickListener(this)
        tvPrivacy.setOnClickListener(this)
        tvSend.setOnClickListener(this)
        tvLogin.setOnClickListener(this)
        tvUserLogin.setOnClickListener(this)
        ivWX.setOnClickListener(this)
        ivQQ.setOnClickListener(this)

        loginAnther = LoginAnther(this)
        toolQQLogin = ToolQQPlatform(this, object : IUiListener {
            override fun onComplete(response: Any?) {
                dismissProgressDialog()
                val json = response as JSONObject
                loginWeServer(toolQQLogin!!.openId, json.optString("nickname"), json.optString("figureurl_qq_2"), "2")
            }

            override fun onError(p0: UiError?) {
                showToast("获取信息失败")
                dismissProgressDialog()
            }

            override fun onCancel() {
                showToast("取消获取信息")
                dismissProgressDialog()
            }
        })
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
                        Toast.makeText(this@ActivityLogin, "登录失败，重新登录试试", Toast.LENGTH_SHORT).show()
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
                                        Toast.makeText(this@ActivityLogin, obj.getString("errorMessage"), Toast.LENGTH_SHORT).show()
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
        if (!isRead) {
            Toast.makeText(this, "请阅读并同意用户协议和隐私政策", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * 登录接口
     */
    private fun okHttpLogin(json: String, dataUrl: String) {
        showProgressDialog()
        Thread {
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = RequestBody.create(mediaType, json)
            OkHttpUtil.enqueue(Request.Builder().url(dataUrl).post(body).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@ActivityLogin, "登录失败", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    runOnUiThread {
                        dismissProgressDialog()
                        Log.e("okHttpLogin", result)
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
                                    MyApplication.saveUserInfo(this@ActivityLogin)

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
                                    Toast.makeText(this@ActivityLogin, getString(R.string.login_succ), Toast.LENGTH_SHORT).show()
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
                val intent = Intent(this@ActivityLogin, ActivityWebView::class.java)
                intent.putExtra("title", "天津惠民软件许可及服务协议")
                intent.putExtra("url", CONST.PROTOCAL)
                intent.putExtra("shareContent", "天津惠民软件许可及服务协议")
                startActivity(intent)
            }
            R.id.tvPrivacy -> {
                val intent = Intent(this@ActivityLogin, ActivityWebView::class.java)
                intent.putExtra("title", "天津惠民软件用户隐私政策")
                intent.putExtra("url", CONST.PRIVACY)
                intent.putExtra("shareContent", "天津惠民软件用户隐私政策")
                startActivity(intent)
            }
            R.id.checkbox -> {
                isRead = !isRead
                if (isRead) {
                    checkbox.setImageResource(R.drawable.bg_checkbox_selected)
                } else {
                    checkbox.setImageResource(R.drawable.bg_checkbox)
                }
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
            R.id.tvLogin -> {
                if (checkInfo()) {
                    val param = JSONObject()
                    param.put("phonenumber", etPhone.text.toString())
                    param.put("content", etPwd.text.toString())
                    param.put("userid", "")
                    param.put("mobileType", "1")//1是android、2是iOS
                    val json = param.toString()
                    Log.e("loginVerify", json)
                    val url = CONST.BASE_URL + "user/loginVerify"
                    Log.e("loginVerify", url)
                    okHttpLogin(json, url)
                }
            }
            R.id.tvUserLogin -> startActivityForResult(Intent(this, UserLoginActivity::class.java), 1001)
            R.id.ivWX -> clickLoginOtherWay(SHARE_MEDIA.WEIXIN)
            R.id.ivQQ -> clickLoginOtherWay(SHARE_MEDIA.QQ)
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

    /**
     * 点击第三方登录按钮
     * @param share
     */
    private fun clickLoginOtherWay(share: SHARE_MEDIA) {
        currentPathFrom = share
        if (share == SHARE_MEDIA.WEIXIN) {
            if (AppTool.isInstalled(this@ActivityLogin, "com.tencent.mm")) {
                loginAnther!!.loginPermission(this@ActivityLogin, currentPathFrom, permission)
            } else {
                showToast("请先安装微信客户端！")
            }
        } else if (share == SHARE_MEDIA.QQ) {
            toolQQLogin!!.login()
        } else {
            loginAnther!!.loginPermission(this@ActivityLogin, currentPathFrom, permission)
        }
    }

    private val permission: UMAuthListener = object : UMAuthListener {
        override fun onStart(share_media: SHARE_MEDIA) {}
        override fun onComplete(share_media: SHARE_MEDIA, i: Int, map: Map<String, String>) {
//            Toast.makeText(this@ActivityLogin, "获取授权完成", Toast.LENGTH_SHORT).show()
            loginAnther!!.getInfo(this@ActivityLogin, share_media, umListener)
        }

        override fun onError(share_media: SHARE_MEDIA, i: Int, throwable: Throwable) {
            Toast.makeText(this@ActivityLogin, "授权错误", Toast.LENGTH_SHORT).show()
        }

        override fun onCancel(share_media: SHARE_MEDIA, i: Int) {
            Toast.makeText(this@ActivityLogin, "授权错误取消授权", Toast.LENGTH_SHORT).show()
        }
    }

    private val umListener: UMAuthListener = object : UMAuthListener {
        override fun onStart(share_media: SHARE_MEDIA) {}
        override fun onComplete(share_media: SHARE_MEDIA, i: Int, map: Map<String, String>) {
//            Toast.makeText(this@ActivityLogin, "获取数据完成", Toast.LENGTH_SHORT).show()
            logInfo(map)
            if (currentPathFrom == SHARE_MEDIA.QQ) {
                getQQInfoSuccess(map)
            } else if (currentPathFrom == SHARE_MEDIA.WEIXIN) {
                getWeiXinInfoSuccess(map)
            }
        }

        override fun onError(share_media: SHARE_MEDIA, i: Int, throwable: Throwable) {
            showToast("获取平台数据出错")
        }

        override fun onCancel(share_media: SHARE_MEDIA, i: Int) {
            showToast("取消获取数据")
        }

        /**
         * 打印返回值
         * @param info
         */
        private fun logInfo(info: Map<String, String>) {
            val sb = StringBuilder()
            val keys = info.keys
            for (key in keys) {
                sb.append(key + "=" + info[key] + "  ")
            }
            Log.e("logInfo", "获取信息完成：$sb")
        }
    }

    /**
     * 获取微信信息成功
     */
    private fun getWeiXinInfoSuccess(info: Map<String, String>) {
        try {
            loginWeServer(info["unionid"], info["name"], info["iconurl"], "3")
        } catch (e: Exception) {
            showToast("数据错误，请用手机号登录")
        }
    }

    /**
     * 腾讯获取数据成功
     * @param info
     */
    private fun getQQInfoSuccess(info: Map<String, String>) {
        try {
            loginWeServer(info["openid"], info["screen_name"], info["profile_image_url"], "2")
        } catch (e: Exception) {
            showToast("数据错误，请用手机号登录")
        }
    }

    /**
     * 向我们的服务器提交数据 platForm: 1为新浪，2为qq，3为微信
     */
    private fun loginWeServer(userId: String?, userName: String?, headUrl: String?, platForm: String) {
        Log.e("loginWeServer", "$userId---$userName---$headUrl---$platForm")
        val param = JSONObject()
        param.put("userid", userId)
        param.put("nick", userName)
        param.put("iconurl", headUrl)
        param.put("type", "1")//1:微信，2:苹果，3:qq
        val json = param.toString()
        Log.e("thirdlogin", json)
        val url = CONST.BASE_URL + "user/thirdlogin"
        Log.e("thirdlogin", url)
        okHttpLogin(json, url)
    }

}
