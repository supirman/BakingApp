package com.example.android.firman.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.firman.bakingapp.model.Ingredient;
import com.example.android.firman.bakingapp.model.Recipe;
import com.example.android.firman.bakingapp.model.Step;

import java.util.List;

/**
 * An activity representing a list of RecipeSteps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeStepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String DETAIL_RECIPE = "recipe_detail";
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private Recipe mRecipe;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRecyclerView =(RecyclerView) findViewById(R.id.recipestep_list);
        assert mRecyclerView != null;

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null && intentThatStartedThisActivity.hasExtra(DETAIL_RECIPE)){
            mRecipe = intentThatStartedThisActivity.getExtras().getParcelable(DETAIL_RECIPE);
            setTitle(mRecipe.getName());
        }
        setupRecyclerView(mRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        if (findViewById(R.id.recipestep_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DETAIL_RECIPE, mRecipe);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mRecipe= savedInstanceState.getParcelable(DETAIL_RECIPE);
        setupRecyclerView(mRecyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        if(mRecipe!=null) {
            recyclerView.setAdapter(new RecipeStepItemRecyclerViewAdapter(mRecipe.getSteps()));

        }
    }

    public class RecipeStepItemRecyclerViewAdapter
            extends RecyclerView.Adapter<RecipeStepItemRecyclerViewAdapter.ViewHolder> {

        private final List<Step> mValues;

        public RecipeStepItemRecyclerViewAdapter(List<Step> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipestep_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            String displayText="Ingredients :";
            if(position == 0) {
                for(Ingredient i : mRecipe.getIngredients()){
                    displayText+=String.format("\n%s %.2f %s", i.getIngredient(), i.getQuantity(), i.getMeasure());
                }
                holder.mContentView.setText(displayText);
                holder.mContentView.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(),android.R.color.darker_gray));
                holder.mContentView.setTextColor(ContextCompat.getColor(holder.mView.getContext(),android.R.color.white));
            } else {
                final Step step = mValues.get(position-1);
                //holder.mItem = step ;
                if (step != null) {
                    holder.mContentView.setText(step.getShortDescription());
                }
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putParcelable(RecipeStepDetailFragment.ITEM_EXTRA, step);
                            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.recipestep_detail_container, fragment)
                                    .commit();
                        } else {
                            Context context = v.getContext();
                            Intent intent = new Intent(context, RecipeStepDetailActivity.class);
                            intent.putExtra(RecipeStepDetailFragment.ITEM_EXTRA, step);
                            context.startActivity(intent);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mRecipe == null ? 0: mValues != null? mValues.size() + 1 : 1 ;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            //public Step mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
