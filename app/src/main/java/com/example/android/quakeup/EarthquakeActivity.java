/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakeup;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<EarthquakeModel>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    private static final int EARTHQUAKE_LOADER_ID = 1;

    private EarthquakeAdapter mEarthquakeAdapter;

    private TextView mEmptyStateView;

    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);


        // Find a reference to the {@link ListView} in the layout
        mEmptyStateView = (TextView) findViewById(R.id.empty_view);

        final ListView earthquakeListView = (ListView) findViewById(R.id.list);
        earthquakeListView.setEmptyView(mEmptyStateView);

        // Create a new {@link ArrayAdapter} of earthquakes
        mEarthquakeAdapter = new EarthquakeAdapter(this, new ArrayList<EarthquakeModel>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface

        if (earthquakeListView != null) {
            earthquakeListView.setAdapter(mEarthquakeAdapter);
            earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    EarthquakeModel currentEarthquake = mEarthquakeAdapter.getItem(i);

                    Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                    PackageManager packageManager = getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(browserIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    boolean isIntentSafe = activities.size() > 0;

                    if (isIntentSafe) {
                        startActivity(browserIntent);
                    }

                    //                Create a chooser
                    //                Intent chooser = Intent.createChooser(browserIntent, "Select Browser");
                    //                if (browserIntent.resolveActivity(getPackageManager()) != null) {
                    //                    startActivity(chooser);
                    //                }
                }
            });
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected){
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
            Log.d(LOG_TAG, "onCreate: Loader initialized.");
        }else {
            mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
            mProgressBar.setVisibility(View.GONE);
            mEmptyStateView.setText(R.string.no_internet);
        }

//        EarthquakeAsyncTask task = new EarthquakeAsyncTask();
//        task.execute(USGS_REQUEST_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_setting){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<EarthquakeModel>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPreferences.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        String maxRadius = sharedPreferences.getString("max_radius","10");

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("longitude","84");
        uriBuilder.appendQueryParameter("latitude", "28");
        uriBuilder.appendQueryParameter("maxradius",maxRadius);
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        Log.d(LOG_TAG, "onCreateLoader: Earthquake Loader created.");
        return new EarthquakeLoader(EarthquakeActivity.this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<EarthquakeModel>> loader, List<EarthquakeModel> earthquakeList) {
        Log.d(LOG_TAG, "onLoadFinished: Earthquake Loading Finished.");
        mEarthquakeAdapter.clear();
        if (earthquakeList != null && !earthquakeList.isEmpty()){
            mEarthquakeAdapter.addAll(earthquakeList);
        }
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mEmptyStateView.setText(R.string.no_earthquakes);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<EarthquakeModel>> loader) {
        Log.d(LOG_TAG, "onLoaderReset: Complete");
        mEarthquakeAdapter.clear();
    }


//    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<EarthquakeModel>> {
//        @Override
//        protected List<EarthquakeModel> doInBackground(String... urls) {
//            String jsonResponse = "";
//            if (urls.length < 1 || urls[0] == null) {
//                return null;
//            }
//            List<EarthquakeModel> result = Utils.fetchEarthquakeData(urls[0]);
//
//            return result;
//        }
//
//        ;
//
//        @Override
//        protected void onPostExecute(List<EarthquakeModel> earthquakeData) {
//            mEarthquakeAdapter.clear();
//
//            if (earthquakeData != null && !earthquakeData.isEmpty()) {
//                mEarthquakeAdapter.addAll(earthquakeData);
//            }
//        }
//    }

}
