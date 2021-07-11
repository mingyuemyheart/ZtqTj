package com.pcs.ztqtj.view.activity.prove

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.pcs.ztqtj.R
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase
import com.pcs.ztqtj.view.myview.MyPagerAdapter
import kotlinx.android.synthetic.main.activity_weather_prove.*

/**
 * 气象证明
 */
class WeatherProveActivity: FragmentActivityZtqBase(), View.OnClickListener {
    
    private val fragments: ArrayList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_prove)
        initWidget()
        initViewPager()
    }

    private fun initWidget() {
        titleText = "气象灾害证明"
        tvProve.setOnClickListener(this)
    }

    /**
     * 初始化viewPager
     */
    private fun initViewPager() {
        fragments.clear()
        llContainer!!.removeAllViews()

        val dataList: ArrayList<ProveDto> = ArrayList()
        var dto = ProveDto()
        dto.columnName = "全部"
        dto.flag = ""
        dataList.add(dto)
        dto = ProveDto()
        dto.columnName = "未完成"
        dto.flag = "N"
        dataList.add(dto)
        dto = ProveDto()
        dto.columnName = "已完成"
        dto.flag = "Y"
        dataList.add(dto)

        for (i in 0 until dataList.size) {
            val data = dataList[i]
            val tv = TextView(this)
            tv.text = data.columnName
            tv.gravity = Gravity.CENTER
            tv.setOnClickListener(MyOnClickListener(i))
            when(i) {
                0 -> {
                    tv.setBackgroundResource(R.drawable.bg_column_blue)
                    tv.setTextColor(Color.WHITE)
                }
                else -> {
                    tv.setBackgroundResource(R.drawable.bg_column_white)
                    tv.setTextColor(ContextCompat.getColor(this, R.color.dgray))
                }
            }
            llContainer!!.addView(tv, i)

            val fragment = WeatherProveFragment()
            val bundle = Bundle()
            bundle.putString("flag", data.flag)
            fragment.arguments = bundle
            fragments.add(fragment)
        }
        viewPager.setSlipping(true) //设置ViewPager是否可以滑动
        viewPager.offscreenPageLimit = fragments.size
        viewPager.setOnPageChangeListener(MyOnPageChangeListener())
        viewPager.adapter = MyPagerAdapter(supportFragmentManager, fragments)
    }

    private inner class MyOnPageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageSelected(arg0: Int) {
            if (llContainer != null) {
                for (i in 0 until llContainer.childCount) {
                    val tv = llContainer.getChildAt(i) as TextView
                    if (i == arg0) {
                        tv.setBackgroundResource(R.drawable.bg_column_blue)
                        tv.setTextColor(Color.WHITE)
                    } else {
                        tv.setBackgroundResource(R.drawable.bg_column_white)
                        tv.setTextColor(ContextCompat.getColor(this@WeatherProveActivity, R.color.dgray))
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

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.tvProve -> startActivity(Intent(this, WeatherProveSubmitActivity::class.java))
        }
    }

}