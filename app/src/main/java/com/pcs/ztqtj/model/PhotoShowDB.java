package com.pcs.ztqtj.model;

import android.content.Context;
import android.util.Log;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalPhotoUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoCenterDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoShowDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoSingle;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    }

    public void onResume() {
        mIsPause = false;
    }

    public void onPause() {
        mIsPause = true;
    }

    public void onDestory() {
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
     * @return 允许请求？
     */
    public boolean reqNextPage(PhotoShowType type) {
        if (!mIsLoading) {
            if (type == PhotoShowType.ORDINARY) {
                if (mNoMoreData) {
                    return false;
                } else {
                    // 普通
                    mIsLoading = true;
                    okHttpOrigin();
                    return true;
                }
            } else if (type == PhotoShowType.SPECIAL) {
                if (mNoMoreDataSepcial) {
                    return false;
                } else {
                    // 精品
                    mIsLoading = true;
                    okHttpSpecial();
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
     * 获取图片数据
     */
    private void okHttpOrigin() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
//                    info.put("areaid", mCityId);
//                    info.put("userId", getUserPack().userId);
                    info.put("imgType", "1");
                    info.put("page", mCurrPage+"");
                    info.put("count", "20");
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
                            if (!TextUtil.isEmpty(result)) {
                                try {
                                    PackPhotoShowDown pack = new PackPhotoShowDown();
                                    JSONObject obj = new JSONObject(result);
//                                    if (!obj.isNull("pages")) {
//                                        totalPage = obj.getInt("pages");
//                                    }
                                    if (!obj.isNull("result")) {
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
                                            if (!itemObj.isNull("shootTime")) {
                                                dto.date_time = itemObj.getString("shootTime");
                                            }
                                            if (!itemObj.isNull("weather")) {
                                                dto.weather = itemObj.getString("weather");
                                            }
                                            pack.photoWallList.add(dto);
                                        }
                                    }
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
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取图片数据
     */
    private void okHttpSpecial() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
//                    info.put("areaid", mCityId);
//                    info.put("userId", getUserPack().userId);
                    info.put("imgType", "1");
                    info.put("page", mCurrPageSpecial+"");
                    info.put("count", "20");
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
                            if (!TextUtil.isEmpty(result)) {
                                try {
                                    PackPhotoShowDown pack = new PackPhotoShowDown();
                                    JSONObject obj = new JSONObject(result);
//                                    if (!obj.isNull("pages")) {
//                                        totalPage = obj.getInt("pages");
//                                    }
                                    if (!obj.isNull("result")) {
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
                                            if (!itemObj.isNull("shootTime")) {
                                                dto.date_time = itemObj.getString("shootTime");
                                            }
                                            if (!itemObj.isNull("weather")) {
                                                dto.weather = itemObj.getString("weather");
                                            }
                                            pack.photoWallList.add(dto);
                                        }
                                    }
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
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
