package com.example.ee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ee.apiInterface.ApiService;
import com.example.ee.apiMethod.Api;
import com.example.ee.entity.Restaurant;
import com.example.ee.retrofit.RetrofitClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private List<Restaurant> restaurants;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LinearLayout restaurantList;
    private static final Api API = new Api();
    // 현재 위치정보 받는 객체
    private FusedLocationProviderClient fusedLocationProviderClient;
    private EditText et_searchRestaurant;
    private ImageButton search_btn;
    private ListView listView;
    private List<String> data;
    private ArrayAdapter<String> adapter;
    private List<Restaurant> restaurantsList;
    private Button detail_btn;
    // 권한 체크 요청코드 정의
    public static final int REQUEST_CODE_PERMISSIONS = 1001;
    private HashMap<String, Marker> restaurantMarkerMap;
    LinearLayout linearLayout, linearLayout1;
    String name;
    TextView restaurantNameText, addressText, restaurantNameText1, addressText1;
    ImageView imageView, imageView1;
    float color;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_searchRestaurant = findViewById(R.id.et_searchRestaurant);
        search_btn = findViewById(R.id.search_btn);
        linearLayout = findViewById(R.id.list);
        restaurantNameText = findViewById(R.id.restaurantText);
        addressText = findViewById(R.id.addressText);
        imageView = findViewById(R.id.restaurantImage);
        detail_btn = findViewById(R.id.detail_btn);
        restaurantNameText1 = findViewById(R.id.restaurantText1);
        addressText1 = findViewById(R.id.addressText1);
        imageView1 = findViewById(R.id.restaurantImage1);

        linearLayout1 = findViewById(R.id.list1);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // 권한 요청 전 설명을 제공
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // 사용자에게 설명 제공
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this, "이 앱은 위치 권한이 필요합니다. 현재 위치를 표시하려면 권한을 허용해주세요.", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_PERMISSIONS);
        }


        // 검색창에서 리턴을 했을 경우 값 가져오기
        et_searchRestaurant.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    String name = et_searchRestaurant.getText().toString().trim();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    et_searchRestaurant.setText("");
                    API.getRestaurantApi(name);

                    return true;
                }
                return false;
            }
        });

        // 검색창에서 검색버튼을 눌렀을 떄 값 가져오기
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = et_searchRestaurant.getText().toString();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                et_searchRestaurant.setText("");
                Log.d("RestaurantName", name);
                ApiService apiService = RetrofitClient.getApiService();
                Call<List<Restaurant>> restaurantsName = apiService.getRestaurantsName(name);
                restaurantsName.enqueue(new Callback<List<Restaurant>>() {
                    @Override
                    public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                        Log.d(response.body().toString(), response.body().toString());
                        if (response.isSuccessful()) {
                            List<Restaurant> body = response.body();
                            for (Restaurant restaurant : body) {
                                double latitude = restaurant.getLatitude();
                                double longitude = restaurant.getLongitude();
                                if(restaurant.getName().equals("용빈각") || restaurant.getName().equals("경대컵밥 동아대점") || restaurant.getName().equals("경대컵밥 동서대점") || restaurant.getName().equals("화반") || restaurant.getName().equals("황금룡") || restaurant.getName().equals("아웃닭냉정점") || restaurant.getName().equals("마꾸니 라멘&덮밥 주례점")) {
                                    // 해당 위치로 지도 이동
                                    LatLng restaurantLocation = new LatLng(latitude, longitude);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantLocation, 20)); // 이동하면서 줌 인
                                    linearLayout.setVisibility(View.VISIBLE);
                                    restaurantNameText.setText(restaurant.getName());
                                    addressText.setText(restaurant.getAddress());
                                    if (restaurant.getName().equals("용빈각")) {
                                        imageView.setImageResource(R.drawable.a);
                                    }
                                    if (restaurant.getName().equals("경대컵밥 동서대점")) {
                                        imageView.setImageResource(R.drawable.kyung);
                                    }
                                    if (restaurant.getName().equals("경대컵밥 동아대점")) {
                                        imageView.setImageResource(R.drawable.kyung1);
                                    }
                                    if (restaurant.getName().equals("화반")) {
                                        imageView.setImageResource(R.drawable.hwa);
                                    }
                                    if (restaurant.getName().equals("마꾸니 라멘&덮밥 주례점")) {
                                        imageView.setImageResource(R.drawable.mikkumi);
                                    }
                                    if (restaurant.getName().equals("황금룡")) {
                                        imageView.setImageResource(R.drawable.hwang);
                                    }
                                    if (restaurant.getName().equals("화반")) {
                                        imageView.setImageResource(R.drawable.hwa);
                                    }
                                    if (restaurant.getName().equals("아웃닭냉정점")) {
                                        imageView.setImageResource(R.drawable.out);
                                    }
                                    // 마커를 클릭한 것처럼 표시
                                    Marker marker = restaurantMarkerMap.get(restaurant.getName());
                                    marker.showInfoWindow(); // 마커 정보 창 표시

                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            linearLayout1.setVisibility(View.INVISIBLE);
                                            restaurantNameText.setText(marker.getTitle());
                                            addressText.setText(marker.getSnippet());
                                            linearLayout.setVisibility(View.VISIBLE);
                                            if (marker.getTitle().equals("용빈각")) {
                                                imageView.setImageResource(R.drawable.a);
                                            }
                                            if (marker.getTitle().equals("경대컵밥 동서대점")) {
                                                imageView.setImageResource(R.drawable.kyung);
                                            }
                                            if (marker.getTitle().equals("경대컵밥 동아대점")) {
                                                imageView.setImageResource(R.drawable.kyung1);
                                            }
                                            if (marker.getTitle().equals("화반")) {
                                                imageView.setImageResource(R.drawable.hwa);
                                            }
                                            if (marker.getTitle().equals("마꾸니 라멘&덮밥 주례점")) {
                                                imageView.setImageResource(R.drawable.mikkumi);
                                            }
                                            if (marker.getTitle().equals("황금룡")) {
                                                imageView.setImageResource(R.drawable.hwang);
                                            }
                                            if (marker.getTitle().equals("화반")) {
                                                imageView.setImageResource(R.drawable.hwa);
                                            }
                                            if (marker.getTitle().equals("아웃닭냉정점")) {
                                                imageView.setImageResource(R.drawable.out);
                                            }
                                            marker.showInfoWindow(); // 마커 정보 창 표시
                                            return true;
                                        }

                                    });
                                detail_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MainActivity.this, RestaurantDetailActivity.class);
                                        intent.putExtra("restaurantName", restaurantNameText.getText());
                                        intent.putExtra("restaurantAddress", addressText.getText());

                                        startActivity(intent);
                                    }
                                });

                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                        Log.e("API Response", "onFailure: " + t.getMessage());
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }



    private void fetchRestaurantData() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Restaurant>> call = apiService.getRestaurants();
        restaurantMarkerMap = new HashMap<>();
        call.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                if (response.isSuccessful()) {
                    restaurants = response.body();
                    if (restaurants != null) {
                        Log.v("restaurants", restaurants.toString());
                        for (Restaurant restaurant : restaurants) {
                            LatLng location = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
                            Log.v("------------------------------", restaurant.getName());

                            // 마커 색상 설정

                            if (restaurant.getSentiment() < 0.3) {
                                color = BitmapDescriptorFactory.HUE_RED;
                            } else if (restaurant.getSentiment() < 0.5) {
                                color = BitmapDescriptorFactory.HUE_ORANGE;
                            } else if (restaurant.getSentiment() < 0.8) {
                                color = BitmapDescriptorFactory.HUE_YELLOW;
                            } else {
                                color = BitmapDescriptorFactory.HUE_GREEN;
                            }

                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(restaurant.getName())
                                    .snippet(restaurant.getAddress())
                                    .icon(BitmapDescriptorFactory.defaultMarker(color)));

                            // HashMap에 마커 저장
                            restaurantMarkerMap.put(restaurant.getName(), marker);
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                Log.e("error", t.getMessage());
                Toast.makeText(MainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng uit = new LatLng(35.145731, 129.007240);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uit, 16));

        fetchRestaurantData();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // 위치 정보 업데이트 로직을 추가할 수 있습니다.
    }

    @Override
    public void onConnectionSuspended(int i) {
        // 연결이 일시 중단된 경우 처리할 로직을 추가할 수 있습니다.
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // 연결 실패 시 처리할 로직을 추가할 수 있습니다.
    }

    public void mCurrentLocation(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_PERMISSIONS);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // 현재 위치
                            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.v("Current Location", "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());

                            mMap.addMarker(new MarkerOptions()
                                    .position(myLocation)
                                    .title("현재 위치"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

                            // 카메라 줌
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한 체크 거부 됨", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
