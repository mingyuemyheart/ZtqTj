package com.pcs.ztqtj.model;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalSetUpdate;

/**
 * 设置数据库
 *
 * @author JiangZy
 */
public class SettingDB {
    private static SettingDB instance = null;

    public static SettingDB getInstance() {
        if (instance == null) {
            instance = new SettingDB();
        }

        return instance;
    }

    /**
     * 更新设置
     */
    private PackLocalSetUpdate mPackLocalSetUpdate = null;

    /**
     * 获取更新配置
     *
     * @return
     */
    public PackLocalSetUpdate getSetUpdate() {
        if (mPackLocalSetUpdate == null) {
            mPackLocalSetUpdate = (PackLocalSetUpdate) PcsDataManager.getInstance().getLocalPack(PackLocalSetUpdate.KEY);
        }

        if (mPackLocalSetUpdate == null) {
            mPackLocalSetUpdate = new PackLocalSetUpdate();
        }
        return mPackLocalSetUpdate;
    }

    /**
     * 保存更新配置
     *
     * @param pack
     */
    public void saveSetUpdate(PackLocalSetUpdate pack) {
        mPackLocalSetUpdate = pack;
        PcsDataManager.getInstance().saveLocalData(PackLocalSetUpdate.KEY, mPackLocalSetUpdate);
    }
}
