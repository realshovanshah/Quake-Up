package com.example.android.quakeup;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class Utils {

    /**
     * Sample JSON response for a USGS query
     */
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";
    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    /**
     * Create a private constructor because no one should ever create a {@link Utils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private Utils() {
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "createUrl: Error with creating url", e);
            return null;
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection;
        InputStream inputStream = null;

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.connect();
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == 200) {
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        } else {
            Log.e(LOG_TAG, "makeHttpRequest: Response code: " + urlConnection.getResponseCode());
        }

        urlConnection.disconnect();
        if (inputStream != null) {
            inputStream.close();
        }

        return jsonResponse;
    }

    ;

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            inputStreamReader.close();
        }
        return output.toString();
    }

    ;

    /**
     * Return a list of {@link EarthquakeModel} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<EarthquakeModel> extractEarthquakesFromJson(String earthquakeJSON) {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<EarthquakeModel> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject root = new JSONObject(earthquakeJSON);

            JSONArray earthquakeArray = root.getJSONArray("features");

            for (int i = 0; i < earthquakeArray.length(); i++) {
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);
                JSONObject earthquakeProperty = currentEarthquake.getJSONObject("properties");

                float magnitude = (float) earthquakeProperty.getDouble("mag");
                String place = earthquakeProperty.getString("place");
                long timeInMilliseconds = earthquakeProperty.getLong("time");

                String url = earthquakeProperty.getString("url");

//                Date dateObject = new Date(timeInMilliseconds);
//                SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM DD, yyyy");
//                String displayDate = dateFormatter.format(dateObject);
                earthquakes.add(new EarthquakeModel(magnitude, place, timeInMilliseconds, url));
            }
            // build up a list of Earthquake objects with the corresponding data.

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

    public static List<EarthquakeModel> fetchEarthquakeData(String requestUrl) {

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<EarthquakeModel> earthquakes = extractEarthquakesFromJson(jsonResponse);

        Log.d(LOG_TAG, "fetchEarthquakeData: successful.");

        return earthquakes;
    }

    public static String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    public static String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    public static String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }

}