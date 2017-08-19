package com.example.android.firman.bakingapp.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by firman on 12/08/17.
 */

public class RecipeRetrofit {
    public static final String URL = "https://d17h27t6h515a5.cloudfront.net/";
    private static Retrofit sInstance;
    public static synchronized Retrofit getInstance(){
        if(sInstance==null) {
            sInstance = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sInstance;
    }
}
