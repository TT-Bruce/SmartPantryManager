Smart Pantry Manager

Description:
Smart Pantry Manager is an Android application developed using Java.
It helps users manage ingredients stored in their pantry and suggests recipes that can be prepared using the ingredients they have available.

Features:
Add ingredients to the pantry.
View a list of pantry ingredients.
Edit ingredient names, quantities, and units.
Delete ingredients with confirmation.
Store pantry data so it remains available after closing the application.
Suggest recipes only when all required ingredients and quantities are available.
View recipe ingredients and cooking instructions.
Access the Settings screen.
Validate ingredient input to prevent empty fields.

Technology Used:
Programming language: Java
Development environment: Android Studio
Database: SQLite
User interface: Android XML layouts
Version control: Git and GitHub

Database Choice:
SQLite was selected because it is built into Android and allows the application to store and retrieve data locally without requiring an internet connection or an external database server.
It is suitable for storing pantry ingredients and recipe information.

How to Run the Application:
Install Android Studio.
Clone or download this repository.
Open the project in Android Studio.
Allow Gradle to sync.
Run the application on an Android emulator or compatible Android device.

Recipe Matching:
The application checks whether every ingredient required by a recipe exists in the pantry in a sufficient quantity.
Recipes are suggested only when all requirements are met.

Project Author:
Developed as part of the Mobile Application Development 700 assignment.

Application Navigation:
The application includes the following screens:

1. Pantry List: Displays stored ingredients and provides options to add, edit, or delete them.
2. Add/Edit Ingredient: Allows users to enter ingredient names, quantities, and units.
3. Suggested Recipes: Displays recipes only when all required ingredients and quantities are available in the pantry.
4. Recipe Detail: Shows the ingredients and cooking instructions for a selected recipe.
5. Settings: Provides access to the application's settings screen.

Testing:
The Smart Pantry Manager application was tested to ensure that its main features work correctly.

Add ingredients: Ingredients can be added to the pantry with a name, quantity and unit.
Input validation: The app displays an error when required fields are empty or the quantity is zero.
Edit and delete: Existing pantry ingredients can be edited or deleted.
Database persistence: Pantry ingredients remain saved after closing and reopening the application.
Strict recipe matching: Recipes are suggested only when all required ingredients are available in sufficient quantities.
Insufficient ingredients: Vegetable Rice disappears from Suggested Recipes when the available Rice quantity is reduced below the required amount.
Recipe details: Users can open a suggested recipe to view its ingredients and cooking method.
Navigation: Users can access Pantry, Suggested Recipes and Settings through the application menu.
