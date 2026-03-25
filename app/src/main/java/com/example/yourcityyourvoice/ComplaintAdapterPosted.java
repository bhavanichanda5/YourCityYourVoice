package com.example.yourcityyourvoice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComplaintAdapterPosted extends RecyclerView.Adapter<ComplaintAdapterPosted.ViewHolder> {

    private static final int CAMERA_REQUEST_CODE = 100;
    private Context context;
    private List<Complaint> complaintList;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

    public ComplaintAdapterPosted(Context context, List<Complaint> complaintList) {
        this.context = context;
        this.complaintList = complaintList;
        this.sharedPreferences = context.getSharedPreferences("VotedComplaints", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.complaint_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Complaint complaint = complaintList.get(position);

        holder.tvComplaintType.setText(complaint.gettvComplaintType());
        holder.tvArea.setText(complaint.getArea());
        holder.tvAdditionalInfo.setText(complaint.getAdditionalInfo());
        holder.id.setText(complaint.getComplaintId());
        holder.status.setText(complaint.getStatus());

        // Load image using Glide
        Glide.with(context)
                .load(complaint.getImageUrl())
                .placeholder(R.drawable.baseline_image_24)
                .error(R.drawable.img_5)
                .into(holder.imageView);

        // Set initial vote button state
        updateVoteButton(holder.voteButton, isVoted(complaint));

        // Handle voting
        holder.voteButton.setOnClickListener(v -> {
            boolean currentlyVoted = isVoted(complaint);
            if (currentlyVoted) {
                removeVotedComplaint(complaint);
                Toast.makeText(context, "Vote removed", Toast.LENGTH_SHORT).show();
            } else {
                addVotedComplaint(complaint);
                Toast.makeText(context, "Voted successfully", Toast.LENGTH_SHORT).show();
            }
            updateVoteButton(holder.voteButton, !currentlyVoted);
        });

        holder.shareButton.setOnClickListener(v -> shareComplaint(complaint));
        holder.commentButton.setOnClickListener(v -> showAddCommentDialog(complaint.getComplaintId()));
        holder.redirectMapButton.setOnClickListener(v -> openGoogleMaps(complaint.getLatitude(), complaint.getLongitude()));
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    private boolean isVoted(Complaint complaint) {
        return sharedPreferences.contains(complaint.getComplaintId());
    }

    private void addVotedComplaint(Complaint complaint) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String complaintJson = gson.toJson(complaint);
        editor.putString(complaint.getComplaintId(), complaintJson);
        editor.apply();
    }

    private void removeVotedComplaint(Complaint complaint) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(complaint.getComplaintId());
        editor.apply();
    }

    private void updateVoteButton(ImageButton voteButton, boolean isVoted) {
        if (isVoted) {
            voteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_thumb_up_24));
            voteButton.setColorFilter(ContextCompat.getColor(context, R.color.seegreen));
        } else {
            voteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_thumb_up_off_alt_24));
            voteButton.setColorFilter(ContextCompat.getColor(context, R.color.seegreen));
        }
    }

    private void showAddCommentDialog(String complaintId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.add_comment_dialog, null);
        builder.setView(dialogView);

        EditText commentEditText = dialogView.findViewById(R.id.commentEditText);
        ImageView btnSend = dialogView.findViewById(R.id.btnSendComment);
        ImageView btnCamera = dialogView.findViewById(R.id.btnAddImage);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSend.setOnClickListener(v -> {
            String commentText = commentEditText.getText().toString().trim();
            if (!commentText.isEmpty()) {
                addCommentToDatabase(complaintId, commentText);
                dialog.dismiss();
            } else {
                commentEditText.setError("Enter a comment");
            }
        });

        btnCamera.setOnClickListener(v -> openCamera());
    }

    private void addCommentToDatabase(String complaintId, String commentText) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Complaints")
                .child(complaintId).child("comments");

        String newCommentId = databaseRef.push().getKey();
        if (newCommentId != null) {
            HashMap<String, Object> commentData = new HashMap<>();
            commentData.put("commentId", newCommentId);
            commentData.put("text", commentText);
            commentData.put("timestamp", System.currentTimeMillis());

            databaseRef.child(newCommentId).setValue(commentData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to add comment", Toast.LENGTH_SHORT).show());
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            ((Activity) context).startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(context, "Camera not supported", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareComplaint(Complaint complaint) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Complaint: " + complaint.getAdditionalInfo() +
                        "\nLocation: " + complaint.getArea());
        context.startActivity(Intent.createChooser(shareIntent, "Share Complaint"));
    }

    private void openGoogleMaps(double latitude, double longitude) {
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude +
                "?q=" + latitude + "," + longitude + "(Complaint Location)");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            Toast.makeText(context, "Google Maps not installed", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateList(List<Complaint> filteredList) {
        complaintList.clear();
        complaintList.addAll(filteredList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvComplaintType, tvArea, tvAdditionalInfo, id, status;
        ImageView imageView;
        ImageButton shareButton, voteButton, commentButton, redirectMapButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComplaintType = itemView.findViewById(R.id.tv_complaint_type);
            tvArea = itemView.findViewById(R.id.tv_area);
            tvAdditionalInfo = itemView.findViewById(R.id.tv_additional_info);
            imageView = itemView.findViewById(R.id.ImgUrl);
            shareButton = itemView.findViewById(R.id.share);
            voteButton = itemView.findViewById(R.id.vote);
            commentButton = itemView.findViewById(R.id.comment);
            redirectMapButton = itemView.findViewById(R.id.redirectingmap);
            id = itemView.findViewById(R.id.tv_id);
            status = itemView.findViewById(R.id.tv_status);
        }
    }
}