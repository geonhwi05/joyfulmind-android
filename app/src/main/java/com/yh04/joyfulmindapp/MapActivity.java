package com.yh04.joyfulmindapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // 지도 준비되면 해달라는 함수
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                // 맵이 준비되면 내 위치를 중심으로 지도가 나오게 해라 라는 코드 작성

                // 1. 특정 위도, 경도 값으로 지도의 위치를 적용시키는 코드
                LatLng myLocation = new LatLng(37.5428, 126.6772); //표현하고자 하는 위치의 위도 경도 작성
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17));

                // 2. 마커를 만들어서, 지도에 표시하는 코드
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(myLocation).title("연희직업전문학교");

                // 3. 마커를 클릭하면, 동작하는 코드 작성
                //  중요! 위에서 MarkerOptions를 만들 때, 태그를 달아줘서 구분해 줘야 한다.
                googleMap.addMarker(markerOptions).setTag(0);
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {

                        int tag = (int) marker.getTag();

                        Toast.makeText(MapActivity.this,
                                "제가 누른 마커의 태그는 : " + tag + "\n 타이틀은 : " + marker.getTitle(),
                                Toast.LENGTH_SHORT).show();

                        return false;
                    }
                });

                // 4. 지도의 타입을 설정하는 코드
                // googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

            }
        });

    }
}