package com.example.neel.nimboopaani;

public class CampData {
    private String mName;
    private String mCapacity;
    private String mContact;
    public CampData(String name,String capacity,String contact){
        this.mName=name;
        this.mCapacity=capacity;
        this.mContact=contact;
    }

    public String getName() { return mName; }

    public String getCapacity() { return mCapacity; }

    public String getContact() { return mContact; }
}
