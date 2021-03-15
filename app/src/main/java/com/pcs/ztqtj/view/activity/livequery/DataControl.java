package com.pcs.ztqtj.view.activity.livequery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackRainStandardDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackRainStandardUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.LevelListBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjImgUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjMaxHourDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjMaxHourUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjRankDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjRankDown.RainFallRank;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltj_1_3Down;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltj_1_3Up;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltj_level_rankingDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltj_level_rankingDown.ItemHour;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltj_level_rankingUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltj_level_ranking_detailDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltj_level_ranking_detailUp;

import java.util.ArrayList;
import java.util.List;

public class DataControl {
    private static DataControl instance;

    /**
     * 清空对象
     */
    public void cleanData() {
        instance = null;
    }

    public static DataControl getInstance(Context context) {
        if (instance == null) {
            instance = new DataControl(context);
        }
        return instance;
    }

    private Context activity;

    private DataControl(Context context) {
        this.activity = context;
    }

    // 雨量查询—省内24小时内最大降雨量排名yltj_max_hour前6
    private List<RainFallRank> rain24Max1Hour = new ArrayList<RainFallRank>();
    private List<RainFallRank> Rain24Max3Hour = new ArrayList<RainFallRank>();
    private List<RainFallRank> Rain24Max6Hour = new ArrayList<RainFallRank>();
    private List<RainFallRank> Rain24Max12Hour = new ArrayList<RainFallRank>();
    private List<RainFallRank> Rain24Max24Hour = new ArrayList<RainFallRank>();

    private List<RainFallRank> _1_3_hourRanking = new ArrayList<RainFallRank>();

    /**
     * 获取1、3小时数据
     */
    public List<RainFallRank> get_1_3_hourRanking() {
        return _1_3_hourRanking;
    }

    /**
     * 省内24小时内最大降雨量排名 --1小时
     *
     * @return
     */
    public List<RainFallRank> getRain24Max1Hour() {
        return rain24Max1Hour;
    }

    /**
     * 省内24小时内最大降雨量排名 --3小时
     *
     * @return
     */
    public List<RainFallRank> getRain24Max3Hour() {
        return Rain24Max3Hour;
    }

    /**
     * 省内24小时内最大降雨量排名 --6小时
     *
     * @return
     */
    public List<RainFallRank> getRain24Max6Hour() {
        return Rain24Max6Hour;
    }

    /**
     * 省内24小时内最大降雨量排名 --12小时
     *
     * @return
     */
    public List<RainFallRank> getRain24Max12Hour() {
        return Rain24Max12Hour;
    }

    /**
     * 省内24小时内最大降雨量排名 --24小时
     *
     * @return
     */
    public List<RainFallRank> getRain24Max24Hour() {
        return Rain24Max24Hour;
    }

    /**
     * 获取数据
     */
    public void request() {

        // 降雨量图片
        PackYltjImgUp imageUp = new PackYltjImgUp();
        PcsDataDownload.addDownload(imageUp);
        // 雨量查询—省内24小时内最大降雨量排名yltj_max_hour前6
        PackYltjMaxHourUp yltjMaxHourUp = new PackYltjMaxHourUp();
        yltjMaxHourUp.falg = "1";
        PcsDataDownload.addDownload(yltjMaxHourUp);

        PackYltjMaxHourUp yltjMax3HourUp = new PackYltjMaxHourUp();
        yltjMax3HourUp.falg = "3";
        PcsDataDownload.addDownload(yltjMax3HourUp);

        PackYltjMaxHourUp yltjMax6HourUp = new PackYltjMaxHourUp();
        yltjMax6HourUp.falg = "6";
        PcsDataDownload.addDownload(yltjMax6HourUp);

        PackYltjMaxHourUp yltjMax12HourUp = new PackYltjMaxHourUp();
        yltjMax12HourUp.falg = "12";
        PcsDataDownload.addDownload(yltjMax12HourUp);

        PackYltjMaxHourUp yltjMax24HourUp = new PackYltjMaxHourUp();
        yltjMax24HourUp.falg = "24";
        PcsDataDownload.addDownload(yltjMax24HourUp);

        // 雨量查询—福建省内近1、3小时最大雨量排行
        PackYltj_1_3Up yltj_1_3Up = new PackYltj_1_3Up();
        PcsDataDownload.addDownload(yltj_1_3Up);

        // 降雨量分级统计方式
        PackYltj_level_rankingUp yltj_level_Up = new PackYltj_level_rankingUp();
        PcsDataDownload.addDownload(yltj_level_Up);

        PackRainStandardUp pacnRainUp = new PackRainStandardUp();//降雨量标准说明；
        PcsDataDownload.addDownload(pacnRainUp);

    }

    private PackYltj_level_ranking_detailUp level_ranking_detailUp;

    private PackRainStandardDown rainStanderdDown;

    public PackRainStandardDown getRainStanderdDown() {
        return rainStanderdDown;
    }


    /**
     * 获取等级统计分析详细数据
     */
    public void getLevelData(String sign) {
        level_ranking_detailUp = new PackYltj_level_ranking_detailUp();
        level_ranking_detailUp.sign = sign;
        PcsDataDownload.addDownload(level_ranking_detailUp);
    }

    /**
     * 24小时内最大降雨量排名下来列表显示数据
     */
    public List<String> getMaxDraw() {
        List<String> dataeaum = new ArrayList<String>();
        dataeaum.add("1小时");
        dataeaum.add("3小时");
        dataeaum.add("6小时");
        dataeaum.add("12小时");
        dataeaum.add("24小时");
        return dataeaum;
    }

    /**
     * 获取降雨量等级统计小时
     */
    public List<String> getLevelHour() {
        List<String> dataeaum = new ArrayList<String>();
        for (int i = 0; i < level_data.size(); i++) {
            dataeaum.add(level_data.get(i).name);
        }
        return dataeaum;
    }

    /**
     * 获取降雨量等级统计区间
     */
    public List<String> getLevelCount(int position) {
        List<String> dataeaum = new ArrayList<String>();
        for (int i = 0; i < level_data.get(position).subDataList.size(); i++) {
            dataeaum.add(level_data.get(position).subDataList.get(i).name);
        }
        return dataeaum;
    }

    /**
     * 获取24小时内最大降雨量排名的顶部标题说明栏目
     *
     * @return
     */
    public RainFallRank getMax24Title() {
        RainFallRank titleMaxRain = new PackYltjRankDown().new RainFallRank();
        titleMaxRain.area_name = "站点";
        titleMaxRain.time = "日期/时段";
        titleMaxRain.rainfall = "雨量";
        return titleMaxRain;
    }

    /**
     * 数据返回处理
     */
    public void successDealWidth() {
        // 服务器返回数据是处理数据
        cleanAllList();
        // 雨量查询—省内24小时内最大降雨量排名yltj_max_hour前6
        analyticalData(rain24Max1Hour, "1");
        analyticalData(Rain24Max3Hour, "3");
        analyticalData(Rain24Max6Hour, "6");
        analyticalData(Rain24Max12Hour, "12");
        analyticalData(Rain24Max24Hour, "24");

        // 解析1、3小时数据
        analytical_1_3();

        // 降雨量分级统计方式
        analytical_level();
    }

    /**
     * 降雨量分级统计方式
     */
    private List<ItemHour> level_data = new ArrayList<ItemHour>();

    /**
     * 降雨量等级统计分析
     *
     * @return
     */
    public List<ItemHour> getLevel_data() {
        return level_data;
    }

    /**
     * 降雨量分级统计方式
     */
    private void analytical_level() {
        try {
            PackYltj_level_rankingDown down = (PackYltj_level_rankingDown) PcsDataManager.getInstance().getNetPack(PackYltj_level_rankingUp.NAME);
            if (down == null) {
            } else {
                level_data.clear();
                level_data.addAll(down.dataList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析1、3小时数据
     */
    private void analytical_1_3() {

        try {
            PackYltj_1_3Up m1_3Up = new PackYltj_1_3Up();

            PackYltj_1_3Down down = (PackYltj_1_3Down) PcsDataManager.getInstance().getNetPack(m1_3Up.getName());
            if (down == null) {
            } else {
                _1_3_hourRanking.clear();

                _1_3_hourRanking.addAll(down.dataList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 请空数据
     */
    private void cleanAllList() {
        rain24Max1Hour.clear();
        Rain24Max3Hour.clear();
        Rain24Max6Hour.clear();
        Rain24Max12Hour.clear();
        Rain24Max24Hour.clear();
    }

    /**
     * 解析数据 1 3 6 12 24小时数据
     *
     * @param ranklist
     * @param key
     */
    private void analyticalData(List<RainFallRank> ranklist, String key) {

        try {
            PackYltjMaxHourUp yltj = new PackYltjMaxHourUp();
            yltj.falg = key;
            PackYltjMaxHourDown yltjMaxHourDwon = (PackYltjMaxHourDown) PcsDataManager.getInstance().getNetPack(yltj.getName());
            if (yltjMaxHourDwon == null) {
            } else {
                ranklist.clear();
                ranklist.addAll(yltjMaxHourDwon.datalist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PopupWindow createPopupWindow(final TextView dropDownView, final List<String> dataeaum, final ListSelect listener, final String key) {
        AdapterData dataAdapter = new AdapterData(activity, dataeaum);
        View popcontent = LayoutInflater.from(activity).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        popcontent.setBackgroundColor(activity.getResources().getColor(R.color.bg_white));
        final PopupWindow pop = new PopupWindow(activity);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth(dropDownView.getWidth());
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setFocusable(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                dropDownView.setText(dataeaum.get(position));
                listener.itemClick(position, key);
            }
        });
        return pop;
    }

    public interface ListSelect {
        public void itemClick(int position, String key);
    }

    /**
     * 更多城市信息点击后保存信息
     *
     * @param postion
     */
    public void moreInfo(int postion) {
        moreData = null;
        // 更多城市数据在这
        moreData = levelData.get(postion);
    }

    private LevelListBean moreData = new LevelListBean();

    /**
     * 获取更多数据
     *
     * @return
     */
    public LevelListBean getMoreData() {
        return moreData;
    }

    /**
     * 等级分析列表数据
     *
     * @return
     */
    public List<LevelListBean> getLevelData() {
        if (levelData.size() == 0) {
            levelData.add(getLevelTitle());
            levelData.add(getNullData());
        }
        return levelData;
    }

    /**
     * 等级分析发布时间
     *
     * @return
     */
    public String getLevel_start_time() {
        return level_start_time;
    }

    /**
     * 等级分析统计时间
     *
     * @return
     */
    public String getLevel_release_time() {
        return level_release_time;
    }

    /**
     * 等级分析列表数据
     */
    private List<LevelListBean> levelData = new ArrayList<LevelListBean>();
    /**
     * 等级分析发布时间
     */
    private String level_start_time = "";
    /**
     * 等级分析统计时间
     */
    private String level_release_time = "";

    public void successRainStandard() {
        rainStanderdDown = (PackRainStandardDown) PcsDataManager.getInstance().getNetPack(PackRainStandardUp.NAME);
    }

    public void successLevelDetail() {
        level_start_time = "";
        level_release_time = "";
        try {
            PackYltj_level_ranking_detailDown yltjMaxHourDwon = (PackYltj_level_ranking_detailDown) PcsDataManager.getInstance().getNetPack(level_ranking_detailUp.getName());
            if (yltjMaxHourDwon == null) {
            } else {
                levelData.clear();
                level_start_time = yltjMaxHourDwon.stat_grading;
                level_release_time = yltjMaxHourDwon.pub_time;
                if (yltjMaxHourDwon.rain_list.size() > 0) {
                    levelData.add(getLevelTitle());
                }
                for (int i = 0; i < yltjMaxHourDwon.rain_list.size(); i++) {
                    LevelListBean bean = new LevelListBean();
                    bean.isTitle = true;
                    bean.rain_station_list = yltjMaxHourDwon.rain_list.get(i).rain_station_list;
                    bean.titleName = yltjMaxHourDwon.rain_list.get(i).name + ":" + bean.rain_station_list.size() + "个";
                    if (bean.rain_station_list.size() > ROWSIZE) {
                        bean.isSmallTen = false;
                    } else {
                        bean.isSmallTen = true;
                    }
                    levelData.add(bean);
                    for (int j = 0; j < bean.rain_station_list.size(); j++) {
                        if (j < ROWSIZE) {
                            LevelListBean item_data = new LevelListBean();
                            item_data.isTitle = false;
                            item_data.stationData = bean.rain_station_list.get(j);
                            levelData.add(item_data);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static final int ROWSIZE = 3;

    public LevelListBean getLevelTitle() {
        PackYltj_level_ranking_detailDown down = new PackYltj_level_ranking_detailDown();
        LevelListBean item_data = new LevelListBean();
        item_data.isTitle = false;
        item_data.stationData = down.new StationData();
        item_data.stationData.area_name = "站点";
        item_data.stationData.order_id = "序号";
        item_data.stationData.rainfall = "雨量（mm）";
        return item_data;
    }

    public LevelListBean getNullData() {
        LevelListBean bean = new LevelListBean();
        bean.isTitle = true;
        bean.titleName = "全省:0个";
        bean.isSmallTen = true;
        return bean;
    }

}
