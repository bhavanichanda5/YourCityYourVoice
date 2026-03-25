package com.example.yourcityyourvoice;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ImageView flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Reference to the ImageView
        flag = findViewById(R.id.video);

        // Set a static image for the ImageView
        flag.setImageResource(R.drawable.i2); // Replace `your_image` with your drawable resource

        // Apply scale and fade-in animation
        animateImageView(flag);

        // Delay to navigate to the login activity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, loginActivity.class);
            startActivity(intent);
            finish();
        }, 4000);
    }

    private void animateImageView(ImageView imageView) {
        // Scale the ImageView from 0.5x to 1.0x (grow effect)
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 0.5f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 0.5f, 1.0f);

        // Fade-in effect
        ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);

        // Combine animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setDuration(1500); // Animation duration in milliseconds
        animatorSet.start();
    }
}
