package com.pcs.ztqtj.view.fragment

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import com.pcs.ztqtj.R
import com.pcs.ztqtj.control.adapter.AdapterLifeFragment
import com.pcs.ztqtj.util.ColumnDto
import com.pcs.ztqtj.util.CommonUtil
import com.pcs.ztqtj.view.activity.life.ActivityChannelList
import com.pcs.ztqtj.view.activity.life.ActivityMeteorologicalScience
import com.pcs.ztqtj.view.activity.life.expert_interpretation.ActivityExpertList
import com.pcs.ztqtj.view.activity.life.health.ActivityHealthWeather
import com.pcs.ztqtj.view.activity.life.travel.ActivityTravelView
import com.pcs.ztqtj.view.activity.product.media.ActivityMediaList
import kotlinx.android.synthetic.main.fragment_live.*
import java.util.*

/**
 * 生活气象
 */
class FragmentLife : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_live, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initGradView()
    }

    private fun initGradView() {
        val itemHeight = (CommonUtil.heightPixels(activity)-CommonUtil.dip2px(activity, 100f).toInt()
                -CommonUtil.statusBarHeight(activity)-CommonUtil.navigationBarHeight(activity)) / 3
        val dto: ColumnDto = arguments!!.getParcelable("data")
        val dataList = ArrayList<ColumnDto>()
        if (dto != null && dto.childList.size > 0) {
            dataList.addAll(dto.childList)
        }
        val mAdapter = AdapterLifeFragment(activity, dataList, itemHeight)
        gridView.adapter = mAdapter
        gridView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val data = dataList[position]
            if (!CommonUtil.isCanAccess(data.flag)) {
                return@OnItemClickListener
            }
            val intent = Intent()
            when (data.dataCode) {
                "401" -> {
                    // 旅游气象
                    intent.setClass(activity, ActivityTravelView::class.java)
                    intent.putExtra("title", data.dataName)
                    activity!!.startActivity(intent)
                }
                "402" -> {
                    // 气象影视
                    intent.setClass(activity, ActivityMediaList::class.java)
                    intent.putExtra("title", data.dataName)
                    activity!!.startActivity(intent)
                }
                "403" -> {
                    //天气新闻
                    intent.setClass(activity, ActivityChannelList::class.java)
                    intent.putExtra("title", data.dataName)
                    intent.putExtra("channel_id", "100005")
                    activity!!.startActivity(intent)
                }
                "404" -> {
                    //专家解读
                    intent.setClass(activity, ActivityExpertList::class.java)
                    intent.putExtra("title", data.dataName)
                    activity!!.startActivity(intent)
                }
                "405" -> {
                    //灾害防御
                    intent.setClass(activity, ActivityChannelList::class.java)
                    intent.putExtra("title", data.dataName)
                    intent.putExtra("channel_id", "100007")
                    intent.putExtra("interfaceUrl", "zhfy")
                    activity!!.startActivity(intent)
                }
                "406" -> {
                    // 气象科普
                    intent.setClass(activity, ActivityMeteorologicalScience::class.java)
                    intent.putExtra("title", data.dataName)
                    activity!!.startActivity(intent)
                }
                "407" -> {
                    // 健康气象
                    intent.setClass(activity, ActivityHealthWeather::class.java)
                    intent.putExtra("title", data.dataName)
                    activity!!.startActivity(intent)
                }
            }
        }
    }

}
