package com.yh04.joyfulmindapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yh04.joyfulmindapp.adapter.NetworkClient;
import com.yh04.joyfulmindapp.api.DiaryApi;
import com.yh04.joyfulmindapp.model.Diary;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditDiaryActivity extends AppCompatActivity {

    private TextView txtDate;
    private EditText txtTitle;
    private EditText txtContent;
    private ImageView imgEdit;
    private ImageView imgSave;
    private Diary diary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);

        txtDate = findViewById(R.id.txtDate);
        txtTitle = findViewById(R.id.txtTitle);
        txtContent = findViewById(R.id.txtContent);
        imgEdit = findViewById(R.id.imgEdit);
        imgSave = findViewById(R.id.imgSave);

        // 다이어리 객체를 인텐트로부터 가져옴
        diary = (Diary) getIntent().getSerializableExtra("diary");

        // 날짜를 문자열로 포맷
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String formattedDate = sdf.format(diary.getDate());

        txtDate.setText(formattedDate);
        txtTitle.setText(diary.getTitle());
        txtContent.setText(diary.getContent());

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText를 수정 가능하게 변경
                txtTitle.setEnabled(true);
                txtContent.setEnabled(true);
                // Save 버튼을 보이게 함
                imgSave.setVisibility(View.VISIBLE);
            }
        });

        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diary.setTitle(txtTitle.getText().toString());
                diary.setContent(txtContent.getText().toString());

                updateDiary(diary);

                // 수정 후 EditText를 다시 비활성화
                txtTitle.setEnabled(false);
                txtContent.setEnabled(false);
                // Save 버튼을 다시 숨김
                imgSave.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void updateDiary(Diary diary) {
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        DiaryApi diaryApi = retrofit.create(DiaryApi.class);
        Call<Void> call = diaryApi.updateDiary(diary.getId(), diary);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditDiaryActivity.this, "일기가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditDiaryActivity.this, "수정을 실패하였습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditDiaryActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
