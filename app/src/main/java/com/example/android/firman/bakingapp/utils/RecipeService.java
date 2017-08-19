package com.example.android.firman.bakingapp.utils;

import com.example.android.firman.bakingapp.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by firman on 12/08/17.
 */

public interface RecipeService {
    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getAllRecipes();
}
