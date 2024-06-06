package com.example.ee.entity;

import java.util.List;

public class Restaurant {
    private int id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private double sentiment;

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }


    public double getLatitude() {
        return latitude;
    }


    public double getLongitude() {
        return longitude;
    }


    public double getSentiment() {
        return sentiment;
    }

}
