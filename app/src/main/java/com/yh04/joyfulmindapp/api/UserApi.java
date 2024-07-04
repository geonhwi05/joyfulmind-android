package com.yh04.joyfulmindapp.api;



import com.yh04.joyfulmindapp.model.User;
import com.yh04.joyfulmindapp.model.UserRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {

    @POST("user/login")
    Call<UserRes> login(@Body User user);

    // 회원가입 API
    @POST("user/register")
    Call<UserRes> register(@Body User user);
}
