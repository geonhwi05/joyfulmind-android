package com.yh04.joyfulmindapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yh04.joyfulmindapp.adapter.NetworkClient;
import com.yh04.joyfulmindapp.api.UserApi;
import com.yh04.joyfulmindapp.model.User;
import com.yh04.joyfulmindapp.model.UserChange;
import com.yh04.joyfulmindapp.model.UserRes;
import com.yh04.joyfulmindapp.config.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileActivity extends AppCompatActivity {

    private UserApi userApi;
    private EditText editText;
    private TextView textViewEmail;
    private TextView textViewGender;
    private TextView textViewAge;
    private ImageView imgChangeNickname;
    private String token;  // JWT 토큰

    private TextView txtChangePassword;
    private TextView txtLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        userApi = retrofit.create(UserApi.class);

        // JWT 토큰 초기화 (예: 로그인 후 Intent를 통해 전달받은 토큰)
        token = getIntent().getStringExtra("token");
        if (token == null) {
            // SharedPreferences에서 토큰 가져오기
            SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
            token = sp.getString("token", null);
        }

        editText = findViewById(R.id.editText);
        textViewEmail = findViewById(R.id.textViewemail);
        textViewGender = findViewById(R.id.textViewgender);
        textViewAge = findViewById(R.id.textViewage);
        imgChangeNickname = findViewById(R.id.imgChangeNickname);
        txtChangePassword = findViewById(R.id.txtChangePassword);
        txtLogout = findViewById(R.id.txtLogout);

        // 프로필 정보 가져오기
        getUserProfile();

        // 클릭 이벤트 처리
        imgChangeNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 닉네임 수정 활성화
                enableNicknameEditing();
            }
        });

        // 비밀번호 변경 클릭 이벤트 처리
        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        // 로그아웃 클릭 이벤트 처리
        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        // 초기에는 닉네임 수정 불가능하게 설정
        editText.setEnabled(false);
    }

    private void getUserProfile() {
        if (token == null) {
            Toast.makeText(this, "토큰이 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ProfileActivity", "Token: " + token);
        Call<UserRes> call = userApi.getUserProfile("Bearer " + token);

        call.enqueue(new Callback<UserRes>() {
            @Override
            public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                Log.d("ProfileActivity", "Response: " + response.toString());
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().user;
                    editText.setText(user.nickname);
                    textViewEmail.setText(user.email);
                    textViewGender.setText(getGenderString(user.gender));
                    textViewAge.setText(calculateAge(user.birthDate));  // 나이 계산하여 표시
                } else {
                    Log.d("ProfileActivity", "Response failed: " + response.message());
                    Toast.makeText(ProfileActivity.this, "프로필 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserRes> call, Throwable t) {
                Log.d("ProfileActivity", "Request failed: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "프로필 정보를 가져오는 중 오류 발생: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String calculateAge(String birthDateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date birthDate = sdf.parse(birthDateString);
            Calendar birthDay = Calendar.getInstance();
            birthDay.setTime(birthDate);

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);

            if (today.get(Calendar.MONTH) < birthDay.get(Calendar.MONTH)) {
                age--;
            } else if (today.get(Calendar.MONTH) == birthDay.get(Calendar.MONTH) &&
                    today.get(Calendar.DAY_OF_MONTH) < birthDay.get(Calendar.DAY_OF_MONTH)) {
                age--;
            }

            return String.valueOf(age);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getGenderString(int gender) {
        if (gender == 0) {
            return "남자";
        } else if (gender == 1) {
            return "여자";
        } else {
            return "기타";
        }
    }

    private void enableNicknameEditing() {
        // EditText를 수정 가능하게 설정
        editText.setEnabled(true);
        editText.requestFocus();

        // 닉네임 변경 버튼을 클릭할 때 변경 요청
        imgChangeNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNickname();
            }
        });
    }

    private void changeNickname() {
        String newNickname = editText.getText().toString().trim();

        if (newNickname.isEmpty()) {
            Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        UserChange request = new UserChange(newNickname);
        Call<UserRes> call = userApi.changeNickname("Bearer " + token, request);

        call.enqueue(new Callback<UserRes>() {
            @Override
            public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ProfileActivity", "Nickname change successful: " + response.body().message);
                    Toast.makeText(ProfileActivity.this, "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                    editText.setEnabled(false);  // 닉네임 변경 후 수정 불가능하게 설정

                    // 닉네임 수정 후 다시 원래 클릭 리스너로 설정
                    imgChangeNickname.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enableNicknameEditing();
                        }
                    });
                } else {
                    Log.d("ProfileActivity", "Nickname change failed: " + response.message());
                    Toast.makeText(ProfileActivity.this, "닉네임 변경 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserRes> call, Throwable t) {
                Log.d("ProfileActivity", "Nickname change error: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "닉네임 변경 중 오류 발생: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("로그아웃하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logout();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 아무일도 하지 않음
                    }
                });
        builder.create().show();
    }

    private void logout() {
        // 토큰 삭제
        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("token");
        editor.apply();

        // 로그인 화면으로 이동
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
