package com.example.yourcityyourvoice;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class whatsnew extends AppCompatActivity {
    private DatabaseReference dbRef;
    private ListView lvWhatsNew;
    private ImageView imgNoUpdates;
    private ArrayList<Spanned> updatesList;
    private ArrayAdapter<Spanned> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatsnew);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("What's New");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        lvWhatsNew = findViewById(R.id.lvWhatsNew);
        imgNoUpdates = findViewById(R.id.emptyImageView);

        dbRef = FirebaseDatabase.getInstance().getReference("updates");

        updatesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, updatesList);
        lvWhatsNew.setAdapter(adapter);

        fetchUpdates();
    }

    private void fetchUpdates() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updatesList.clear();
                for (DataSnapshot updateSnapshot : snapshot.getChildren()) {
                    String title = updateSnapshot.child("title").getValue(String.class);
                    String description = updateSnapshot.child("description").getValue(String.class);
                    String type = updateSnapshot.child("type").getValue(String.class);

                    if (title != null && description != null && type != null) {
                        String formattedUpdate = " \uD83C\uDFAF <font color='#304FFE'>" + title + "</font><br>" + description + "<br><font color='#126C6C'>(" + type + ")</font><br>";
                        updatesList.add(Html.fromHtml(formattedUpdate, Html.FROM_HTML_MODE_LEGACY));
                    }
                }

                if (updatesList.isEmpty()) {
                    imgNoUpdates.setVisibility(View.VISIBLE);
                    lvWhatsNew.setVisibility(View.GONE);
                } else {
                    imgNoUpdates.setVisibility(View.GONE);
                    lvWhatsNew.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(whatsnew.this, "Error fetching updates", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
