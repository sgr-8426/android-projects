package com.sagar.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.*;


public class MainActivity extends AppCompatActivity {

    private EditText editTextName, editTextAge;
    private TextView textViewDisplay;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MyPrefs",
            KEY_NAME = "name",
            KEY_AGE = "age";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        textViewDisplay = findViewById(R.id.textViewDisplay);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_NAME, editTextName.getText().toString());
            editor.putString(KEY_AGE, editTextAge.getText().toString());
            editor.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences != null) {
            String name = sharedPreferences.getString(KEY_NAME, "");
            String age = sharedPreferences.getString(KEY_AGE, "");
            if (editTextName != null) editTextName.setText(name);
            if (editTextAge != null) editTextAge.setText(age);
            textViewDisplay.setText("Name: " + name + "\nAge: " + age);
        }
    }
}
