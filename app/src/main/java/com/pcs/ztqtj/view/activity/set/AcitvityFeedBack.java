package com.pcs.ztqtj.view.activity.set;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUserInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSuggDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSuggUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSuggestListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSuggestListUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.SuggestListInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.set.abs_feed_tu.AbsActivityFeekTu;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置-您的建议
 */
public class AcitvityFeedBack extends AbsActivityFeekTu {

    public void proInitData() {
        Bundle bundle = getIntent().getExtras();
        setTitleText(bundle.getString("title"));
    }

    private PackSuggestListUp packSuggestListUp;
    public List<SuggestListInfo> arrsuggestListInfo = new ArrayList<>();

    @Override
    public void reqComment() {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog();
        /**
         * 请求反馈列表
         **/
        packSuggestListUp = new PackSuggestListUp();
        packSuggestListUp.count = "20";
        PcsDataDownload.addDownload(packSuggestListUp);
    }

    @Override
    public void receiverBack(String nameStr, String errorStr) {
        if (packSuggestListUp != null && packSuggestListUp.getName().equals(nameStr)) {
            dismissProgressDialog();
            arrsuggestListInfo.clear();
            PackSuggestListDown packSuggestListDown =
                    (PackSuggestListDown) PcsDataManager.getInstance().getNetPack(nameStr);
            if (packSuggestListDown == null || packSuggestListDown.arrsuggestListInfo.size() == 0) {
                return;
            }
            arrsuggestListInfo.addAll(packSuggestListDown.arrsuggestListInfo);
            reflushListView(arrsuggestListInfo);
        } else if (uppack != null && uppack.getName().equals(nameStr)) {
            dismissProgressDialog();
            PackSuggDown down = (PackSuggDown) PcsDataManager.getInstance().getNetPack(nameStr);
            if (down != null && down.result.equals("1")) {
                Toast.makeText(AcitvityFeedBack.this, "您反馈的意见已收录！感谢您的建议！", Toast.LENGTH_SHORT).show();
                PackLocalUser myUserInfo = ZtqCityDB.getInstance().getMyInfo();
                if (TextUtils.isEmpty(myUserInfo.mobile)) {
                    myUserInfo.mobile = phoneNum;
                    PackLocalUserInfo packLocalUserInfo = new PackLocalUserInfo();
                    packLocalUserInfo.currUserInfo = myUserInfo;
                    ZtqCityDB.getInstance().setMyInfo(packLocalUserInfo);
                }
                cleanUpInfo();
                reqComment();
            } else {
                Toast.makeText(AcitvityFeedBack.this, "提交失败咯。麻烦您重新提交。", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void commitInformation(String upContent, String phoneNumber) {
        reqNet(upContent, phoneNumber);
    }

    private PackSuggUp uppack;

    private void reqNet(String msg, String contact) {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog();
        uppack = new PackSuggUp();
        uppack.call_way = contact;
        uppack.user_id = ZtqCityDB.getInstance().getMyInfo().sys_user_id;
        if (TextUtils.isEmpty(
                ZtqCityDB.getInstance().getMyInfo().mobile)) {
            uppack.is_bd = "1";
            uppack.mobile = contact;
        }
        uppack.msg = msg;
        PcsDataDownload.addDownload(uppack);
    }
}
