package com.pcs.ztqtj.view.activity.prove;

import android.os.Parcel;
import android.os.Parcelable;

public class ProveDto implements Parcelable {

    public String columnName;
    public String flag;//flag:Y：已完成，N:未完成，全部：不传
    public String contactsName;//姓名
    public String type;//1：保险理赔，2：其他专用
    public String companyName;//保险公司名称
    public String policyNumber;//保单号
    public String disPosition;//受灾地点
    public double lat = 0, lng = 0;
    public String createTime;
    public String status;//审核状态
    public String auditOpinion;//审核意见
    public String pdfPath;//下载链接

    public String imgName,imgUrl;
    public long fileSize;//文件大小
    public int drawable;
    public boolean isSelected;
    public boolean isShowSelected;

    public ProveDto() {

    }

    protected ProveDto(Parcel in) {
        columnName = in.readString();
        flag = in.readString();
        contactsName = in.readString();
        type = in.readString();
        companyName = in.readString();
        policyNumber = in.readString();
        disPosition = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        createTime = in.readString();
        status = in.readString();
        auditOpinion = in.readString();
        pdfPath = in.readString();
        imgName = in.readString();
        imgUrl = in.readString();
        fileSize = in.readLong();
        drawable = in.readInt();
        isSelected = in.readByte() != 0;
        isShowSelected = in.readByte() != 0;
    }

    public static final Creator<ProveDto> CREATOR = new Creator<ProveDto>() {
        @Override
        public ProveDto createFromParcel(Parcel in) {
            return new ProveDto(in);
        }

        @Override
        public ProveDto[] newArray(int size) {
            return new ProveDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(columnName);
        dest.writeString(flag);
        dest.writeString(contactsName);
        dest.writeString(type);
        dest.writeString(companyName);
        dest.writeString(policyNumber);
        dest.writeString(disPosition);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(createTime);
        dest.writeString(status);
        dest.writeString(auditOpinion);
        dest.writeString(pdfPath);
        dest.writeString(imgName);
        dest.writeString(imgUrl);
        dest.writeLong(fileSize);
        dest.writeInt(drawable);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeByte((byte) (isShowSelected ? 1 : 0));
    }
}
