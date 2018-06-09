package com.earthquakeapp.sololearn.earthquakeapp;

public class Earthquake {

    private double mMagnitude;
    private String mLocation;
    private long mTimeInMilliseconds;
    private String mDetailsUrl;

    public Earthquake(double magnitude, String location, long timeInMilliseconds, String detailsUrl){
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliseconds = timeInMilliseconds;
        mDetailsUrl = detailsUrl;
    }

    public double getMagnitude(){
        return mMagnitude;
    }

    public String getLocation(){
        return mLocation;
    }

    public long getDateInMilliseconds(){
        return mTimeInMilliseconds;
    }

    public String getDetailsUrl(){
        return mDetailsUrl;
    }
}
