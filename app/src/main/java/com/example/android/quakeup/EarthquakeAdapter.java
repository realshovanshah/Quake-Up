package com.example.android.quakeup;

import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

public class EarthquakeAdapter extends ArrayAdapter<EarthquakeModel> {

    private static final String LOCATION_SEPARATOR = " of ";

    public EarthquakeAdapter(EarthquakeActivity context, List<EarthquakeModel> earthquakes) {
        super(context, 0,  earthquakes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View listView = convertView;
        if(listView==null){
            listView = LayoutInflater.from(getContext()).inflate(
                    R.layout.earthquake_list_item, parent, false
            );

            holder = new ViewHolder();
            holder.magnitudeView = (TextView) listView.findViewById(R.id.magnitude);
            holder.primaryLocationView = (TextView) listView.findViewById(R.id.primary_location);
            holder.offsetLocationView = (TextView) listView.findViewById(R.id.location_offset);
            holder.dateView = (TextView) listView.findViewById(R.id.date);
            holder.timeView = (TextView) listView.findViewById(R.id.time);

            listView.setTag(holder);

        }

        EarthquakeModel currentEarthquake = getItem(position);

        String location = currentEarthquake.getLocation();
        String locationOffset;
        String primaryLocation;
        if (location.contains(LOCATION_SEPARATOR)) {
            String[] locationParts = location.split(LOCATION_SEPARATOR);
            locationOffset = locationParts[0] + LOCATION_SEPARATOR;
            primaryLocation = locationParts[1];
        } else {
            locationOffset = getContext().getString(R.string.near);
            primaryLocation = location;
        }

        Date dateObject = new Date(currentEarthquake.getTimeInMilliseconds());
        String displayTime = Utils.formatTime(dateObject);
        String displayDate = Utils.formatDate(dateObject);

        String displayMagnitude = Utils.formatMagnitude(currentEarthquake.getMagnitude());

        holder = (ViewHolder) listView.getTag();
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.magnitudeView.getBackground();
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);
        holder.magnitudeView.setText(displayMagnitude);
        holder.offsetLocationView.setText(locationOffset);
        holder.primaryLocationView.setText(primaryLocation);
        holder.dateView.setText(displayDate);
        holder.timeView.setText(displayTime);

        return listView;
    }

    private static class ViewHolder{
        TextView magnitudeView, primaryLocationView, offsetLocationView, dateView, timeView;
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}
