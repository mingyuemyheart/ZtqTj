package com.pcs.ztqtj.view.activity.product.media;

import android.text.TextUtils;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMediaTauntedDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMediaTauntedListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMediaTauntedListUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMediaTauntedUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.SuggestListInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.media.MediaInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.set.abs_feed_tu.AbsActivityFeekTu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Z on 2016/6/3.
 * 影视吐槽
 */
public class ActivityMediaTaunted extends AbsActivityFeekTu {
    public void proInitData() {
        MediaInfo mediaInfo = (MediaInfo) getIntent().getSerializableExtra("mediaInfo");
        String title = mediaInfo.title_two;
        channel_id = mediaInfo.channel_id;
        if (!TextUtils.isEmpty(title)) {
            setTitleText(title);
        } else {
            if (!TextUtils.isEmpty(mediaInfo.title)) {
                setTitleText(mediaInfo.title);
            } else {
                setTitleText("气象影视");
            }
        }
    }

    private PackMediaTauntedListUp packTauntedListUp;
    private String channel_id;

    @Override
    public void reqComment() {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        /**
         * 请求反馈列表
         **/
        packTauntedListUp = new PackMediaTauntedListUp();
        packTauntedListUp.channel_id = channel_id;
        PcsDataDownload.addDownload(packTauntedListUp);

    }

    @Override
    public void receiverBack(String nameStr, String errorStr) {

        if (packTauntedListUp != null && packTauntedListUp.getName().equals(nameStr)) {
            dismissProgressDialog();
            arrsuggestListInfo.clear();
            PackMediaTauntedListDown packSuggestListDown = (PackMediaTauntedListDown) PcsDataManager.getInstance().getNetPack(nameStr);
            if (packSuggestListDown == null || packSuggestListDown.arrsuggestListInfo.size() == 0) {
                return;
            }
            arrsuggestListInfo.addAll(packSuggestListDown.arrsuggestListInfo);
            reflushListView(arrsuggestListInfo);
        } else if (upPackSuggest != null && upPackSuggest.getName().equals(nameStr)) {
            dismissProgressDialog();
            down = (PackMediaTauntedDown) PcsDataManager.getInstance().getNetPack(nameStr);
            if (down != null && down.result.equals("1")) {
                Toast.makeText(ActivityMediaTaunted.this, "您反馈的意见已收录！感谢您的建议！", Toast.LENGTH_SHORT).show();
                if (TextUtils.isEmpty(MyApplication.MOBILE)) {
                    MyApplication.MOBILE = phoneNum;
                }
                cleanUpInfo();
                reqComment();
            } else {
                Toast.makeText(ActivityMediaTaunted.this, "提交失败咯。麻烦您重新提交。", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void commitInformation(String upContent, String phoneNumber) {
        showProgressDialog();
        reqNet(upContent, phoneNumber);
    }

    private PackMediaTauntedDown down;
    public List<SuggestListInfo> arrsuggestListInfo = new ArrayList<SuggestListInfo>();
    private PackMediaTauntedUp upPackSuggest;

    private void reqNet(String msg, String contact) {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        upPackSuggest = new PackMediaTauntedUp();
        upPackSuggest.call_way = contact;
        upPackSuggest.user_id = MyApplication.UID;
        upPackSuggest.msg = msg;
        upPackSuggest.nick_name = MyApplication.NAME;
        if (TextUtils.isEmpty(MyApplication.MOBILE)) {
            upPackSuggest.is_bd = "1";
            upPackSuggest.mobile = contact;
        }
        upPackSuggest.channel_id = channel_id;
        PcsDataDownload.addDownload(upPackSuggest);
    }
}
