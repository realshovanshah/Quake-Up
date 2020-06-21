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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Create a fake list of earthquake locations.
//        ArrayList<EarthquakeModel> earthquakes = new ArrayList<>();
//        earthquakes.add(new EarthquakeModel("6.8", "India", "2 Feb, 2016"));
//        earthquakes.add(new EarthquakeModel("5.1", "China", "20 July, 2017"));
//        earthquakes.add(new EarthquakeModel("5.8", "Bangladesh", "13 Oct, 2017"));
//        earthquakes.add(new EarthquakeModel("7.2", "Pakistan", "28 Dec, 2016"));
//        earthquakes.add(new EarthquakeModel("6.3", "Nepal", "3 Mar, 2018"));

        // Find a reference to the {@link ListView} in the layout
        final ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        final EarthquakeAdapter adapter = new EarthquakeAdapter(this, Utils.extractEarthquakes());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EarthquakeModel currentEarthquake = adapter.getItem(i);

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

        Log.d(LOG_TAG, "onCreate: " + Utils.extractEarthquakes());
    }

}
