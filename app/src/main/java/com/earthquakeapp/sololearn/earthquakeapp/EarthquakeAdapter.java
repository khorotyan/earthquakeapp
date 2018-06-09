package com.earthquakeapp.sololearn.earthquakeapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    private static final String LOCATION_SEPARATOR = " of ";

    public EarthquakeAdapter(Context context, List<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.earthquake_list_item, parent, false);
        }

        // Find the earthquake at the given position in the earthquake list
        Earthquake currentEarthquake = getItem(position);

        TextView magnitudeView = listItemView.findViewById(R.id.magnitude);
        // Display the magnitude of the current earthquake in the magnitude TextView
        String magnitude = formatMagnitude(currentEarthquake.getMagnitude());
        magnitudeView.setText(magnitude);

        // Set the proper background color on the magnitude circle
        // Fetch the background from the TextView, which is a GradientDrawable
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeView.getBackground();
        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(magnitude);
        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        TextView nearView = listItemView.findViewById(R.id.near);
        TextView locationView = listItemView.findViewById(R.id.location);
        Pair<String, String> locationPair = getLocationPair(currentEarthquake.getLocation());
        nearView.setText(locationPair.first);
        locationView.setText(locationPair.second);

        TextView dateView = listItemView.findViewById(R.id.date);
        TextView timeView = listItemView.findViewById(R.id.time);

        Date dateObject = new Date(currentEarthquake.getDateInMilliseconds());
        String formattedDate = formatDate(dateObject);
        String formattedTime = formatTime(dateObject);

        dateView.setText(formattedDate);
        timeView.setText(formattedTime);

        // Return the list item view that is not showing the appropriate data
        return listItemView;
    }

    // Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");

        return dateFormat.format(dateObject);
    }

    // Return the formatted date string (i.e. "4:30 PM") from a Date object.
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

        return timeFormat.format(dateObject);
    }

    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");

        return magnitudeFormat.format(magnitude);
    }

    private Pair<String, String> getLocationPair(String data) {
        String nearLocation;
        String mainLocation;

        if (data.contains(LOCATION_SEPARATOR)) {
            String[] parts = data.split(LOCATION_SEPARATOR);
            nearLocation = parts[0] + " Near Of";
            mainLocation = parts[1];
        } else {
            nearLocation = getContext().getString(R.string.near_the);
            mainLocation = data;
        }

        return Pair.create(nearLocation, mainLocation);
    }

    private int getMagnitudeColor(String magnitudeString) {
        double magnitude = Double.parseDouble(magnitudeString);
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
