package com.example.android.firman.bakingapp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.firman.bakingapp.databinding.RecipeListItemBinding;
import com.example.android.firman.bakingapp.model.Recipe;

import java.util.List;

/**
 * Created by firman on 19/08/17.
 */

public class RecipeRecycleAdapter extends RecyclerView.Adapter<RecipeRecycleAdapter.RecipeViewHolder> {
    private List<Recipe> recipes;
    private Context mContext;
    private RecipeOnCLickHandler mOnCLickHandler;

    RecipeRecycleAdapter(Context context, RecipeOnCLickHandler onCLickHandler){
        mContext = context;
        mOnCLickHandler = onCLickHandler;
    }

    interface RecipeOnCLickHandler {
        void onCLick(Recipe recipe);
    }

    @Override
    public RecipeRecycleAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecipeListItemBinding itemBinding = DataBindingUtil.inflate(inflater,
                R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(RecipeRecycleAdapter.RecipeViewHolder holder, int position) {
        holder.bind(recipes.get(position));
    }

    @Override
    public int getItemCount() {
        return recipes==null? 0 : recipes.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RecipeListItemBinding binding;
        public RecipeViewHolder(RecipeListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
            itemBinding.getRoot().setOnClickListener(this);
        }

        void bind(Recipe recipe){
            binding.setRecipe(recipe);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            mOnCLickHandler.onCLick(binding.getRecipe());
        }
    }

    public void setResipes(List<Recipe> resipes){
        this.recipes = resipes;
        notifyDataSetChanged();
    }

    public List<Recipe> getRecipes(){
        return recipes;
    }
}
