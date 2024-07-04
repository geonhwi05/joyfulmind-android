package com.yh04.joyfulmindapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.yh04.joyfulmindapp.adapter.NetworkClient;
import com.yh04.joyfulmindapp.api.UserApi;
import com.yh04.joyfulmindapp.config.Config;
import com.yh04.joyfulmindapp.model.User;
import com.yh04.joyfulmindapp.model.UserRes;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;
    EditText editNickname;
    EditText editAge;
    RadioButton btnMale;
    RadioButton btnFemale;
    Button btnRegister;
    TextView txtLogin;
    RadioGroup radioGroupGender;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editNickname = findViewById(R.id.editNickname);
        editAge = findViewById(R.id.editAge);
        btnMale = findViewById(R.id.btnMale);
        btnFemale = findViewById(R.id.btnFemale);
        btnRegister = findViewById(R.id.btnRegister);
        radioGroupGender = findViewById(R.id.radioGroupGender);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                String nickname = editNickname.getText().toString().trim();
                String strAge = editAge.getText().toString().trim();

                // 필수 항목 체크
                if (email.isEmpty() || password.isEmpty() || nickname.isEmpty() || strAge.isEmpty() || radioGroupGender.getCheckedRadioButtonId() == -1) {
                    Snackbar.make(btnRegister, "필수 항목입니다. 모두 입력하세요.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // 나이 형식 체크
                int age;
                try {
                    age = Integer.parseInt(strAge);
                } catch (NumberFormatException e) {
                    Snackbar.make(btnRegister, "나이를 올바르게 입력하세요.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // 이메일 형식 체크
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if (!pattern.matcher(email).matches()) {
                    Snackbar.make(btnRegister, "이메일 형식을 바르게 작성하세요.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // 성별 체크
                int gender = (btnMale.isChecked()) ? 0 : 1;

                showProgress();

                Retrofit retrofit = NetworkClient.getRetrofitClient(RegisterActivity.this);
                UserApi api = retrofit.create(UserApi.class);
                User user = new User(email, password, nickname, age, gender);
                Call<UserRes> call = api.register(user);
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

                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Snackbar.make(btnRegister, "회원가입에 실패했습니다. 다시 시도하세요.", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable throwable) {
                        dismissProgress();
                        Snackbar.make(btnRegister, "네트워크 오류가 발생했습니다. 다시 시도하세요.", Snackbar.LENGTH_SHORT).show();
                    }
                });
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
