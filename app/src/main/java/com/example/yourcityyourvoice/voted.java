package com.example.yourcityyourvoice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class voted extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView emptyImageView;
    private ComplaintAdapterPosted adapter;
    private List<Complaint> votedComplaints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voted);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Voted Complaints");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        initializeComponents();
        loadVotedComplaints();
    }


    private void initializeComponents() {
        recyclerView = findViewById(R.id.recyclerView);
        emptyImageView = findViewById(R.id.emptyImageView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        votedComplaints = new ArrayList<>();
    }

    private void loadVotedComplaints() {
        SharedPreferences sharedPreferences = getSharedPreferences("VotedComplaints", MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        Gson gson = new Gson();

        votedComplaints.clear();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String json = (String) entry.getValue();
            Complaint complaint = gson.fromJson(json, Complaint.class);
            votedComplaints.add(complaint);
        }

        if (votedComplaints.isEmpty()) {
            showEmptyView();
            Toast.makeText(this, "No voted complaints", Toast.LENGTH_SHORT).show();
        } else {
            showComplaintsList();
        }
    }

    private void showEmptyView() {
        recyclerView.setVisibility(View.GONE);
        emptyImageView.setVisibility(View.VISIBLE);
    }

    private void showComplaintsList() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyImageView.setVisibility(View.GONE);
        adapter = new ComplaintAdapterPosted(this, votedComplaints);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVotedComplaints(); // Refresh list when returning to activity
    }
}