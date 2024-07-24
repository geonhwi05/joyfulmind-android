package com.yh04.joyfulmindapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.navercorp.nid.NaverIdLoginSDK;
import com.yh04.joyfulmindapp.adapter.NetworkClient;
import com.yh04.joyfulmindapp.adapter.ViewPager2Adapter;
import com.yh04.joyfulmindapp.api.NaverApiService;
import com.yh04.joyfulmindapp.api.UserApi;
import com.yh04.joyfulmindapp.config.Config;
import com.yh04.joyfulmindapp.databinding.ActivityMainBinding;
import com.yh04.joyfulmindapp.model.NidProfileResponse;
import com.yh04.joyfulmindapp.model.Profile;
import com.yh04.joyfulmindapp.model.User;
import com.yh04.joyfulmindapp.model.UserRes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private ViewPager2Adapter adapter;

    TextView tvBtn1;
    TextView tvBtn2;
    TextView tvBtn3;
    TextView tvBtn4;
    TextView txtUserName;

    private ActivityMainBinding binding;
    private UserApi userApi;
    private String token;
    private String naverAccessToken;
    private String nickname;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle(" ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvBtn1 = findViewById(R.id.textView0);
        tvBtn2 = findViewById(R.id.textView1);
        tvBtn3 = findViewById(R.id.textView2);
        tvBtn4 = findViewById(R.id.textView3);
        txtUserName = findViewById(R.id.txtUserName);

        viewPager2 = findViewById(R.id.viewPager2);
        adapter = new ViewPager2Adapter(this);
        viewPager2.setAdapter(adapter);

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                CurrentPositionChk(position);
            }
        });

        tvBtn1.setOnClickListener(v -> viewPager2.setCurrentItem(0));
        tvBtn2.setOnClickListener(v -> viewPager2.setCurrentItem(1));
        tvBtn3.setOnClickListener(v -> viewPager2.setCurrentItem(2));
        tvBtn4.setOnClickListener(v -> viewPager2.setCurrentItem(3));

        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        userApi = retrofit.create(UserApi.class);

        Intent intent = getIntent();
        naverAccessToken = intent.getStringExtra("naverAccessToken");
        token = intent.getStringExtra("token");

        if (naverAccessToken != null) {
            getProfileInfo(naverAccessToken);
        } else if (token != null) {
            getUserProfile(token);
        } else {
            Toast.makeText(this, "액세스 토큰을 받지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void CurrentPositionChk(int position) {
        int currentFragmentId = 0;

        if (currentFragmentId != position) {
            currentFragmentId = position;
        }

        tvBtn1 = findViewById(R.id.textView0);
        tvBtn2 = findViewById(R.id.textView1);
        tvBtn3 = findViewById(R.id.textView2);
        tvBtn4 = findViewById(R.id.textView3);

        String BaseHexColor = "#F6BC78";
        int rgbColor = Color.parseColor(BaseHexColor);
        int selectedAlpha = 128;
        int deselectedAlpha = 25;

        if (currentFragmentId == 0) {
            tvBtn1.setBackgroundColor(
                    Color.argb(selectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
            tvBtn2.setBackgroundColor(
                    Color.argb(deselectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
            tvBtn3.setBackgroundColor(
                    Color.argb(deselectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
            tvBtn4.setBackgroundColor(
                    Color.argb(deselectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
        } else if (currentFragmentId == 1) {
            tvBtn1.setBackgroundColor(
                    Color.argb(deselectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
            tvBtn2.setBackgroundColor(
                    Color.argb(selectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
            tvBtn3.setBackgroundColor(
                    Color.argb(deselectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
            tvBtn4.setBackgroundColor(
                    Color.argb(deselectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
        } else if (currentFragmentId == 2) {
            tvBtn1.setBackgroundColor(
                    Color.argb(deselectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
            tvBtn2.setBackgroundColor(
                    Color.argb(deselectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
            tvBtn3.setBackgroundColor(
                    Color.argb(selectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
            tvBtn4.setBackgroundColor(
                    Color.argb(deselectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
        } else if (currentFragmentId == 3) {
            tvBtn1.setBackgroundColor(
                    Color.argb(deselectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
            tvBtn2.setBackgroundColor(
                    Color.argb(deselectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
            tvBtn3.setBackgroundColor(
                    Color.argb(deselectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
            tvBtn4.setBackgroundColor(
                    Color.argb(selectedAlpha, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor))
            );
        }
    }

    private void logout() {
        NaverIdLoginSDK.INSTANCE.logout();

        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("naverAccessToken");
        editor.remove("token");
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void getProfileInfo(String accessToken) {
        Retrofit retrofit = NetworkClient.getNaverRetrofitClient(this);
        NaverApiService apiService = retrofit.create(NaverApiService.class);

        Call<NidProfileResponse> call = apiService.getProfile("Bearer " + accessToken);
        call.enqueue(new Callback<NidProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<NidProfileResponse> call, @NonNull Response<NidProfileResponse> response) {
                if (response.isSuccessful()) {
                    NidProfileResponse profileResponse = response.body();
                    Profile profile = profileResponse.getProfile();

                    nickname = profile.getNickname();

                    Log.d("ProfileInfo", "Nickname: " + nickname);

                    SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("userNickname", nickname);
                    editor.apply();

                    txtUserName.setText(nickname + "님");
                } else {
                    Log.e("ProfileError", "Response message: " + response.message());
                    Log.e("ProfileError", "Response error body: " + response.errorBody().toString());
                    Toast.makeText(MainActivity.this, "프로필 가져오기 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NidProfileResponse> call, @NonNull Throwable t) {
                Log.e("ProfileError", "Error message: " + t.getMessage());
                Toast.makeText(MainActivity.this, "프로필 가져오기 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserProfile(String token) {
        Call<UserRes> call = userApi.getUserProfile("Bearer " + token);

        call.enqueue(new Callback<UserRes>() {
            @Override
            public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().user;
                    nickname = user.nickname;
                    Log.d("ProfileInfo", "User Nickname: " + nickname);

                    SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("userNickname", nickname);
                    editor.apply();

                    txtUserName.setText(nickname + "님");
                } else {
                    Log.d("ProfileError", "Response failed: " + response.message());
                    Toast.makeText(MainActivity.this, "프로필 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserRes> call, Throwable t) {
                Log.d("ProfileError", "Request failed: " + t.getMessage());
                Toast.makeText(MainActivity.this, "프로필 정보를 가져오는 중 오류 발생: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onProfileEditClick(View view) {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        if (naverAccessToken != null) {
            intent.putExtra("naverAccessToken", naverAccessToken);
        }
        if (token != null) {
            intent.putExtra("token", token);
        }
        startActivity(intent);
    }
}
