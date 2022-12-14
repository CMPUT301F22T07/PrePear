/**
 * Class Name: AddMealPlanActivity
 * Version Information: Version 1.0
 * Date: Nov 23, 2022
 * Author: Marafi Mergani
 * Copyright Notice:
 */
package com.example.prepear;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.prepear.ui.Ingredient.IngredientFragment;
import com.example.prepear.ui.MealPlan.MealPlanFragment;
import com.example.prepear.ui.Recipe.RecipeFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * This class defines the add meal plan activity that allows user to either add a new meal plan or
 * view an existing one.
 */
public class AddMealPlanActivity extends AppCompatActivity implements View.OnClickListener,
        IngredientFragment.IngredientOnCallbackReceived, RecipeFragment.RecipeOnCallbackReceived{
    private String startDate;
    private String endDate;
    private EditText startDateView;
    private EditText endDateView;
    private LinearLayout amountLayout;
    private EditText amountEditText;
    private LinearLayout numberOfServingsLayout;
    private EditText numberOfServingsEditText;
    private Button confirm;
    private Button cancel;
    private DatePickerDialog dialog; // create datePicker for best dates
    private RadioButton ingredientButton;
    private RadioButton recipeButton;
    private final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal_plan);

        // connect variables to view objects
        startDateView = findViewById(R.id.start_date);
        endDateView = findViewById(R.id.end_date);
        amountLayout = findViewById(R.id.amount_layout);
        amountEditText = findViewById(R.id.amount);
        numberOfServingsLayout = findViewById(R.id.number_of_servings_layout);
        numberOfServingsEditText = findViewById(R.id.number_of_servings);
        confirm = findViewById(R.id.confirm);
        cancel = findViewById(R.id.cancel);
        ingredientButton = findViewById(R.id.ingredient_radioButton);
        recipeButton = findViewById(R.id.recipe_radioButton);

        // set on click listener to confirm button
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // display toast message, since user needs to finish adding the meal plan
                CharSequence text = "Error, Please Finish Adding This Meal Plan Or Click Cancel!";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // return to view fragment
                finish(); // exit activity
                Intent intentBack = new Intent();
                setResult(Activity.RESULT_CANCELED, intentBack); // set cancel result
            }
        });

        // set date picker dialog
        startDateView.setOnClickListener(this);
        endDateView.setOnClickListener(this);
        // set click listener for radio buttons
        ingredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeKeyboard();
                ingredientButton.setChecked(true);
                recipeButton.setChecked(false);
                // on below: call IngredientFragment
                FragmentTransaction transaction;
                transaction = getSupportFragmentManager().beginTransaction(); // begin fragment transaction
                FrameLayout fl = findViewById(android.R.id.content);
                fl.removeAllViews();    // remove all views from display
                // replace the current view with the fragment
                transaction.replace(android.R.id.content, new IngredientFragment(), "selectIngredient");
                transaction.addToBackStack(null);
                transaction.commit();
                getSupportFragmentManager().executePendingTransactions();
            }
        });
        recipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeKeyboard();
                recipeButton.setChecked(true);
                ingredientButton.setChecked(false);
                // on below call RecipeFragment
                FragmentTransaction transaction;
                transaction = getSupportFragmentManager().beginTransaction(); // begin fragment transaction
                FrameLayout fl = findViewById(android.R.id.content);
                fl.removeAllViews(); // remove all views from display
                // replace the current view with the fragment
                transaction.replace(android.R.id.content, new RecipeFragment(), "selectRecipe");
                transaction.addToBackStack(null);
                transaction.commit();
                getSupportFragmentManager().executePendingTransactions();
            }
        });
    }

    /**
     * This method removes all present soft keyboards and is used when user clicks on one of the spinners
     */
    private void removeKeyboard() {
        /* get the input method manager which manages interaction within the system.
         * get System service is used to access Android system-level services like the keyboard
         */
        InputMethodManager inputMethodManager =
                (InputMethodManager) AddMealPlanActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
        // On below part: Hide the soft keyboards associated with description and amount edit text fields
        inputMethodManager.hideSoftInputFromWindow(amountLayout.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(numberOfServingsLayout.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        // On below part: create a date picker for the best before date of the ingredient
        Calendar currentDate = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH);
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
        dialog = new DatePickerDialog(AddMealPlanActivity.this, R.style.activity_date_picker,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        /* On below parts:
                         * set the day of month , the month of year and the year value in the edit text
                         * if-conditional statement helps to regulate the format of yyyy-mm-dd.
                         * */
                        if (monthOfYear < 9 && dayOfMonth < 10) {
                            if (v.getId() == R.id.start_date) {
                                startDate = year + "-" + "0" + (monthOfYear + 1) +
                                        "-" + "0" + dayOfMonth;
                            }else{
                                endDate = year + "-" + "0" + (monthOfYear + 1) +
                                        "-" + "0" + dayOfMonth;
                            }
                        } else if (dayOfMonth < 10) {
                            if (v.getId() == R.id.start_date) {
                                startDate = year + "-" + (monthOfYear + 1) +
                                        "-" + "0" + dayOfMonth;
                            }else{
                                endDate = year + "-" + (monthOfYear + 1) +
                                        "-" + "0" + dayOfMonth;
                            }
                        } else if (monthOfYear < 9) {
                            if (v.getId() == R.id.start_date) {
                                startDate = year + "-" + "0" + (monthOfYear + 1) +
                                        "-" + dayOfMonth;
                            }else{
                                endDate = year + "-" + "0" + (monthOfYear + 1) +
                                        "-" + dayOfMonth;
                            }
                        } else {
                            if (v.getId() == R.id.start_date) {
                                startDate = year + "-" + (monthOfYear + 1) +
                                        "-" + dayOfMonth;
                            }else{
                                endDate = year + "-" + (monthOfYear + 1) +
                                        "-" + dayOfMonth;
                            }
                        }
                        if (v.getId() == R.id.start_date) {
                            startDateView.setText(startDate);
                        }else{
                            endDateView.setText(endDate);
                        }
                    }
                }, currentYear, currentMonth, currentDay);
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
        // On below line: temporarily remove keyboards before displaying the dialog
        removeKeyboard();
    }

    /**
     * This method receives the selected ingredient and sends it to MealPlanFragment if
     * all other input is valid
     * @param selectedIngredient the user selected ingredient
     */
    @Override
    public void addIngredientTypeMeal(IngredientInStorage selectedIngredient) {
        setContentView(R.layout.activity_add_meal_plan);
        // connect variables with the view objects
        amountEditText = findViewById(R.id.amount);
        amountLayout = findViewById(R.id.amount_layout);
        numberOfServingsLayout = findViewById(R.id.number_of_servings_layout);
        startDateView = findViewById(R.id.start_date);
        endDateView = findViewById(R.id.end_date);
        ingredientButton = findViewById(R.id.ingredient_radioButton);
        recipeButton = findViewById(R.id.recipe_radioButton);
        confirm = findViewById(R.id.confirm);
        cancel = findViewById(R.id.cancel);
        // check off the ingredient radio button
        ingredientButton.setChecked(true);
        // set dialog click listener
        startDateView.setOnClickListener(this);
        endDateView.setOnClickListener(this);
        if (startDate != null){ // if user selected a start date before choosing an ingredient
            startDateView.setText(startDate); // display the start date
        }
        if (endDate != null){ // if user selected an end date before choosing an ingredient
            endDateView.setText(endDate); // display the end date
        }
        recipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeKeyboard();
                // disable the recipe radio button
                recipeButton.setChecked(false);
                // display toast error message
                CharSequence text = "Error, Please Finish Adding This Ingredient Or Click Cancel!";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
        if (selectedIngredient != null) {
            // display amount layout, and hide number of servings layout
            amountLayout.setVisibility(View.VISIBLE);
            numberOfServingsLayout.setVisibility(View.GONE);
            // set click listener for the confirm button
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get the entered amount
                    String amount = amountEditText.getText().toString().trim();
                    if (amount.isEmpty()) {
                        // display a toast error message if amount is left empty
                        CharSequence text = "Error, Please Enter an Amount!";
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                    }else if(startDate.isEmpty() || endDate.isEmpty()){
                        // display a toast error message if start or end dates are left empty
                        CharSequence text = "Error, Please Finish Selecting The Dates!";
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                    }else if (amount.equals("0")){
                        // display a toast error message if amount is entered as zero
                        CharSequence text = "Error, Please Enter An Amount Greater Than Zero!";
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                    } else {
                        // input is valid so create a new intent to call the MealPlanFragment
                        Intent intentBack = new Intent();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            // convert date strings to local dates
                            Date start = sdf.parse(startDate); // parse string dates using the sdf format
                            LocalDate localStart = start.toInstant().atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            Date end = sdf.parse(endDate);
                            LocalDate localEnd = end.toInstant().atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            Integer counter = 0;  // counter for the number of Meal objects
                            if(localEnd.isBefore(localStart)){
                                // set error message if end date is before start date
                                CharSequence text = "Error, End Date Must Come After Start Date!";
                                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                            }else {
                                for (LocalDate date = localStart; date.isBefore(localEnd) || date.isEqual(localEnd);
                                     date = date.plusDays(1)) {
                                    // create a Meal object for each DailyMealPlan object
                                    Meal firstMeal = new Meal("IngredientInStorage",
                                            selectedIngredient.getDocumentId(), selectedIngredient.getDocumentId());
                                    firstMeal.setCustomizedAmount( Double.parseDouble(amount));
                                    counter += 1; // counts the number of meals created
                                    // create the DailyMealPlan object
                                    DailyMealPlan newMeal = new DailyMealPlan(date.toString(), firstMeal);
                                    // send DailyMealPlan object to MealFragment
                                    intentBack.putExtra("meal" + counter, newMeal);
                                }
                                intentBack.putExtra("counter", counter); // send the counter back
                                setResult(Activity.RESULT_OK, intentBack);
                                finish(); // exit activity
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // return to view fragment
                    finish(); // exit activity
                    Intent intentBack = new Intent();
                    setResult(Activity.RESULT_CANCELED, intentBack); // set cancel result
                }
            });
        }/*else{
            // return to view fragment
            finish(); // exit activity
            Intent intentBack = new Intent();
            setResult(Activity.RESULT_CANCELED, intentBack); // set cancel result
        }*/
    }

    @Override
    public void addRecipeTypeMeal(Recipe selectedRecipe) {
        setContentView(R.layout.activity_add_meal_plan);
        // connect variables with the view objects
        numberOfServingsEditText = findViewById(R.id.number_of_servings);
        amountLayout = findViewById(R.id.amount_layout);
        numberOfServingsLayout = findViewById(R.id.number_of_servings_layout);
        startDateView = findViewById(R.id.start_date);
        endDateView = findViewById(R.id.end_date);
        ingredientButton = findViewById(R.id.ingredient_radioButton);
        numberOfServingsLayout =  findViewById(R.id.number_of_servings_layout);
        recipeButton = findViewById(R.id.recipe_radioButton);
        confirm = findViewById(R.id.confirm);
        cancel = findViewById(R.id.cancel);
        // check off the recipe radio button
        recipeButton.setChecked(true);
        // set dialog click listener
        startDateView.setOnClickListener(this);
        endDateView.setOnClickListener(this);
        if (startDate != null){  // if user selected a start date before choosing an ingredient
            startDateView.setText(startDate); // display the start date
        }
        if (endDate != null){ // if user selected an end date before choosing an ingredient
            endDateView.setText(endDate); // display the end date
        }
        ingredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeKeyboard();
                // disable the ingredient radio button
                ingredientButton.setChecked(false);
                // display error message
                CharSequence text = "Error, Please Finish Adding This Recipe Or Click Cancel!";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
        if (selectedRecipe != null) {
            // display number of servings layout, and hide amount layout
            amountLayout.setVisibility(View.GONE);
            numberOfServingsLayout.setVisibility(View.VISIBLE);
            // set click listener for the confirm button
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get entered number of servings
                    String numberOfServings = numberOfServingsEditText.getText().toString().trim();
                    if (numberOfServings.isEmpty()) {
                        // display a toast error message if amount is left empty
                        CharSequence text = "Error, Please Enter The Number Of Servings!";
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                    } else if(startDate.isEmpty() || endDate.isEmpty()){
                        // display a toast error message if start or end dates are left empty
                        CharSequence text = "Error, Please Finish Selecting The Dates!";
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                    }else if (numberOfServings.equals("0")){
                        // display a toast error message if number of servings is zero
                        CharSequence text = "Error, Please Enter a Number Of Servings Greater Than Zero!";
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                    }else {
                        // input is valid so create an intent
                        Intent intentBack = new Intent();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            // convert date strings to local dates
                            Date start = sdf.parse(startDate); // parse string dates using the sdf format
                            LocalDate localStart = start.toInstant().atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            Date end = sdf.parse(endDate);
                            LocalDate localEnd = end.toInstant().atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            Integer counter = 0;  // counter for the number of Meal objects
                            if(localEnd.isBefore(localStart)){
                                // set error message if end date is before start date
                                CharSequence text = "Error, End Date Must Come After Start Date!";
                                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                            } else {
                                for (LocalDate date = localStart; date.isBefore(localEnd) || date.isEqual(localEnd);
                                     date = date.plusDays(1)) {
                                    // create a Meal object for each DailyMealPlan object
                                    Meal firstMeal = new Meal("Recipe",selectedRecipe.getId(),String.valueOf(new Date()));
                                    firstMeal.setCustomizedNumberOfServings(Integer.parseInt(numberOfServings));
                                    // create the DailyMealPlan object
                                    counter += 1;
                                    DailyMealPlan newMeal = new DailyMealPlan(date.toString(), firstMeal);
                                    // send DailyMealPlan object to MealFragment
                                    intentBack.putExtra("meal" + counter, newMeal);
                                }
                                intentBack.putExtra("counter", counter);
                                setResult(Activity.RESULT_OK, intentBack);
                                finish(); // exit activity
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // exit activity
                    Intent intentBack = new Intent();
                    setResult(Activity.RESULT_CANCELED, intentBack); // set cancel result
                }
            });
        } /*else {
            // return to view fragment
            finish(); // exit activity
            Intent intentBack = new Intent();
            setResult(Activity.RESULT_CANCELED, intentBack); // set cancel result
        }*/
    }
}