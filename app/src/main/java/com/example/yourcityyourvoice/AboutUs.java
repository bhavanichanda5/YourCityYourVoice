package com.example.yourcityyourvoice;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class AboutUs extends AppCompatActivity {

    TextView tvComplaintInfo, tvFilingSteps, tvEligibility, tvMap, tvLogout;//tv_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Initialize the TextViews for the clickable items
        tvComplaintInfo = findViewById(R.id.tv_complaint_info);
        tvFilingSteps = findViewById(R.id.tv_filing_steps);
        tvEligibility = findViewById(R.id.tv_eligibility);
        tvMap = findViewById(R.id.tv_map);
       // tv_home = findViewById(R.id.tv_home);
        tvLogout = findViewById(R.id.tv_logout);

       /* tv_home.setOnClickListener(view -> {
            Intent intent = new Intent(AboutUs.this, Home.class);
            startActivity(intent);
        });*/


        tvFilingSteps.setOnClickListener(view -> {
            Intent intent = new Intent(AboutUs.this, filing_steps.class);
            startActivity(intent);
        });


        tvEligibility.setOnClickListener(view -> {
            Intent intent = new Intent(AboutUs.this, eligibility.class);
            startActivity(intent);
        });


        tvMap.setOnClickListener(view -> {
            Intent intent = new Intent(AboutUs.this, Location.class);
            startActivity(intent);
        });


        tvLogout.setOnClickListener(view -> {

            getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().putBoolean("isLoggedIn", false).apply();


            Intent intent = new Intent(AboutUs.this, loginActivity.class);
            startActivity(intent);
            finish();  // Close AboutUs activity
        });
    }

    @Override
    public void onBackPressed() {
        // Handle the back button behavior
        // You can either leave this to default or redirect to the login screen, depending on your needs
        Intent intent = new Intent(AboutUs.this, loginActivity.class); // Or the previous page you want to navigate to
        startActivity(intent);
        finish();
    }
}
