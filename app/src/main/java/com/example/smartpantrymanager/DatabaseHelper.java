package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";

    private static final String TABLE_RECIPES = "recipes";
    private static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createIngredientsTable(db);
        createRecipeTables(db);
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db, int oldVersion, int newVersion) {

        // Preserve existing pantry ingredients during upgrades.
        if (oldVersion < 2) {
            createRecipeTables(db);
            seedRecipes(db);
        }
    }

    private void createIngredientsTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " +
                TABLE_INGREDIENTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_QUANTITY + " REAL NOT NULL, " +
                COLUMN_UNIT + " TEXT NOT NULL)";

        db.execSQL(sql);
    }

    private void createRecipeTables(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL UNIQUE, " +
                        "method TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "required_quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id))"
        );
    }

    // CREATE: Add a pantry ingredient.
    public boolean addIngredient(
            String name, double quantity, String unit) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);

        return db.insert(TABLE_INGREDIENTS, null, values) != -1;
    }

    // READ: Get pantry ingredients.
    public Cursor getAllIngredients() {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_INGREDIENTS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_NAME + " ASC"
        );
    }

    // UPDATE: Edit a pantry ingredient.
    public boolean updateIngredient(
            int id, String name, double quantity, String unit) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);

        int rows = getWritableDatabase().update(
                TABLE_INGREDIENTS,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return rows > 0;
    }

    // DELETE: Remove a pantry ingredient.
    public boolean deleteIngredient(int id) {
        int rows = getWritableDatabase().delete(
                TABLE_INGREDIENTS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return rows > 0;
    }

    // READ: Return only recipes with every required ingredient
    // available in at least the required quantity and matching unit.
    public Cursor getSuggestedRecipes() {
        String sql =
                "SELECT r.id, r.name, r.method " +
                        "FROM recipes r " +
                        "WHERE NOT EXISTS (" +
                        " SELECT 1 FROM recipe_ingredients ri " +
                        " WHERE ri.recipe_id = r.id " +
                        " AND NOT EXISTS (" +
                        "  SELECT 1 FROM ingredients i " +
                        "  WHERE LOWER(TRIM(i.name)) = " +
                        "        LOWER(TRIM(ri.ingredient_name)) " +
                        "  AND LOWER(TRIM(i.unit)) = LOWER(TRIM(ri.unit)) " +
                        "  AND i.quantity >= ri.required_quantity" +
                        " )" +
                        ") ORDER BY r.name ASC";

        return getReadableDatabase().rawQuery(sql, null);
    }

    // READ: Get the required ingredients for one recipe.
    public Cursor getRecipeIngredients(int recipeId) {
        return getReadableDatabase().query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                "recipe_id = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                "ingredient_name ASC"
        );
    }

    // Add the starter recipes the first time the database is created.
    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Tomato Rice",
                "1. Cook the rice until tender.\n" +
                        "2. Chop and cook the tomato with oil.\n" +
                        "3. Mix together and serve.",
                new String[][]{
                        {"Rice", "1", "cups"},
                        {"Tomato", "2", "items"},
                        {"Oil", "1", "tbsp"}
                });

        addRecipe(db, "Egg Fried Rice",
                "1. Cook the rice.\n" +
                        "2. Scramble the eggs in a pan with oil.\n" +
                        "3. Add rice and stir-fry.",
                new String[][]{
                        {"Rice", "2", "cups"},
                        {"Egg", "2", "items"},
                        {"Oil", "1", "tbsp"}
                });

        addRecipe(db, "Potato and Onion Fry",
                "1. Chop the potatoes and onion.\n" +
                        "2. Heat oil in a pan.\n" +
                        "3. Fry until the potatoes are tender.",
                new String[][]{
                        {"Potato", "2", "items"},
                        {"Onion", "1", "items"},
                        {"Oil", "1", "tbsp"}
                });

        addRecipe(db, "Cheese Omelette",
                "1. Beat the eggs.\n" +
                        "2. Cook them in a lightly oiled pan.\n" +
                        "3. Add cheese and fold the omelette.",
                new String[][]{
                        {"Egg", "2", "items"},
                        {"Cheese", "0.5", "cups"},
                        {"Oil", "1", "tbsp"}
                });

        addRecipe(db, "Garlic Pasta",
                "1. Cook the pasta.\n" +
                        "2. Cook the garlic briefly in oil.\n" +
                        "3. Toss the pasta with the garlic oil.",
                new String[][]{
                        {"Pasta", "2", "cups"},
                        {"Garlic", "2", "cloves"},
                        {"Oil", "1", "tbsp"}
                });

        addRecipe(db, "Tomato Pasta",
                "1. Cook the pasta.\n" +
                        "2. Chop and cook the tomatoes.\n" +
                        "3. Mix the sauce with the pasta.",
                new String[][]{
                        {"Pasta", "2", "cups"},
                        {"Tomato", "2", "items"},
                        {"Oil", "1", "tbsp"}
                });

        addRecipe(db, "Chicken and Rice",
                "1. Cook the chicken thoroughly.\n" +
                        "2. Cook the rice separately.\n" +
                        "3. Serve the chicken with the rice.",
                new String[][]{
                        {"Chicken", "2", "pieces"},
                        {"Rice", "2", "cups"},
                        {"Oil", "1", "tbsp"}
                });

        addRecipe(db, "Bean and Tomato Stew",
                "1. Cook the beans until tender.\n" +
                        "2. Cook chopped tomatoes and onion in oil.\n" +
                        "3. Add beans and simmer.",
                new String[][]{
                        {"Beans", "1", "cups"},
                        {"Tomato", "2", "items"},
                        {"Onion", "1", "items"},
                        {"Oil", "1", "tbsp"}
                });

        addRecipe(db, "Garlic Bread",
                "1. Crush the garlic.\n" +
                        "2. Mix garlic with oil and spread on bread.\n" +
                        "3. Toast until golden.",
                new String[][]{
                        {"Bread", "2", "slices"},
                        {"Garlic", "1", "cloves"},
                        {"Oil", "1", "tbsp"}
                });

        addRecipe(db, "Pancakes",
                "1. Mix flour, milk and egg into a batter.\n" +
                        "2. Heat a lightly oiled pan.\n" +
                        "3. Cook both sides until golden.",
                new String[][]{
                        {"Flour", "1", "cups"},
                        {"Milk", "1", "cups"},
                        {"Egg", "1", "items"},
                        {"Oil", "1", "tbsp"}
                });

        addRecipe(db, "Mashed Potatoes",
                "1. Boil the potatoes until soft.\n" +
                        "2. Drain and mash them.\n" +
                        "3. Stir in the milk.",
                new String[][]{
                        {"Potato", "3", "items"},
                        {"Milk", "0.5", "cups"}
                });

        addRecipe(db, "Chicken Pasta",
                "1. Cook the chicken thoroughly.\n" +
                        "2. Boil the pasta.\n" +
                        "3. Mix and serve.",
                new String[][]{
                        {"Chicken", "2", "pieces"},
                        {"Pasta", "2", "cups"},
                        {"Oil", "1", "tbsp"}
                });

        addRecipe(db, "Tomato and Cheese Toast",
                "1. Place sliced tomato on bread.\n" +
                        "2. Add cheese.\n" +
                        "3. Toast until the cheese melts.",
                new String[][]{
                        {"Bread", "2", "slices"},
                        {"Tomato", "1", "items"},
                        {"Cheese", "0.5", "cups"}
                });

        addRecipe(db, "Onion Omelette",
                "1. Beat the eggs.\n" +
                        "2. Add chopped onion.\n" +
                        "3. Cook in an oiled pan until set.",
                new String[][]{
                        {"Egg", "2", "items"},
                        {"Onion", "1", "items"},
                        {"Oil", "1", "tbsp"}
                });

        addRecipe(db, "Bean Rice Bowl",
                "1. Cook the rice.\n" +
                        "2. Heat the beans with chopped tomato.\n" +
                        "3. Serve the beans over the rice.",
                new String[][]{
                        {"Beans", "1", "cups"},
                        {"Rice", "1", "cups"},
                        {"Tomato", "1", "items"}
                });
    }

    private void addRecipe(
            SQLiteDatabase db,
            String name,
            String method,
            String[][] requiredIngredients) {

        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("method", method);

        db.insertWithOnConflict(
                TABLE_RECIPES,
                null,
                recipeValues,
                SQLiteDatabase.CONFLICT_IGNORE
        );

        Cursor cursor = db.query(
                TABLE_RECIPES,
                new String[]{"id"},
                "name = ?",
                new String[]{name},
                null,
                null,
                null
        );

        int recipeId;

        try {
            if (!cursor.moveToFirst()) {
                return;
            }
            recipeId = cursor.getInt(0);
        } finally {
            cursor.close();
        }

        Cursor existing = db.query(
                TABLE_RECIPE_INGREDIENTS,
                new String[]{"id"},
                "recipe_id = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                null
        );

        try {
            if (existing.getCount() > 0) {
                return;
            }
        } finally {
            existing.close();
        }

        for (String[] ingredient : requiredIngredients) {
            ContentValues values = new ContentValues();
            values.put("recipe_id", recipeId);
            values.put("ingredient_name", ingredient[0]);
            values.put("required_quantity",
                    Double.parseDouble(ingredient[1]));
            values.put("unit", ingredient[2]);

            db.insert(TABLE_RECIPE_INGREDIENTS, null, values);
        }
    }
}
