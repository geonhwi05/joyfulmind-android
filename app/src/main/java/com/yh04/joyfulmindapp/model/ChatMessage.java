package com.yh04.joyfulmindapp.model;

import com.google.firebase.Timestamp;

public class ChatMessage {
    private String nickname;
    private String message;
    private Timestamp timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String message) {
        this.message = message;
    }

    public ChatMessage(String nickname, String message, Timestamp timestamp) {
        this.nickname = nickname;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }




    @Override
    public String toString() {
        return "ChatMessage{" +
                "nickname='" + nickname + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
