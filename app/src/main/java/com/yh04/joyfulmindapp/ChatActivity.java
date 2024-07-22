package com.yh04.joyfulmindapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // SharedPreferences에서 토큰 가져오기
        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        token = sp.getString("token", null);

        if (token == null) {
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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        fetchUserProfile(); // 사용자 프로필 정보를 가져옴
    }

    private void fetchUserProfile() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(ChatActivity.this);
        UserApi userApi = retrofit.create(UserApi.class);
        Call<UserRes> call = userApi.getUserProfile("Bearer " + token);

        call.enqueue(new Callback<UserRes>() {
            @Override
            public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserRes userRes = response.body();
                    nickname = userRes.getUser().nickname;

                    chatAdapter = new ChatAdapter(chatMessages, nickname);
                    recyclerView.setAdapter(chatAdapter);
                } else {
                    Log.e("ChatActivity", "사용자 정보를 불러오는데 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<UserRes> call, Throwable t) {
                Log.e("ChatActivity", "사용자 정보를 불러오는데 실패했습니다.", t);
            }
        });
    }

    private void sendMessage() {
        if (nickname == null || nickname.isEmpty()) {
            Log.e("ChatActivity", "닉네임이 설정되지 않았습니다.");
            return;
        }

        String message = editChat.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            ChatMessage chatMessage = new ChatMessage(nickname, message, Timestamp.now());
            db.collection("UserChatting").add(chatMessage);
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
                    ChatMessage responseMessage = new ChatMessage("조이", chatResponse.getAnswer(), Timestamp.now());

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
}
