package com.example.yourcityyourvoice;

public class YellowSpot {

    private String tvComplaintType;
    private String tvPostedBy;
    private String area;
    private String additionalInfo;
    private String imageUrl;

    // Constructor for YellowSpot
    public YellowSpot(String tvComplaintType,String tvPostedBy,String area, String additionalInfo, String imageUrl) {

        this.tvComplaintType = tvComplaintType;
        this.tvPostedBy=tvPostedBy;
        this.area = area;
        this.additionalInfo = additionalInfo;
        this.imageUrl = imageUrl;
    }

    // Getters and setters for the fields

    public String getTvComplaintType() {
        return tvComplaintType;
    }

    public void setTvComplaintType(String tvComplaintType) {
        this.tvComplaintType = tvComplaintType;}

    public String getTvPostedBy() {
        return tvPostedBy;
    }

    public void setTvPostedBy(String tvPostedBy) {
        this.tvPostedBy = tvPostedBy;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

