/*
* */
package com.example.prepear;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/*
*
* */
public class DailyMealPlan {
    private ArrayList<Meal> dailyMealDataList; // contains today's all meals (Meal objects)
    String currentDailyMealPlanDate; // represents this current daily meal plan's date

    /**
     * @return all meals inside this one-day daily meal plan
     */
    public ArrayList<Meal> getDailyMealDataList() {
        return this.dailyMealDataList;
    }

    /**
     * @param dailyMealDataList an one-day daily meal plan which stores all meals for this day
     */
    public void setDailyMealDataList(ArrayList<Meal> dailyMealDataList) {
        this.dailyMealDataList = dailyMealDataList;
    }

    /**
     * @return the date for this current daily meal plan
     */
    public String getCurrentDailyMealPlanDate() {
        return this.currentDailyMealPlanDate;
    }

    /**
     * @param currentDailyMealPlanDate current daily meal plan's date
     */
    public void setCurrentDailyMealPlanDate(String currentDailyMealPlanDate) {
        this.currentDailyMealPlanDate = currentDailyMealPlanDate;
    }

    
}
