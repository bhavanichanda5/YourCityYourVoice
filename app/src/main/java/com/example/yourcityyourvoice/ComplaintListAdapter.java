package com.example.yourcityyourvoice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

public class ComplaintListAdapter extends BaseAdapter {
    private Context context;
    private List<Complaint> complaintList;
    private LayoutInflater inflater;
    private Map<String, LatLng> areaCoordinates;

    public ComplaintListAdapter(Context context, List<Complaint> complaintList, Map<String, LatLng> areaCoordinates) {
        this.context = context;
        this.complaintList = complaintList;
        this.inflater = LayoutInflater.from(context);
        this.areaCoordinates = areaCoordinates;
    }

    public void updateList(List<Complaint> newList) {
        this.complaintList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return complaintList != null ? complaintList.size() : 0;
    }

    @Override
    public Complaint getItem(int position) {
        return complaintList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.complaint_items, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Complaint complaint = getItem(position);
        if (complaint != null) {
            holder.bind(complaint);

            // Set click listener for map button
            holder.redirectMapButton.setOnClickListener(v -> {
                String areaName = complaint.getArea() != null ?
                        complaint.getArea().toLowerCase() : "";

                if (areaCoordinates.containsKey(areaName)) {
                    LatLng location = areaCoordinates.get(areaName);
                    openMaps(location, complaint.getArea());
                } else {
                    Toast.makeText(context,
                            "Location not available for this area",
                            Toast.LENGTH_SHORT).show();
                }
            });

            // Set click listener for share button
            holder.shareButton.setOnClickListener(v -> shareComplaint(complaint));
        }

        return convertView;
    }

    private void openMaps(LatLng location, String areaName) {
        Uri gmmIntentUri = Uri.parse("geo:" + location.latitude + "," + location.longitude +
                "?q=" + location.latitude + "," + location.longitude + "(" + areaName + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            Toast.makeText(context, "Google Maps not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareComplaint(Complaint complaint) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Complaint: " + complaint.gettvComplaintType() +
                        "\nArea: " + complaint.getArea() +
                        "\nDetails: " + complaint.getAdditionalInfo());
        context.startActivity(Intent.createChooser(shareIntent, "Share Complaint"));
    }

    private static class ViewHolder {
        TextView tvComplaintType, tvArea, tvAdditionalInfo, id, status;
        ImageView imageView;
        ImageButton shareButton, redirectMapButton;

        ViewHolder(View view) {
            tvComplaintType = view.findViewById(R.id.tv_complaint_type);
            tvArea = view.findViewById(R.id.tv_area);
            tvAdditionalInfo = view.findViewById(R.id.tv_additional_info);
            id = view.findViewById(R.id.tv_id);
            status = view.findViewById(R.id.tv_status);
            imageView = view.findViewById(R.id.ImgUrl);
            shareButton = view.findViewById(R.id.share);
            redirectMapButton = view.findViewById(R.id.redirectingmap);
        }

        void bind(Complaint complaint) {
            tvComplaintType.setText(complaint.gettvComplaintType());
            tvArea.setText(complaint.getArea());
            tvAdditionalInfo.setText(complaint.getAdditionalInfo());
            id.setText(complaint.getComplaintId());
            status.setText(complaint.getStatus());

            Glide.with(imageView.getContext())
                    .load(complaint.getImageUrl())
                    .placeholder(R.drawable.baseline_image_24)
                    .error(R.drawable.img_5)
                    .into(imageView);
        }
    }
}