package com.example.yourcityyourvoice;

import java.util.List;

public class Complaint {
    private String complaintId;
    private String tvComplaintType;
    private String area;
    private String additionalInfo;
    private String imageUrl;
    private boolean isVoted;
    private double latitude;
    private double longitude;
    private List<String> comments;
    private String id;
    private String status;

    // Default constructor (needed for Firebase)
    public Complaint() {
    }

    // Parameterized constructor
    public Complaint(String complaintId,String id,String status, String tvComplaintType, String area, String additionalInfo, String imageUrl, double latitude, double longitude) {
        this.complaintId = complaintId;
        this.tvComplaintType = tvComplaintType;
        this.area = area;
        this.additionalInfo = additionalInfo;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isVoted = false;
        this.id=id;
        this.status=status;
    }

    // Getters
    public String getComplaintId() { return complaintId; }
    public String gettvComplaintType() { return tvComplaintType; }
    public String getArea() { return area; }
    public String getAdditionalInfo() { return additionalInfo; }
    public String getImageUrl() { return imageUrl; }
    public boolean isVoted() { return isVoted; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public List<String> getComments() { return comments; }

    // Setters
    public void setVoted(boolean voted) { isVoted = voted; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setComments(List<String> comments) { this.comments = comments; }

    public String getId() {
        return id;
    }
    public CharSequence getStatus() {return status;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Complaint complaint = (Complaint) o;
        return complaintId.equals(complaint.complaintId);
    }

    @Override
    public int hashCode() {
        return complaintId.hashCode();
    }
}
