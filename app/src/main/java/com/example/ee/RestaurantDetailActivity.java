package com.example.ee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ee.apiInterface.ApiService;
import com.example.ee.entity.Review;
import com.example.ee.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailActivity extends AppCompatActivity {

    private TextView restaurantNameTextView;
    private TextView restaurantAddressTextView;
    private LinearLayout linearLayout;
    ImageView imageView;
    TextView textView, textView1;
    LinearLayout reviewWrap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        Intent intent = getIntent();
        reviewWrap = findViewById(R.id.reviewWrap);
        imageView = findViewById(R.id.iv_restaurant);
        textView = findViewById(R.id.restaurant_name);
        textView1 = findViewById(R.id.restaurant_address);
        String restaurantName = intent.getStringExtra("restaurantName");
        String restaurantAddress = intent.getStringExtra("restaurantAddress");
        textView.setText(restaurantName);
        textView1.setText(restaurantAddress);
        if (restaurantName.equals("용빈각")) {
            imageView.setImageResource(R.drawable.a);
        }
        if (restaurantName.equals("경대컵밥 동서대점")) {
            imageView.setImageResource(R.drawable.kyung);
        }
        if (restaurantName.equals("경대컵밥 동아대점")) {
            imageView.setImageResource(R.drawable.kyung1);
        }
        if (restaurantName.equals("화반")) {
            imageView.setImageResource(R.drawable.hwa);
        }
        if (restaurantName.equals("마꾸니 라멘&덮밥 주례점")) {
            imageView.setImageResource(R.drawable.mikkumi);
        }
        if (restaurantName.equals("황금룡")) {
            imageView.setImageResource(R.drawable.hwang);
        }
        if (restaurantName.equals("화반")) {
            imageView.setImageResource(R.drawable.hwa);
        }
        if (restaurantName.equals("아웃닭냉정점")) {
            imageView.setImageResource(R.drawable.out);
        }

        ApiService apiService = RetrofitClient.getApiService();

        Call<List<Review>> call = apiService.getRestaurantReviews(restaurantName);

        call.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful()) {
                    List<Review> body = response.body();

                    for (Review review : body) {
                        // 리뷰 추가
                        TextView reviewTextView = new TextView(RestaurantDetailActivity.this);
                        reviewTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        reviewTextView.setText(review.getReview() + "\n" + String.valueOf(review.getSentiment()));
                        reviewTextView.setPadding(16, 16, 16, 16);
                        reviewWrap.addView(reviewTextView);

                        // 구분선 추가
                        View divider = new View(RestaurantDetailActivity.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                1 // 구분선의 높이 (1dp)
                        );
                        params.setMargins(0, 8, 0, 8); // 구분선 상하 마진
                        divider.setLayoutParams(params);
                        divider.setBackgroundColor(ContextCompat.getColor(RestaurantDetailActivity.this, R.color.black)); // 구분선 색상 (검은색)
                        reviewWrap.addView(divider);
                    }

                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {

            }
        });
    }
}
