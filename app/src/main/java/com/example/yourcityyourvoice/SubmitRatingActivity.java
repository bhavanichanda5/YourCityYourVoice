package com.example.yourcityyourvoice;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class SubmitRatingActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextView ratingDescription;
    private EditText feedbackEditText;
    private Button submitButton;
    private String scannedData;

    // Firebase variables
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_rating);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("toilet_ratings");

        // Get the scanned QR code data
        scannedData = getIntent().getStringExtra("scannedData");

        // Initialize views
        ratingBar = findViewById(R.id.ratingBar);
        ratingDescription = findViewById(R.id.ratingDescription);
        feedbackEditText = findViewById(R.id.feedbackEditText);
        submitButton = findViewById(R.id.submitButton);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Feedback");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        setupRatingBar();
        setupSubmitButton();
    }

    private void setupRatingBar() {
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            String description = getRatingDescription(rating);
            ratingDescription.setText(description);
        });
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String feedback = feedbackEditText.getText().toString().trim();

            if (rating == 0) {
                Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
                return;
            }

            submitRatingToDatabase(scannedData, rating, feedback);
        });
    }

    private void submitRatingToDatabase(String qrData, float rating, String feedback) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Please sign in to submit feedback", Toast.LENGTH_SHORT).show();
            // You might want to redirect to login screen here
            return;
        }

        String userEmail = currentUser.getEmail(); // Get user's email
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "No email associated with this account", Toast.LENGTH_SHORT).show();
            return;
        }

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Create rating data with email instead of UID
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("userEmail", userEmail);
        ratingData.put("qrCodeData", qrData);
        ratingData.put("rating", rating);
        ratingData.put("feedback", feedback);
        ratingData.put("timestamp", timestamp);

        // Push to database
        databaseReference.push().setValue(ratingData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SubmitRatingActivity.this,
                                "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                        navigateToHome();
                    } else {
                        Toast.makeText(SubmitRatingActivity.this,
                                "Failed to submit feedback: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getRatingDescription(float rating) {
        if (rating == 5) return "Excellent";
        if (rating >= 4) return "Very Good";
        if (rating >= 3) return "Good";
        if (rating >= 2) return "Fair";
        if (rating > 0) return "Poor";
        return "";
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, bottomnavigation.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateToHome();
    }
}