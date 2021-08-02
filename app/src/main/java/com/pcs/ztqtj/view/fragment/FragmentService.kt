package com.pcs.ztqtj.view.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser
import com.pcs.ztqtj.MyApplication
import com.pcs.ztqtj.R
import com.pcs.ztqtj.model.ZtqCityDB
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.ColumnDto
import com.pcs.ztqtj.util.CommonUtil
import com.pcs.ztqtj.view.activity.ActivityMain
import com.pcs.ztqtj.view.activity.help.ActivityHelp
import com.pcs.ztqtj.view.activity.photoshow.ActivityLogin
import com.pcs.ztqtj.view.activity.service.ActivityMyServer
import com.pcs.ztqtj.view.activity.service.ActivityServerHyqx
import com.pcs.ztqtj.view.activity.service.ActivityServerSecond
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_service.*

/**
 * 专项服务
 */
class FragmentService : BaseFragment(), OnClickListener {

    private var localUserinfo: PackLocalUser? = null
    private var mImageFetcher: ImageFetcher? = null
    private var areaId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_service, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = activity as ActivityMain?
        mImageFetcher = activity!!.imageFetcher
        initView()
    }

    private fun initView() {
        imageghelp.setOnClickListener(this)
        btnLogin.setOnClickListener(this)

        reflashUserData()
        if (!TextUtils.isEmpty(localUserinfo!!.user_id)) {
            btnLogin!!.text = "退出"
        } else {
            btnLogin!!.text = "登录"
        }

        val data: ColumnDto = arguments!!.getParcelable("data")
        if (data != null) {
            for (i in 0 until data.childList.size) {
                val dto = data.childList[i]
                when(dto.dataCode) {
                    "101030301" -> {
                        btnJuece.text = dto.dataName
                        if (CommonUtil.isCanAccess(dto.flag)) {
                            btnJuece.setOnClickListener {
                                if (CommonUtil.isCanAccess(dto.flag)) {
                                    if (localUserinfo == null || TextUtils.isEmpty(localUserinfo!!.user_id)) {
                                        gotoLogin()
                                    } else {
                                        gotoService()
                                    }
                                }
                            }
                        } else {
                            btnJuece.setBackgroundResource(R.drawable.corner_server_gray)
                            btnJuece.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                        }
                    }
                    "101030302" -> {
                        btnHy.text = dto.dataName
                        if (CommonUtil.isCanAccess(dto.flag)) {
                            btnHy.setOnClickListener {
                                if (CommonUtil.isCanAccess(dto.flag)) {
                                    val intent = Intent(activity, ActivityServerHyqx::class.java)
                                    intent.putExtra("title", dto.dataName)
                                    val bundle = Bundle()
                                    bundle.putParcelableArrayList("dataList", dto.childList)
                                    intent.putExtras(bundle)
                                    startActivity(intent)
                                }
                            }
                        } else {
                            btnHy.setBackgroundResource(R.drawable.corner_server_gray)
                            btnHy.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                        }
                    }
                    "101030303" -> {
                        intentDetail(ivQxt, tvQxt, dto)
                    }
                    "101030304" -> {
                        intentDetail(ivBgs, tvBgs, dto)
                    }
                    "101030305" -> {
                        intentDetail(ivBhxq, tvBhxq, dto)
                    }
                    "101030306" -> {
                        intentDetail(ivDlq, tvDlq, dto)
                    }
                    "101030307" -> {
                        intentDetail(ivXqq, tvXqq, dto)
                    }
                    "101030308" -> {
                        intentDetail(ivJnq, tvJnq, dto)
                    }
                    "101030309" -> {
                        intentDetail(ivBcq, tvBcq, dto)
                    }
                    "101030310" -> {
                        intentDetail(ivWqq, tvWqq, dto)
                    }
                    "101030311" -> {
                        intentDetail(ivBdq, tvBdq, dto)
                    }
                    "101030312" -> {
                        intentDetail(ivJhq, tvJhq, dto)
                    }
                    "101030313" -> {
                        intentDetail(ivNhq, tvNhq, dto)
                    }
                    "101030314" -> {
                        intentDetail(ivJzq, tvJzq, dto)
                    }
                }
            }
        }
    }

    private fun intentDetail(imageView: ImageView, textView: TextView, data: ColumnDto) {
        if (!TextUtils.isEmpty(data.icon)) {
            val imgUrl = getString(R.string.msyb)+data.icon
            Picasso.get().load(imgUrl).error(R.drawable.no_pic).into(imageView)
        } else {
            imageView.setBackgroundResource(R.drawable.no_pic)
            textView.setTextColor(Color.BLACK)
        }
        if (data.dataName != null) {
            textView.text = data.dataName
        }
        imageView.setOnClickListener {
            if (CommonUtil.isCanAccess(data.flag)) {
                toServiceActivity(data.dataCode)
            }
        }
    }

    private fun gotoLogin() {
        val intent = Intent(activity, ActivityLogin::class.java)
        startActivityForResult(intent, CONST.RESULT_LOGIN)
    }

    private fun logout() {
        ZtqCityDB.getInstance().removeMyInfo()
        MyApplication.clearUserInfo(activity)
        //刷新栏目数据
        val bdIntent = Intent()
        bdIntent.action = CONST.BROADCAST_REFRESH_COLUMNN
        activity!!.sendBroadcast(bdIntent)

        reflashUserData()
        localUserinfo!!.user_id = ""
        reflashUserData()
    }

    private fun reflashUserData() {
        localUserinfo = ZtqCityDB.getInstance().myInfo
    }

    private fun toServiceActivity(areaid: String?) {
        val intent = Intent(activity, ActivityServerSecond::class.java)
        intent.putExtra("area_id", areaid)
        startActivity(intent)
    }

    private fun gotoService() {
        val intent = Intent(activity, ActivityMyServer::class.java)
        intent.putExtra("channel", "")
        intent.putExtra("title", "我的服务")
        intent.putExtra("subtitle", "1")
        startActivity(intent)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.imageghelp -> startActivity(Intent(activity, ActivityHelp::class.java))
            R.id.btnLogin -> {
                if (btnLogin!!.text.toString() == "登录") {
                    gotoLogin()
                } else {
                    logout()
                    btnLogin!!.text = "登录"
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CONST.RESULT_LOGIN -> {
                    localUserinfo = ZtqCityDB.getInstance().myInfo
                    if (!TextUtils.isEmpty(localUserinfo!!.user_id)) {
                        if (TextUtils.isEmpty(areaId)) {
                            gotoService()
                        } else {
                            toServiceActivity(areaId)
                        }
                        btnLogin!!.text = "退出"
                    }
                }
            }
        }
    }

}