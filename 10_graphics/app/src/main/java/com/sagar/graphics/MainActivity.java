package com.sagar.graphics;

import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the custom view that handles drawing
        setContentView(new CustomView(this));
    }
}
