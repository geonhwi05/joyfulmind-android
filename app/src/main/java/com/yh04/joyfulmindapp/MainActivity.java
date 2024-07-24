package com.yh04.joyfulmindapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.yh04.joyfulmindapp.adapter.ViewPager2Adapter;
import com.yh04.joyfulmindapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // 뷰페이저2 인스턴스
    private ViewPager2 viewPager2;
    // 뷰페이저2를 보여줄 어댑터
    private ViewPager2Adapter adapter;

    TextView tvBtn1;
    TextView tvBtn2;
    TextView tvBtn3;
    TextView tvBtn4;

    private ActivityMainBinding binding;

    // 액션바의 로그아웃 버튼을 활성화시킴
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 액션바 이름 변경
        getSupportActionBar().setTitle(" ");
        // 액션바에 화살표 백버튼을 표시하는 코드
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvBtn1 = findViewById(R.id.textView0);
        tvBtn2 = findViewById(R.id.textView1);
        tvBtn3 = findViewById(R.id.textView2);
        tvBtn4 = findViewById(R.id.textView3);

        // 뷰페이저2 어댑터 설정
        viewPager2 = findViewById(R.id.viewPager2);
        adapter = new ViewPager2Adapter(this); // 어댑터 생성
        viewPager2.setAdapter(adapter);

        // 페이지가 변경될 때마다 호출되는 콜백 함수
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                CurrentPositionChk(position);
            }
        });

        // 첫번째 텍스트뷰 클릭 이벤트 : ViewPager2에서 Fragment1의 화면으로 이동
        tvBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(0);
            }
        });

        // 두번째 텍스트뷰 클릭 이벤트 : ViewPager2에서 Fragment2의 화면으로 이동
        tvBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(1);
            }
        });

        // 세번째 텍스트뷰 클릭 이벤트 : ViewPager2에서 Fragment3의 화면으로 이동
        tvBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(2);
            }
        });

        // 네번째 텍스트뷰 클릭 이벤트 : ViewPager2에서 Fragment3의 화면으로 이동
        tvBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(3);
            }
        });
    }

    private void CurrentPositionChk(int position) {
        // 프래그먼트 화면 인덱스 저장 함수
        int currentFragmentId = 0;

        // 페이지 인덱스 정보 저장
        if (currentFragmentId != position) {
            currentFragmentId = position;
        }

        // 텍스트뷰 버튼 객체 연결 : tvBtn1, tvBtn2, tvBtn3, tvBtn4
        tvBtn1 = findViewById(R.id.textView0);
        tvBtn2 = findViewById(R.id.textView1);
        tvBtn3 = findViewById(R.id.textView2);
        tvBtn4 = findViewById(R.id.textView3);

        // 텍스트뷰 버튼 색상 정의 : 현재 페이지의 위치를 구분하기 위해 선택된 페이지의 투명도를 더 높게 설정
        String BaseHexColor = "#F6BC78";
        int rgbColor = Color.parseColor(BaseHexColor);
        int selectedAlpha = 128;
        int deselectedAlpha = 25;

        // 첫번째 화면일 경우 왼쪽 버튼 활성화 표시 (배경색을 진하게 함)
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
        }
        // 두번째 화면일 경우 가운데 버튼 활성화 표시 (배경색을 진하게 함)
        else if (currentFragmentId == 1) {
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
        }
        // 세번째 화면일 경우 오른쪽 버튼 활성화 표시 (배경색을 진하게 함)
        else if (currentFragmentId == 2) {
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
        }
        // 네번째 화면일 경우 오른쪽 버튼 활성화 표시 (배경색을 진하게 함)
        else if (currentFragmentId == 3) {
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
