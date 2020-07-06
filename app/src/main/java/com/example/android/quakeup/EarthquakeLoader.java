package com.example.android.quakeup;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<List<EarthquakeModel>> {
    private static final String TAG = "EarthquakeLoader";
    private String mUrl;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.d(TAG, "onStartLoading: Earthquake loader started.");
    }

    @Override
    public List<EarthquakeModel> loadInBackground() {
        Log.d(TAG, "loadInBackground: started");
        return Utils.fetchEarthquakeData(mUrl);
    }
}