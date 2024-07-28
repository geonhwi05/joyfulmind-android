package com.yh04.joyfulmindapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.yh04.joyfulmindapp.adapter.NetworkClient;
import com.yh04.joyfulmindapp.api.SpotifyService;
import com.yh04.joyfulmindapp.model.Song;
import com.yh04.joyfulmindapp.model.SongResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongRecActivity extends AppCompatActivity {

    private TextView textViewResults;
    private LinearLayout songContainer;
    private SpotifyService spotifyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_rec);


        textViewResults = findViewById(R.id.textViewResults);
        songContainer = findViewById(R.id.songContainer);

        spotifyService = NetworkClient.getRetrofitClient(this).create(SpotifyService.class);

        // Intent에서 감정 분석 결과를 받습니다.
        Intent intent = getIntent();
        String emotion = intent.getStringExtra("emotion");

        // 감정이 null인 경우 기본값 설정
        if (emotion == null) {
            emotion = "기쁨";  // 기본값을 기쁨으로 설정
        }

        // 감정 분석 결과에 따라 노래를 추천합니다.
        getRecommendedSongs(emotion, 20);
    }

    private void getRecommendedSongs(String emotion, int limit) {
        Call<SongResponse> call = spotifyService.getRecommendedSongs(emotion, limit);

        call.enqueue(new Callback<SongResponse>() {
            @Override
            public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                if (!response.isSuccessful()) {
                    textViewResults.setText("Code: " + response.code());
                    return;
                }

                SongResponse songResponse = response.body();
                List<Song> songs = songResponse.getSongs();
                songContainer.removeAllViews();

                for (Song song : songs) {
                    String previewUrl = song.getPreview_url();
                    if (previewUrl != null && !previewUrl.isEmpty()) {
                        View songView = LayoutInflater.from(SongRecActivity.this).inflate(R.layout.song_item, songContainer, false);

                        TextView songName = songView.findViewById(R.id.songName);
                        TextView songArtist = songView.findViewById(R.id.songArtist);
                        ImageView songThumbnail = songView.findViewById(R.id.songThumbnail);

                        songName.setText(song.getName());
                        songArtist.setText(song.getArtists());

                        String thumbnailUrl = song.getAlbum_cover_url();
                        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                            Glide.with(SongRecActivity.this).load(thumbnailUrl).into(songThumbnail);
                        } else {
                            Glide.with(SongRecActivity.this).load(R.drawable.defaultprofileimg).into(songThumbnail); // 기본 이미지 설정
                        }

                        songView.setOnClickListener(v -> openSongUrl(previewUrl));

                        songContainer.addView(songView);
                    }
                }
            }

            @Override
            public void onFailure(Call<SongResponse> call, Throwable t) {
                textViewResults.setText(t.getMessage());
            }
        });
    }

    private void openSongUrl(String url) {
        if (url != null && !url.isEmpty()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } else {
            Toast.makeText(this, "Preview URL is not available", Toast.LENGTH_SHORT).show();
        }
    }
}
