package com.example.android.firman.bakingapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.android.firman.bakingapp.model.Ingredient;
import com.example.android.firman.bakingapp.model.Recipe;
import com.example.android.firman.bakingapp.utils.RecipeRetrofit;
import com.example.android.firman.bakingapp.utils.RecipeService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The configuration screen for the {@link BakingRecipeWidget BakingRecipeWidget} AppWidget.
 */
public class BakingRecipeWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.example.android.firman.bakingapp.BakingRecipeWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    public static final String RECIPE_EXTRA = "recipe_extra";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    BakingRecipeWidgetRecyclerAdapter mAdapter;
//    View.OnClickListener mOnClickListener = new View.OnClickListener() {
//        public void onClick(View v) {
//            final Context context = BakingRecipeWidgetConfigureActivity.this;
//
//            // When the button is clicked, store the string locally
//            String widgetText = mAppWidgetText.getText().toString();
//            saveTitlePref(context, mAppWidgetId, widgetText);
//
//            // It is the responsibility of the configuration activity to update the app widget
//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//            BakingRecipeWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);
//
//            // Make sure we pass back the original appWidgetId
//            Intent resultValue = new Intent();
//            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
//            setResult(RESULT_OK, resultValue);
//            finish();
//        }
//    };

    public BakingRecipeWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.baking_recipe_widget_configure);
        RecyclerView recyclerView = findViewById(R.id.rv_recipe_config_list);
        mAdapter = new BakingRecipeWidgetRecyclerAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Call<List<Recipe>> recipeCall = RecipeRetrofit.getInstance()
                .create(RecipeService.class).getAllRecipes();
        recipeCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                mAdapter.setRecipes(response.body());

            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.v("Fail","Fail to load data");
                t.printStackTrace();
            }
        });

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        //mAppWidgetText.setText(loadTitlePref(BakingRecipeWidgetConfigureActivity.this, mAppWidgetId));
    }

    class BakingRecipeWidgetRecyclerAdapter extends RecyclerView.Adapter<BakingRecipeWidgetRecyclerAdapter.ViewHolder> {

        public void setRecipes(List<Recipe> recipes) {
            this.recipes = recipes;
            notifyDataSetChanged();
        }

        List<Recipe> recipes;

        @Override
        public BakingRecipeWidgetRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(BakingRecipeWidgetRecyclerAdapter.ViewHolder holder, int position) {
            holder.setRecipe(recipes.get(position));
        }

        @Override
        public int getItemCount() {
            return recipes==null?0:recipes.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public void setRecipe(Recipe recipe) {
                this.mRecipe = recipe;
                textView.setText(recipe.getName());
            }

            Recipe mRecipe;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(BakingRecipeWidgetConfigureActivity.this);

                        RemoteViews views = new RemoteViews(BakingRecipeWidgetConfigureActivity.this.getPackageName(), R.layout.baking_recipe_widget);

                        String widgetText=mRecipe.getName();
                        for(Ingredient i : mRecipe.getIngredients()){
                            widgetText+=String.format("\n%s %.2f %s", i.getIngredient(), i.getQuantity(), i.getMeasure());
                        }

                        views.setTextViewText(R.id.appwidget_text, widgetText);
                        // Make sure we pass back the original appWidgetId
                        appWidgetManager.updateAppWidget(mAppWidgetId, views);

                        Intent resultValue = new Intent();
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                        //resultValue.putExtra(RECIPE_EXTRA, mRecipe);
                        setResult(RESULT_OK, resultValue);
                        finish();
                    }
                });
            }
        }
    }
}

