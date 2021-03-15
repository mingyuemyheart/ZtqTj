package com.pcs.ztqtj.view.fragment.warning.picture;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.fragment.warning.video.Code;
import com.pcs.ztqtj.view.myview.ImageTouchView;
import com.pcs.lib.lib_pcs_v3.model.image.ImageResizer;

import java.io.File;

/**
 * 显示一个完整的照片
 *
 * @author JiangZy
 */
public class ActivityPhotoFull extends Activity {
    private String mPath;
    private ImageResizer mResizer;
    // 等待对话框
    private ProgressDialog mProgress;
    private Intent intent;
    /**
     * 照片的Bitmap
     */
    private Bitmap mBitmapPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster_pic);
        setResult(Activity.RESULT_OK);
        showProgressDialog();
        intent=getIntent();
        mResizer = new ImageResizer();

        mPath = intent.getStringExtra("path");
        File file = null;
        if (!TextUtils.isEmpty(mPath)) {
            file = new File(mPath);
        }

        if (file.exists()) {
            // 加载SD卡图片
            reloadImageSd(mPath);
            // 旋转按钮
            initBtnRotate();

        } else {
            // 加载失败
            Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT)
                    .show();
        }

        // 点击事件
        View layoutRoot = findViewById(R.id.layout_root_pic);
        layoutRoot.setOnClickListener(mOnClick);

        dismissProgressDialog();
    }

    @Override
    protected void onDestroy() {
        if (mBitmapPhoto != null && !mBitmapPhoto.isRecycled()) {
            mBitmapPhoto.recycle();
        }
        super.onDestroy();
    }

    /**
     * 初始化按钮
     */
    private void initBtnRotate() {
        Button btn_del, btn_up, btn_finish;

        btn_del = (Button) findViewById(R.id.btn_pic_del);
        btn_del.setOnClickListener(mOnClick);

        btn_up = (Button) findViewById(R.id.btn_pic_up);
        btn_up.setOnClickListener(mOnClick);

        btn_finish = (Button) findViewById(R.id.btn_disaster_finish);
        btn_finish.setOnClickListener(mOnClick);

    }

    /**
     * 加载SD卡图片
     *
     * @param path
     */
    private void reloadImageSd(String path) {
        if (mBitmapPhoto != null && !mBitmapPhoto.isRecycled()) {
            mBitmapPhoto.recycle();
        }
        mBitmapPhoto = BitmapFactory.decodeFile(mPath);
        ImageTouchView imageView = (ImageTouchView) findViewById(R.id.image_pic);
        imageView.setMyImageBitmap(mBitmapPhoto);
    }

    /**
     * 显示等待对话框
     */
    public void showProgressDialog(String keyWord) {
        if (mProgress == null) {
            mProgress = new ProgressDialog(this);
            mProgress.setCancelable(true);
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.setOnCancelListener(mProgressOnCancel);
        }
        if (mProgress.isShowing()) {
            mProgress.setMessage(keyWord);
        } else {
            mProgress.show();
            mProgress.setMessage(keyWord);
        }
    }

    public void showProgressDialog() {
        showProgressDialog(getResources().getString(R.string.please_wait));
    }

    /**
     * 取消等待对话框
     */
    public void dismissProgressDialog() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    private View.OnClickListener mOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.layout_root_pic:
                    finish();
                    break;
                case R.id.btn_pic_del:
                    // 删除
                    //把返回数据存入Intent
                    intent.putExtra("type", "0");
                    //设置返回数据
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    // showProgressDialog();
                    //mResizer.rotateSD(mPath, -90, mImageListener);
                    break;
                case R.id.btn_pic_up:
                    //重新上传
                    //把返回数据存入Intent
                    intent.putExtra("type", "1");
                     //设置返回数据
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    // showProgressDialog();
                    // mResizer.rotateSD(mPath, 90, mImageListener);
                    break;
                case R.id.btn_disaster_finish:
                //把返回数据存入Intent
                    intent.putExtra("type", "2");
                //设置返回数据
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            intent.putExtra("result", "2");
            setResult(Code.REQUEST_CODE_DSIASTER, intent);
            ActivityPhotoFull.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 进度框OnCancel
     */
    private DialogInterface.OnCancelListener mProgressOnCancel = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            ActivityPhotoFull.this.finish();
        }
    };

    /**
     * 图片修改监听
     */
    private ImageResizer.ImageResizerListener mImageListener = new ImageResizer.ImageResizerListener() {
        @Override
        public void doneSD(String path, boolean isSucc) {
            if (isSucc) {
                reloadImageSd(path);
               // setResult(Activity.RESULT_OK);
            }

            dismissProgressDialog();
        }
    };
}
