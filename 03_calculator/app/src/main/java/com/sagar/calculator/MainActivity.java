package com.sagar.calculator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class MainActivity extends AppCompatActivity {
    TextView query, result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        query = findViewById(R.id.input);
        result = findViewById(R.id.answer);

        int[] numberIds = {R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine};
        for (int id : numberIds) findViewById(id).setOnClickListener(v -> { query.append(((Button) v).getText()); getResult(); });

        findViewById(R.id.decimal).setOnClickListener(v -> {
            String text = query.getText().toString();
            if (!text.matches(".*\\.\\d*$")) query.append(".");
            getResult();
        });

        findViewById(R.id.negation).setOnClickListener(v -> {
            if (!query.getText().toString().isEmpty())
                query.setText(String.valueOf(-Float.parseFloat(result.getText().toString())));
            getResult();
        });

        View.OnClickListener opListener = v -> {
            String text = query.getText().toString();
            if (!text.isEmpty() && "+-X/%".indexOf(text.charAt(text.length() - 1)) == -1) {
                query.append(((Button) v).getText());
                getResult();
            }
        };

        int[] ops = {R.id.addition, R.id.subtraction, R.id.multiplication, R.id.division, R.id.percentage};
        for (int id : ops) findViewById(id).setOnClickListener(opListener);

        findViewById(R.id.clear).setOnClickListener(v -> { query.setText(""); result.setText(""); });

        findViewById(R.id.del).setOnClickListener(v -> {
            String text = query.getText().toString();
            if (!text.isEmpty()) query.setText(text.substring(0, text.length() - 1));
            getResult();
        });

        findViewById(R.id.equals).setOnClickListener(v -> {
            getResult();
            query.setText(result.getText());
        });
    }

    public void getResult() {
        String expr = query.getText().toString().replace("X", "*");
        try {
            double res = new net.objecthunter.exp4j.ExpressionBuilder(expr).build().evaluate();
            result.setText(res == (int) res ? String.valueOf((int) res) : String.valueOf(res));
        } catch (Exception e) {
            Log.e("CALC_ERROR", e.getMessage());
            result.setText("Error");
        }
    }
}















