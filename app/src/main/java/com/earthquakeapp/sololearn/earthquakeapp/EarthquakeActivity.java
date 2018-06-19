package com.earthquakeapp.sololearn.earthquakeapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    private static final String EARTHQUAKE_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    private static final int EARTHQUAKE_LOADER_ID = 1;

    private int mOffset = 1;

    private EarthquakeAdapter mAdapter;

    private TextView mEmptyStateTextView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);

        RecyclerView earthquakeRecyclerView = findViewById(R.id.earthquake_recycler_view);

        mEmptyStateTextView = findViewById(R.id.empty_view);

        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        earthquakeRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        earthquakeRecyclerView.setLayoutManager(linearLayoutManager);

        // Get reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data networks
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // If there is a network connection, get data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager to interact with loaders
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            // Hide the loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText("No internet connection.");
        }

        // When a user refreshes the earthquakes list
        mSwipeRefreshLayout = findViewById(R.id.earthquake_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ReloadPage();
            }
        });

        earthquakeRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                LoadMoreData();
            }
        });
    }

    private void ReloadPage() {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(EARTHQUAKE_LOADER_ID, null, this);
    }

    private void LoadMoreData() {
        mOffset += 20;
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(EARTHQUAKE_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String minMagnitude = sharedPreferences.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default)
        );
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(EARTHQUAKE_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("offset", Integer.toString(mOffset));
        uriBuilder.appendQueryParameter("limit", "20");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        // Hide the loading indicator as the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText("No earthquakes found.");

        if (mOffset == 1) {
            // Clear the adapter of previous earthquake data
            mAdapter.clearEarthquakesList();
        }

        // If there is a valid list of Earthquakes list, then add them to the adapter's data set
        //  which will trigger the ListView to update
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addEarthquakes(earthquakes);

            TextView emptyTextView = findViewById(R.id.empty_view);
            emptyTextView.setVisibility(View.GONE);
        }

        // Stop the refresh animation if not already stopped
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
            mOffset = 1;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        mAdapter.clearEarthquakesList();
        mOffset = 1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
