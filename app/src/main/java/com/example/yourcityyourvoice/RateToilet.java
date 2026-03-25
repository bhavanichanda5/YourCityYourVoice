package com.example.yourcityyourvoice;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.lang.reflect.Method;

public class RateToilet extends AppCompatActivity {

    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton flashlightButton;
    private boolean isFlashlightOn = false;
    private ViewfinderView viewfinderView;
    private ImageView scanLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_toilet);

        barcodeScannerView = findViewById(R.id.barcodeScannerView);
        flashlightButton = findViewById(R.id.flashlightButton);
        viewfinderView = barcodeScannerView.getViewFinder();
        scanLine = findViewById(R.id.scan_line);

        // Customize the scanner view
        customizeViewfinder();

        // Setup toolbar
        setupToolbar();

        // Flashlight toggle button
        flashlightButton.setOnClickListener(v -> toggleFlashlight());

        // Start scanning animation
        startScanAnimation();

        // Continuous scanning
        setupBarcodeScanner();
    }

    private void customizeViewfinder() {
        try {
            // Use reflection to access the hidden methods
            Class<?> clazz = viewfinderView.getClass();

            Method setBorderColor = clazz.getMethod("setBorderColor", int.class);
            setBorderColor.invoke(viewfinderView, Color.GREEN);

            Method setBorderStrokeWidth = clazz.getMethod("setBorderStrokeWidth", int.class);
            setBorderStrokeWidth.invoke(viewfinderView, 5);

            Method setBorderLineLength = clazz.getMethod("setBorderLineLength", int.class);
            setBorderLineLength.invoke(viewfinderView, 60);

            Method setLaserEnabled = clazz.getMethod("setLaserEnabled", boolean.class);
            setLaserEnabled.invoke(viewfinderView, true);

            Method setLaserColor = clazz.getMethod("setLaserColor", int.class);
            setLaserColor.invoke(viewfinderView, Color.GREEN);

            Method setMaskColor = clazz.getMethod("setMaskColor", int.class);
            setMaskColor.invoke(viewfinderView, Color.argb(150, 0, 0, 0));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Scan Public Toilet QR Code");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }

    private void startScanAnimation() {
        scanLine.setVisibility(View.VISIBLE);
        scanLine.animate()
                .translationY(viewfinderView.getHeight())
                .setDuration(2000)
                .withEndAction(() -> {
                    scanLine.setTranslationY(0);
                    startScanAnimation(); // Loop the animation
                })
                .start();
    }

    private void stopScanAnimation() {
        scanLine.animate().cancel();
        scanLine.setVisibility(View.GONE);
    }

    private void toggleFlashlight() {
        try {
            isFlashlightOn = !isFlashlightOn;
            barcodeScannerView.getBarcodeView().setTorch(isFlashlightOn);

            // Update button icon based on flashlight state
            flashlightButton.setImageResource(
                    isFlashlightOn ?
                            R.drawable.flashlight :  // Make sure these resources exist
                            R.drawable.baseline_flashlight_off_24
            );

            // Button animation
            flashlightButton.animate()
                    .scaleX(0.8f).scaleY(0.8f)
                    .setDuration(100)
                    .withEndAction(() ->
                            flashlightButton.animate()
                                    .scaleX(1f).scaleY(1f)
                                    .setDuration(100)
                                    .start()
                    )
                    .start();

        } catch (Exception e) {
            // Handle devices without flashlight
            flashlightButton.setEnabled(false);
        }
    }

    private void setupBarcodeScanner() {
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result != null) {
                    stopScanAnimation();
                    handleScannedResult(result.getText());
                }
            }

            @Override
            public void possibleResultPoints(java.util.List<com.google.zxing.ResultPoint> resultPoints) {
                // Optional: Handle possible result points
            }
        });
    }

    private void handleScannedResult(String scannedData) {
        Intent intent = new Intent(this, SubmitRatingActivity.class);
        intent.putExtra("scannedData", scannedData);
        startActivityForResult(intent, 1);
        barcodeScannerView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeScannerView.resume();
        startScanAnimation();
        resetFlashlightState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
        stopScanAnimation();
        turnOffFlashlight();
    }

    private void resetFlashlightState() {
        if (isFlashlightOn) {
            barcodeScannerView.getBarcodeView().setTorch(false);  // Fixed here
            isFlashlightOn = false;
            flashlightButton.setImageResource(R.drawable.baseline_flashlight_off_24);
        }
    }

    private void turnOffFlashlight() {
        if (isFlashlightOn) {
            barcodeScannerView.getBarcodeView().setTorch(false);  // Fixed here
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            barcodeScannerView.resume();
            startScanAnimation();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateToHome();
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, bottomnavigation.class);
        startActivity(intent);
        finish();
    }
}