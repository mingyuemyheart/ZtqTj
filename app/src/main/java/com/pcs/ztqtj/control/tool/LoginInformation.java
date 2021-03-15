package com.pcs.ztqtj.control.tool;

import android.content.Context;
import android.text.TextUtils;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoLoginDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoLoginUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoUserInfoDown;
import com.pcs.ztqtj.R;


public class LoginInformation {
    private static LoginInformation instance;
    private PackPhotoLoginDown packDown;

    public static LoginInformation getInstance() {
        if (instance == null) {
            instance = new LoginInformation();
        }
        return instance;
    }

    private LoginInformation() {
        packDown = (PackPhotoLoginDown) PcsDataManager.getInstance().getNetPack(PackPhotoLoginUp.NAME);
        if (packDown == null) {
            packDown = new PackPhotoLoginDown();
        }
    }

    public PackPhotoLoginDown getLoginInfo() {
        return packDown;
    }

    public void savePhotoLoginDown(PackPhotoLoginDown packDown) {
        this.packDown = packDown;

        PcsDataManager.getInstance().saveLocalData(PackPhotoLoginUp.NAME, packDown);
    }

    public void savePhotoLoginDown() {
        PcsDataManager.getInstance().saveLocalData(PackPhotoLoginUp.NAME, packDown);
    }


    /**
     * 用户头像的URl
     */
    public String getUserIconUrl() {
        if (packDown == null || TextUtils.isEmpty(packDown.head_url)) {
            return "";
        } else {
            return packDown.head_url;
        }
    }

    /**
     * 获取用户头像URL
     *
     * @return
     */
    public String getUserHeadURL(Context context) {
        String imageURL = getUserIconUrl();
        if (PackPhotoLoginDown.PLATFORM_ZTQ == getPlatformType()) {
            imageURL = context.getString(R.string.file_download_url) + imageURL;
        }
        return imageURL;
    }

    /**
     * 用户名称
     */
    public String getUsername() {
        if (packDown == null || TextUtils.isEmpty(packDown.nick_name)) {
            return "";
        } else {
            return packDown.nick_name;
        }
    }


    /**
     * 用户phone
     */
    public String getUserPhone() {
        if (packDown == null || TextUtils.isEmpty(packDown.mobile)) {
            return "";
        } else {
            return packDown.mobile;
        }
    }

    /**
     * 返回平台类型
     */
    public String getPlatformType() {
        if (packDown == null || TextUtils.isEmpty(packDown.platform_type)) {
            return "";
        } else {
            return packDown.platform_type;
        }
    }

    /**
     * 用户ID
     */
    public String getUserId() {
        if (packDown == null || TextUtils.isEmpty(packDown.user_id)) {
            return "";
        } else {
            return packDown.user_id;
        }
    }

    public String getServiceId(){
        if (packDown == null || TextUtils.isEmpty(packDown.user_id)) {
            return "";
        } else {
            return packDown.fw_user_id;
        }
    }

    /**
     * 重设值
     */
    public void reSetValue(PackPhotoLoginDown packDown) {
        this.packDown = packDown;
    }

    /**
     * 是否有登录
     *
     * @return
     */
    public boolean hasLogin() {
        return !(packDown == null || TextUtils.isEmpty(packDown.user_id));
    }

    /**
     * 清除登录信息
     */
    public void clearLoginInfo() {
        PcsDataManager.getInstance().removeLocalData(PackPhotoLoginUp.NAME);
        instance = null;
    }

    /**
     * 更新用户信息
     * @param info
     */
    public void updateInfo(PackPhotoUserInfoDown info) {
        if(packDown != null) {
            packDown.nick_name = info.nick_name;
            packDown.head_url = info.head_url;
            //packDown.mobile = info.mobile;
        }
    }

}
