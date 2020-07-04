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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";

    private EarthquakeAdapter mEarthquakeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        final ListView earthquakeListView = (ListView) findViewById(R.id.list);

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

        EarthquakeAsyncTask task = new EarthquakeAsyncTask();
        task.execute(USGS_REQUEST_URL);

    }

    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<EarthquakeModel>> {
        @Override
        protected List<EarthquakeModel> doInBackground(String... urls) {
            String jsonResponse = "";
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<EarthquakeModel> result = Utils.fetchEarthquakeData(urls[0]);

            return result;
        }

        ;

        @Override
        protected void onPostExecute(List<EarthquakeModel> earthquakeData) {
            mEarthquakeAdapter.clear();

            if (earthquakeData != null && !earthquakeData.isEmpty()) {
                mEarthquakeAdapter.addAll(earthquakeData);
            }
        }
    }

}
