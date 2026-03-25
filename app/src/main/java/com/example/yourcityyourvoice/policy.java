package com.example.yourcityyourvoice;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class policy extends AppCompatActivity {

    private TextView tvExpand;
    private LinearLayout privacyContent;
    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);


        // Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Privacy Policy");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        tvExpand = findViewById(R.id.tv_expand);
        privacyContent = findViewById(R.id.privacy_content);

        tvExpand.setOnClickListener(v -> {
            if (isExpanded) {
                privacyContent.setVisibility(View.GONE);
                tvExpand.setText("Read More ▼");
            } else {
                privacyContent.setVisibility(View.VISIBLE);
                tvExpand.setText("Read Less ▲");
            }
            isExpanded = !isExpanded;
        });
    }
}
