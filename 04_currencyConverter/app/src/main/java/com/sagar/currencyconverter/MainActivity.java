package com.sagar.currencyconverter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Spinner from, to;
    TextView converted;
    EditText amount;
    Map<String, Map<String, Double>> conversionRates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
        converted = findViewById(R.id.converted);
        amount = findViewById(R.id.amount);

        String[] currency = {"INR", "USD", "JPY", "EUR", "GBP"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currency);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        from.setAdapter(adapter);
        to.setAdapter(adapter);

        setupConversionRates();

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                convertCurrency();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                convertCurrency();
            }

            @Override
            public void afterTextChanged(Editable s) {
                convertCurrency();
            }
        });

        from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                convertCurrency();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                convertCurrency();
            }
        });
        to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                convertCurrency();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                convertCurrency();
            }
        });
    }

    private void setupConversionRates() {
        conversionRates = new HashMap<>();

        conversionRates.put("INR", Map.of("USD", 0.012, "JPY", 1.55, "EUR", 0.011, "GBP", 0.010, "INR", 1.0));
        conversionRates.put("USD", Map.of("INR", 76.2, "JPY", 113.4, "EUR", 0.92, "GBP", 0.81, "USD", 1.0));
        conversionRates.put("JPY", Map.of("INR", 0.62, "USD", 0.0079, "EUR", 0.0091, "GBP", 0.0081, "JPY", 1.0));
        conversionRates.put("EUR", Map.of("INR", 85.8, "USD", 1.08, "JPY", 131.4, "GBP", 0.85, "EUR", 1.0));
        conversionRates.put("GBP", Map.of("INR", 102.4, "USD", 1.19, "JPY", 153.6, "EUR", 1.14, "GBP", 1.0));
    }

    private void convertCurrency() {
        try {
            String fromCurrency = from.getSelectedItem().toString();
            String toCurrency = to.getSelectedItem().toString();
            double amountValue = Double.parseDouble(amount.getText().toString());
            double rate = conversionRates.getOrDefault(fromCurrency, new HashMap<>()).getOrDefault(toCurrency, 1.0);
            converted.setText(String.valueOf(amountValue * rate));
        } catch (Exception e) {
            converted.setText("");
        }
    }
}