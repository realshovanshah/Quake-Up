package com.example.android.quakeup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EarthquakeAdapter extends ArrayAdapter<EarthquakeModel> {


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
            holder.locationView = (TextView) listView.findViewById(R.id.location);
            holder.dateView = (TextView) listView.findViewById(R.id.date);

            listView.setTag(holder);

        }

        EarthquakeModel currentEarthquake = getItem(position);

        holder = (ViewHolder) listView.getTag();
        holder.magnitudeView.setText(currentEarthquake.getMagnitude());
        holder.locationView.setText(currentEarthquake.getLocation());
        holder.dateView.setText(currentEarthquake.getDate());

        return listView;
    }

    private static class ViewHolder{
        TextView magnitudeView, locationView, dateView;
    }

}
