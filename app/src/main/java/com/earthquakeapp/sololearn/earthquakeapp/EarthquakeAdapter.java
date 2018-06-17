package com.earthquakeapp.sololearn.earthquakeapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.ViewHolder> {

    private List<Earthquake> mEarthquakes;
    private Context context;

    private static final String LOCATION_SEPARATOR = " of ";

    public EarthquakeAdapter(Context context, List<Earthquake> earthquakes) {
        mEarthquakes = earthquakes;
        this.context = context;
    }

    public void addEarthquakes(List<Earthquake> earthquakes) {
        mEarthquakes.addAll(earthquakes);
        notifyItemRangeInserted(mEarthquakes.size() - 1, earthquakes.size());
    }

    public void clearEarthquakesList() {
        mEarthquakes.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.earthquake_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        String magnitude = formatMagnitude(mEarthquakes.get(position).getMagnitude());
        holder.magnitude.setText(magnitude);

        // Set the proper background color on the magnitude circle
        // Fetch the background from the TextView, which is a GradientDrawable
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.magnitude.getBackground();
        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(magnitude);
        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        Pair<String, String> locationPair = getLocationPair(mEarthquakes.get(position).getLocation());
        holder.nearLocation.setText(locationPair.first);
        holder.location.setText(locationPair.second);

        Date dateObject = new Date(mEarthquakes.get(position).getDateInMilliseconds());
        String formattedDate = formatDate(dateObject);
        String formattedTime = formatTime(dateObject);

        holder.date.setText(formattedDate);
        holder.time.setText(formattedTime);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Find the current earthquake that was clicked on
                Earthquake currentEarthquake = mEarthquakes.get(position);

                // Convert the String URL into a URI object
                Uri earthquakeUri = Uri.parse(currentEarthquake.getDetailsUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                context.startActivity(websiteIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEarthquakes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView magnitude;
        private TextView nearLocation;
        private TextView location;
        private TextView date;
        private TextView time;
        private ConstraintLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            magnitude = itemView.findViewById(R.id.magnitude);
            nearLocation = itemView.findViewById(R.id.near);
            location = itemView.findViewById(R.id.location);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
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
            nearLocation = context.getString(R.string.near_the);
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

        return ContextCompat.getColor(context, magnitudeColorResourceId);
    }
}
