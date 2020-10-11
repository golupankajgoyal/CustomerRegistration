package com.digiflying.customerregistration;

public class Upload {

    private String mImageUrl;
    private String mName;
    public Upload(){

    }

    public Upload(String url, String name){
        mName=name;
        mImageUrl=url;
    }

    public String getmImageUrl(){
        return mImageUrl;
    }
    public String getmName(){
        return mName;
    }
}
