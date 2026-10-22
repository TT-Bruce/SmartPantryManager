package com.example.smartpantrymanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SuggestedRecipesActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private LinearLayout recipeList;
    private TextView recipeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        databaseHelper = new DatabaseHelper(this);

        recipeList = findViewById(R.id.recipeList);
        recipeMessage = findViewById(R.id.txtRecipeMessage);

        Button backButton = findViewById(R.id.btnBackToPantry);

        backButton.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && recipeList != null) {
            displaySuggestedRecipes();
        }
    }

    private void displaySuggestedRecipes() {
        recipeList.removeAllViews();

        Cursor cursor = databaseHelper.getSuggestedRecipes();

        try {
            if (cursor.getCount() == 0) {
                recipeMessage.setVisibility(View.VISIBLE);
                recipeMessage.setText(
                        "No recipes available yet. Add the required " +
                                "ingredients and quantities to your pantry."
                );
                return;
            }

            recipeMessage.setVisibility(View.VISIBLE);
            recipeMessage.setText(
                    "You can make these recipes with your current pantry:"
            );

            while (cursor.moveToNext()) {
                int recipeId = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String recipeName = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                String method = cursor.getString(
                        cursor.getColumnIndexOrThrow("method")
                );

                TextView recipe = new TextView(this);

                recipe.setText(
                        recipeName +
                                "\nTap to view cooking instructions"
                );

                recipe.setTextSize(18);
                recipe.setPadding(12, 20, 12, 20);
                recipe.setClickable(true);
                recipe.setFocusable(true);

                recipe.setOnClickListener(view -> {
                    Intent intent = new Intent(
                            SuggestedRecipesActivity.this,
                            RecipeDetailActivity.class
                    );

                    intent.putExtra("recipe_id", recipeId);
                    intent.putExtra("recipe_name", recipeName);
                    intent.putExtra("recipe_method", method);

                    startActivity(intent);
                });

                recipeList.addView(recipe);
            }

        } finally {
            cursor.close();
        }
    }
}
