package com.example.myapplication.activitys.network;

import com.example.myapplication.activitys.util.Constant;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRetrofit {
    private static Retrofit retrofit;
    public static Retrofit retrofit()
    {

        retrofit = new Retrofit.Builder().baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        return retrofit;
    }
}