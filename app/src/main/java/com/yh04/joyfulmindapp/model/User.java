package com.yh04.joyfulmindapp.model;

public class User {

    public String email;
    public String password;
    public String nickname;
    public int age;
    public int gender;

    public User(String email, String password, String nickname, int age, int gender) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
    }

    public User(){

    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
