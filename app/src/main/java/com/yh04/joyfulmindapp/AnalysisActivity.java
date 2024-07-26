package com.yh04.joyfulmindapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.yh04.joyfulmindapp.config.Config;

import java.util.Calendar;

public class AnalysisActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String email;  // 이메일 필드 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        db = FirebaseFirestore.getInstance();

        // SharedPreferences에서 이메일 가져오기
        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        email = sp.getString("email", null);

        DatePicker datePicker = findViewById(R.id.datePicker);
        ImageView imgDetail = findViewById(R.id.imgDetail);

        // 현재 날짜로 설정
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // 날짜가 변경될 때 호출됩니다.
                String selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                imgDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fetchChatData(selectedDate, email); // 이메일을 여기에 추가
                    }
                });
            }
        });
    }

    private void fetchChatData(String selectedDate, String email) {
        // 선택된 날짜의 시작과 끝을 설정
        Calendar selectedCalendar = Calendar.getInstance();
        String[] dateParts = selectedDate.split("-");
        selectedCalendar.set(Calendar.YEAR, Integer.parseInt(dateParts[0]));
        selectedCalendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
        selectedCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[2]));

        Calendar startOfDay = (Calendar) selectedCalendar.clone();
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);

        Calendar endOfDay = (Calendar) selectedCalendar.clone();
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);

        db.collection("UserChattingTest")
                .whereEqualTo("email", email)  // 닉네임 대신 이메일로 변경
                .whereGreaterThanOrEqualTo("timestamp", startOfDay.getTime())
                .whereLessThanOrEqualTo("timestamp", endOfDay.getTime())
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            // 데이터가 없는 경우 스낵바를 사용하여 안내 팝업을 표시합니다.
                            Snackbar.make(findViewById(R.id.main), "해당 날짜의 채팅 기록이 필요합니다.", Snackbar.LENGTH_LONG).show();
                        } else {
                            StringBuilder chatData = new StringBuilder();
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                String message = document.getString("message");
                                chatData.append(message).append("\n");
                            }

                            Intent intent = new Intent(AnalysisActivity.this, DetailAnalysisActivity.class);
                            intent.putExtra("chatData", chatData.toString());
                            startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 실패 시 처리
                        Snackbar.make(findViewById(R.id.main), "데이터를 가져오는 중 오류가 발생했습니다.", Snackbar.LENGTH_LONG).show();
                    }
                });
    }
}
