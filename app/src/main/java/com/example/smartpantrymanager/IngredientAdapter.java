
package com.example.smartpantrymanager;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class IngredientAdapter extends ArrayAdapter<Ingredient> {

    public IngredientAdapter(
            Activity context,
            List<Ingredient> ingredients) {

        super(context, 0, ingredients);
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(
                            R.layout.item_ingredient,
                            parent,
                            false
                    );
        }

        Ingredient ingredient = getItem(position);

        TextView ingredientText =
                convertView.findViewById(R.id.txtIngredientItem);

        if (ingredient != null) {
            ingredientText.setText(
                    ingredient.getName()
                            + " — "
                            + ingredient.getQuantity()
                            + " "
                            + ingredient.getUnit()
                            + "\nTap to edit or delete"
            );
        }

        // Allow the ListView to handle taps on each row.
        convertView.setClickable(false);
        ingredientText.setClickable(false);
        ingredientText.setFocusable(false);

        return convertView;
    }
}
