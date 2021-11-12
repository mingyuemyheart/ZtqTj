package com.pcs.ztqtj.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ColumnDto implements Parcelable {

    public String dataCode,dataName,dataType,desc,parentId,icon,flag,url;

    public ArrayList<ColumnDto> childList = new ArrayList<>();

    public ColumnDto() {

    }

    protected ColumnDto(Parcel in) {
        dataCode = in.readString();
        dataName = in.readString();
        dataType = in.readString();
        desc = in.readString();
        parentId = in.readString();
        icon = in.readString();
        flag = in.readString();
        url = in.readString();
        childList = in.createTypedArrayList(ColumnDto.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dataCode);
        dest.writeString(dataName);
        dest.writeString(dataType);
        dest.writeString(desc);
        dest.writeString(parentId);
        dest.writeString(icon);
        dest.writeString(flag);
        dest.writeString(url);
        dest.writeTypedList(childList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ColumnDto> CREATOR = new Creator<ColumnDto>() {
        @Override
        public ColumnDto createFromParcel(Parcel source) {
            return new ColumnDto(source);
        }

        @Override
        public ColumnDto[] newArray(int size) {
            return new ColumnDto[size];
        }
    };
}
