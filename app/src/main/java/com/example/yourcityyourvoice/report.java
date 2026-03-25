package com.example.yourcityyourvoice;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class report extends AppCompatActivity {

    private EditText editIssue;
    private Button btnSubmitIssue;
    private FloatingActionButton fabEmail;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference; // Firebase Database reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Report_Technical_Issues");

        // Initialize UI elements
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Report Technical Issues");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        editIssue = findViewById(R.id.editIssue);
        btnSubmitIssue = findViewById(R.id.btnSubmitIssue);
        fabEmail = findViewById(R.id.fab_email);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE); // Hide initially

        // Submit button click listener
        btnSubmitIssue.setOnClickListener(v -> submitIssue());

        // Floating Action Button to send email
        fabEmail.setOnClickListener(v -> sendEmail());
    }

    private void submitIssue() {
        String issueDescription = editIssue.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(issueDescription)) {
            Toast.makeText(this, "Please describe the issue", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar while submitting
        progressBar.setVisibility(View.VISIBLE);
        btnSubmitIssue.setEnabled(false); // Disable button to prevent multiple clicks

        // Generate a unique ID for the issue
        String issueId = databaseReference.push().getKey();

        // Create a HashMap to store data
        Map<String, Object> issueData = new HashMap<>();
        issueData.put("issueId", issueId);
        issueData.put("description", issueDescription);
        issueData.put("timestamp", System.currentTimeMillis());

        // Store the issue in Firebase
        if (issueId != null) {
            databaseReference.child(issueId).setValue(issueData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(report.this, "Issue reported successfully", Toast.LENGTH_SHORT).show();
                        editIssue.setText(""); // Clear input after submission
                        progressBar.setVisibility(View.GONE);
                        btnSubmitIssue.setEnabled(true);
                        hideKeyboard();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(report.this, "Failed to report issue", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        btnSubmitIssue.setEnabled(true);
                    });
        }
    }

    private void sendEmail() {
        String issueText = editIssue.getText().toString().trim();

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@yourapp.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Issue Report - Your City Your Voice");
        emailIntent.putExtra(Intent.EXTRA_TEXT, issueText.isEmpty() ? "Describe your issue here..." : issueText);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email client installed!", Toast.LENGTH_SHORT).show();
        }
    }

    // Hide the keyboard after submission
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
