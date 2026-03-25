package com.example.yourcityyourvoice;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Toilet implements com.google.maps.android.clustering.ClusterItem {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String address;
    private String type;
    private boolean isAccessible;
    private boolean isFree;
    private double rating;
    private float zIndex = 0;  // Default z-index

    public Toilet(String id, String name, double latitude, double longitude,
                  String address, String type, boolean isAccessible,
                  boolean isFree, double rating) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.type = type;
        this.isAccessible = isAccessible;
        this.isFree = isFree;
        this.rating = rating;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @Nullable
    @Override
    public String getTitle() {
        return name;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return address;
    }

    @Override
    public Float getZIndex() {
        return zIndex;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getAddress() { return address; }
    public String getType() { return type; }
    public boolean isAccessible() { return isAccessible; }
    public boolean isFree() { return isFree; }
    public double getRating() { return rating; }
}