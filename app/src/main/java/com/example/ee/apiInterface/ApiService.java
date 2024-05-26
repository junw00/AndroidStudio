package com.example.ee.apiInterface;

import com.example.ee.entity.Restaurant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("/restaurants")
    Call<List<Restaurant>> getRestaurants();

}
