package com.example.bottomnavactivity.Services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceClient {

    public Retrofit BuildRetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://70beae306da5.ngrok-free.app/api/")
                .baseUrl("https://a00499dcbdc9.ngrok-free.app/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
