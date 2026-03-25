package com.example.yourcityyourvoice;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class FeedbackSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_success);

        ImageView starIcon = findViewById(R.id.starIcon);
        Animation bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        starIcon.startAnimation(bounceAnimation);

    }
}
