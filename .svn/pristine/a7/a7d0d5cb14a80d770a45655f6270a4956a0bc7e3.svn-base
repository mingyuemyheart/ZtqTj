package com.pcs.ztqtj.model;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalPhotoUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoCenterDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoShowDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoShowUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoSingle;
import com.pcs.ztqtj.control.tool.LoginInformation;

import java.util.ArrayList;
import java.util.List;

/**
 * 实景开拍数据
 *
 * @author JiangZy
 */
public class PhotoShowDB {
    /**
     * 主页是否需要刷新
     *
     * @author JiangZy
     */
    public enum PhotoRefreshType {
        NO_NEED, VIEW, DATA
    }

    /**
     * 首页请求类型，是普通的还是精选类型
     */
    public enum PhotoShowType {
        ORDINARY, SPECIAL
    }

    private Context mContext;
    private PhotoShowDBListener mListener;

    // 是否需要刷新
    private PhotoRefreshType mPhotoRefreshType = PhotoRefreshType.NO_NEED;

    /**
     * 当前页
     */
    private int mCurrPage = 1;
    /**
     * 精品当前页
     */
    private int mCurrPageSpecial = 1;
    /**
     * 暂停中
     */
    private boolean mIsPause = false;
    /**
     * 正在加载
     */
    private boolean mIsLoading = false;
    /**
     * 没有更多数据了
     */
    private boolean mNoMoreData = false;
    /**
     * 精品图片没有更多数据了
     */
    private boolean mNoMoreDataSepcial = false;
    /**
     * 首页图片列表
     */
    private List<PackPhotoSingle> mListPhoto = new ArrayList<PackPhotoSingle>();
    /**
     * 首页精选图片列表
     */
    private List<PackPhotoSingle> mListPhotoSpecial = new ArrayList<PackPhotoSingle>();
    /**
     * 首页上传包
     */
    private PackPhotoShowUp mPackUp = new PackPhotoShowUp();
    /**
     * 首页精品图片上传包
     */
    private PackPhotoShowUp mPackUpSpecial = new PackPhotoShowUp();

    /**
     * 用户中心下载包
     */
    private PackPhotoCenterDown mPackCenterDown = new PackPhotoCenterDown();
    /**
     * 本地用户信息
     */
    private PackLocalPhotoUser mPackLocalPhotoUser = null;
    /**
     * 城市ID
     */
    private String mCityId = "";

    /**
     * 数据监听
     *
     * @author JiangZy
     */
    public interface PhotoShowDBListener {
        public void done();
    }

    private static PhotoShowDB instance = null;

    private PhotoShowDB() {
    }

    public static PhotoShowDB getInstance() {
        if (instance == null) {
            instance = new PhotoShowDB();
        }

        return instance;
    }

    public void onCreate(Context context, String cityId) {
        clearData();
        mContext = context;
        mCityId = cityId;
        // 初始化上传包
        initShowUpPack();
        // 注册监听
        PcsDataBrocastReceiver.registerReceiver(mContext, mReceiver);
    }

    public void onResume() {
        mIsPause = false;
    }

    public void onPause() {
        mIsPause = true;
    }

    public void onDestory() {
        try {
            PcsDataBrocastReceiver.unregisterReceiver(mContext, mReceiver);
        } catch (Exception ex) {

        }
        mIsPause = true;
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setListener(PhotoShowDBListener listener) {
        mListener = listener;
    }

    /**
     * 设置数据已变更
     */
    public void setRefreshType(PhotoRefreshType photoRefreshType) {
        mPhotoRefreshType = photoRefreshType;
    }

    /**
     * 获取数据是否变更
     *
     * @return
     */
    public PhotoRefreshType getRefreshType() {
        return mPhotoRefreshType;
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public PackLocalPhotoUser getUserPack() {
        if (mPackLocalPhotoUser == null) {
            mPackLocalPhotoUser = new PackLocalPhotoUser();
            // String json =
            // PcsDataManager.getInstance().loadData(mPackLocalPhotoUser.getName(null));
            // mPackLocalPhotoUser.fillData(json);
        }

        mPackLocalPhotoUser.email = "";
        PackLocalUser localUser=ZtqCityDB.getInstance().getMyInfo();
        mPackLocalPhotoUser.userId = localUser.sys_user_id;
        mPackLocalPhotoUser.nickName = localUser.sys_nick_name;
        return mPackLocalPhotoUser;
    }

    /**
     * 初始化首页上传包
     */
    private void initShowUpPack() {
        TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);

        // 城市ID
        mPackUp.area_id = mCityId;
        // 用户ID
        mPackUp.user_id = getUserPack().userId;
        // IMEI
        mPackUp.imei = tm.getDeviceId();
        mPackUpSpecial.keyword = "";

        // 城市ID
        mPackUpSpecial.area_id = mCityId;
        // 用户ID
        mPackUpSpecial.user_id = getUserPack().userId;
        mPackUpSpecial.imei = tm.getDeviceId();
        mPackUpSpecial.keyword = "preity_pic";
    }

    /**
     * 获取照片列表
     *
     * @return
     */
    public List<PackPhotoSingle> getPhotoList(PhotoShowType getDataType) {
        if (getDataType == PhotoShowType.ORDINARY) {
            return mListPhoto;
        } else {
            return mListPhotoSpecial;
        }

    }

    /**
     * 是否有图片
     *
     * @param type
     * @return
     */
    public boolean hasPhotoList(PhotoShowType type) {
        if (type == PhotoShowType.ORDINARY) {
            if (mListPhoto == null || mListPhoto.size() == 0) {
                return false;
            } else {
                return true;
            }
        } else if (type == PhotoShowType.SPECIAL) {
            if (mListPhotoSpecial == null || mListPhotoSpecial.size() == 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取照片列表(个人中心)
     *
     * @return
     */
    public List<PackPhotoSingle> getPhotoListCenter() {
        return mPackCenterDown.mList;
    }

    /**
     * 设置个人中心的图片列表
     *
     * @param position
     */
    public void updatePhotoListCenter(int position) {
        if (mPackCenterDown.mList != null &&
                mPackCenterDown.mList != null &&
                mPackCenterDown.mList.size() != 0 &&
                position < mPackCenterDown.mList.size() &&
                position >= 0) {
            mPackCenterDown.mList.remove(position);
        }
    }

    /**
     * 保存照片列表(个人中心)
     *
     * @param jsonStr
     */
    public void setPhotoListCenter(String jsonStr) {
        mPackCenterDown = (PackPhotoCenterDown) PcsDataManager.getInstance().getNetPack(jsonStr);
        if (mPackCenterDown == null) {
            mPackCenterDown = new PackPhotoCenterDown();
        }
        mPackCenterDown.setUserInfo(mPackLocalPhotoUser.userId, mPackLocalPhotoUser.nickName);
    }

    /**
     * 请求下一页
     *
     * @return 允许请求？
     */
    public boolean reqNextPage(PhotoShowType type) {
        if (!mIsLoading) {
            if (type == PhotoShowType.ORDINARY) {
                if (mNoMoreData) {
                    return false;
                } else {
                    // 普通
                    mPackUp.page = String.valueOf(mCurrPage);
                    PcsDataDownload.addDownload(mPackUp);
                    mIsLoading = true;
                    return true;
                }
            } else if (type == PhotoShowType.SPECIAL) {
                if (mNoMoreDataSepcial) {
                    return false;
                } else {
                    // 精品
                    mPackUpSpecial.page = String.valueOf(mCurrPageSpecial);
                    PcsDataDownload.addDownload(mPackUpSpecial);
                    mIsLoading = true;
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 设置用户中心数据
     *
     * @param jsonStr
     */
    public void setCenterData(String jsonStr) {
        mPackCenterDown.fillData(jsonStr);
    }

    /**
     * 清空数据
     */
    public void clearData() {
        mPhotoRefreshType = PhotoRefreshType.NO_NEED;
        mCurrPage = 1;
        mCurrPageSpecial = 1;
        mNoMoreData = false;
        mNoMoreDataSepcial = false;
        mIsPause = false;
        mIsLoading = false;
        mListPhoto.clear();
        mListPhotoSpecial.clear();
        mCityId = "";
    }

    /**
     * 设置个人中心图片信息
     *
     * @param index
     * @param info
     */
    public void setPhotoInfo(int index, PackPhotoSingle info) {
        if (mPackCenterDown.mList != null && index < mPackCenterDown.mList.size()) {
            mPackCenterDown.mList.set(index, info);
        }
    }

    /**
     * 获取城市ID
     */
    public String getCityId() {
        return mCityId;
    }

    /**
     * 广播接收
     */
    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (nameStr.equals(mPackUp.getName())) {
                PackPhotoShowDown pack = (PackPhotoShowDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (pack != null) {
                    if (pack.photoWallList.size() == 0) {
                        mNoMoreData = true;
                    } else {
                        // 添加数据
                        mListPhoto.addAll(pack.photoWallList);
                        mCurrPage++;
                    }
                    mIsLoading = false;
                    // 监听
                    if (!mIsPause && mListener != null) {
                        mListener.done();
                    }
                }
            } else if (nameStr.equals(mPackUpSpecial.getName())) {
                PackPhotoShowDown pack = (PackPhotoShowDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (pack != null) {
                    if (pack.photoWallList.size() == 0) {
                        mNoMoreDataSepcial = true;
                    } else {
                        // 添加数据
                        mListPhotoSpecial.addAll(pack.photoWallList);
                        mCurrPageSpecial++;
                    }
                    mIsLoading = false;
                    // 监听
                    if (!mIsPause && mListener != null) {
                        mListener.done();
                    }
                }
            }
        }
    };
}
