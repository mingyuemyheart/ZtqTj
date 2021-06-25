package com.pcs.ztqtj.control.loading;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalInit;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.command.AbstractCommand;
import com.pcs.ztqtj.model.ZtqCityDB;

/**
 * 闪屏页面，单位名称
 */
public class CommandLoadingUnit extends AbstractCommand {

    private Activity mActivity;

    public CommandLoadingUnit(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void execute() {
        super.execute();
        setStatus(Status.SUCC);
        TextView text = mActivity.findViewById(R.id.text_observatory);
        ImageView iv_observatory = mActivity.findViewById(R.id.iv_observatory);
        PackLocalInit packInit = (PackLocalInit) PcsDataManager.getInstance().getLocalPack(PackLocalInit.KEY);
        if (packInit == null || !packInit.isNotFirst) {
//            第一次初始app显示
            text.setText("天津市气象局");
        } else {
//            非第一次初始化显示
            noFirstInit(text);
        }
        iv_observatory.setVisibility(View.VISIBLE);
    }

    private void noFirstInit(TextView text) {
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        if (cityMain == null || TextUtils.isEmpty(cityMain.ID) || !cityMain.isFjCity) {
            text.setText("天津市气象局");
            return;
        }
        String name = cityMain.NAME;
        if (name.equals("天津市区")) {
            text.setText("天津市气象局");
        } else if (name.equals("宝坻区") || name.equals("北辰区") || name.equals("东丽区") || name.equals("滨海新区") || name.equals("静海区")
        || name.equals("蓟州区") || name.equals("津南区") || name.equals("武清区") || name.equals("宁河区") || name.equals("西青区")){
            text.setText(name + "气象局");
        } else {
            text.setText("天津市气象局");
        }
    }
}
