package com.example.yourcityyourvoice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class category_selection extends AppCompatActivity {

    RecyclerView recyclerView;

    String[] categories = {
            "Street Art", "Painting Public assets", "Public Toilet Clean Up",
            "Green Neighbourhoods", "Park Clean Up", "Swachhata Hi Seva",
            "Plogging", "Others"
    };

    int[] images = {
            R.drawable.img_7, R.drawable.img_8, R.drawable.img_9,
            R.drawable.img_10, R.drawable.img_11, R.drawable.img_12,
            R.drawable.img_14, R.drawable.img_13
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        // ✅ Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Categories");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        recyclerView = findViewById(R.id.recyclerView);

        // ✅ RecyclerView Setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        CategoryAdapter adapter = new CategoryAdapter(categories, images, selectedCategory -> {
            Intent intent = new Intent();
            intent.putExtra("category", selectedCategory);
            setResult(RESULT_OK, intent);
            finish(); // Close activity after selection
        });
        recyclerView.setAdapter(adapter);
    }
}
