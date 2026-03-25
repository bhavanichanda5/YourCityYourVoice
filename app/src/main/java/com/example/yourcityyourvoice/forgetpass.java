package com.example.yourcityyourvoice;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class forgetpass extends AppCompatActivity {

    EditText emailEditText;
    Button otpButton;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpass);

        emailEditText = findViewById(R.id.email);
        otpButton = findViewById(R.id.otp_button);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        otpButton.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            emailEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        otpButton.setEnabled(false); // Disable button to prevent multiple requests

        // Send password reset email
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    otpButton.setEnabled(true); // Re-enable button

                    if (task.isSuccessful()) {
                        Toast.makeText(forgetpass.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(forgetpass.this, loginActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = "Failed to send reset email";
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            errorMessage = "Email not registered";
                        } else if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        Toast.makeText(forgetpass.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("Reset Password Error", errorMessage);
                    }
                });
    }
}
