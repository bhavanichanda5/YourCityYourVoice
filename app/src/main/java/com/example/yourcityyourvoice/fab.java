package com.example.yourcityyourvoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class fab extends AppCompatActivity {

    private static final int CATEGORY_REQUEST_CODE = 100;
    private AutoCompleteTextView categoryDropdown;
    private Button nextButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab);

        // Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Select Event Categories");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        // Initialize Views
        categoryDropdown = findViewById(R.id.categoryDropdown);
        nextButton = findViewById(R.id.nextButton);

        // ➡️ Open Category Selection Activity on Dropdown Click
        categoryDropdown.setOnClickListener(v -> {
            Intent intent = new Intent(fab.this, category_selection.class);
            startActivityForResult(intent, CATEGORY_REQUEST_CODE);
        });

        // ✅ Handle Next Button Click
        nextButton.setOnClickListener(v -> {
            String selectedCategory = categoryDropdown.getText().toString().trim();
            if (!selectedCategory.isEmpty()) {
                Toast.makeText(this, "Selected: " + selectedCategory, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(fab.this, event_next_page.class);
                startActivity(intent);

            } else {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ⬅️ Handle Result from CategorySelectionActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CATEGORY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String selectedCategory = data.getStringExtra("category");
            categoryDropdown.setText(selectedCategory);
        }
    }
}
