package com.example.ee.apiMethod;

import com.example.ee.apiInterface.ApiService;
import com.example.ee.entity.Restaurant;
import com.example.ee.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Api {

    private final ApiService apiService = RetrofitClient.getApiService();
    private Restaurant restaurant;
    private List<Restaurant> restaurants;
    public Restaurant getRestaurantApi(String restaurantName) {
        Call<Restaurant> call = apiService.getRestaurant(restaurantName);
        call.enqueue(new Callback<Restaurant>() {
            @Override
            public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                if(response.isSuccessful()) {
                    restaurant = response.body();
                }
            }

            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {

            }
        });

        return restaurant;
    }

    public List<Restaurant> getRestaurantsName(String restaurant) {
        Call<List<Restaurant>> restaurantsName = apiService.getRestaurantsName(restaurant);

        restaurantsName.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                if(response.isSuccessful()) {
                    restaurants = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {

            }
        });
        return restaurants;
    }


}
