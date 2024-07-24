package com.yh04.joyfulmindapp.model;

import com.google.firebase.Timestamp;

public class ChatMessage {
    private String nickname;
    private String message;
    private Timestamp timestamp;
    private String profileImageUrl; // 프로필 이미지 URL 추가

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

    public ChatMessage(String nickname, String message, Timestamp timestamp, String profileImageUrl) {
        this.nickname = nickname;
        this.message = message;
        this.timestamp = timestamp;
        this.profileImageUrl = profileImageUrl; // 프로필 이미지 URL 추가
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

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "nickname='" + nickname + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                '}';
    }
}
