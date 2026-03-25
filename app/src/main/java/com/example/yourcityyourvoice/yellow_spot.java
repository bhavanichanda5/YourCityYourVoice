package com.example.yourcityyourvoice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class yellow_spot extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_PERMISSION_CODE = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 200;

    private MapView mapView;
    private GoogleMap googleMap;

    private EditText etSelectArea, etAddInfo;
    private TextView btnDirty, btnLackToilet, btnNearby, btnSmelly, btnUnhygienic;
    private TextInputEditText tvPostedBy,tvComplaintType;
    private ImageView ivAddPhoto;
    private Button btnDone;

    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yellow_spot);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Yellow Spot");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        initializeViews();
        setupMapView(savedInstanceState);
        setupListeners();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        TextInputEditText tvPostedBy = findViewById(R.id.tvPostedBy);

        if (user != null) {
            String email = user.getEmail();
            tvPostedBy.setText(email);
        } else {
            tvPostedBy.setText("No user logged in");
        }


    }

    private void initializeViews() {
        mapView = findViewById(R.id.mapView);
        tvComplaintType = findViewById(R.id.tvComplaintType);
        tvComplaintType.setText(getSupportActionBar().getTitle().toString());
        tvPostedBy = findViewById(R.id.tvPostedBy);
        etSelectArea = findViewById(R.id.etSelectArea);
        etAddInfo = findViewById(R.id.etAddInfo);
        btnDirty = findViewById(R.id.btnDirty);
        btnLackToilet = findViewById(R.id.btnLackToilet);
        btnNearby = findViewById(R.id.btnNearby);
        btnSmelly = findViewById(R.id.smelly);
        btnUnhygienic = findViewById(R.id.unhygin);
        ivAddPhoto = findViewById(R.id.ivAddPhoto);
        btnDone = findViewById(R.id.btnDone);
    }



    private void setupMapView(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    private void setupListeners() {
        setupQuickSelectionListeners();
        ivAddPhoto.setOnClickListener(v -> checkPermissionsAndOpenImagePicker());
        btnDone.setOnClickListener(v -> submitForm());

    }


    private void setupQuickSelectionListeners() {
        View.OnClickListener quickSelectionListener = v -> {
            TextView button = (TextView) v;
            etAddInfo.append(button.getText().toString() + " ");
        };

        btnDirty.setOnClickListener(quickSelectionListener);
        btnLackToilet.setOnClickListener(quickSelectionListener);
        btnNearby.setOnClickListener(quickSelectionListener);
        btnSmelly.setOnClickListener(quickSelectionListener);
        btnUnhygienic.setOnClickListener(quickSelectionListener);
    }

    private void checkPermissionsAndOpenImagePicker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION_CODE);
        } else {
            showImagePickerOptions();
        }
    }

    private void showImagePickerOptions() {
        String[] options = {"Take a Photo", "Choose from Gallery"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Select Option")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermission();
                    } else {
                        openGallery();
                    }
                }).show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.yourcityyourvoice.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (IOException ex) {
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    private void submitForm() {
        String complaintType= tvComplaintType.getText().toString().trim();
        String posted_by= tvPostedBy.getText().toString().trim();
        String selectedArea = etSelectArea.getText().toString().trim();
        String additionalInfo = etAddInfo.getText().toString().trim();

        if (complaintType.isEmpty() || posted_by.isEmpty()|| selectedArea.isEmpty() || additionalInfo.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentPhotoPath != null) {
            uploadPhotoAndSaveData(complaintType,posted_by,selectedArea, additionalInfo);
        } else {
            saveDataToFirebase(complaintType,posted_by,selectedArea, additionalInfo, null);
        }
    }

    private void uploadPhotoAndSaveData(String tvComplaintType,String tvPostedBy,String selectedArea, String additionalInfo) {
        Uri fileUri = Uri.fromFile(new File(currentPhotoPath));
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Complaint_Clicks/" + fileUri.getLastPathSegment());

        storageReference.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveDataToFirebase(tvComplaintType,tvPostedBy,selectedArea, additionalInfo, imageUrl);
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload image.", Toast.LENGTH_SHORT).show());
    }

    private void saveDataToFirebase(String tvComplaintType,String tvPostedBy,String selectedArea, String additionalInfo, String imageUrl) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Complaints").child("YellowSpots");
        String id = databaseReference.push().getKey();
        if (id != null) {
            YellowSpot data = new YellowSpot(tvComplaintType,tvPostedBy,selectedArea, additionalInfo, imageUrl);
            databaseReference.child(id).setValue(data)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Complaint submitted successfully.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Submission failed.", Toast.LENGTH_SHORT).show());
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        enableUserLocation();
        setupMapClickListener();
    }

    private void enableUserLocation() {
        if (hasLocationPermission()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
            updateToUserLocation();
        } else {
            requestLocationPermission();
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void updateToUserLocation() {
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            }
        });
    }

    private void setupMapClickListener() {
        googleMap.setOnMapClickListener(latLng -> {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    etSelectArea.setText(addresses.get(0).getAddressLine(0));
                } else {
                    etSelectArea.setText(String.format(Locale.getDefault(), "Lat: %.2f, Lng: %.2f", latLng.latitude, latLng.longitude));
                }
            } catch (IOException e) {
                e.printStackTrace();
                etSelectArea.setText(String.format(Locale.getDefault(), "Lat: %.2f, Lng: %.2f", latLng.latitude, latLng.longitude));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                ivAddPhoto.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
                scanFile();
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImage = data.getData();
                ivAddPhoto.setImageURI(selectedImage);
            }
        }
    }

    private void scanFile() {
        MediaScannerConnection.scanFile(this, new String[]{currentPhotoPath}, null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showImagePickerOptions();
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
