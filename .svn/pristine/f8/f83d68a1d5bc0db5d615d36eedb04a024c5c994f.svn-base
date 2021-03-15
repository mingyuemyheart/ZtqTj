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
import android.telephony.TelephonyManager;
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
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalPhotoUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoCenterUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoDeleteDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoDeleteItemDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoDeleteItemUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoDeleteUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoPraiseDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoPraiseUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoSingle;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.photo.AdapterPhotoCenter;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.LoginInformation;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.control.tool.image.GetImageView;
import com.pcs.ztqtj.model.PhotoShowDB;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.user.ActivityUserInformation;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.ztqtj.view.myview.MyListView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 实景个人中心
 * 
 * @author JiangZy
 * 
 */
public class ActivityPhotoUserCenter extends FragmentActivityZtqBase {
	// 适配器
	private AdapterPhotoCenter mAdapter;
	// 用户中心上传包
	private PackPhotoCenterUp mPackCenterUp = new PackPhotoCenterUp();
	// 删除数据上传包
	private PackPhotoDeleteUp mPackDeleteUp = new PackPhotoDeleteUp();
	// 删除数据下载包
	private PackPhotoDeleteDown mPackDeleteDown = new PackPhotoDeleteDown();
	// 删除的Position
	private int mDeletePosition = -1;
	private Button rightbtn_ok;

    private GetImageView getImageView = new GetImageView();

    private View viewHead;
    private int mClickPosition = -1;

    private DialogTwoButton deleteDialog;

    // 弹出框
    private PopupWindow mPopupWindow;

    // 退出弹出框
    private DialogTwoButton mLogoutDialog;
    private PackLocalUser localUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_center);
		setTitleText("我");
		createImageFetcher();
		bundle = getIntent().getExtras();
//		String name= bundle.getString("nick_name");
//		String img= bundle.getString("head_url");
		showProgressDialog();
         localUser= ZtqCityDB.getInstance().getMyInfo();
		// 初始化列表
		initList();
		// 请求数据
		reqCenterData();
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
            textHead.setText(localUser.sys_nick_name);
        }
		PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
        if(viewHead != null) {
            ImageView iv = (ImageView) viewHead.findViewById(R.id.iv_head);

            getImageView.setImageView(this, localUser.sys_head_url, iv);
        }
        reqCenterData();

	}

	@Override
	protected void onPause() {
		super.onPause();
		PcsDataBrocastReceiver.unregisterReceiver(this, mReceiver);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
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
		viewHead = LayoutInflater.from(this).inflate(
				R.layout.view_photo_center_head, null);
        textHead= (TextView) viewHead.findViewById(R.id.my_name_tv);
//		textHead.setText(PhotoShowDB.getInstance().getUserPack().nickName);//【以前使用】
		textHead.setText(localUser.sys_nick_name);
        ImageView iv = (ImageView) viewHead.findViewById(R.id.iv_head);
        getImageView.setImageView(this, localUser.sys_head_url, iv);
        LinearLayout ll = (LinearLayout) viewHead.findViewById(R.id.ll_user);
        int height = getViewHeadHeight();
        viewHead.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUserInformation();
            }
        });
		listView.addHeaderView(viewHead);
		// 适配器
		mAdapter = new AdapterPhotoCenter(ActivityPhotoUserCenter.this, getImageFetcher());

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
                intent.setClass(ActivityPhotoUserCenter.this,
                        ActivityPhotoDetail.class);
                intent.putExtra("position", position-1);
                intent.putExtra("ActivityPhotoUserCenter", true);
                mClickPosition = position-1;

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
        View view = LayoutInflater.from(this).inflate(R.layout.pop_photograph,
                null);
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

    /**
     * 退出账号
     */
    private void logout() {
        ZtqCityDB.getInstance().removeMyInfo();
//        LoginInformation.getInstance().clearLoginInfo();
        setResult(Activity.RESULT_OK);
        finish();
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
        Intent it = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
        View view = LayoutInflater.from(ActivityPhotoUserCenter.this).inflate(
                R.layout.dialog_message, null);
        TextView tv = (TextView) view.findViewById(R.id.dialogmessage);
        tv.setText("要删除这张照片吗？");
        deleteDialog = new DialogTwoButton(ActivityPhotoUserCenter.this, view, "确定", "取消", new DialogFactory.DialogListener() {


            @Override
            public void click(String str) {
                deleteDialog.dismiss();
                if(str.equals("确定")) {
                    clickDelete(info, position);
                }
            }
        });
        deleteDialog.show();
    }

    /**
     * 跳转个人信息页面
     */
    private void gotoUserInformation() {
        Intent intent = new Intent(this, ActivityUserInformation.class);
        startActivity(intent);
    }

    /**
     * 点赞
     */
    private void clickPraise(PackPhotoSingle info, int position) {
        mClickPosition = position;
        if (info.isPraised) {
            showToast("已点赞！");
            return;
        }

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        PackPhotoPraiseUp packUp = new PackPhotoPraiseUp();
        packUp.userId = info.userId;
        TelephonyManager tm = (TelephonyManager) this
                .getSystemService(TELEPHONY_SERVICE);
        packUp.imei = tm.getDeviceId();
        packUp.itemId = info.itemId;
        PcsDataDownload.addDownload(packUp);
    }

    /**
     * 删除条目
     * @param info
     * @param position
     */
    private void clickDelete(PackPhotoSingle info, int position) {
        mDeletePosition = position;
        showProgressDialog();
        PackPhotoDeleteItemUp packUp = new PackPhotoDeleteItemUp();
        packUp.itemId = info.itemId;
        PcsDataDownload.addDownload(packUp);
    }

	/**
	 * 请求用户中心数据
	 */
	private void reqCenterData() {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
		PackLocalPhotoUser packUser = PhotoShowDB.getInstance().getUserPack();
		mPackCenterUp.userId = packUser.userId;
		PcsDataDownload.addDownload(mPackCenterUp);
	}

	/**
	 * 请求删除数据
	 */
	private void reqDeleteData(int position) {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
		PackPhotoSingle pack = PhotoShowDB.getInstance().getPhotoListCenter()
				.get(position);
		mPackDeleteUp.itemId = pack.itemId;

		PcsDataDownload.addDownload(mPackDeleteUp);
	}

	/**
	 * 个人中心返回
	 */
	private void receiveCenter() {

		PhotoShowDB.getInstance().setPhotoListCenter(mPackCenterUp.getName());

		// 更新
		mAdapter.notifyDataSetChanged();

		dismissProgressDialog();
	}

	/**
	 * 删除数据返回
	 */
	private void receiveDelete() {
		mPackDeleteDown= (PackPhotoDeleteDown) PcsDataManager.getInstance().getNetPack(mPackDeleteUp.getName());
		if (!mPackDeleteDown.isSucc) {
			// 不成功
			Toast.makeText(this, R.string.delete_err, Toast.LENGTH_SHORT).show();
			return;
		}

		// 从列表删除
		PhotoShowDB.getInstance().getPhotoListCenter().remove(mDeletePosition);
		// 设置首页刷新
		PhotoShowDB.getInstance().setRefreshType(
				PhotoShowDB.PhotoRefreshType.DATA);
		// 更新
		mAdapter.notifyDataSetChanged();

		dismissProgressDialog();
	}

	/**
	 * 数据广播接收
	 */
	private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
		@Override
		public void onReceive(String nameStr, String errorStr) {
			if (mPackCenterUp.getName().equals(nameStr)) {
				// 用户中心
				receiveCenter();
			} else if (mPackDeleteUp.getName().equals(nameStr)) {
				// 删除数据
				receiveDelete();
			}
            if (PackPhotoPraiseUp.NAME.equals(nameStr)) {
                dismissProgressDialog();
                PackPhotoPraiseDown down = (PackPhotoPraiseDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(!down.isSucc) {
                    // 不成功
                    Toast.makeText(ActivityPhotoUserCenter.this, R.string.praise_err, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                // 成功
                List<PackPhotoSingle> list =  PhotoShowDB.getInstance().getPhotoListCenter();
                if(mClickPosition < list.size() && mClickPosition >= 0) {
                    PackPhotoSingle info = list.get(mClickPosition);
                    info.isPraised = true;
                    int num = Integer.valueOf(info.praise);
                    info.praise = String.valueOf(num + 1);
                    PhotoShowDB.getInstance().setPhotoInfo(mClickPosition, info);
                    mAdapter.notifyDataSetChanged();
                }

            }

            if(PackPhotoDeleteItemUp.NAME.equals(nameStr)) {
                dismissProgressDialog();
                PackPhotoDeleteItemDown down = (PackPhotoDeleteItemDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return ;
                }
                if(down.result.equals("1")) {
                    PhotoShowDB.getInstance().updatePhotoListCenter(mDeletePosition);
                    mAdapter.notifyDataSetChanged();
                } else {
                    showToast("删除失败");
                }
            }
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
			intent.setClass(ActivityPhotoUserCenter.this,
					ActivityPhotoDetail.class);
			intent.putExtra("position", position-2);
			intent.putExtra("ActivityPhotoUserCenter", true);
            mClickPosition = position-2;

			startActivity(intent);
		}
	};
	private Bundle bundle;
    private File mFilePhoto;
    /**
     * 点击照相
     */
    private void clickCamera() {

        String tempStr = String.valueOf(System.currentTimeMillis());
        mFilePhoto = new File(PcsGetPathValue.getInstance().getMyPhotoPath()
                + tempStr + ".jpg");
        mFilePhoto.getParentFile().mkdirs();
        CommUtils.openCamera(this, mFilePhoto, MyConfigure.REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
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
     *
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
            Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT)
                    .show();
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
        Cursor c = this.getContentResolver().query(uri, filePathColumns, null,
                null, null);
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
        mFilePhoto = new File(PcsGetPathValue.getInstance().getMyPhotoPath()
                + oldFile.getName());
        if (mFilePhoto.exists()) {
            mFilePhoto.delete();
        }
        mFilePhoto.getParentFile().mkdirs();
        if (!copyFile(oldFile, mFilePhoto)) {
            Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT)
                    .show();
            dismissProgressDialog();
            return;
        }

        // 跳转提交图片页面
        Intent intent = new Intent();
        intent.setClass(this, ActivityPhotoSubmit.class);
        intent.putExtra("photo_path", mFilePhoto.getPath());
        startActivity(intent);
    }
}
