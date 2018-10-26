package com.example.neel.nimboopaani;

public class PlacesData {
    String mName,mLat,mLon;
    Double mDist;
    public PlacesData(String name,String Lat,String Lon,Double distance){
        mName=name;
        mLat=Lat;
        mLon=Lon;
        mDist=distance;
    }

    public String getmName() {
        return mName;
    }

    public String getmLat() {
        return mLat;
    }

    public String getmLon() {
        return mLon;
    }
    public Double getmDist() {
        return mDist;
    }
}
