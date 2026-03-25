package com.example.yourcityyourvoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;

public class post_a_complaint extends AppCompatActivity {

    private ListView listView;
    private ComplaintAdapter adapter;
    private ArrayList<HashMap<String, Object>> complaintList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_a_complaint);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Choose Category");
        getSupportActionBar().setSubtitle("Post a complaint");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setSubtitleTextColor(getResources().getColor(android.R.color.white));

        listView = findViewById(R.id.listViewComplaints);

        // Populate the complaint list
        complaintList = new ArrayList<>();
        addComplaintItems();

        // Set the adapter
        adapter = new ComplaintAdapter(this, complaintList);
        listView.setAdapter(adapter);

        // Set an item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> selectedItem = (HashMap<String, Object>) parent.getItemAtPosition(position);
                String selectedTitle = (String) selectedItem.get("title");

                // Check the selected title and launch the corresponding activity
                if (selectedTitle.equals("Yellow Spot (Public Urination Spot)")) {
                    Intent intent = new Intent(post_a_complaint.this, yellow_spot.class);
                    startActivity(intent);
                }
                if (selectedTitle.equals("Overflow of Septic Tanks (New)")) {
                    Intent intent = new Intent(post_a_complaint.this, overflowofseptictanks.class);
                    startActivity(intent);
                }
                if (selectedTitle.equals("Overflow of Sewerage or Storm Water")) {
                    Intent intent = new Intent(post_a_complaint.this, overflowofstormwater.class);
                    startActivity(intent);
                }
                if (selectedTitle.equals("Dead animal(s)")) {
                    Intent intent = new Intent(post_a_complaint.this, deadanimals.class);
                    startActivity(intent);
                }
                if (selectedTitle.equals("Dustbins not cleaned")) {
                    Intent intent = new Intent(post_a_complaint.this, dustbinnotcleaned.class);
                    startActivity(intent);
                }
                if (selectedTitle.equals("Garbage dump")) {
                    Intent intent = new Intent(post_a_complaint.this, garbagedump.class);
                    startActivity(intent);
                }
                if (selectedTitle.equals("Garbage vehicle not arrived")) {
                    Intent intent = new Intent(post_a_complaint.this, garbagevehiclenotarraived.class);
                    startActivity(intent);
                }
                if (selectedTitle.equals("Sweeping not done")) {
                    Intent intent = new Intent(post_a_complaint.this, sweeping.class);
                    startActivity(intent);
                }
                if (selectedTitle.equals("No electricity")) {
                    Intent intent = new Intent(post_a_complaint.this, noelectricity.class);
                    startActivity(intent);
                }
                if (selectedTitle.equals("No water supply")) {
                    Intent intent = new Intent(post_a_complaint.this, nowatersupply.class);
                    startActivity(intent);
                }
                if (selectedTitle.equals("Public toilet(s) blockage")) {
                    Intent intent = new Intent(post_a_complaint.this, publictoiletblockage.class);
                    startActivity(intent);
                }
                if (selectedTitle.equals("Open Manholes or Drains")) {
                    Intent intent = new Intent(post_a_complaint.this, openmanholesordrains.class);
                    startActivity(intent);
                }
                if (selectedTitle.equals("Stagnant Water on the Road")) {
                    Intent intent = new Intent(post_a_complaint.this, stagnantwateronroad.class);
                    startActivity(intent);
                }
                if (selectedTitle.equals("Debris Removal/Construction Material")) {
                    Intent intent = new Intent(post_a_complaint.this, constructionmaterial.class);
                    startActivity(intent);
                }
                if (selectedTitle.equals("Burning Of Garbage in Open Space")) {
                    Intent intent = new Intent(post_a_complaint.this, burningofgarbageinopen.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void addComplaintItems() {
        addComplaintItem("Yellow Spot (Public Urination Spot)", R.drawable.yellow_spot);
        addComplaintItem("Overflow of Septic Tanks (New)", R.drawable.septic_tank);
        addComplaintItem("Overflow of Sewerage or Storm Water", R.drawable.sewerage_overflow);
        addComplaintItem("Dead animal(s)", R.drawable.dead_animal);
        addComplaintItem("Dustbins not cleaned", R.drawable.dustbin_clean);
        addComplaintItem("Garbage dump", R.drawable.garbage_dump);
        addComplaintItem("Garbage vehicle not arrived", R.drawable.vehicle_not_arrived);
        addComplaintItem("Sweeping not done", R.drawable.sweeping_not_done);
        addComplaintItem("No electricity", R.drawable.no_electricity_in_public_toilet);
        addComplaintItem("No water supply", R.drawable.no_water_supply_in_public);
        addComplaintItem("Public toilet(s) blockage", R.drawable.public_toilets_blockage);
        addComplaintItem("Open Manholes or Drains", R.drawable.open_manholes_drains);
        addComplaintItem("Stagnant Water on the Road", R.drawable.stagnant_water_on_road);
        addComplaintItem("Debris Removal/Construction Material", R.drawable.construction_material);
        addComplaintItem("Burning Of Garbage in Open Space", R.drawable.burning_garbage_inopen_space);
    }

    private void addComplaintItem(String title, int icon) {
        HashMap<String, Object> item = new HashMap<>();
        item.put("title", title);
        item.put("icon", icon);
        complaintList.add(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(post_a_complaint.this, bottomnavigation.class);
        startActivity(intent);
        finish();
    }
}
