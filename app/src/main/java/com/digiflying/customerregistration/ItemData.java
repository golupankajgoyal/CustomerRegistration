package com.digiflying.customerregistration;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemData implements Parcelable {

    private String imageUrl=null;
    private String name=null;
    private String address=null;
    private String addhar=null;
    private String mobile=null;
    private String employeeId=null;
    private String document=null;
    private String addharFront=null;
    private String addharBack=null;
    private String wefareId=null;


    public ItemData(String mName, String mAddress, String mAddhar, String mMobile,
                    String mEmployeeId, String mImage, String mDocument, String mAddharFront, String mAddharBack,String mWelfareId){


        name=mName;
        address=mAddress;
        addhar=mAddhar;
        mobile=mMobile;
        employeeId=mEmployeeId;
        imageUrl=mImage;
        document=mDocument;
        addharFront=mAddharFront;
        addharBack=mAddharBack;
        wefareId=mWelfareId;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public String getName(){
        return name;
    }

    public  String getAddress(){
        return address;
    }

    public String getAddhar(){
        return addhar;
    }

    public String getMobile(){
        return mobile;
    }

    public String getEmployeeId(){
        return employeeId;
    }

    public String getDocument(){
        return document;
    }

    public String getAddharFront(){
        return addharFront;
    }

    public String getAddharBack(){
        return addharBack;
    }

    public String getWefareId(){
        return wefareId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(addhar);
        dest.writeString(mobile);
        dest.writeString(employeeId);
        dest.writeString(imageUrl);
        dest.writeString(document);
        dest.writeString(addharFront);
        dest.writeString(addharBack);
        dest.writeString(wefareId);
    }

    protected ItemData(Parcel in) {
        name = in.readString();
        address = in.readString();
        addhar = in.readString();
        mobile = in.readString();
        employeeId= in.readString();
        imageUrl= in.readString();
        document=in.readString();
        addharFront= in.readString();
        addharBack= in.readString();
        wefareId= in.readString();
    }

    public static final Creator<ItemData> CREATOR = new Creator<ItemData>() {
        @Override
        public ItemData createFromParcel(Parcel source) {
            return new ItemData(source);
        }

        @Override
        public ItemData[] newArray(int size) {
            return new ItemData[size];
        }
    };



}
