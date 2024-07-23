package com.yh04.joyfulmindapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.OAuthLoginCallback;
import com.navercorp.nid.oauth.view.NidOAuthLoginButton;
import com.yh04.joyfulmindapp.adapter.NetworkClient;
import com.yh04.joyfulmindapp.api.UserApi;
import com.yh04.joyfulmindapp.config.Config;
import com.yh04.joyfulmindapp.model.User;
import com.yh04.joyfulmindapp.model.UserRes;

import java.security.MessageDigest;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;
    ImageView ImgLogin;
    TextView txtRegister;
    TextView textView;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //뷰 객체 연결
        NidOAuthLoginButton btnLogin = findViewById(R.id.btn_login);

        //네아로 객체 초기화
        NaverIdLoginSDK.INSTANCE.initialize(this, getString(R.string.naver_client_id),
                getString(R.string.naver_client_secret), getString(R.string.app_name));

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        ImgLogin = findViewById(R.id.ImgLogin);
        txtRegister = findViewById(R.id.txtRegister);
        textView = findViewById(R.id.textView);

        //btnLogin이 null인지 확인
        if (btnLogin != null) {
            btnLogin.setOAuthLogin(new OAuthLoginCallback() {
                @Override
                public void onSuccess() {
                    // 로그인 성공시
                    // 액세스 토큰 가져오기
                    String accessToken = NaverIdLoginSDK.INSTANCE.getAccessToken();
                    textView.setText(accessToken);
                    btnLogin.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int httpStatus, @NonNull String message) {
                    // 통신 오류
                    Log.e("네아로", "onFailure: httpStatus - " + httpStatus + " / message - " + message);
                }

                @Override
                public void onError(int errorCode, @NonNull String message) {
                    // 네이버 로그인 중 오류 발생
                    Log.e("네아로", "onError: errorCode - " + errorCode + " / message - " + message);
                }
            });
        } else {
            Log.e("LoginActivity", "btnLogin is null.");
        }

        ImgLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                // 이메일과 패스워드는 필수다.
                if (email.isEmpty() || password.isEmpty()) {
                    Snackbar.make(ImgLogin,
                            "필수 항목입니다. 모두 입력하세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // 이메일 형식 체크
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if (!pattern.matcher(email).matches()) {
                    Snackbar.make(ImgLogin,
                            "이메일 형식을 바르게 작성하세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                showProgress();

                // 레트로핏 라이브러리를 이횽해서 네트워크 호출한다.
                Retrofit retrofit = NetworkClient.getRetrofitClient(LoginActivity.this);
                UserApi api = retrofit.create(UserApi.class);
                User user = new User(email, password);

                Call<UserRes> call = api.login(user);
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        dismissProgress();

                        if (response.isSuccessful()) {
                            UserRes userRes = response.body();

                            SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("token", userRes.accessToken);
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("token", userRes.accessToken);
                            startActivity(intent);

                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "로그인 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable throwable) {
                        dismissProgress();
                    }
                });
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void dismissProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    void showProgress() {
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(this));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}