package com.pcs.ztqtj.view.activity.life;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 健康气象
 */
public class HealthDto implements Parcelable {

    public String columnName,dataTime,aqi,aqiLevel,aqiTips,hazeTips,aqiswTips,wrqxtjTips,wrqxtj;

    //指数
    public String code,level,name,tips;

    public String indexName,indexDesc,indexContent,indexMonth,indexIcon;
    public boolean isFirst;

    public HealthDto() {
    }

    protected HealthDto(Parcel in) {
        columnName = in.readString();
        dataTime = in.readString();
        aqi = in.readString();
        aqiLevel = in.readString();
        aqiTips = in.readString();
        hazeTips = in.readString();
        aqiswTips = in.readString();
        wrqxtjTips = in.readString();
        wrqxtj = in.readString();
    }

    public static final Creator<HealthDto> CREATOR = new Creator<HealthDto>() {
        @Override
        public HealthDto createFromParcel(Parcel in) {
            return new HealthDto(in);
        }

        @Override
        public HealthDto[] newArray(int size) {
            return new HealthDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(columnName);
        dest.writeString(dataTime);
        dest.writeString(aqi);
        dest.writeString(aqiLevel);
        dest.writeString(aqiTips);
        dest.writeString(hazeTips);
        dest.writeString(aqiswTips);
        dest.writeString(wrqxtjTips);
        dest.writeString(wrqxtj);
    }
}
