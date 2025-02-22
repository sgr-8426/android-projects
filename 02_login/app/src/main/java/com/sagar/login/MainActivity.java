package com.sagar.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button logIn = findViewById(R.id.btnLogin),
                cancel = findViewById(R.id.btnCancel);

        EditText usr = findViewById(R.id.username),
                ps = findViewById(R.id.password);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usr.getText().toString().trim().equals("sugarcane") && ps.getText().toString().trim().equals("ganna")){
                    Intent welcome = new Intent(MainActivity.this, Welcome.class);
                    startActivity(welcome);
                }else{
                    Toast.makeText(MainActivity.this, "Incorrect username or password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ps.setText("");
                usr.setText("");
            }
        });



    }
}