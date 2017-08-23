package com.example.android.firman.bakingapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.android.firman.bakingapp.model.Step;

import java.util.ArrayList;

/**
 * An activity representing a single RecipeStep detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeDetailActivity}.
 */
public class RecipeStepDetailActivity extends AppCompatActivity {

    ArrayList<Step> mSteps;
    int mActiveStepIndex;
    FloatingActionButton fab_next, fab_before;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        fab_next = (FloatingActionButton) findViewById(R.id.fab_next);
        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                int next_index = mActiveStepIndex+1;
                if(next_index<mSteps.size()){
                    updateFragment(mSteps, next_index, false);
                }
            }
        });
        fab_before = (FloatingActionButton) findViewById(R.id.fab_before);
        fab_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int next_index = mActiveStepIndex-1;
                if(next_index>=0){
                    updateFragment(mSteps, next_index, false);
                }
            }
        });
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            mSteps = getIntent().getParcelableArrayListExtra(RecipeStepDetailFragment.ITEMS_EXTRA);
            mActiveStepIndex=getIntent().getIntExtra(RecipeStepDetailFragment.ITEM_NUMBER_EXTRA, 0);
            updateFragment(mSteps, mActiveStepIndex, true);
        }
    }

    private void updateFragment(ArrayList<Step> steps, int index, boolean isNew){
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(RecipeStepDetailFragment.ITEMS_EXTRA, steps);
        arguments.putInt(RecipeStepDetailFragment.ITEM_NUMBER_EXTRA, index);
        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        fragment.setArguments(arguments);
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(isNew) {
            ft.add(R.id.recipestep_detail_container, fragment);
        } else {
            ft.replace(R.id.recipestep_detail_container,fragment);
        }
        ft.commit();
        mActiveStepIndex = index;

        if(index<=0) fab_before.hide();
        else {
            fab_before.show();
            fab_before.setAlpha(0.5f);
        }

        if(index>=steps.size()-1) fab_next.hide();
        else {
            fab_next.show();
            fab_next.setAlpha(0.5f);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
