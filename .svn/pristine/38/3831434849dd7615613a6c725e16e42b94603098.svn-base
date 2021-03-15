package com.pcs.ztqtj.view.activity.product.dataquery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.LoginInformation;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoLogin;
import com.pcs.ztqtj.view.activity.service.AcitvityServeLogin;

/**
 * Created by tyaathome on 2017/11/2.
 */

public class ActivityDataQueryLogin extends Activity {

    private TextView tvProfessional, tvNormal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_query_login);
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MyConfigure.REQUEST_DATA_QUERY_LOGIN:
                    finishActivity();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        tvProfessional = (TextView) findViewById(R.id.tv_professional);
        tvNormal = (TextView) findViewById(R.id.tv_normal);
        tvProfessional.setText(getClickableSpan(R.string.professional_user_login_tips, R.color.text_orange,
                AcitvityServeLogin.class));
        tvProfessional.setMovementMethod(LinkMovementMethod.getInstance());// 设置该句使文本的超连接起作用
        tvNormal.setText(getClickableSpan(R.string.normal_user_login_tips, R.color.text_blue,
                ActivityPhotoLogin.class));
        tvNormal.setMovementMethod(LinkMovementMethod.getInstance());// 设置该句使文本的超连接起作用
    }

    private void initEvent() {

    }

    private void initData() {

    }

    /**
     * 获取超链接文本
     *
     * @return
     */
    private SpannableString getClickableSpan(int strid, int color, final Class<?> clz) {
        String str = getString(strid);
        SpannableString spanStr = new SpannableString(str);
        // 设置下划线文字
        spanStr.setSpan(new UnderlineSpan(), 0, str.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置文字的单击事件
//        spanStr.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                Intent intent =  new Intent(ActivityDataQueryLogin.this, clz);
//                startActivityForResult(intent, MyConfigure.REQUEST_DATA_QUERY_LOGIN);
//            }
//        }, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(clickableSpan, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置文字的前景色
        spanStr.setSpan(
                new ForegroundColorSpan(getResources().getColor(
                        color)), 0, str.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    private ClickableSpan clickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            switch (widget.getId()) {
                case R.id.tv_professional:
                    Intent intent = new Intent(ActivityDataQueryLogin.this, AcitvityServeLogin.class);
                    startActivityForResult(intent, MyConfigure.REQUEST_DATA_QUERY_LOGIN);
                    break;
                case R.id.tv_normal:
                    if(!LoginInformation.getInstance().hasLogin()) {
                        Intent intent1 = new Intent(ActivityDataQueryLogin.this, ActivityPhotoLogin.class);
                        startActivityForResult(intent1, MyConfigure.REQUEST_DATA_QUERY_LOGIN);
                    } else {
                        finishActivity();
                    }
                    break;
            }
        }
    };

    /**
     * 完成登录
     */
    private void finishActivity() {
        setResult(RESULT_OK);
        finish();
    }
}
