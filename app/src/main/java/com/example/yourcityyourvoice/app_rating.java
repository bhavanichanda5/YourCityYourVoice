package com.example.yourcityyourvoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class app_rating extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextView ratingDescription;
    private EditText feedbackEditText;
    private Button submitButton, laterButton;

    // Firebase variables
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_rating);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Portal Feedback");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("app_ratings");

        // Initialize views
        ratingBar = findViewById(R.id.ratingBar);
        ratingDescription = findViewById(R.id.ratingDescription);
        feedbackEditText = findViewById(R.id.feedbackEditText);
        submitButton = findViewById(R.id.submitButton);
        laterButton = findViewById(R.id.laterButton);

        // Set up rating bar listener
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            String description = getRatingDescription(rating);
            ratingDescription.setText(description);
        });

        // Submit button click listener
        submitButton.setOnClickListener(v -> submitRating());

        // Later button click listener
        laterButton.setOnClickListener(v -> finish());
    }

    private String getRatingDescription(float rating) {
        if (rating == 5) return "Excellent";
        if (rating >= 4) return "Very Good";
        if (rating >= 3) return "Good";
        if (rating >= 2) return "Fair";
        if (rating > 0) return "Poor";
        return "Tap to rate";
    }

    private void submitRating() {
        float rating = ratingBar.getRating();
        String feedback = feedbackEditText.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : "anonymous";
        String userEmail = currentUser != null ? currentUser.getEmail() : "anonymous@example.com";

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Create rating data
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("userId", userId);
        ratingData.put("userEmail", userEmail);
        ratingData.put("rating", rating);
        ratingData.put("feedback", feedback);
        ratingData.put("timestamp", timestamp);

        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            ratingData.put("appVersion", versionName);
        } catch (Exception e) {
            ratingData.put("appVersion", "unknown");
        }

        // Push to database
        databaseReference.push().setValue(ratingData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(app_rating.this,
                                "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(app_rating.this,
                                "Failed to submit rating: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(app_rating.this, bottomnavigation.class);
        startActivity(intent);
        finish();
    }
}