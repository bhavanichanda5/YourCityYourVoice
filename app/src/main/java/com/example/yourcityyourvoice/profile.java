package com.example.yourcityyourvoice;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class profile extends Fragment {

    LinearLayout notification, laguage, playstore, report, policy, checknew, logout;
    ImageView profileImage;
    EditText uname, phoneNumber, email, address;
    Button editButton, saveButton;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    String userId;
    Uri imageUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        uname = view.findViewById(R.id.uname);
        profileImage = view.findViewById(R.id.profile_image);
        phoneNumber = view.findViewById(R.id.phone_number);
        email = view.findViewById(R.id.email);
        address = view.findViewById(R.id.address);
        editButton = view.findViewById(R.id.edit_button);
        saveButton = view.findViewById(R.id.save_button);
        notification = view.findViewById(R.id.notify);
        laguage = view.findViewById(R.id.language);
        playstore = view.findViewById(R.id.playstore);
        report = view.findViewById(R.id.report);
        policy = view.findViewById(R.id.policy);
        checknew = view.findViewById(R.id.whatsnew);
        logout = view.findViewById(R.id.logout);

        notification.setOnClickListener(v -> startActivity(new Intent(requireContext(), notification.class)));
        laguage.setOnClickListener(v -> startActivity(new Intent(requireContext(), language.class)));
        playstore.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + requireContext().getPackageName()))));
        report.setOnClickListener(v -> startActivity(new Intent(requireContext(), report.class)));
        policy.setOnClickListener(v -> startActivity(new Intent(requireContext(), policy.class)));
        checknew.setOnClickListener(v -> startActivity(new Intent(requireContext(), whatsnew.class)));

        logout.setOnClickListener(v -> {
            requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().putBoolean("isLoggedIn", false).apply();
            startActivity(new Intent(requireContext(), loginActivity.class));
            requireActivity().finish();
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(getContext(), "User not signed in.", Toast.LENGTH_SHORT).show();
            return view;
        }

        loadUserData();

        profileImage.setOnClickListener(v -> ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start());

        editButton.setOnClickListener(v -> enableEditing());

        saveButton.setOnClickListener(v -> saveUserData());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Image selection canceled", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserData() {
        String updatedUname = uname.getText().toString();
        String updatedPhone = phoneNumber.getText().toString();
        String updatedEmail = email.getText().toString();
        String updatedAddress = address.getText().toString();

        if (imageUri != null) {
            StorageReference imageRef = storageReference.child(userId + ".jpg");
            UploadTask uploadTask = imageRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(taskSnapshot ->
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        databaseReference.child(userId).child("username").setValue(updatedUname);
                        databaseReference.child(userId).child("phone").setValue(updatedPhone);
                        databaseReference.child(userId).child("email").setValue(updatedEmail);
                        databaseReference.child(userId).child("address").setValue(updatedAddress);
                        databaseReference.child(userId).child("profileImage").setValue(imageUrl);

                        disableEditing();
                        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    })
            ).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Image Upload", "Error uploading image", e);
            });

        } else {
            databaseReference.child(userId).child("username").setValue(updatedUname);
            databaseReference.child(userId).child("phone").setValue(updatedPhone);
            databaseReference.child(userId).child("email").setValue(updatedEmail);
            databaseReference.child(userId).child("address").setValue(updatedAddress);

            disableEditing();
            Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData() {
        databaseReference.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String unameStr = task.getResult().child("username").getValue(String.class);
                String phone = task.getResult().child("phone").getValue(String.class);
                String emailStr = task.getResult().child("email").getValue(String.class);
                String addressStr = task.getResult().child("address").getValue(String.class);
                String imageUrl = task.getResult().child("profileImage").getValue(String.class);

                uname.setText(unameStr);
                phoneNumber.setText(phone);
                email.setText(emailStr);
                address.setText(addressStr);

                if (imageUrl != null) {
                    Glide.with(requireContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.profile_placeholder) // Replace with your placeholder image
                            .error(R.drawable.profile_placeholder)      // Replace with your error image
                            .into(profileImage);
                }
            } else {
                Toast.makeText(getContext(), "Error retrieving user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableEditing() {
        uname.setEnabled(true);
        phoneNumber.setEnabled(true);
        email.setEnabled(true);
        address.setEnabled(true);
        saveButton.setVisibility(View.VISIBLE);
    }

    private void disableEditing() {
        uname.setEnabled(false);
        phoneNumber.setEnabled(false);
        email.setEnabled(false);
        address.setEnabled(false);
        saveButton.setVisibility(View.GONE);
    }
}
