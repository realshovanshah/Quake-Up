package com.example.android.quakeup;

public class EarthquakeModel {

    private float mMagnitude;
    private String mLocation;
    private long mTimeInMilliseconds;
    private String mUrl;

    public EarthquakeModel(float magnitude, String location, long timeInMilliseconds, String url) {
        this.mMagnitude = magnitude;
        this.mLocation = location;
        this.mTimeInMilliseconds = timeInMilliseconds;
        this.mUrl = url;
    }

    public float getMagnitude() {
        return mMagnitude;
    }

    public String getLocation() {
        return mLocation;
    }

    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    public String getUrl() {
        return mUrl;
    }
}
