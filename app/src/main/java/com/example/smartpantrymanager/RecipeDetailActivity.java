package com.example.smartpantrymanager;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class RecipeDetailActivity extends Activity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        databaseHelper = new DatabaseHelper(this);

        TextView recipeNameView =
                findViewById(R.id.txtRecipeDetailName);

        TextView ingredientsView =
                findViewById(R.id.txtRecipeIngredients);

        TextView methodView =
                findViewById(R.id.txtRecipeMethod);

        Button backButton = findViewById(R.id.btnBackToRecipes);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        String recipeName = getIntent().getStringExtra("recipe_name");
        String method = getIntent().getStringExtra("recipe_method");

        recipeNameView.setText(
                recipeName == null ? "Recipe Details" : recipeName
        );

        methodView.setText(
                method == null ? "No cooking method available." : method
        );

        StringBuilder ingredientsText = new StringBuilder();

        if (recipeId != -1) {
            Cursor cursor = databaseHelper.getRecipeIngredients(recipeId);

            try {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(
                            cursor.getColumnIndexOrThrow("ingredient_name")
                    );

                    double quantity = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("required_quantity")
                    );

                    String unit = cursor.getString(
                            cursor.getColumnIndexOrThrow("unit")
                    );

                    ingredientsText
                            .append("• ")
                            .append(name)
                            .append(" — ")
                            .append(quantity)
                            .append(" ")
                            .append(unit)
                            .append("\n");
                }
            } finally {
                cursor.close();
            }
        }

        if (ingredientsText.length() == 0) {
            ingredientsText.append("No ingredients found.");
        }

        ingredientsView.setText(ingredientsText.toString());

        backButton.setOnClickListener(view -> finish());
    }
}
