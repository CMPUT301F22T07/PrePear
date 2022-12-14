package com.example.prepear;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class CustomShoppingList extends ArrayAdapter<IngredientInRecipe> {
    private ArrayList<IngredientInRecipe> ingredientsInShoppingList;
    private Context context;

    public CustomShoppingList(Context context, ArrayList<IngredientInRecipe> ingredientsInShoppingList) {
        super(context, 0, ingredientsInShoppingList);
        this.ingredientsInShoppingList = ingredientsInShoppingList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // return super.getView(position, convertView, parent);
        View view = convertView;
        /* check error */
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_ingredient_in_shopping_list, parent, false);
        }

        IngredientInRecipe ingredientInShoppingList = ingredientsInShoppingList.get(position);

        // connects the the layout with the views
        TextView briefDescriptionTextView = view.findViewById(R.id.brief_description_TextView_shopping);
        TextView amountTextView = view.findViewById(R.id.amount_TextView_shopping);
        TextView unitTextView = view.findViewById(R.id.unit_TextView_shopping);
        TextView ingredientCategoryTextView = view.findViewById(R.id.ingredient_category_TextView_shopping);
        CheckBox shoppingListCheckBox = view.findViewById(R.id.ingredient_in_shopping_list_CheckBox);


        // sets the detailed information to the view
        briefDescriptionTextView.setText(ingredientInShoppingList.getBriefDescription());
        amountTextView.setText(String.valueOf(ingredientInShoppingList.getAmountValue()));
        unitTextView.setText(ingredientInShoppingList.getUnit());
        ingredientCategoryTextView.setText(ingredientInShoppingList.getIngredientCategory());
        shoppingListCheckBox.setChecked(false);

        // when user click checkbox
        shoppingListCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                On below:
                show first pop up the dialogFragment to input more details
                 */
                FragmentActivity activity = (FragmentActivity)(context);
                FragmentManager fm = activity.getSupportFragmentManager();
                ShoppingListClickboxFragment.newInstance(ingredientInShoppingList)
                        .show(fm, "ADD_INGREDIENT_DETAILS");
                shoppingListCheckBox.setChecked(true);
            }
        });

        // when user click ingredient in list
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                On below:
                show first pop up the dialogFragment to view details of ingredient
                 */
                FragmentActivity activity = (FragmentActivity)(context);
                FragmentManager fm = activity.getSupportFragmentManager();
                ShoppingListViewIngredientFragment.newInstance(ingredientInShoppingList)
                        .show(fm, "VIEW_INGREDIENT");
                Log.d("CLICKED", "row number: " + position);
            }
        });
        return view;
    }
}

