package com.pcs.ztqtj.view.activity.prove

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.pcs.ztqtj.R
import com.pcs.ztqtj.control.tool.utils.TextUtil
import com.pcs.ztqtj.util.CommonUtil
import com.pcs.ztqtj.util.OkHttpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.floor

/**
 * 气象证明
 */
class WeatherProveAdapter(private val context: Context?, private val mArrayList: ArrayList<ProveDto>?) : BaseAdapter(){

	private val mInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
	private val mUIHandler: Handler = Handler()

	class ViewHolder {
		var tvContactsName: TextView? = null
		var tvType: TextView? = null
		var tvPolicyNumber: TextView? = null
		var tvCreateTime: TextView? = null
		var tvStatus: TextView? = null
		var tvOption: TextView? = null
	}

	override fun getCount(): Int {
		return mArrayList!!.size
	}

	override fun getItem(position: Int): Any? {
		return position
	}

	override fun getItemId(position: Int): Long {
		return position.toLong()
	}

	override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
		var convertView = view
		val mHolder: ViewHolder
		if (convertView == null) {
			convertView = mInflater!!.inflate(R.layout.adapter_weather_prove, null)
			mHolder = ViewHolder()
			mHolder.tvContactsName = convertView.findViewById(R.id.tvContactsName)
			mHolder.tvType = convertView.findViewById(R.id.tvType)
			mHolder.tvPolicyNumber = convertView.findViewById(R.id.tvPolicyNumber)
			mHolder.tvCreateTime = convertView.findViewById(R.id.tvCreateTime)
			mHolder.tvStatus = convertView.findViewById(R.id.tvStatus)
			mHolder.tvOption = convertView.findViewById(R.id.tvOption)
			convertView.tag = mHolder
		} else {
			mHolder = convertView.tag as ViewHolder
		}

		val dto = mArrayList!![position]
		if (dto.contactsName != null) {
			mHolder.tvContactsName!!.text = "姓名：${dto.contactsName}"
		}
		when(dto.type) {
			"1" -> {
				mHolder.tvType!!.text = "申请证明类型：保险理赔"
			}
			else -> {
				mHolder.tvType!!.text = "申请证明类型：其他专用"
			}
		}
		if (dto.policyNumber != null) {
			mHolder.tvPolicyNumber!!.text = "保单号：${dto.policyNumber}"
		}
		if (dto.createTime != null) {
			mHolder.tvCreateTime!!.text = dto.createTime
		}
		when(dto.status) {
			"0" -> {
				mHolder.tvStatus!!.text = "待审核"
				mHolder.tvOption!!.text = ""
				mHolder.tvOption!!.setBackgroundColor(ContextCompat.getColor(context!!, R.color.transparent))
			}
			"1" -> {
				mHolder.tvStatus!!.text = "制作中"
				mHolder.tvOption!!.text = ""
				mHolder.tvOption!!.setBackgroundColor(ContextCompat.getColor(context!!, R.color.transparent))
			}
			"2" -> {
				mHolder.tvStatus!!.text = "审核未通过"
				mHolder.tvStatus!!.setTextColor(Color.RED)
				if (dto.auditOpinion != null) {
					mHolder.tvOption!!.text = dto.auditOpinion
					mHolder.tvOption!!.setTextColor(Color.RED)
					mHolder.tvOption!!.setBackgroundColor(ContextCompat.getColor(context!!, R.color.transparent))
				}
			}
			"3" -> {
				mHolder.tvStatus!!.text = "已制作"
				mHolder.tvOption!!.text = "待推送"
				mHolder.tvOption!!.setBackgroundColor(ContextCompat.getColor(context!!, R.color.transparent))
			}
			"4" -> {
				mHolder.tvStatus!!.text = "已推送"
				if (TextUtils.isEmpty(dto.imgPath)) {
					mHolder.tvOption!!.text = ""
					mHolder.tvOption!!.setBackgroundColor(ContextCompat.getColor(context!!, R.color.transparent))
				} else {
					mHolder.tvOption!!.text = "下载"
					mHolder.tvOption!!.setBackgroundColor(ContextCompat.getColor(context!!, R.color.bg_title))
					mHolder.tvOption!!.setTextColor(Color.WHITE)
					mHolder.tvOption!!.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
					mHolder.tvOption!!.setPadding(20, 3, 20, 3)
					mHolder.tvOption!!.setOnClickListener {
						okHttpFile(context.getString(R.string.msyb)+"/"+dto.imgPath)
					}
				}
			}
		}

		return convertView
	}

	private fun okHttpFile(url: String) {
		var fileName = "1.png"
		if (url.contains("/")) {
			val names = url.split("/")
			fileName = names[names.size-1]
		}
		Thread {
			OkHttpUtil.enqueue(Request.Builder().url(url).build(), object : Callback {
				override fun onFailure(call: Call, e: IOException) {}

				@Throws(IOException::class)
				override fun onResponse(call: Call, response: Response) {
					if (!response.isSuccessful) {
						return
					}
					var inputStream: InputStream? = null
					var fos: FileOutputStream? = null
					try {
						inputStream = response.body!!.byteStream() //获取输入流
						val total = response.body!!.contentLength().toFloat() //获取文件大小
						if (inputStream != null) {
							val files = File("${context!!.getExternalFilesDir(null)}/TianjinWeather")
							if (!files.exists()) {
								files.mkdirs()
							}
							val filePath = "${files.absolutePath}/$fileName"
							fos = FileOutputStream(filePath)
							val buf = ByteArray(1024)
							var ch = -1
							var process = 0
							while (inputStream.read(buf).also { ch = it } != -1) {
								fos.write(buf, 0, ch)
								process += ch
								val percent = floor((process / total * 100).toDouble()).toInt()
								Log.e("percent", percent.toString())
								if (percent >= 100) {
									mUIHandler.post {
										Toast.makeText(context, "文件已保存至${filePath}", Toast.LENGTH_LONG).show()
										CommonUtil.notifyAlbum(context, File(filePath))
									}
								}
//								val msg = handler.obtainMessage(1001)
//								msg.what = 1001
//								msg.obj = filePath
//								msg.arg1 = percent
//								handler.sendMessage(msg)
							}
						}
						fos!!.flush()
						fos.close() // 下载完成
					} catch (e: Exception) {
						e.printStackTrace()
					} finally {
						inputStream?.close()
						fos?.close()
					}
				}
			})
		}.start()
	}

}
