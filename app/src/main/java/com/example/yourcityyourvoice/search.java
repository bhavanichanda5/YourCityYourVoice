package com.example.yourcityyourvoice;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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

public class search extends AppCompatActivity {

    private EditText searchBox;
    private RecyclerView recyclerView;
    private ImageView emptyImage;
    private ComplaintAdapterPosted adapter;
    private List<Complaint> complaintList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Search Complaints");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        // Initialize UI components
        searchBox = findViewById(R.id.search_users);
        recyclerView = findViewById(R.id.recycler_view);
        emptyImage = findViewById(R.id.empty_image);

        // Setup RecyclerView
        recyclerView.addItemDecoration(new ItemSpacingDecoration(30));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        complaintList = new ArrayList<>();
        adapter = new ComplaintAdapterPosted(this, complaintList);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Complaints");

        // Load all complaints from Firebase
        fetchAllComplaints();

        // Keep existing search functionality unchanged
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterComplaints(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchAllComplaints() {
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

                            Complaint complaint = new Complaint(
                                    complaintSnapshot.getKey(),
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
                            Toast.makeText(search.this, "Error loading complaint", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                // Update UI
                if (complaintList.isEmpty()) {
                    emptyImage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyImage.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(search.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                emptyImage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void filterComplaints(String query) {
        List<Complaint> filteredList = new ArrayList<>();
        for (Complaint complaint : complaintList) {
            if (complaint.gettvComplaintType() != null &&
                    complaint.gettvComplaintType().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(complaint);
            }
        }

        // Update RecyclerView with the filtered data
        adapter.updateList(filteredList);

        // Show empty image if no matching results
        emptyImage.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}