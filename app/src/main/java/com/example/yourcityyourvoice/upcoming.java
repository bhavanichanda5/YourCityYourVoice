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

public class upcoming extends AppCompatActivity {

    private ListView listView;
    private ImageView emptyImage;
    private EventAdapter eventAdapter;
    private List<EventModel> eventList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Upcoming Events");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        listView = findViewById(R.id.all);
        emptyImage = findViewById(R.id.empty_image);

        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList, null);
        listView.setAdapter(eventAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Events");

        fetchUpcomingEvents();
    }

    private void fetchUpcomingEvents() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();
                long currentTime = System.currentTimeMillis();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EventModel event = snapshot.getValue(EventModel.class);
                    if (event != null && event.getTimestamp() > currentTime) {
                        eventList.add(event);
                    }
                }

                if (eventList.isEmpty()) {
                    listView.setVisibility(View.GONE);
                    emptyImage.setVisibility(View.VISIBLE);
                } else {
                    listView.setVisibility(View.VISIBLE);
                    emptyImage.setVisibility(View.GONE);
                    eventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(upcoming.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
