package com.yh04.joyfulmindapp.api;

import com.yh04.joyfulmindapp.model.Diary;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DiaryApi {

    // 모든 다이어리를 가져오는 엔드포인트
    @GET("diary")
    Call<List<Diary>> getAllDiaries();

    // 특정 날짜의 다이어리를 가져오는 엔드포인트
    @GET("diary")
    Call<List<Diary>> getDiariesForDate(@Query("date") String date);

    // 새로운 다이어리를 생성하는 엔드포인트
    @POST("diary")
    Call<Void> createDiary(@Body Diary diary);

    // 특정 다이어리를 업데이트하는 엔드포인트
    @PUT("diary/{diaryId}")
    Call<Void> updateDiary(@Path("diaryId") int diaryId, @Body Diary diary);

    // 특정 다이어리를 삭제하는 엔드포인트
    @DELETE("diary/{diaryId}")
    Call<Void> deleteDiary(@Path("diaryId") int diaryId);
}
