package com.yh04.joyfulmindapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.yh04.joyfulmindapp.R;
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

        getRecommendedSongs("프로미스나인", 20);
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
                    View songView = LayoutInflater.from(SongRecActivity.this).inflate(R.layout.song_item, songContainer, false);

                    TextView songName = songView.findViewById(R.id.songName);
                    TextView songArtist = songView.findViewById(R.id.songArtist);
                    ImageView songThumbnail = songView.findViewById(R.id.songThumbnail);

                    songName.setText(song.getName());
                    songArtist.setText(song.getArtist());
                    Glide.with(SongRecActivity.this).load(song.getThumbnail()).into(songThumbnail);

                    songView.setOnClickListener(v -> openSongUrl(song.getUrl()));

                    songContainer.addView(songView);
                }
            }

            @Override
            public void onFailure(Call<SongResponse> call, Throwable t) {
                textViewResults.setText(t.getMessage());
            }
        });
    }

    private void openSongUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}