package com.example.yourcityyourvoice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class event_next_page extends AppCompatActivity {

    private EditText etEmail,etEventTitle, etEventDescription, etEventOrganizer, etEventDate, etLocation;
    private Button btnNext;
    private ImageView event_img;
    private DatabaseReference eventsRef;
    private StorageReference storageRef;
    private ProgressDialog progressDialog;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_next_page);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Post Event");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        // Initialize Firebase
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
        storageRef = FirebaseStorage.getInstance().getReference().child("Events");

        // Initialize Views
        etEmail=findViewById(R.id.et_email);
        etEventTitle = findViewById(R.id.et_event_title);
        etEventDescription = findViewById(R.id.et_event_description);
        etEventOrganizer = findViewById(R.id.et_event_Organizer);
        etEventDate = findViewById(R.id.et_event_date);
        etLocation = findViewById(R.id.et_location);
        btnNext = findViewById(R.id.btn_next);
        event_img = findViewById(R.id.event_image);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving event...");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        etEmail = findViewById(R.id.et_email);

        // Auto-fill the email field if the user is logged in
        if (user != null) {
            etEmail.setText(user.getEmail());
            etEmail.setEnabled(false); // Disable editing
        }

        // Open Image Picker on Click
        event_img.setOnClickListener(v -> ImagePicker.with(this)
                .crop() // Enables cropping
                .compress(1024) // Compress image
                .maxResultSize(1080, 1080)
                .start());

        // Save Event on Button Click
        btnNext.setOnClickListener(v -> saveEventDetails());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            event_img.setImageURI(imageUri);
        }
    }

    private void saveEventDetails() {
        String createdBy = etEmail.getText().toString().trim();
        String title = etEventTitle.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();
        String organizer = etEventOrganizer.getText().toString().trim();
        String time = etEventDate.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (TextUtils.isEmpty(createdBy) ||TextUtils.isEmpty(title) || TextUtils.isEmpty(description) ||
                TextUtils.isEmpty(organizer) || TextUtils.isEmpty(time) ||
                TextUtils.isEmpty(location)) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        String eventId = eventsRef.push().getKey();

        // Get current timestamp
        long timestamp = System.currentTimeMillis();

        HashMap<String, Object> eventMap = new HashMap<>();
        eventMap.put("createdBy", createdBy);
        eventMap.put("description", description);
        eventMap.put("imageUrl", "");
        eventMap.put("key", eventId);
        eventMap.put("location", location);
        eventMap.put("organizer", organizer);
        eventMap.put("time", time);  // This is the event time entered by user
        eventMap.put("title", title);
        eventMap.put("timestamp", timestamp);  // This is when the event was submitted

        if (imageUri != null) {
            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        eventMap.put("imageUrl", uri.toString());
                        saveEventToDatabase(eventMap, eventId);
                    }))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(event_next_page.this, "Image Upload Failed!", Toast.LENGTH_SHORT).show();
                    });
        } else {
            saveEventToDatabase(eventMap, eventId);
        }
    }

    private void saveEventToDatabase(HashMap<String, Object> eventMap, String eventId) {
        eventsRef.child(eventId).setValue(eventMap)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(event_next_page.this, "Event Saved Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(event_next_page.this, "Failed to save event!", Toast.LENGTH_SHORT).show();
                });
    }
}
