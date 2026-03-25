package com.example.yourcityyourvoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity {

    Button btn;
    TextView tv, tv_forget_pass;
    EditText edt_email, edt_password;

    // Firebase instance
    private FirebaseAuth mAuth;

    // State to track password visibility
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check login state from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // If user is already logged in, skip login page and navigate directly to AboutUs
            navigateToHome();
            return;
        }

        setContentView(R.layout.activity_login);

        btn = findViewById(R.id.btn_login);
        tv = findViewById(R.id.tv_sign_up);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        tv_forget_pass = findViewById(R.id.tv_forget_pass);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set listener for the login button
        btn.setOnClickListener(v -> {
            String email = edt_email.getText().toString().trim();
            String password = edt_password.getText().toString().trim();

            if (validateInput(email, password)) {
                loginUser(email, password);
            }
        });

        // Set listener for the sign-up text
        tv.setOnClickListener(v -> {
            Intent intent = new Intent(loginActivity.this, signUpActivity.class);
            startActivity(intent);
            finish();
        });
        tv_forget_pass.setOnClickListener(v -> {
            Intent intent = new Intent(loginActivity.this, forgetpass.class);
            startActivity(intent);
            finish();
        });

        // Add functionality for password visibility toggle
        edt_password.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Check if the touch is on the drawable end (right side)
                if (event.getRawX() >= (edt_password.getRight() - edt_password.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            edt_email.setError("Email is required");
            edt_email.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edt_email.setError("Enter a valid email");
            edt_email.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            edt_password.setError("Password is required");
            edt_password.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            edt_password.setError("Password must be at least 6 characters long");
            edt_password.requestFocus();
            return false;
        }

        return true;
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Save login state in SharedPreferences
                        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        // Login successful, redirect to AboutUs
                        Toast.makeText(loginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        navigateToHome();
                    } else {
                        // Login failed
                        Toast.makeText(loginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(loginActivity.this, bottomnavigation.class);
        startActivity(intent);
        finish();
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            edt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edt_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
        } else {
            edt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            edt_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
        }
        isPasswordVisible = !isPasswordVisible;
        edt_password.setSelection(edt_password.getText().length()); // Move cursor to the end
    }
}
