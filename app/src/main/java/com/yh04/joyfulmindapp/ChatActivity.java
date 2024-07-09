package com.yh04.joyfulmindapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.yh04.joyfulmindapp.adapter.ChatAdapter;
import com.yh04.joyfulmindapp.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editChat;
    private Button btnSend;
    private List<ChatMessage> chatMessages;
    private FirebaseFirestore db;
    private ChatAdapter chatAdapter;
    private String nickname;
    private String profileImageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        nickname = getIntent().getStringExtra("nickname");

        chatMessages = new ArrayList<>();
        editChat = findViewById(R.id.editChat);
        btnSend = findViewById(R.id.btnSend);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        chatAdapter = new ChatAdapter(chatMessages, nickname);
        recyclerView.setAdapter(chatAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        loadMessages();

    }
    private void sendMessage() {
        String message = editChat.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            ChatMessage chatMessage = new ChatMessage(nickname, message, Timestamp.now(), profileImageUrl);
            db.collection("Chatting").add(chatMessage);
            editChat.setText("");
        }
    }

    private void loadMessages() {
        db.collection("Chatting")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        if (snapshots != null) {
                            chatMessages.clear();
                            for (DocumentSnapshot document : snapshots.getDocuments()) {
                                ChatMessage message = document.toObject(ChatMessage.class);
                                chatMessages.add(message);
                                Log.d("ChatApp", "Message added: " + message.getMessage());
                            }
                            chatAdapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(chatMessages.size() - 1);
                        }
                    }
                });
    }
}