package com.example.yourcityyourvoice;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class notification extends AppCompatActivity {

    private ListView lvNotifications;
    private ImageView emptyImageView;
    private List<NotificationModel> notificationList;
    private NotificationAdapter adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Notification");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        // Initialize Views
        lvNotifications = findViewById(R.id.lvNotifications);
        emptyImageView = findViewById(R.id.emptyImageView);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("notifications");

        // Initialize list and adapter
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(this, notificationList);
        lvNotifications.setAdapter(adapter);

        // Fetch Data
        fetchNotifications();
    }

    private void fetchNotifications() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                notificationList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NotificationModel notification = dataSnapshot.getValue(NotificationModel.class);
                    if (notification != null) {
                        notificationList.add(notification);
                    }
                }

                if (notificationList.isEmpty()) {
                    emptyImageView.setVisibility(View.VISIBLE);
                    lvNotifications.setVisibility(View.GONE);
                } else {
                    emptyImageView.setVisibility(View.GONE);
                    lvNotifications.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(notification.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
