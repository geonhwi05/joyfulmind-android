package com.yh04.joyfulmindapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;
import com.yh04.joyfulmindapp.adapter.DiaryAdapter;
import com.yh04.joyfulmindapp.adapter.NetworkClient;
import com.yh04.joyfulmindapp.api.DiaryApi;
import com.yh04.joyfulmindapp.config.Config;
import com.yh04.joyfulmindapp.model.Diary;
import com.yh04.joyfulmindapp.model.DiaryResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DiaryListActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private RecyclerView recyclerView;
    private DiaryAdapter adapter;
    private List<Diary> diaryList = new ArrayList<>();
    private String token;
    private TextView txtSelectedDateRange;
    private Button btnSelectDateRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        calendarView = findViewById(R.id.cv_calendar);
        recyclerView = findViewById(R.id.recyclerView);
        txtSelectedDateRange = findViewById(R.id.txtSelectedDateRange);
        btnSelectDateRange = findViewById(R.id.btnSelectDateRange);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        token = getTokenFromSharedPreferences();
        adapter = new DiaryAdapter(diaryList, token);
        recyclerView.setAdapter(adapter);

        calendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
                if (!dates.isEmpty()) {
                    CalendarDay startDate = dates.get(0);
                    CalendarDay endDate = dates.get(dates.size() - 1);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String start = sdf.format(new Date(startDate.getYear() - 1900, startDate.getMonth() - 1, startDate.getDay()));
                    String end = sdf.format(new Date(endDate.getYear() - 1900, endDate.getMonth() - 1, endDate.getDay()));
                    txtSelectedDateRange.setText(start + " to " + end);
                    fetchDiariesForDateRange(startDate, endDate);
                }
            }
        });

        btnSelectDateRange.setOnClickListener(v -> {
            calendarView.setVisibility(calendarView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });

        fetchAllDiaries();
    }

    private void fetchAllDiaries() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        DiaryApi diaryApi = retrofit.create(DiaryApi.class);
        Call<DiaryResponse> call = diaryApi.getAllDiaries("Bearer " + token);

        call.enqueue(new Callback<DiaryResponse>() {
            @Override
            public void onResponse(Call<DiaryResponse> call, Response<DiaryResponse> response) {
                if (response.isSuccessful()) {
                    diaryList = response.body().getItems();
                    sortAndDisplayDiaries();
                } else {
                    Toast.makeText(DiaryListActivity.this, "Failed to fetch diaries", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DiaryResponse> call, Throwable t) {
                Toast.makeText(DiaryListActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDiariesForDateRange(CalendarDay startDate, CalendarDay endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String start = sdf.format(new Date(startDate.getYear() - 1900, startDate.getMonth() - 1, startDate.getDay()));
        String end = sdf.format(new Date(endDate.getYear() - 1900, endDate.getMonth() - 1, endDate.getDay()));

        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        DiaryApi diaryApi = retrofit.create(DiaryApi.class);
        Call<DiaryResponse> call = diaryApi.getDiariesForRange("Bearer " + token, start, end);

        call.enqueue(new Callback<DiaryResponse>() {
            @Override
            public void onResponse(Call<DiaryResponse> call, Response<DiaryResponse> response) {
                if (response.isSuccessful()) {
                    diaryList = response.body().getItems();
                    sortAndDisplayDiaries();
                } else {
                    Toast.makeText(DiaryListActivity.this, "Failed to fetch diaries for selected date range", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DiaryResponse> call, Throwable t) {
                Toast.makeText(DiaryListActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sortAndDisplayDiaries() {
        Collections.sort(diaryList, new Comparator<Diary>() {
            @Override
            public int compare(Diary o1, Diary o2) {
                return o2.getCreatedAt().compareTo(o1.getCreatedAt());
            }
        });
        adapter.setDiaryList(diaryList);
    }

    private String getTokenFromSharedPreferences() {
        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        return sp.getString("token", "");
    }
}
