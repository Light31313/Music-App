package com.example.musicapplication.entity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MusicRetrofit {
    @GET("songInfo")
    Call<ArrayList<Music>> getAllMusics();

    @GET("songInfo/{id}")
    Call<Music> getMusicById(@Path("id") int id);

    @POST("songInfo")
    Call<Void> createMusic(@Body Music music);

    @PUT("songInfo/{id}")
    Call<Void> updateMusic(@Path("id") int id, @Body Music music);

    @DELETE("songInfo/{id}")
    Call<Void> deleteMusic(@Path("id") int id);
}
