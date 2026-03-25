package com.example.yourcityyourvoice;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class otp extends AppCompatActivity {

    EditText otpDigit1, otpDigit2, otpDigit3, otpDigit4, otpDigit5, otpDigit6;
    Button verifyButton;
    private String enteredOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpDigit1 = findViewById(R.id.otp_digit1);
        otpDigit2 = findViewById(R.id.otp_digit2);
        otpDigit3 = findViewById(R.id.otp_digit3);
        otpDigit4 = findViewById(R.id.otp_digit4);
        otpDigit5 = findViewById(R.id.otp_digit5);
        otpDigit6 = findViewById(R.id.otp_digit6);
        verifyButton = findViewById(R.id.verify_otp_button);

        // Set up TextWatchers for automatic focus shifting
        setupTextWatcher(otpDigit1, otpDigit2);
        setupTextWatcher(otpDigit2, otpDigit3);
        setupTextWatcher(otpDigit3, otpDigit4);
        setupTextWatcher(otpDigit4, otpDigit5);
        setupTextWatcher(otpDigit5, otpDigit6);

        verifyButton.setOnClickListener(v -> {
            enteredOtp = otpDigit1.getText().toString() + otpDigit2.getText().toString() +
                    otpDigit3.getText().toString() + otpDigit4.getText().toString() +
                    otpDigit5.getText().toString() + otpDigit6.getText().toString();

            if (enteredOtp.length() == 6) {
                // Perform OTP verification here.  For this example, I'm just
                // checking if it's "123456".  Replace this with your actual
                // OTP verification logic.
                if (enteredOtp.equals("123456")) { // Replace with your OTP check
                    Intent intent = new Intent(this, newpassword.class);
                    startActivity(intent);
                    finish(); // Optional: Finish the OTP activity
                } else {
                    Toast.makeText(this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter all 6 digits", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void setupTextWatcher(EditText currentEditText, final EditText nextEditText) { // Made nextEditText final
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used in this example, but must be implemented
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used in this example, but must be implemented
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    if (nextEditText != null) { // Check if nextEditText is not null
                        nextEditText.requestFocus();
                    }
                }
            }
        });
    }
}