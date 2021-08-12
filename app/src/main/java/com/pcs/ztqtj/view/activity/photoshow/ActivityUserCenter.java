package com.pcs.ztqtj.view.activity.photoshow;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoSingle;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.photo.AdapterPhotoCenter;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.control.tool.image.GetImageView;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.PhotoShowDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.user.ActivityUserInformation;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.ztqtj.view.myview.MyListView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 设置-我的
 */
public class ActivityUserCenter extends FragmentActivityZtqBase {

	// 适配器
	private AdapterPhotoCenter mAdapter;
    public List<PackPhotoSingle> photoWallList = new ArrayList();
	// 删除的Position
	private Button rightbtn_ok;

    private GetImageView getImageView = new GetImageView();

    private View viewHead;
    private int mClickPosition = -1;

    private DialogTwoButton deleteDialog;

    // 弹出框
    private PopupWindow mPopupWindow;

    // 退出弹出框
    private DialogTwoButton mLogoutDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center);
		setTitleText("我");
		createImageFetcher();
		// 初始化列表
		initList();
		okHttpOrigin();
		rightbtn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                showLogoutDialog();
			}
		});
        initPopupWindow();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		rightbtn_ok.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (textHead!=null){
            textHead.setText(MyApplication.NAME);
        }
		PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
        if(viewHead != null) {
            ImageView iv = (ImageView) viewHead.findViewById(R.id.iv_head);
            getImageView.setImageView(this, MyApplication.PORTRAIT, iv);
        }
	}

	@Override
	protected void onPause() {
		super.onPause();
		PcsDataBrocastReceiver.unregisterReceiver(this, mReceiver);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case MyConfigure.REQUEST_ALBUM:
                // 相册
                resultAlbum(intent);
                break;
            case MyConfigure.REQUEST_CAMERA:
                // 相机
                resultCamera(intent);
                break;
        }
    }

    private TextView textHead;
	/**
	 * 初始化列表
	 */
	private void initList() {
		rightbtn_ok=(Button) findViewById(R.id.rightbtn_ok);
		rightbtn_ok.setVisibility(View.VISIBLE);
		rightbtn_ok.setText("退出");
		//SlideCutListView listView = (SlideCutListView) findViewById(R.id.listview);
        MyListView listView = (MyListView) findViewById(R.id.listview);
		// 添加顶部视图
		viewHead = LayoutInflater.from(this).inflate(R.layout.view_photo_center_head, null);
        textHead= (TextView) viewHead.findViewById(R.id.my_name_tv);
//		textHead.setText(PhotoShowDB.getInstance().getUserPack().nickName);//【以前使用】
		textHead.setText(MyApplication.NAME);
        ImageView iv = (ImageView) viewHead.findViewById(R.id.iv_head);
        getImageView.setImageView(this, MyApplication.PORTRAIT, iv);
        LinearLayout ll = (LinearLayout) viewHead.findViewById(R.id.ll_user);
        int height = getViewHeadHeight();
        viewHead.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                gotoUserInformation();
            }
        });
		listView.addHeaderView(viewHead);
		// 适配器
		mAdapter = new AdapterPhotoCenter(ActivityUserCenter.this, getImageFetcher(), photoWallList);

        // 设置删除回调监听
        mAdapter.setDeleteListener(new AdapterPhotoCenter.RemoveListener() {
            @Override
            public void removeItem(PackPhotoSingle info, int position) {
                showDeleteDialog(info, position);
            }
        });

        // 图片点击监听
        mAdapter.setImageListener(new AdapterPhotoCenter.ImageListener() {
            @Override
            public void onClick(int position) {
                if(position == 0) {
                    // 照相机
                    //clickCamera();
                    showPopupWindow();
                    return ;
                }
                Intent intent = new Intent();
                intent.putExtra("ActivityPhotoUserCenter", true);
                mClickPosition = position;
                intent.putExtra("imgType", "1");
                intent.putExtra("position", position);
                intent.putExtra("isSpecial", false);
                intent.setClass(ActivityUserCenter.this, ActivityPhotoDetail.class);
                startActivity(intent);
            }
        });

        // 设置点赞监听回调
//        mAdapter.setPraiseListener(new AdapterPhotoCenter.PraiseListener() {
//            @Override
//            public void onPraise(PackPhotoSingle info, int position) {
//                clickPraise(info, position);
//            }
//        });
        listView.setAdapter(mAdapter);
        //listView.setOnItemClickListener(mOnItemClick);
	}

    /**
     * 获取viewhead高度
     * @return
     */
    private int getViewHeadHeight() {
        return (int) (findViewById(R.id.layout).getHeight() / 3.0f);
    }

    /**
     * 初始化弹出框
     */
    private void initPopupWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_photograph, null);
        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置可以获得焦点
        mPopupWindow.setFocusable(true);
        // 设置弹窗内可点击
        mPopupWindow.setTouchable(true);
        // 设置弹窗外可点击
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismissPopupWindow();
                    return true;
                }
                return false;
            }
        });
        View btn;
        // 相机按钮
        btn = view.findViewById(R.id.btnCamera);
        btn.setOnClickListener(mOnClick);
        // 相册按钮
        btn = view.findViewById(R.id.btnAlbum);
        btn.setOnClickListener(mOnClick);
        // 取消按钮
        btn = view.findViewById(R.id.btnCancel);
        btn.setOnClickListener(mOnClick);
    }

    /**
     * 显示退出弹出框
     */
    private void showLogoutDialog() {
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_message, null);
        TextView tv = (TextView) layout.findViewById(R.id.dialogmessage);
        tv.setText("确认退出当前账号吗？");
        mLogoutDialog = new DialogTwoButton(this, layout, "确定", "取消", new DialogFactory.DialogListener() {
            @Override
            public void click(String str) {
                mLogoutDialog.dismiss();
                if(str.equals("确定")) {
                    logout();
                }
            }
        });
        mLogoutDialog.show();
    }

    private OnClickListener mOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnCamera:
                    // 点击相机
                    clickCamera();
                    break;
                case R.id.btnAlbum:
                    // 点击相册
                    clickAlbum();
                    break;
                case R.id.btnCancel:
                    // 点击取消
                    clickCancel();
                    break;
            }
        }
    };

    /**
     * 点击相册
     */
    private void clickAlbum() {
        dismissPopupWindow();
        Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(it, MyConfigure.REQUEST_ALBUM);
    }

    /**
     * 点击取消
     */
    private void clickCancel() {
        dismissPopupWindow();
    }

    /**
     * 显示弹出框
     */
    private void showPopupWindow() {
        if(mPopupWindow != null && !mPopupWindow.isShowing()) {
            View layout = findViewById(R.id.layout);
            mPopupWindow.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
        }
    }

    /**
     * 关闭弹出框
     */
    private void dismissPopupWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 显示删除图片对话框
     */
    private void showDeleteDialog(final PackPhotoSingle info, final int position) {
        View view = LayoutInflater.from(ActivityUserCenter.this).inflate(R.layout.dialog_message, null);
        TextView tv = (TextView) view.findViewById(R.id.dialogmessage);
        tv.setText("要删除这张照片吗？");
        deleteDialog = new DialogTwoButton(ActivityUserCenter.this, view, "确定", "取消", new DialogFactory.DialogListener() {
            @Override
            public void click(String str) {
                deleteDialog.dismiss();
                if(str.equals("确定")) {
                    okHttpDelete(info.itemId, position);
                }
            }
        });
        deleteDialog.show();
    }

    /**
     * 跳转个人信息页面
     */
    private void gotoUserInformation() {
        startActivity(new Intent(this, ActivityUserInformation.class));
    }

	/**
	 * 数据广播接收
	 */
	private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
		@Override
		public void onReceive(String nameStr, String errorStr) {
//            if (PackPhotoPraiseUp.NAME.equals(nameStr)) {
//                dismissProgressDialog();
//                PackPhotoPraiseDown down = (PackPhotoPraiseDown) PcsDataManager.getInstance().getNetPack(nameStr);
//                if(!down.isSucc) {
//                    // 不成功
//                    Toast.makeText(ActivityPhotoUserCenter.this, R.string.praise_err, Toast.LENGTH_SHORT)
//                            .show();
//                    return;
//                }
//                // 成功
//                List<PackPhotoSingle> list =  PhotoShowDB.getInstance().getPhotoListCenter();
//                if(mClickPosition < list.size() && mClickPosition >= 0) {
//                    PackPhotoSingle info = list.get(mClickPosition);
//                    info.isPraised = true;
//                    int num = Integer.valueOf(info.praise);
//                    info.praise = String.valueOf(num + 1);
//                    PhotoShowDB.getInstance().setPhotoInfo(mClickPosition, info);
//                    mAdapter.notifyDataSetChanged();
//                }
//            }
		}
	};

	/**
	 * 列表点击事件
	 */
	private OnItemClickListener mOnItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position == 0) {
				return;
			}
            if(position == 1) {
                // 照相机
                clickCamera();
                return ;
            }
			Intent intent = new Intent();
			intent.setClass(ActivityUserCenter.this, ActivityPhotoDetail.class);
			intent.putExtra("position", position-2);
			intent.putExtra("ActivityPhotoUserCenter", true);
            mClickPosition = position-2;

			startActivity(intent);
		}
	};

    private File mFilePhoto;
    /**
     * 点击照相
     */
    private void clickCamera() {
        String tempStr = String.valueOf(System.currentTimeMillis());
        mFilePhoto = new File(PcsGetPathValue.getInstance().getMyPhotoPath() + tempStr + ".jpg");
        mFilePhoto.getParentFile().mkdirs();
        CommUtils.openCamera(this, mFilePhoto, MyConfigure.REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MyConfigure.REQUEST_CAMERA) {
            PermissionsTools.onRequestPermissionsResult(this, permissions, grantResults, new PermissionsTools.RequestPermissionResultCallback() {

                @Override
                public void onSuccess() {
                    clickCamera();
                }

                @Override
                public void onDeny() {

                }

                @Override
                public void onDenyNeverAsk() {

                }
            });
        }
    }

    /**
     * 拷贝文件
     * @param oldFile
     * @param newFile
     */
    private boolean copyFile(File oldFile, File newFile) {
        if (!oldFile.exists()) {
            return false;
        }
        newFile.getParentFile().mkdirs();
        InputStream inStream = null;
        FileOutputStream fs = null;
        int byteread = 0;
        try {
            inStream = new FileInputStream(oldFile); // 读入原文件
            fs = new FileOutputStream(newFile);
            byte[] buffer = new byte[1444];
            while ((byteread = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 从相机返回
     */
    private void resultCamera(Intent fromIntent) {
        if (mFilePhoto == null || !mFilePhoto.exists()) {
            Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(mFilePhoto.getPath());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();
        try {
            FileOutputStream fos = new FileOutputStream(mFilePhoto);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 跳转提交图片页面
        Intent intent = new Intent();
        intent.setClass(this, ActivityPhotoSubmit.class);
        intent.putExtra("photo_path", mFilePhoto.getPath());
        startActivity(intent);
    }

    /**
     * 从相册返回
     *
     * @param fromIntent
     */
    private void resultAlbum(Intent fromIntent) {
        Uri uri = fromIntent.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor c = this.getContentResolver().query(uri, filePathColumns, null, null, null);
        if (c == null) {
            return;
        }
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePathColumns[0]);
        String picturePath = c.getString(columnIndex);
        c.close();
        // ---------拷贝文件
        // 旧文件
        File oldFile = new File(picturePath);
        // 新文件
        mFilePhoto = new File(PcsGetPathValue.getInstance().getMyPhotoPath() + oldFile.getName());
        if (mFilePhoto.exists()) {
            mFilePhoto.delete();
        }
        mFilePhoto.getParentFile().mkdirs();
        if (!copyFile(oldFile, mFilePhoto)) {
            Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT).show();
            dismissProgressDialog();
            return;
        }

        // 跳转提交图片页面
        Intent intent = new Intent();
        intent.setClass(this, ActivityPhotoSubmit.class);
        intent.putExtra("photo_path", mFilePhoto.getPath());
        startActivity(intent);
    }

    /**
     * 获取图片数据
     */
    private void okHttpOrigin() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
//                    info.put("areaid", mCityId);
                    info.put("userId", MyApplication.UID);
                    info.put("imgType", "1");//图片类型，1（实景开拍），2（农业开拍分类）必须传
                    info.put("page", "1");
                    info.put("count", "100");
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("livePhotoList", json);
                    final String url = CONST.BASE_URL+"live_photo/livePhotoList";
                    Log.e("livePhotoList", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
//                                    if (!obj.isNull("pages")) {
//                                        totalPage = obj.getInt("pages");
//                                    }
                                            if (!obj.isNull("result")) {
                                                photoWallList.clear();
                                                photoWallList.add(new PackPhotoSingle());
                                                JSONArray array = obj.getJSONArray("result");
                                                for (int i = 0; i < array.length(); i++) {
                                                    PackPhotoSingle dto = new PackPhotoSingle();
                                                    JSONObject itemObj = array.getJSONObject(i);
                                                    if (!itemObj.isNull("id")) {
                                                        dto.itemId = itemObj.getString("id");
                                                    }
                                                    if (!itemObj.isNull("imageUrl")) {
                                                        dto.thumbnailUrl = itemObj.getString("imageUrl");
                                                    }
                                                    if (!itemObj.isNull("browseNum")) {
                                                        dto.browsenum = itemObj.getString("browseNum");
                                                    }
                                                    if (!itemObj.isNull("address")) {
                                                        dto.address = itemObj.getString("address");
                                                    }
                                                    if (!itemObj.isNull("nickName")) {
                                                        dto.nickName = itemObj.getString("nickName");
                                                    }
                                                    if (!itemObj.isNull("des")) {
                                                        dto.des = itemObj.getString("des");
                                                    }
                                                    if (!itemObj.isNull("likeNum")) {
                                                        dto.praise = itemObj.getString("likeNum");
                                                    }
                                                    if (!itemObj.isNull("createTime")) {
                                                        dto.date_time = itemObj.getString("createTime");
                                                    }
                                                    if (!itemObj.isNull("weather")) {
                                                        dto.weather = itemObj.getString("weather");
                                                    }
                                                    photoWallList.add(dto);
                                                }
                                                PhotoShowDB.getInstance().setPhotoListCenter(photoWallList);
                                                if (mAdapter != null) {
                                                    mAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 删除图片
     */
    private void okHttpDelete(final String itemId, final int mDeletePosition) {
        if (TextUtils.isEmpty(itemId)) {
            return;
        }
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("userId", MyApplication.UID);
                    info.put("id", itemId);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("delete", json);
                    final String url = CONST.BASE_URL+"live_photo/delete";
                    Log.e("delete", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("status")) {
                                                String status = obj.getString("status");
                                                if (TextUtils.equals(status, "success")) {
//                                                    // 从列表删除
                                                    PhotoShowDB.getInstance().getPhotoListCenter().remove(mDeletePosition);
//                                                    // 设置首页刷新
                                                    PhotoShowDB.getInstance().setRefreshType(PhotoShowDB.PhotoRefreshType.DATA);
                                                    if (mAdapter != null) {
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                } else {
                                                    Toast.makeText(ActivityUserCenter.this, R.string.delete_err, Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(ActivityUserCenter.this, R.string.delete_err, Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 登出当前账号
     */
    private void logout() {
        MyApplication.clearUserInfo(ActivityUserCenter.this);

        //刷新栏目数据
        Intent bdIntent = new Intent();
        bdIntent.setAction(CONST.BROADCAST_REFRESH_COLUMNN);
        sendBroadcast(bdIntent);

        setResult(Activity.RESULT_OK);
        finish();
    }

}
