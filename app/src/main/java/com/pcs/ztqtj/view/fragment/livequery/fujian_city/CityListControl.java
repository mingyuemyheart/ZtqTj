package com.pcs.ztqtj.view.fragment.livequery.fujian_city;

import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Z on 2017/5/26.
 */
public class CityListControl {

    private List<PackLocalCity> listParentCityInfo;
    private List<PackLocalCity> listChilCityInfo;
    private PackLocalCity cutParent = new PackLocalCity();// 当前父类别呗选择的城市
    private PackLocalCity cutChild = new PackLocalCity();//当前子列表被选择的城市

    private List<String> listParentCityInfoShowName;
    private List<String> listChilCityInfoShowName;

    public CityListControl(PackLocalCity mainCity) {
        this(mainCity, false);
    }

    public CityListControl(PackLocalCity mainCity, boolean isLevelCity2) {
        listParentCityInfoShowName = new ArrayList<>();
        listChilCityInfoShowName = new ArrayList<>();
        listParentCityInfo = new ArrayList<>();
        listParentCityInfo.addAll(ZtqCityDB.getInstance().getProvincesLiveQueryList());
        if(mainCity.isFjCity) {
            List<PackLocalCity> tempList = ZtqCityDB.getInstance().getCityLv1();
            PackLocalCity city = ZtqCityDB.getInstance().getCityInfo1_ID(mainCity.ID);
            if(ZtqCityDB.getInstance().isServiceAccessible()) {
                if (city == null) {
                    if (tempList == null || tempList.size() == 0) {
                        city = mainCity;
                    } else {
                        city = tempList.get(0);
                    }
                }
                if (city.ID.equals("1279") && tempList != null && tempList.size() > 0) {
                    city.copyCity(tempList.get(0));
                }
            } else {
                if (tempList == null || tempList.size() == 0) {
                    city = mainCity;
                } else {
                    city = tempList.get(0);
                }
            }
            cutChild.copyCity(city);
        } else {
            List<PackLocalCity> tempList = ZtqCityDB.getInstance().getCountryCityList(mainCity.PARENT_ID);
            if(tempList != null && tempList.size() > 0) {
                cutChild.copyCity(tempList.get(0));
                for(PackLocalCity city : tempList) {
                    if(city.NAME.equals(mainCity.NAME)) {
                        cutChild.copyCity(city);
                        break;
                    }
                }
            } else {
                cutChild.copyCity(mainCity);
            }
        }

        if(cutChild != null) {
            for(PackLocalCity bean : listParentCityInfo) {
                if(bean.ID.equals(cutChild.PARENT_ID)) {
                    cutParent.copyCity(bean);
                    break;
                }
            }
        }

        for (int i = 0; i < listParentCityInfo.size(); i++) {
            listParentCityInfoShowName.add(listParentCityInfo.get(i).NAME);
        }
        listChilCityInfo = new ArrayList<>();
        reflushChildList();
    }


    private void reflushChildList() {
        listChilCityInfoShowName.clear();
        listChilCityInfo = new ArrayList<>();
        if(cutParent.NAME.equals("天津")) {
            listChilCityInfo.addAll(ZtqCityDB.getInstance().getCityLv1());
        } else {
            listChilCityInfo.addAll(ZtqCityDB.getInstance().getCountryCityList(cutParent.ID));
        }
        for (int i = 0; i < listChilCityInfo.size(); i++) {
            listChilCityInfoShowName.add(listChilCityInfo.get(i).NAME);
        }
    }

    public List<PackLocalCity> getParentCityInfo() {
        return listParentCityInfo;
    }

    public List<PackLocalCity> getChilCityInfo() {
        return listChilCityInfo;
    }

    public void checkParent(int position) {
        try {
            cutParent.copyCity(listParentCityInfo.get(position));
            reflushChildList();
            checkChild(0);//默认获取第一个城市名称。
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void checkChild(int position) {
        cutChild.copyCity(listChilCityInfo.get(position));
    }

    public List<String> getParentShowNameList() {
        return listParentCityInfoShowName;
    }

    public List<String> getChildShowNameList() {
        return listChilCityInfoShowName;
    }

    /***
     * 是否获取全部的数据
     *
     *  如果子列表的地区信息跟当前父列表的信息一致。则获取全部信息
     * @return
     */
    public Boolean getParentData() {
        if (cutParent != null && cutParent.PARENT_ID.equals(cutChild.PARENT_ID)) {
            return true;
        } else {
            return false;
        }
    }

    public PackLocalCity getCutParentCity() {
        return cutParent;
    }

    public PackLocalCity getCutChildCity() {
        return cutChild;
    }
}
