package com.example.smartpantrymanager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddIngredientActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private EditText nameInput;
    private EditText quantityInput;
    private EditText unitInput;
    private Button saveButton;

    private int ingredientId = -1;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        databaseHelper = new DatabaseHelper(this);

        nameInput = findViewById(R.id.editIngredientName);
        quantityInput = findViewById(R.id.editIngredientQuantity);
        unitInput = findViewById(R.id.editIngredientUnit);
        saveButton = findViewById(R.id.btnSaveIngredient);

        // Check whether an existing ingredient is being edited.
        if (getIntent().hasExtra("ingredient_id")) {
            isEditing = true;

            ingredientId = getIntent().getIntExtra(
                    "ingredient_id", -1
            );

            String name = getIntent().getStringExtra(
                    "ingredient_name"
            );

            double quantity = getIntent().getDoubleExtra(
                    "ingredient_quantity", 0
            );

            String unit = getIntent().getStringExtra(
                    "ingredient_unit"
            );

            nameInput.setText(name);
            quantityInput.setText(String.valueOf(quantity));
            unitInput.setText(unit);

            saveButton.setText("Update Ingredient");

            TextView heading = findViewById(R.id.txtFormHeading);
            if (heading != null) {
                heading.setText("Edit Ingredient");
            }
        }

        saveButton.setOnClickListener(view -> saveIngredient());
    }

    private void saveIngredient() {
        String name = nameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();
        String unit = unitInput.getText().toString().trim();

        if (name.isEmpty() || quantityText.isEmpty() || unit.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            Toast.makeText(
                    this,
                    "Please enter a valid quantity",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (quantity <= 0) {
            Toast.makeText(
                    this,
                    "Quantity must be greater than zero",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        boolean saved;

        if (isEditing) {
            saved = databaseHelper.updateIngredient(
                    ingredientId, name, quantity, unit
            );
        } else {
            saved = databaseHelper.addIngredient(
                    name, quantity, unit
            );
        }

        if (saved) {
            String message = isEditing
                    ? "Ingredient updated successfully"
                    : "Ingredient added successfully";

            Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        } else {
            Toast.makeText(
                    this,
                    "Could not save ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
