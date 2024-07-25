package com.yh04.joyfulmindapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yh04.joyfulmindapp.adapter.ChatAdapter;
import com.yh04.joyfulmindapp.adapter.NetworkClient;
import com.yh04.joyfulmindapp.api.ChatApi;
import com.yh04.joyfulmindapp.api.UserApi;
import com.yh04.joyfulmindapp.config.Config;
import com.yh04.joyfulmindapp.model.ChatMessage;
import com.yh04.joyfulmindapp.model.ChatResponse;
import com.yh04.joyfulmindapp.model.UserRes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editChat;
    private Button btnSend;
    private List<ChatMessage> chatMessages;
    private FirebaseFirestore db;
    private ChatAdapter chatAdapter;
    private String nickname;
    private String token;
    private String naverAccessToken;
    private String profileImageUrl;

    private static final String DEFAULT_IMAGE = "https://firebasestorage.googleapis.com/v0/b/joyfulmindapp.appspot.com/o/profile_image%2Fdefaultprofileimg.png?alt=media&token=87768af9-03ef-4cc3-b801-ce17b9a1ece1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 액션바 이름 변경
        getSupportActionBar().setTitle(" ");
        // 액션바에 화살표 백버튼을 표시하는 코드
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // SharedPreferences에서 토큰과 프로필 이미지 URL 가져오기
        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        token = sp.getString("token", null);
        naverAccessToken = sp.getString("naverAccessToken", null);
        profileImageUrl = sp.getString("profileImageUrl", DEFAULT_IMAGE);

        Log.d("ChatActivity", "Token: " + token);
        Log.d("ChatActivity", "Naver Access Token: " + naverAccessToken);
        Log.d("ChatActivity", "Profile Image URL: " + profileImageUrl);

        if (token == null && naverAccessToken == null) {
            // 토큰이 없는 경우 로그인 화면으로 이동
            Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        chatMessages = new ArrayList<>();
        editChat = findViewById(R.id.editChat);
        btnSend = findViewById(R.id.btnSend);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        btnSend.setOnClickListener(v -> sendMessage());

        fetchUserProfile(); // 사용자 프로필 정보를 가져옴
    }

    private void fetchUserProfile() {
        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        nickname = sp.getString("userNickname", "YourNickname");
        profileImageUrl = sp.getString("profileImageUrl", DEFAULT_IMAGE);

        Log.d("ChatActivity", "Fetched Nickname: " + nickname);
        Log.d("ChatActivity", "Fetched Profile Image URL: " + profileImageUrl);

        chatAdapter = new ChatAdapter(chatMessages, nickname, profileImageUrl);
        recyclerView.setAdapter(chatAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // SharedPreferences에서 닉네임과 프로필 이미지 URL 불러오기
        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        nickname = sp.getString("userNickname", "YourNickname");
        profileImageUrl = sp.getString("profileImageUrl", DEFAULT_IMAGE);
        Log.d("ChatActivity", "Updated Nickname: " + nickname);
        Log.d("ChatActivity", "Updated Profile Image URL: " + profileImageUrl);

        // 어댑터에 닉네임 및 프로필 이미지 URL 업데이트
        if (chatAdapter != null) {
            chatAdapter.setNickname(nickname);
            chatAdapter.setProfileImageUrl(profileImageUrl);
            chatAdapter.notifyDataSetChanged();
        }
    }

    private void sendMessage() {
        if (nickname == null || nickname.isEmpty()) {
            Log.e("ChatActivity", "닉네임이 설정되지 않았습니다.");
            return;
        }

        String message = editChat.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            ChatMessage chatMessage = new ChatMessage(nickname, message, Timestamp.now(), profileImageUrl);
            db.collection("UserChatting").add(chatMessage)
                    .addOnSuccessListener(documentReference -> Log.d("ChatActivity", "Message sent successfully"))
                    .addOnFailureListener(e -> Log.e("ChatActivity", "Error sending message", e));
            editChat.setText("");

            // 화면에 사용자 메시지 추가
            chatMessages.add(chatMessage);
            chatAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(chatMessages.size() - 1);

            // 새로운 메시지를 추가했으므로, 최신 메시지를 API로 전송
            sendToChatApi(chatMessage);
        }
    }

    private void sendToChatApi(ChatMessage chatMessage) {
        Retrofit retrofit = NetworkClient.getRetrofitClient2(ChatActivity.this);
        ChatApi api = retrofit.create(ChatApi.class);

        Call<ChatResponse> call = api.chat(chatMessage);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChatResponse chatResponse = response.body();
                    Log.d("ChatApi", "응답 메시지: " + chatResponse.getAnswer());
                    Log.d("ChatApi", "응답: " + chatResponse.toString());

                    // ChatResponse를 ChatMessage로 변환하여 닉네임을 "조이"로 설정
                    ChatMessage responseMessage = new ChatMessage("조이", chatResponse.getAnswer(), Timestamp.now(), profileImageUrl);

                    // 응답 메시지를 JoyChatting 컬렉션에 추가
                    db.collection("JoyChatting").add(responseMessage);

                    // 화면에 조이의 메시지 추가
                    chatMessages.add(responseMessage);
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(chatMessages.size() - 1);
                } else {
                    try {
                        Log.e("ChatApi", "응답 오류: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("ChatApi", "전체 응답: " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable throwable) {
                Log.e("ChatApi", "API 호출 실패: " + throwable.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
