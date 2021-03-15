package com.pcs.ztqtj.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pcs.ztqtj.R;


/**
 * Created by tyaathome on 2018/11/29.
 */
public class PermissionVerifyDialog extends Dialog {

    private Button btnConfirm;
    private TextView tvMessage;
    private String message = "";
    private String okButtonMsg = "确定";
    private View.OnClickListener listener;

    public PermissionVerifyDialog(@NonNull Context context) {
        super(context);
    }

    public PermissionVerifyDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_permission_verify);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        btnConfirm.setText(okButtonMsg);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onClick(v);
                }
                //dismiss();
            }
        });
        fillMessage(message);
    }

    public void setMessage(String message) {
        this.message = message;
        if(isShowing()) {
            fillMessage(message);
        }
    }

    public void setOKButtonMessage(String message) {
        this.okButtonMsg = message;
        if(isShowing()) {
            btnConfirm.setText(okButtonMsg);
        }
    }

    public void setOnConfirmListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void fillMessage(String message) {
        if(!TextUtils.isEmpty(message)) {
            String string = String.format("未获取%s权限,将无法正常使用客户端。", message);
            SpannableString spannableString = new SpannableString(string);
            ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#FF9932"));
            spannableString.setSpan(span, 3, message.length()+3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvMessage.setText(spannableString);
        }
    }
}
