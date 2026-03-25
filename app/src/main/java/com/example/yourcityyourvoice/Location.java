package com.example.yourcityyourvoice;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Location extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient locationProviderClient;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private FloatingActionButton fabCurrentLocation;
    private EditText etSearch;
    private ImageButton btnSearch, btnClearSearch;
    private ProgressBar progressBar;
    private ClusterManager<Toilet> clusterManager;
    private Toolbar toolbar;

    // SBM Toilets API (replace with actual API endpoint)
    private static final String SBM_TOILETS_API = "https://example.com/api/toilets?lat=%f&lng=%f&radius=5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("SBM Toilet Locator");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        initializeViews();
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        setupClickListeners();
        setupSearchField();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Rest of the code remains exactly the same...
    private void initializeViews() {
        mapView = findViewById(R.id.mapView);
        fabCurrentLocation = findViewById(R.id.fabCurrentLocation);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        fabCurrentLocation.setOnClickListener(v -> {
            animateFabClick();
            findNearestToilet();
        });

        btnSearch.setOnClickListener(v -> searchLocation());
        btnClearSearch.setOnClickListener(v -> clearSearch());
    }

    private void setupSearchField() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void animateFabClick() {
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0.8f, 1f);
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            fabCurrentLocation.setScaleX(value);
            fabCurrentLocation.setScaleY(value);
        });
        animator.start();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setOnMapClickListener(this);

        // Initialize ClusterManager
        clusterManager = new ClusterManager<>(this, googleMap);
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            enableUserLocation();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // Clear focus from search field when map is clicked
        etSearch.clearFocus();
    }

    private void enableUserLocation() {
        try {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            findNearestToilet();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void findNearestToilet() {
        progressBar.setVisibility(View.VISIBLE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            animateCamera(userLocation, 15);
                            new FetchToiletsTask().execute(userLocation);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            showToast("Unable to get current location");
                        }
                    });
        } else {
            progressBar.setVisibility(View.GONE);
            requestLocationPermission();
        }
    }

    private void animateCamera(LatLng location, float zoom) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoom), 1000,
                new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        // Animation finished
                    }

                    @Override
                    public void onCancel() {
                        // Animation canceled
                    }
                });
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                showToast("Location permission denied");
            }
        }
    }

    private class FetchToiletsTask extends AsyncTask<LatLng, Void, List<Toilet>> {
        @Override
        protected List<Toilet> doInBackground(LatLng... latLngs) {
            LatLng location = latLngs[0];
            List<Toilet> toilets = new ArrayList<>();

            try {
                // In a real app, replace this with actual API call to SBM toilets database
                String apiUrl = String.format(SBM_TOILETS_API, location.latitude, location.longitude);
                String jsonResponse = fetchDataFromApi(apiUrl);

                // Parse JSON response
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray jsonArray = jsonObject.getJSONArray("toilets");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject toiletJson = jsonArray.getJSONObject(i);
                    Toilet toilet = new Toilet(
                            toiletJson.getString("id"),
                            toiletJson.getString("name"),
                            toiletJson.getDouble("lat"),
                            toiletJson.getDouble("lng"),
                            toiletJson.getString("address"),
                            toiletJson.getString("type"),
                            toiletJson.optBoolean("accessible", false),
                            toiletJson.optBoolean("free", true),
                            toiletJson.optDouble("rating", 0.0)
                    );
                    toilets.add(toilet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return toilets;
        }

        @Override
        protected void onPostExecute(List<Toilet> toilets) {
            progressBar.setVisibility(View.GONE);

            if (toilets.isEmpty()) {
                showToast("No toilets found nearby");
                return;
            }

            // Clear previous markers
            googleMap.clear();
            clusterManager.clearItems();

            // Add all toilets to cluster manager
            clusterManager.addItems(toilets);
            clusterManager.cluster();

            // Find and zoom to nearest toilet
            Toilet nearest = findNearestToilet(toilets);
            if (nearest != null) {
                LatLng nearestLocation = new LatLng(nearest.getLatitude(), nearest.getLongitude());
                animateCamera(nearestLocation, 16);
                showToast("Nearest toilet: " + nearest.getName());
            }
        }
    }

    private Toilet findNearestToilet(List<Toilet> toilets) {
        // In a real app, implement proper distance calculation
        return toilets.isEmpty() ? null : toilets.get(0);
    }

    private String fetchDataFromApi(String apiUrl) throws IOException {
        // This is a mock implementation - replace with actual API call
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(apiUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000); // 10 seconds
            urlConnection.setReadTimeout(10000); // 10 seconds
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            return buffer.toString();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void searchLocation() {
        String locationName = etSearch.getText().toString().trim();
        if (locationName.isEmpty()) {
            showToast("Please enter a location");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        googleMap.clear();
                        clusterManager.clearItems();

                        googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(locationName));
                        animateCamera(latLng, 15);
                    } else {
                        showToast("Location not found");
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    showToast("Error searching location");
                });
            }
        }).start();
    }

    private void clearSearch() {
        etSearch.setText("");
        etSearch.clearFocus();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Lifecycle methods for the MapView
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Location.this, bottomnavigation.class);
        startActivity(intent);
        finish();
    }
}