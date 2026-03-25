package com.example.yourcityyourvoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public class language extends AppCompatActivity {
    private Spinner spinnerLanguages;
    private Button btnApplyLanguage;
    private String selectedLanguage = "en"; // Default language

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Select Language");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        spinnerLanguages = findViewById(R.id.spinnerLanguages);
        btnApplyLanguage = findViewById(R.id.btnApplyLanguage);

        // Get saved language preference
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        selectedLanguage = prefs.getString("language", "en");

        // Handle Spinner Selection
        spinnerLanguages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedLanguage = "en"; // English
                } else if (position == 1) {
                    selectedLanguage = "hi"; // Hindi
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Apply Language Change
        btnApplyLanguage.setOnClickListener(v -> {
            setLocale(selectedLanguage);
        });
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Save language preference
        SharedPreferences.Editor editor = getSharedPreferences("AppSettings", MODE_PRIVATE).edit();
        editor.putString("language", lang);
        editor.apply();

        // Restart Activity to apply changes
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
