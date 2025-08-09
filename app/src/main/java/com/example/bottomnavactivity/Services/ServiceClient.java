package com.example.bottomnavactivity.Services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceClient {

    private static final String BASE_URL = "https://783c57d3190a.ngrok-free.app/api/";
    private static Retrofit retrofit;

    public static Retrofit BuildRetrofitClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

//    public Retrofit BuildRetrofitClient() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://3c60b0354a20.ngrok-free.app/api/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        return retrofit;
//    }

}
