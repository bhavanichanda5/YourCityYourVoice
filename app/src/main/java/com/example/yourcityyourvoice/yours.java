package com.example.yourcityyourvoice;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class yours extends AppCompatActivity implements BookmarkUpdateListener {
    @Override
    public void onBookmarkUpdated() {
    }

    private ListView listView;
    private ImageView emptyImageView;
    private DatabaseReference databaseReference;
    private List<EventModel> eventList;
    private EventAdapter eventAdapter;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yours);

        setupToolbar();
        initializeComponents();
        fetchCurrentUserEmail();

        if (currentUserEmail != null) {
            loadUserEvents();
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Your Events");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void initializeComponents() {
        listView = findViewById(R.id.yours_list);
        emptyImageView = findViewById(R.id.emptyImageView); // Reference to empty image
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList, this);
        listView.setAdapter(eventAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Events");
    }

    private void fetchCurrentUserEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserEmail = user.getEmail();
        }
    }

    private void loadUserEvents() {
        databaseReference.orderByChild("createdBy").equalTo(currentUserEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EventModel event = dataSnapshot.getValue(EventModel.class);
                    if (event != null) {
                        eventList.add(event);
                    }
                }

                if (eventList.isEmpty()) {
                    listView.setVisibility(View.GONE);
                    emptyImageView.setVisibility(View.VISIBLE);
                } else {
                    listView.setVisibility(View.VISIBLE);
                    emptyImageView.setVisibility(View.GONE);
                    eventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(yours.this, "Failed to load events: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
