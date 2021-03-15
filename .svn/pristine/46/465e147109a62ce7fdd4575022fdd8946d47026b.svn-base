package com.pcs.ztqtj.control.loading;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.command.AbstractCommand;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalInit;

/**
 * 显示单位
 *
 * @author JiangZY
 */
public class CommandLoadingUnit extends AbstractCommand {

    private Activity mActivity;

    public CommandLoadingUnit(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void execute() {
        super.execute();
        Log.e("jzy", "执行CommandLoadingUnit");
        setStatus(Status.SUCC);
        TextView text = (TextView) mActivity.findViewById(R.id.text_observatory);
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
        } else {
            text.setText(name + "气象局");
//            name = name.replace("天津", "").replace("区", "");
//            String unit = ZtqCityDB.getInstance().getUnitByCity(name);
//            if(TextUtils.isEmpty(unit)) {
//                text.setText("天津市气象局");
//            } else {
//                text.setText(unit);
//            }
        }
    }
}
