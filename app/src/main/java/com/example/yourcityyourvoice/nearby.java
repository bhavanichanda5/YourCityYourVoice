package com.example.yourcityyourvoice;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class nearby extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private EditText etSearch;
    private ImageButton btnSearch, btnClearSearch;
    private FloatingActionButton fabCurrentLocation;
    private ProgressBar progressBar;
    private ListView nearbyListView;
    private ImageView emptyImage;

    private DatabaseReference databaseReference;
    private List<Complaint> allComplaints = new ArrayList<>();
    private List<Complaint> nearbyComplaints = new ArrayList<>();
    private ComplaintListAdapter complaintAdapter;

    static final Map<String, LatLng> AREA_COORDINATES = new HashMap<String, LatLng>() {{
        put("vesu", new LatLng(21.1702, 72.8311));
        put("adajan", new LatLng(21.1784, 72.7641));
        put("althan", new LatLng(21.1626, 72.7693));
        // Add more areas as needed
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        initializeViews();
        setupToolbar();
        initializeMap();
        setupFirebase();
        setupAdapter();
        loadAllComplaints();
        setupClickListeners();
    }

    private void initializeViews() {
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        fabCurrentLocation = findViewById(R.id.fabCurrentLocation);
        progressBar = findViewById(R.id.progressBar);
        nearbyListView = findViewById(R.id.nearby);
        emptyImage = findViewById(R.id.empty_image);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Nearby Complaints");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initializeMap() {
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(null);
        mapView.getMapAsync(this);
    }

    private void setupFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Complaints");
    }

    private void setupAdapter() {
        complaintAdapter = new ComplaintListAdapter(this, nearbyComplaints, AREA_COORDINATES);
        nearbyListView.setAdapter(complaintAdapter);
    }

    private void setupClickListeners() {
        btnSearch.setOnClickListener(v -> searchLocation());
        btnClearSearch.setOnClickListener(v -> clearSearch());
        fabCurrentLocation.setOnClickListener(v -> showAllComplaints());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (googleMap != null) {
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
        }
    }

    private void loadAllComplaints() {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allComplaints.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot complaintSnapshot : categorySnapshot.getChildren()) {
                        try {
                            // Get complaint ID from snapshot key
                            String complaintId = complaintSnapshot.getKey();

                            // Get other complaint fields
                            String status = complaintSnapshot.child("status").getValue(String.class);
                            String tvComplaintType = complaintSnapshot.child("tvComplaintType").getValue(String.class);
                            String area = complaintSnapshot.child("area").getValue(String.class);
                            String additionalInfo = complaintSnapshot.child("additionalInfo").getValue(String.class);
                            String imageUrl = complaintSnapshot.child("imageUrl").getValue(String.class);
                            Double latitude = complaintSnapshot.child("latitude").getValue(Double.class);
                            Double longitude = complaintSnapshot.child("longitude").getValue(Double.class);

                            if (latitude == null) latitude = 0.0;
                            if (longitude == null) longitude = 0.0;

                            // Create complaint object with all fields including ID
                            Complaint complaint = new Complaint(
                                    complaintId,
                                    complaintId, // Assuming you want to store ID in both fields
                                    status,
                                    tvComplaintType,
                                    area,
                                    additionalInfo,
                                    imageUrl,
                                    latitude,
                                    longitude
                            );

                            if (complaint.getArea() != null) {
                                allComplaints.add(complaint);
                            }
                        } catch (Exception e) {
                            showToast("Error loading some complaints");
                        }
                    }
                }
                progressBar.setVisibility(View.GONE);
                showAllComplaints();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                showToast("Failed to load complaints: " + error.getMessage());
                showEmptyState();
            }
        });
    }

    private void searchLocation() {
        String query = etSearch.getText().toString().trim().toLowerCase();
        if (!query.isEmpty()) {
            if (AREA_COORDINATES.containsKey(query)) {
                showComplaintsForArea(query);
            } else {
                filterComplaintsByText(query);
            }
        } else {
            showToast("Please enter an area name");
        }
    }

    private void showComplaintsForArea(String areaName) {
        nearbyComplaints.clear();
        LatLng areaLocation = AREA_COORDINATES.get(areaName);

        // Filter complaints for this area
        for (Complaint complaint : allComplaints) {
            if (complaint.getArea() != null &&
                    complaint.getArea().toLowerCase().contains(areaName)) {
                nearbyComplaints.add(complaint);

                // Update map marker to show complaint ID
                googleMap.addMarker(new MarkerOptions()
                        .position(areaLocation)
                        .title("Complaint ID: " + complaint.getId())
                        .snippet("Type: " + complaint.gettvComplaintType()));
            }
        }

        // Update map
        googleMap.clear();
        if (!nearbyComplaints.isEmpty()) {
            googleMap.addMarker(new MarkerOptions()
                    .position(areaLocation)
                    .title(areaName)
                    .snippet(nearbyComplaints.size() + " complaints"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(areaLocation, 14));
        }

        updateUI();
        showToast("Showing complaints for " + areaName);
    }

    private void filterComplaintsByText(String query) {
        nearbyComplaints.clear();
        for (Complaint complaint : allComplaints) {
            if ((complaint.gettvComplaintType() != null &&
                    complaint.gettvComplaintType().toLowerCase().contains(query)) ||
                    (complaint.getArea() != null &&
                            complaint.getArea().toLowerCase().contains(query)) ||
                    (complaint.getId() != null &&
                            complaint.getId().toLowerCase().contains(query))) { // Search by ID too
                nearbyComplaints.add(complaint);

                // Add marker with ID
                String areaKey = complaint.getArea() != null ? complaint.getArea().toLowerCase() : "";
                if (AREA_COORDINATES.containsKey(areaKey)) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(AREA_COORDINATES.get(areaKey))
                            .title("ID: " + complaint.getId())
                            .snippet(complaint.gettvComplaintType()));
                }
            }
        }

        // Try to find matching area for map
        for (Map.Entry<String, LatLng> entry : AREA_COORDINATES.entrySet()) {
            if (entry.getKey().contains(query)) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions()
                        .position(entry.getValue())
                        .title(entry.getKey())
                        .snippet("Approximate location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(entry.getValue(), 12));
                break;
            }
        }

        updateUI();
        showToast("Found " + nearbyComplaints.size() + " matching complaints");
    }

    private void showAllComplaints() {
        nearbyComplaints.clear();
        nearbyComplaints.addAll(allComplaints);

        // Update map with all complaints and their IDs
        googleMap.clear();
        for (Complaint complaint : allComplaints) {
            if (complaint.getArea() != null) {
                String areaKey = complaint.getArea().toLowerCase();
                if (AREA_COORDINATES.containsKey(areaKey)) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(AREA_COORDINATES.get(areaKey))
                            .title("ID: " + complaint.getId())
                            .snippet(complaint.gettvComplaintType()));
                }
            }
        }

        // Center map on first area if available
        if (!allComplaints.isEmpty() && allComplaints.get(0).getArea() != null) {
            String firstArea = allComplaints.get(0).getArea().toLowerCase();
            if (AREA_COORDINATES.containsKey(firstArea)) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        AREA_COORDINATES.get(firstArea), 12));
            }
        }

        updateUI();
    }

    private void clearSearch() {
        etSearch.setText("");
        showAllComplaints();
    }

    private void updateUI() {
        if (nearbyComplaints.isEmpty()) {
            showEmptyState();
        } else {
            showComplaintsList();
        }
    }

    private void showEmptyState() {
        emptyImage.setVisibility(View.VISIBLE);
        nearbyListView.setVisibility(View.GONE);
    }

    private void showComplaintsList() {
        emptyImage.setVisibility(View.GONE);
        nearbyListView.setVisibility(View.VISIBLE);
        complaintAdapter.updateList(nearbyComplaints);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}