package com.example.yourcityyourvoice;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class newpassword extends AppCompatActivity {

    EditText newPasswordEditText, confirmPasswordEditText;
    Button changePasswordButton;
    ProgressBar progressBar;
    DatabaseReference databaseReference;
    String userEmail; // Store the user's email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpassword);

        newPasswordEditText = findViewById(R.id.et_pass1);
        confirmPasswordEditText = findViewById(R.id.et_confirmpass1);
        changePasswordButton = findViewById(R.id.chnge_button);
        progressBar = findViewById(R.id.progressBar); // Make sure you have a ProgressBar in your layout
        databaseReference = FirebaseDatabase.getInstance().getReference("PersonalDetails");

        // Get the user's email from the intent (passed from the otp activity)
        userEmail = getIntent().getStringExtra("email");

        changePasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(newPassword)) {
                newPasswordEditText.setError("New password is required");
                return;
            }

            if (newPassword.length() < 6) {
                newPasswordEditText.setError("Password must be at least 6 characters");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                confirmPasswordEditText.setError("Passwords do not match");
                return;
            }

            progressBar.setVisibility(View.VISIBLE); // Show progress bar

            // Find the user in the database using their email
            Query query = databaseReference.orderByChild("email").equalTo(userEmail);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String userId = snapshot.getKey(); // Get the user's ID

                            // Update the password in the database
                            databaseReference.child(userId).child("password").setValue(newPassword)
                                    .addOnCompleteListener(task -> {
                                        progressBar.setVisibility(View.GONE); // Hide progress bar
                                        if (task.isSuccessful()) {
                                            Toast.makeText(newpassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(newpassword.this, loginActivity.class);
                                            startActivity(intent);
                                            finish(); // Finish the newpassword activity
                                        } else {
                                            Toast.makeText(newpassword.this, "Failed to update password: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            break; // Exit the loop after finding the user
                        }
                    } else {
                        progressBar.setVisibility(View.GONE); // Hide progress bar
                        Toast.makeText(newpassword.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE); // Hide progress bar
                    Toast.makeText(newpassword.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}