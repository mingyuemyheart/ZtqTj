package com.pcs.ztqtj.view.activity.photoshow

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.pcs.ztqtj.MyApplication
import com.pcs.ztqtj.R
import com.pcs.ztqtj.control.tool.CommUtils
import com.pcs.ztqtj.control.tool.MyConfigure
import com.pcs.ztqtj.model.ZtqCityDB
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.OkHttpUtil
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase
import com.pcs.ztqtj.view.activity.web.webview.ActivityWebView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_user_login.*
import kotlinx.android.synthetic.main.activity_user_login.checkbox
import kotlinx.android.synthetic.main.activity_user_login.etPhone
import kotlinx.android.synthetic.main.activity_user_login.etPwd
import kotlinx.android.synthetic.main.activity_user_login.ivClearPhone
import kotlinx.android.synthetic.main.activity_user_login.ivClearPwd
import kotlinx.android.synthetic.main.activity_user_login.layout_main
import kotlinx.android.synthetic.main.activity_user_login.tvLogin
import kotlinx.android.synthetic.main.activity_user_login.tvPrivacy
import kotlinx.android.synthetic.main.activity_user_login.tvProtocal
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

/**
 * 账号密码登录界面
 */
class ActivityUserLogin : FragmentActivityZtqBase(), OnClickListener {

	private var isRead = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_user_login)
		initWidget()
	}

	/**
	 * 初始化控件
	 */
	private fun initWidget() {
		titleText = "账号密码登录"
		layout_main.setOnClickListener(this)
		ivClearPhone.setOnClickListener(this)
		ivClearPwd.setOnClickListener(this)
		tvProtocal.setOnClickListener(this)
		tvPrivacy.setOnClickListener(this)
		checkbox.setOnClickListener(this)
		tvLogin.setOnClickListener(this)
		tvModifyPwd.setOnClickListener(this)
		tvFindPwd.setOnClickListener(this)
	}

	/**
	 * 点击注册按钮
	 */
	private fun clickRegister() {
		val intent = Intent(this, ActivityRegister::class.java)
		val bundle = Bundle()
		bundle.putString("register_type", "0")
		bundle.putString("title", "注册")
		intent.putExtras(bundle)
		startActivityForResult(intent, MyConfigure.RESULT_USER_REGISTER)
	}

	override fun onClick(v: View?) {
		when(v!!.id) {
			R.id.layout_main -> CommUtils.closeKeyboard(this, v)
			R.id.ivClearPhone -> etPhone.setText("")
			R.id.ivClearPwd -> etPwd.setText("")
			R.id.tvProtocal -> {
				val intent = Intent(this, ActivityWebView::class.java)
				intent.putExtra("title", "天津气象软件许可及服务协议")
				intent.putExtra("url", CONST.PROTOCAL)
				intent.putExtra("shareContent", "天津气象软件许可及服务协议")
				startActivity(intent)
			}
			R.id.tvPrivacy -> {
				val intent = Intent(this, ActivityWebView::class.java)
				intent.putExtra("title", "天津气象软件用户隐私政策")
				intent.putExtra("url", CONST.PRIVACY)
				intent.putExtra("shareContent", "天津气象软件用户隐私政策")
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
			R.id.tvLogin -> okHttpLogin()
			R.id.tvModifyPwd -> {
				val intent = Intent(this, ActivityPhotoPasswordManager::class.java)
				intent.putExtra("type", 1)// 1:修改密码 2:找回密码
				startActivity(intent)
			}
			R.id.tvFindPwd -> {
				val intent = Intent(this, ActivityPhotoPasswordManager::class.java)
				intent.putExtra("type", 2)// 1:修改密码 2:找回密码
				startActivity(intent)
			}
		}
	}

	/**
	 * 用户登录
	 */
	private fun okHttpLogin() {
		if (TextUtils.isEmpty(etPhone.text.toString())) {
			Toast.makeText(this, "请输入已注册的手机号码或账号", Toast.LENGTH_SHORT).show()
			return
		}
		if (TextUtils.isEmpty(etPwd.text.toString())) {
			Toast.makeText(this, "请输入您的密码", Toast.LENGTH_SHORT).show()
			return
		}
		if (!isRead) {
			Toast.makeText(this, "请阅读并同意用户协议和隐私政策", Toast.LENGTH_SHORT).show()
			return
		}
		showProgressDialog()
		Thread {
			try {
				val param = JSONObject()
				param.put("loginName", etPhone.text.toString())
				param.put("pwd", etPwd.text.toString())
				val json = param.toString()
				Log.e("login", json)
				val url = CONST.BASE_URL + "user/login"
				Log.e("login", url)
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
							Log.e("login", result)
							if (!TextUtils.isEmpty(result)) {
								try {
									val obj = JSONObject(result)
									if (!obj.isNull("token")) {
										MyApplication.TOKEN = obj.getString("token")
										Log.e("token", MyApplication.TOKEN)
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
										MyApplication.saveUserInfo(this@ActivityUserLogin)

										//刷新栏目数据
										val bdIntent = Intent()
										bdIntent.action = CONST.BROADCAST_REFRESH_COLUMNN
										sendBroadcast(bdIntent)
										Toast.makeText(this@ActivityUserLogin, getString(R.string.login_succ), Toast.LENGTH_SHORT).show()
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
			} catch (e: JSONException) {
				e.printStackTrace()
			}
		}.start()
	}
	
}
