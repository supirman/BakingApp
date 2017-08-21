package com.example.android.firman.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.android.firman.bakingapp.model.Recipe;
import com.example.android.firman.bakingapp.utils.RecipeRetrofit;
import com.example.android.firman.bakingapp.utils.RecipeService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements RecipeRecycleAdapter.RecipeOnCLickHandler {

    RecyclerView mRecyclerView;
    RecipeRecycleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = RecipeRetrofit.getInstance();
        RecipeService recipeService = retrofit.create(RecipeService.class);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_recipe_list);
        mAdapter = new RecipeRecycleAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Call<List<Recipe>> recipeCall = recipeService.getAllRecipes();
        recipeCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                mAdapter.setResipes(response.body());
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.v("Fail","Fail to load data");
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onCLick(Recipe recipe) {
        Intent intent = new Intent(getBaseContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.DETAIL_RECIPE, recipe);
        startActivity(intent);
    }
}
