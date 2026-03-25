package com.example.yourcityyourvoice;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class bottomnavigation extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

            if (keypadHeight > screenHeight * 0.15) { // Keyboard is open
                bottomNavigationView.setVisibility(View.GONE);
            } else {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottomnavigation);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        setTitle("Home");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.home) {
                    loadFragment(new home_(), "Home");
                } else if (itemId == R.id.events) {
                    loadFragment(new Events(), "Events");
                } else if (itemId == R.id.plus) {
                    startActivity(new Intent(bottomnavigation.this, post_a_complaint.class));
                } else if (itemId == R.id.complaint) {
                    loadFragment(new complaints(), "Complaints");
                } else if (itemId == R.id.profile) {
                    loadFragment(new profile(), "Profile");
                } else {
                    Toast.makeText(bottomnavigation.this, "Invalid selection", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        if (savedInstanceState == null) {
            loadFragment(new home_(), "Home");
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }


    private void loadFragment(androidx.fragment.app.Fragment fragment, String title) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        setTitle(title);
    }
}
