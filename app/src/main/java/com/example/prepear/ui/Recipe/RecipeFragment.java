/**
 * Classname: AddEditRecipeActivity
 * Version Information: 1.0.0
 * Date: 11/2/2022
 * Author: Jiayin He, Yingyue Cao
 * Copyright Notice:
 */
package com.example.prepear.ui.Recipe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.prepear.AddDailyMealActivity;
import com.example.prepear.AddDailyMealConfirmationFragment;
import com.example.prepear.AddEditRecipeActivity;
import com.example.prepear.AddMealPlanActivity;
import com.example.prepear.ConfirmationDialog;
import com.example.prepear.CustomRecipeList;
import com.example.prepear.IngredientInRecipe;
import com.example.prepear.IngredientInStorage;
import com.example.prepear.R;
import com.example.prepear.Recipe;
import com.example.prepear.RecipeController;
import com.example.prepear.ViewRecipeActivity;
import com.example.prepear.databinding.FragmentRecipeBinding;
import com.example.prepear.ui.Ingredient.IngredientFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * This class define the view and the function of recipe fragment
 */
public class RecipeFragment extends Fragment implements ConfirmationDialog.OnFragmentInteractionListener, AddDailyMealConfirmationFragment.OnFragmentInteractionListener{

    // declare all the variables here
    private RecipeOnCallbackReceived callback;
    private RecipeTypeMealChoiceReceiver recipeTypeMealChoiceReceiver;
    int positionOfItemClicked;
    private FragmentRecipeBinding binding;
    ListView recipeList;
    CustomRecipeList recipeAdapter;
    RecipeController recipeDataList;
    int sortItemRecipe = 0;
    int recipePosition = -1;
    int LAUNCH_ADD_RECIPE_ACTIVITY = 1;
    int LAUNCH_VIEW_RECIPE_ACTIVITY = 2;
    final String[] sortItemSpinnerContent = {"  ","Title", "Preparation Time", "Number Of Serving", "Recipe Category"};
    Recipe newRecipe;
    Boolean reverse = Boolean.FALSE;

    /**
     * This interface is used when the view recipe fragment is called by the meal plan function
     */
    public interface RecipeOnCallbackReceived {
        public void addRecipeTypeMeal(Recipe selectedRecipe);
    }

    /**
     * This interface is used when the view recipe fragment is called by the meal plan function
     */
    public interface RecipeTypeMealChoiceReceiver{
        public void addRecipeTypeMealInDailyMealPlan(Recipe clickedRecipe);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (RecipeOnCallbackReceived) activity;
        } catch (ClassCastException e) {

        }
        try {
            recipeTypeMealChoiceReceiver = (RecipeTypeMealChoiceReceiver) activity;
        } catch (ClassCastException e) {

        }
    }

    /**
     * This function connect the fragment with its xml
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
         * All variables to link to the layout elements are defined here below
         */
        final Spinner sortItemSpinner;
        final FloatingActionButton addRecipe;
        final ImageButton sortSequence;

        /*
         * All variables to link to the layout elements are linked to layout by id here below
         */
        sortItemSpinner = view.findViewById(R.id.sort_spinner);
        addRecipe = view.findViewById(R.id.add_recipe_button);
        recipeList = view.findViewById(R.id.recipe_listview);
        sortSequence = view.findViewById(R.id.sort_button);
        sortSequence.setImageResource(R.drawable.ic_sort);

        /*
         * Database are defined and connected to collection with id "Recipes" here below
         */
        final String TAG = "Recipes";
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userUID = firebaseUser.getUid();
        final CollectionReference collectionReference =
                db.collection("User").document(userUID).collection("Recipes");

        /*
         * The arraylist and adapter for the recipes are assigned and linked to each other here
         * below
         */
        recipeDataList = new RecipeController();
        recipeAdapter = new CustomRecipeList(this.getContext(), recipeDataList);
        recipeList.setAdapter(recipeAdapter); /* Link the arraylist and adapter(controller) */

        /*
         * When the sort order button were pressed, the sort order should reverse
         */
        sortSequence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reverse) {
                    sortSequence.setImageResource(R.drawable.ic_sort);
                    reverse = Boolean.FALSE;
                } else {
                    sortSequence.setImageResource(R.drawable.ic_sort_reverse);
                    reverse = Boolean.TRUE;
                }
                recipeDataList.reverseOrder();
                recipeAdapter.notifyDataSetChanged();
            }
        });

        /*
         * When the addRecipe button is clicked, a new activity will start, which is activity to add
         * recipe.
         */
        addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof AddMealPlanActivity || getActivity() instanceof AddDailyMealActivity) {
                    CharSequence text = "Error, Please Click On a Recipe From The List!";
                    Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
                }else {
                    recipePosition = -1;
                    Intent intent = new Intent(getActivity(), AddEditRecipeActivity.class);
                    intent.putExtra("calling activity", "1");
                    startActivityForResult(intent, LAUNCH_ADD_RECIPE_ACTIVITY);
                }
            }
        });

        /*
         * When item on the listview was clicked, a new activity will start, which is activity to
         * view the details of that Recipe
         */
        recipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (getActivity() instanceof AddMealPlanActivity) {
                    // Grab the clicked item out of the ListView
                    positionOfItemClicked = i;
                    DialogFragment confirmationDialog = new ConfirmationDialog();
                    confirmationDialog.setTargetFragment(RecipeFragment.this, 0);
                    confirmationDialog.show(getFragmentManager(), "confirm selection");
                } else if (getActivity() instanceof AddDailyMealActivity) {
                    positionOfItemClicked = i;
                    DialogFragment confirmationFragment = new AddDailyMealConfirmationFragment();
                    confirmationFragment.setTargetFragment(RecipeFragment.this, 0);
                    confirmationFragment.show(getFragmentManager(), "Recipe Type Meal Addition Confirmation");
                } else {
                    recipePosition = i;
                    Recipe viewedRecipe = recipeAdapter.getItem(i);
                    Intent intent = new Intent(getActivity(), ViewRecipeActivity.class);
                    intent.putExtra("viewed recipe", viewedRecipe);
                    startActivityForResult(intent, LAUNCH_VIEW_RECIPE_ACTIVITY);
                }
            }
        });

        /*
         * The spinner in the activity is set and linked to the string array which contains all the
         * possible sort values
         */
        sortItemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0){
                    /* If it is not the first item in the spinner, which is a blank, is selected, the recipe
                     * will be sorted by the item selected */
                    sortItemRecipe = i - 1; /* get the index of the item the recipes should be sorted by */
                    recipeDataList.sortRecipe(sortItemRecipe); /* sort the recipes */
                    recipeAdapter.notifyDataSetChanged(); /* Notifying the adapter to render any new data fetched from the cloud */
                    sortSequence.setImageResource(R.drawable.ic_sort);
                    reverse = Boolean.FALSE;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // set adapter for the spinner to choose the sort item
        ArrayAdapter<String> ad = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, sortItemSpinnerContent);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortItemSpinner.setAdapter(ad);

        db
                .collection("Users")
                .document(userUID)
                .collection("Recipes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                recipeDataList.clearAllRecipes(); /* Clear the old list */

                /*
                 * Loop through all the documents in the collection named "Recipes"
                 */
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Log.d(TAG, String.valueOf(doc.getData().get("Preparation Time"))); /* Set an
                    error message */

                    /*
                     * get the id of the document
                     */
                    String id = doc.getId();

                    /*
                     * Get all the attributes in each document
                     */
                    String title = (String) doc.getData().get("Title");
                    Number preparationTime = (Number) doc.getData().get("Preparation Time");
                    Number numberOfServings = (Number) doc.getData().get("Number of Servings");
                    String recipeCategory = (String) doc.getData().get("Recipe Category");
                    String comments = (String) doc.getData().get("Comments");
                    String imageURI = (String) doc.getData().get("Image URI");

                    /*
                     * Create a new object of type {Recipe} with all the attributes added
                     */
                    newRecipe = new Recipe(imageURI, title, preparationTime.intValue(),
                            numberOfServings.intValue(), recipeCategory, comments); /* initialize
                                                                                 a recipe object */
                    newRecipe.setId(id); /* set id of the recipe in database */

                    /*
                     * add the newly generated recipe item to the recipeDataList
                     */
                    recipeDataList.addRecipe(newRecipe);
                }

                /*
                 * Loop through all the documents and get collections named "Ingredient" which
                 * and get information of all the ingredients needed by one Recipe.
                 */
                for (int i = 0; i < recipeDataList.countRecipes(); i++) {
                    int indexOfRecipe = i;

                    /*
                     * Get information of all the ingredients needed by the recipe at certain index
                     */
                    db
                            .collection("Users")
                            .document(userUID)
                            .collection("Recipes")
                            .document(recipeDataList.getRecipeAt(indexOfRecipe).getId())
                            .collection("Ingredient")
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    recipeDataList.getRecipeAt(indexOfRecipe).deleteAllIngredients();

                                    /*
                                     * Loop through all the documents in Ingredient collection
                                     */
                                    for (QueryDocumentSnapshot doc : value) {
                                        /*
                                         * get the id of the document
                                         */
                                        String id = doc.getId();

                                        /*
                                         * Get all the attributes in each document
                                         */
                                        String briefDescription = (String) doc.getData().get("Brief Description");
                                        Number amount = (Number) doc.getData().get("Amount");
                                        String unit = (String) doc.getData().get("Unit");
                                        String ingredientCategory = (String) doc.getData().get("Ingredient Category");

                                        /*
                                         * Create a new object of type {Ingredient} with all the attributes added
                                         */
                                        IngredientInRecipe NewIngredient = new IngredientInRecipe(briefDescription,amount.toString(),unit,ingredientCategory); /* initialize
                                                                                 a ingredient object */
                                        NewIngredient.setId(id); /* set id of the ingredient in database */

                                        /*
                                         * add the newly generated ingredient item to the recipe in the recipeDataList
                                         */
                                        recipeDataList.getRecipeAt(indexOfRecipe).addIngredientToRecipe(NewIngredient);
                                    }
                                }
                            });
                }

                recipeAdapter.notifyDataSetChanged(); /* Notifying the adapter to render any new data fetched from the cloud */
            }
        });
    }

    /**
     * This function is used to sent data back to meal plan when a recipe type meal is added
     */
    @Override
    public void onConfirmPressed() {
        Object clickedItem = recipeList.getItemAtPosition(positionOfItemClicked);
        // Casting this clicked item to IngredientInStorage type from Object type
        Recipe clickedFood = (Recipe) clickedItem;
        callback.addRecipeTypeMeal(clickedFood);
    }

    /**
     * This function is used return to meal plan when no recipe type meal is added
     */
    @Override
    public void onBackPressed() {
        getActivity().finish();
        startActivity(new Intent(getActivity(), AddDailyMealActivity.class));
    }

    /**
     * This function is used to sent data back to meal plan when a recipe type meal is added
     */
    @Override
    public void onOkPressed() {
        Object clickedItem = recipeList.getItemAtPosition(positionOfItemClicked);
        Recipe clickedRecipe = (Recipe) clickedItem;
        recipeTypeMealChoiceReceiver.addRecipeTypeMealInDailyMealPlan(clickedRecipe);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
