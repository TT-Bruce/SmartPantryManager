
package com.example.smartpantrymanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private ListView pantryList;
    private TextView emptyPantryMessage;

    private ArrayList<Ingredient> ingredients;
    private IngredientAdapter ingredientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);
        Button btnViewRecipes = findViewById(R.id.btnViewRecipes);
        Button btnSettings = findViewById(R.id.btnSettings);

        pantryList = findViewById(R.id.pantryList);
        emptyPantryMessage = findViewById(R.id.txtEmptyPantry);

        ingredients = new ArrayList<>();
        ingredientAdapter = new IngredientAdapter(this, ingredients);
        pantryList.setAdapter(ingredientAdapter);

        btnAddIngredient.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
            );
            startActivity(intent);
        });

        btnViewRecipes.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );
            startActivity(intent);
        });

        btnSettings.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );
            startActivity(intent);
        });

        pantryList.setOnItemClickListener((parent, view, position, id) -> {
            Ingredient ingredient = ingredients.get(position);

            showIngredientOptions(
                    ingredient.getId(),
                    ingredient.getName(),
                    ingredient.getQuantity(),
                    ingredient.getUnit()
            );
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_pantry) {
            // Return to the Pantry screen.
            return true;

        } else if (itemId == R.id.menu_recipes) {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );
            startActivity(intent);
            return true;

        } else if (itemId == R.id.menu_settings) {
            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && ingredientAdapter != null) {
            displayIngredients();
        }
    }

    private void displayIngredients() {
        ingredients.clear();

        Cursor cursor = databaseHelper.getAllIngredients();

        if (cursor == null) {
            emptyPantryMessage.setVisibility(View.VISIBLE);
            emptyPantryMessage.setText("Could not load ingredients.");
            ingredientAdapter.notifyDataSetChanged();
            return;
        }

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_ID
                        )
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_NAME
                        )
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_QUANTITY
                        )
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_UNIT
                        )
                );

                ingredients.add(
                        new Ingredient(id, name, quantity, unit)
                );
            }
        } finally {
            cursor.close();
        }

        ingredientAdapter.notifyDataSetChanged();

        if (ingredients.isEmpty()) {
            emptyPantryMessage.setVisibility(View.VISIBLE);
            emptyPantryMessage.setText(
                    "Your pantry is empty. Add your first ingredient!"
            );
        } else {
            emptyPantryMessage.setVisibility(View.GONE);
        }
    }

    private void showIngredientOptions(
            int id,
            String name,
            double quantity,
            String unit) {

        String[] options = {
                "Edit ingredient",
                "Delete ingredient"
        };

        new AlertDialog.Builder(this)
                .setTitle(name)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(
                                MainActivity.this,
                                AddIngredientActivity.class
                        );

                        intent.putExtra("ingredient_id", id);
                        intent.putExtra("ingredient_name", name);
                        intent.putExtra("ingredient_quantity", quantity);
                        intent.putExtra("ingredient_unit", unit);

                        startActivity(intent);

                    } else if (which == 1) {
                        confirmDelete(id, name);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete(int id, String name) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage(
                        "Are you sure you want to delete " + name + "?"
                )
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = databaseHelper.deleteIngredient(id);

                    if (deleted) {
                        Toast.makeText(
                                MainActivity.this,
                                name + " deleted successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        displayIngredients();

                    } else {
                        Toast.makeText(
                                MainActivity.this,
                                "Could not delete ingredient",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
