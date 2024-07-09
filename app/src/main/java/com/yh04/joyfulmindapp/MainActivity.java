package com.yh04.joyfulmindapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnChat;
    EditText editNick;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnChat = findViewById(R.id.btnChat);
        editNick = findViewById(R.id.editNick);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = editNick.getText().toString().trim();

                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("nickname", nickname);
                startActivity(intent);
            }
        });

    }

}
