package com.yh04.joyfulmindapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.yh04.joyfulmindapp.adapter.DiaryAdapter;
import com.yh04.joyfulmindapp.adapter.NetworkClient;
import com.yh04.joyfulmindapp.model.Diary;
import com.yh04.joyfulmindapp.api.DiaryApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        calendarView = findViewById(R.id.cv_calendar);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DiaryAdapter(diaryList);
        recyclerView.setAdapter(adapter);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                fetchDiariesForSelectedDate(date);
            }
        });

        fetchAllDiaries();
    }

    private void fetchAllDiaries() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        DiaryApi diaryApi = retrofit.create(DiaryApi.class);
        Call<List<Diary>> call = diaryApi.getAllDiaries();

        call.enqueue(new Callback<List<Diary>>() {
            @Override
            public void onResponse(Call<List<Diary>> call, Response<List<Diary>> response) {
                if (response.isSuccessful()) {
                    diaryList = response.body();
                    adapter.setDiaryList(diaryList);
                } else {
                    Toast.makeText(DiaryListActivity.this, "Failed to fetch diaries", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Diary>> call, Throwable t) {
                Toast.makeText(DiaryListActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDiariesForSelectedDate(CalendarDay date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDate = sdf.format(new Date(date.getYear() - 1900, date.getMonth() - 1, date.getDay()));

        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        DiaryApi diaryApi = retrofit.create(DiaryApi.class);
        Call<List<Diary>> call = diaryApi.getDiariesForDate(selectedDate);

        call.enqueue(new Callback<List<Diary>>() {
            @Override
            public void onResponse(Call<List<Diary>> call, Response<List<Diary>> response) {
                if (response.isSuccessful()) {
                    diaryList = response.body();
                    adapter.setDiaryList(diaryList);
                } else {
                    Toast.makeText(DiaryListActivity.this, "Failed to fetch diaries", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Diary>> call, Throwable t) {
                Toast.makeText(DiaryListActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
