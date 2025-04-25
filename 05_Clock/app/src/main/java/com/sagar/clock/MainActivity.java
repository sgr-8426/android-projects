package com.sagar.clock;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView timer, ms;
    private Button startStop, reset;
    private Handler handler = new Handler();
    private long startTime = 0L, elapsedTime = 0L;
    private boolean isRunning = false;

    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            long timeDiff = System.currentTimeMillis() - startTime + elapsedTime;
            int millis = (int) (timeDiff % 1000);  // Milliseconds (3 digits)
            int seconds = (int) (timeDiff / 1000) % 60; // Seconds
            int minutes = (int) (timeDiff / 1000) / 60; // Minutes

            timer.setText(String.format("%02d:%02d", minutes, seconds));
            ms.setText(String.format("%03d ms", millis));

            if (isRunning) {
                handler.postDelayed(this, 1); // Update every 10ms
            }
        }
    };

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

        timer = findViewById(R.id.timer);
        ms = findViewById(R.id.ms);
        startStop = findViewById(R.id.startStop);
        reset = findViewById(R.id.reset);

        startStop.setOnClickListener(v -> {
            if (isRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        reset.setOnClickListener(v -> resetTimer());
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        isRunning = true;
        handler.post(updateTimer);
        startStop.setText("Pause");
    }

    private void pauseTimer() {
        elapsedTime += System.currentTimeMillis() - startTime;
        isRunning = false;
        handler.removeCallbacks(updateTimer);
        startStop.setText("Start");
    }

    private void resetTimer() {
        isRunning = false;
        handler.removeCallbacks(updateTimer);
        elapsedTime = 0L;
        timer.setText("00:00");
        ms.setText("000 ms");
        startStop.setText("Start");
    }
}