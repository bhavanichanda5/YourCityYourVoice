package com.example.yourcityyourvoice;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class city extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView emptyImageView;
    private List<Complaint> complaintList;
    private ComplaintAdapterPosted adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        setupToolbar();
        initializeComponents();
        fetchAllComplaints();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("City Complaints");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void initializeComponents() {
        recyclerView = findViewById(R.id.recyclerView);
        emptyImageView = findViewById(R.id.empty_image);
        recyclerView.addItemDecoration(new ItemSpacingDecoration(30));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        complaintList = new ArrayList<>();
        adapter = new ComplaintAdapterPosted(this, complaintList);
        recyclerView.setAdapter(adapter);
    }

    private void fetchAllComplaints() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Complaints");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                complaintList.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot complaintSnapshot : categorySnapshot.getChildren()) {
                        try {
                            String id = complaintSnapshot.child("id").getValue(String.class);
                            String status = complaintSnapshot.child("status").getValue(String.class);
                            String tvComplaintType = complaintSnapshot.child("tvComplaintType").getValue(String.class);
                            String area = complaintSnapshot.child("area").getValue(String.class);
                            String additionalInfo = complaintSnapshot.child("additionalInfo").getValue(String.class);
                            String imageUrl = complaintSnapshot.child("imageUrl").getValue(String.class);
                            Double latitude = complaintSnapshot.child("latitude").getValue(Double.class);
                            Double longitude = complaintSnapshot.child("longitude").getValue(Double.class);

                            if (latitude == null) latitude = 0.0;
                            if (longitude == null) longitude = 0.0;

                            String complaintId = complaintSnapshot.getKey();
                            Complaint complaint = new Complaint(
                                    complaintId,
                                    id,
                                    status,
                                    tvComplaintType,
                                    area,
                                    additionalInfo,
                                    imageUrl,
                                    latitude,
                                    longitude
                            );
                            complaintList.add(complaint);
                        } catch (Exception e) {
                            Toast.makeText(city.this, "Error loading some complaints", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(city.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                showEmptyView();
            }
        });
    }

    private void updateUI() {
        if (complaintList.isEmpty()) {
            showEmptyView();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyImageView.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    private void showEmptyView() {
        recyclerView.setVisibility(View.GONE);
        emptyImageView.setVisibility(View.VISIBLE);
    }
}