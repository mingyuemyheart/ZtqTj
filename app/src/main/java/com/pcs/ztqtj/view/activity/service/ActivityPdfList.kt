package com.pcs.ztqtj.view.activity.service

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.pcs.ztqtj.R
import com.pcs.ztqtj.util.ColumnDto
import com.pcs.ztqtj.util.CommonUtil
import com.pcs.ztqtj.view.activity.web.FragmentActivityZtqWithHelp
import com.pcs.ztqtj.view.myview.MyPagerAdapter
import kotlinx.android.synthetic.main.activity_weather_prove.*
import java.util.*

/**
 * 专项服务-专项服务-gridview点击-pdf列表
 */
class ActivityPdfList : FragmentActivityZtqWithHelp() {

    private val fragments: ArrayList<Fragment> = ArrayList()

    override fun onCreate(arg0: Bundle?) {
        super.onCreate(arg0)
        setContentView(R.layout.activity_special_service)
        initViewPager()
    }

    /**
     * 初始化viewPager
     */
    private fun initViewPager() {
        fragments.clear()
        llContainer!!.removeAllViews()

        if (intent.hasExtra("data")) {
            val dto: ColumnDto = intent.getParcelableExtra("data")
            if (dto != null) {
                if (dto.dataName != null) {
                    titleText = dto.dataName
                }

                if (dto.childList.size > 0) {
                    for (i in 0 until dto.childList.size) {
                        val llItem = LinearLayout(this)
                        llItem.orientation = LinearLayout.VERTICAL
                        llItem.gravity = Gravity.CENTER_VERTICAL
                        val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        param.weight = 1f
                        llItem.layoutParams = param

                        val data = dto.childList[i]
                        val tv = TextView(this)
                        tv.text = data.dataName
                        tv.setTextColor(Color.WHITE)
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                        tv.gravity = Gravity.CENTER
                        tv.setOnClickListener(MyOnClickListener(i))
                        llItem.addView(tv)

                        val tvBar = TextView(this)
                        val paramBar = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        paramBar.width = tv.layoutParams.width
                        paramBar.height = CommonUtil.dip2px(this, 1f).toInt()
                        paramBar.topMargin = CommonUtil.dip2px(this, 5f).toInt()
                        tvBar.layoutParams = paramBar
                        llItem.addView(tvBar)

                        when(i) {
                            0 -> {
                                tvBar.setBackgroundColor(Color.WHITE)
                            }
                            else -> {
                                tvBar.setBackgroundColor(Color.TRANSPARENT)
                            }
                        }
                        llContainer.addView(llItem, i)

                        val fragment = PdfListFragment()
                        val bundle = Bundle()
                        bundle.putParcelable("data", data)
                        fragment.arguments = bundle
                        fragments.add(fragment)
                    }
                } else {
                    llContainer.visibility = View.GONE
                    val fragment = PdfListFragment()
                    val bundle = Bundle()
                    bundle.putParcelable("data", dto)
                    fragment.arguments = bundle
                    fragments.add(fragment)
                }
                viewPager.setSlipping(true) //设置ViewPager是否可以滑动
                viewPager.offscreenPageLimit = fragments.size
                viewPager.setOnPageChangeListener(MyOnPageChangeListener())
                viewPager.adapter = MyPagerAdapter(supportFragmentManager, fragments)
            }
        }
    }

    private inner class MyOnPageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageSelected(arg0: Int) {
            if (llContainer != null) {
                for (i in 0 until llContainer.childCount) {
                    val llItem = llContainer.getChildAt(i) as LinearLayout
                    val tv = llItem.getChildAt(0) as TextView
                    val tvBar = llItem.getChildAt(1) as TextView
                    if (i == arg0) {
                        tvBar.setBackgroundColor(Color.WHITE)
                    } else {
                        tvBar.setBackgroundColor(Color.TRANSPARENT)
                    }
                }
            }
        }
        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    /**
     * 头标点击监听
     * @author shawn_sun
     */
    private inner class MyOnClickListener(private val index: Int) : View.OnClickListener {
        override fun onClick(v: View) {
            if (viewPager != null) {
                viewPager.setCurrentItem(index, true)
            }
        }
    }

}
