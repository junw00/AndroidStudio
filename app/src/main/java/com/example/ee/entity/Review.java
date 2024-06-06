package com.example.ee.entity;

import java.io.Serializable;

public class Review implements Serializable {
    private String review;
    private float sentiment;

    public float getSentiment() {
        return sentiment;
    }

    public String getReview() {
        return review;
    }
}
