package com.example.yourcityyourvoice;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class signUpActivity extends AppCompatActivity {

    private Button btnSignUp;
    private TextView tvLogin;
    private EditText edtEmail, edtMobile, edtPassword, edtConfirmPassword;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    // Password visibility
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignUp = findViewById(R.id.btn_signup);
        tvLogin = findViewById(R.id.tv_login_up);
        edtEmail = findViewById(R.id.et_email);
        edtMobile = findViewById(R.id.et_mobile);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_re_password);

        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        btnSignUp.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String mobile = edtMobile.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (validateInput(email, mobile, password, confirmPassword)) {
                registerUser(email, mobile, password);
            }
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(signUpActivity.this, loginActivity.class));
            finish();
        });

        togglePasswordVisibility(edtPassword, true);
        togglePasswordVisibility(edtConfirmPassword, false);
    }

    private void togglePasswordVisibility(EditText editText, boolean isMainPassword) {
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP &&
                    event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width())) {

                if (isMainPassword) {
                    isPasswordVisible = !isPasswordVisible;
                    editText.setInputType(isPasswordVisible
                            ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editText.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            isPasswordVisible ? R.drawable.baseline_visibility_24 : R.drawable.baseline_visibility_off_24, 0);
                } else {
                    isConfirmPasswordVisible = !isConfirmPasswordVisible;
                    editText.setInputType(isConfirmPasswordVisible
                            ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editText.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            isConfirmPasswordVisible ? R.drawable.baseline_visibility_24 : R.drawable.baseline_visibility_off_24, 0);
                }

                editText.setSelection(editText.getText().length());
                return true;
            }
            return false;
        });
    }


    private boolean validateInput(String email, String mobile, String password, String confirmPassword) {
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Enter a valid email");
            return false;
        }

        if (TextUtils.isEmpty(mobile) || mobile.length() != 10 || !mobile.matches("\\d{10}")) {
            edtMobile.setError("Enter a valid 10-digit mobile number");
            return false;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            edtPassword.setError("Password must be at least 6 characters");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private void registerUser(String email, String mobile, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            saveUserData(firebaseUser.getUid(), email, mobile);
                        }
                    } else {
                        Toast.makeText(signUpActivity.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String userId, String email, String mobile) {
        HashMap<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("mobile", mobile);
        userData.put("password", edtPassword.getText().toString().trim());

        databaseReference.child(userId).setValue(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(signUpActivity.this, "Sign-up successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(signUpActivity.this, loginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(signUpActivity.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
