package com.pcs.ztqtj.view.activity.prove

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import com.pcs.ztqtj.R
import com.pcs.ztqtj.util.CONST
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_display_picture.*
import java.io.File

/**
 * 图片预览
 */
class DisplayPictureActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_picture)
//        Sofia.with(this)
//                .invasionStatusBar() //设置顶部状态栏缩进
//                .statusBarBackground(Color.TRANSPARENT) //设置状态栏颜色
//                .invasionNavigationBar()
//                .navigationBarBackground(Color.TRANSPARENT)
        initWidget()
    }

    private fun initWidget() {
        if (intent.hasExtra(CONST.WEB_URL)) {
            val imgUrl = intent.getStringExtra(CONST.WEB_URL)
            if (!TextUtils.isEmpty(imgUrl)) {
                if (imgUrl.startsWith("http")) {
                    Picasso.get().load(imgUrl).into(imageView)
                } else {
                    val file = File(imgUrl)
                    if (file.exists()) {
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap)
                        }
                    }
                }
            }
        }
    }

}
