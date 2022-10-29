package com.example.prepear;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomIngredientInRecipeList extends ArrayAdapter<IngredientInRecipe> {

    private ArrayList<IngredientInRecipe> ingredientsInRecipe;
    private Context context;

    public CustomIngredientInRecipeList(Context context, ArrayList<IngredientInRecipe> ingredientsInRecipe) {
        super(context, 0, ingredientsInRecipe);
        this.ingredientsInRecipe = ingredientsInRecipe;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // return super.getView(position, convertView, parent);
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_ingredient_in_recipe, parent, false);
        }

        IngredientInRecipe ingredientInRecipe = ingredientsInRecipe.get(position);

        TextView briefDescriptionTextView = view.findViewById(R.id.brief_description_TextView);
        TextView amountTextView = view.findViewById(R.id.amount_TextView);
        TextView unitTextView = view.findViewById(R.id.unit_TextView);
        TextView ingredientCategoryTextView = view.findViewById(R.id.ingredient_category_TextView);

        briefDescriptionTextView.setText(ingredientInRecipe.getBriefDescription());
        amountTextView.setText(ingredientInRecipe.getAmount().toString());
        unitTextView.setText(ingredientInRecipe.getUnit());
        ingredientCategoryTextView.setText(ingredientInRecipe.getIngredientCategory());

        return view;
    }
}