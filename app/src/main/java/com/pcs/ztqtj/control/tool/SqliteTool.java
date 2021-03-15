package com.pcs.ztqtj.control.tool;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pcs.ztqtj.model.pack.TrafficHighWay;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityUnit;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalStation;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalWarn;

import java.io.File;
import java.util.List;

public class SqliteTool {
    private static SqliteTool sqlHolder;
    private SQLiteDatabase db = null;
    private Context context;

    private SqliteTool() {
    }

    public static SqliteTool getInstance() {
        if (sqlHolder == null) {
            sqlHolder = new SqliteTool();
        }
        return sqlHolder;
    }

    /**
     * @param context
     * @param dbAbsPath E:"/mnt/sdcard/pcs.db"
     */
    public void openDB(Context context, String dbAbsPath) {
        if (db != null && db.isOpen()) {
            return;
        }
        this.context = context;
        File f_table = new File(dbAbsPath);
        if (!f_table.exists()) {
            return;
        }

        this.db = SQLiteDatabase.openOrCreateDatabase(dbAbsPath, null);
    }

    public void closeDB() {
        if (this.db.isOpen()) {
            this.db.close();
        }
    }

    /**
     * 查询数据
     *
     * @param tableName TableZTQ.TABLE_NAME;
     * @return
     */
    public void getInfo(String tableName, List<PackLocalCity> mListCity) {
        mListCity.clear();
        String sql = "SELECT * FROM " + tableName;
        try {
            if (db.isOpen()) {
                Cursor result = this.db.rawQuery(sql, null);
                for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
                    PackLocalCity cityInfo = new PackLocalCity();
                    cityInfo.ID = result.getString(0);
                    cityInfo.NAME = result.getString(1);
                    cityInfo.PARENT_ID = result.getString(2);
                    cityInfo.PINGYIN = result.getString(3);
                    cityInfo.PY = result.getString(4);
                    //cityInfo.isFjCity = isFjCity;
                    cityInfo.isFjCity = cityInfo.PARENT_ID.equals("25148")
                            || cityInfo.PARENT_ID.equals("25182") || cityInfo.ID.equals("25182") || cityInfo.ID.equals("25148");
                    if (result.getString(5) != null) {
                        cityInfo.CITY = result.getString(5);
                    } else {
                        cityInfo.CITY = "";
                    }
                    mListCity.add(cityInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询预警信息数据
     *
     * @return
     */
    public void getWarnInfo(String tableName, List<PackLocalWarn> mListWarn) {
        mListWarn.clear();
        String sql = "SELECT * FROM " + tableName;
        try {
            if (db.isOpen()) {
                Cursor result = this.db.rawQuery(sql, null);
//			int a=0;
                for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
                    PackLocalWarn warnInfo = new PackLocalWarn();
                    warnInfo.TYPE = result.getString(0);
                    warnInfo.LEVEL_STR = result.getString(1);
                    warnInfo.ICO = result.getString(2);
                    mListWarn.add(warnInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询单位信息数据
     *
     * @param tableName
     * @param mListUnit
     */
    public void getUnitInfo(String tableName, List<PackLocalCityUnit> mListUnit) {
        mListUnit.clear();
        String sql = "SELECT * FROM " + tableName;
        try {
            if (db.isOpen()) {
                Cursor result = this.db.rawQuery(sql, null);
//			int a=0;
                for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
                    PackLocalCityUnit warnInfo = new PackLocalCityUnit();
                    warnInfo.CITY = result.getString(0);
                    warnInfo.UNIT = result.getString(1);
                    mListUnit.add(warnInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询站点名称
     *
     * @param tableName
     * @param mListStation
     */
    public void getStaitonInfo(String tableName, List<PackLocalStation> mListStation) {
        mListStation.clear();
        String sql = "SELECT * FROM " + tableName;
        try {
            if (db.isOpen()) {
                Cursor result = this.db.rawQuery(sql, null);
//			int a=0;
                for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
                    PackLocalStation warnInfo = new PackLocalStation();
                    warnInfo.ID = result.getString(0);
                    warnInfo.STATIONID = result.getString(1);
                    warnInfo.STATIONNAME = result.getString(2);
                    warnInfo.LONGITUDE = result.getString(3);
                    warnInfo.LATITUDE = result.getString(4);
                    mListStation.add(warnInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTrafficHighWayInfo(String tableName, List<TrafficHighWay> mListTraffic) {
        mListTraffic.clear();
        String sql = "SELECT * FROM " + tableName;
        try {
            if (db.isOpen()) {
                Cursor result = this.db.rawQuery(sql, null);
//			int a=0;
                for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
                    TrafficHighWay trafficInfo = new TrafficHighWay();
                    trafficInfo.ID = result.getString(0);
                    trafficInfo.NAME = result.getString(1);
                    trafficInfo.SEARCH_NAME = result.getString(2);
                    String lat = result.getString(3);
                    String log = result.getString(4);
                    try {
                        trafficInfo.SHOW_LATITUDE = Double.parseDouble(lat);
                        trafficInfo.SHOW_LONGITUDE = Double.parseDouble(log);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    trafficInfo.IMAGE_PATH = result.getString(5);
                    trafficInfo.DETAIL_IMAGE = result.getString(6);
                    mListTraffic.add(trafficInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
