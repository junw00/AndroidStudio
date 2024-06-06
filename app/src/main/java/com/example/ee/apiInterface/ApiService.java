package com.example.ee.apiInterface;

import com.example.ee.entity.Restaurant;
import com.example.ee.entity.Review;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/restaurants")
    Call<List<Restaurant>> getRestaurants();

    // 새로운 엔드포인트 추가: 특정 음식점의 리뷰 가져오기
    @GET("/reviews")
    Call<List<Review>> getRestaurantReviews(@Query("restaurantName") String restaurantName);

    @GET("/restaurant")
    Call<Restaurant> getRestaurant(@Query("restaurantName") String restaurantName);

    @GET("/restaurant/name")
    Call<List<Restaurant>> getRestaurantsName(@Query("restaurantName") String restaurantName);
}
