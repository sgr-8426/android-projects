package com.sagar.roadsim;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    private TextView scoreTextView;
    private TextView highScoreTextView;
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "RoadSimPrefs";
    private static final String HIGH_SCORE_KEY = "highScore";
    private View gameOverLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make the activity fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        gameView = findViewById(R.id.gameView);
        scoreTextView = findViewById(R.id.scoreTextView);
        highScoreTextView = findViewById(R.id.highScoreTextView);
        gameOverLayout = findViewById(R.id.gameOverLayout);
        ImageButton leftButton = findViewById(R.id.leftButton);
        ImageButton rightButton = findViewById(R.id.rightButton);
        Button restartButton = findViewById(R.id.restartButton);

        leftButton.setOnClickListener(v -> gameView.movePlayerLeft());
        rightButton.setOnClickListener(v -> gameView.movePlayerRight());
        restartButton.setOnClickListener(v -> restartGame());

        int highScore = preferences.getInt(HIGH_SCORE_KEY, 0);
        highScoreTextView.setText("High Score: " + highScore);

        gameView.setScoreUpdateListener(score -> {
            scoreTextView.setText("Score: " + score);
            checkAndUpdateHighScore(score);
        });

        gameView.setGameOverListener(finalScore -> {
            gameOverLayout.setVisibility(View.VISIBLE);
            checkAndUpdateHighScore(finalScore);
        });
    }

    private void restartGame() {
        gameOverLayout.setVisibility(View.GONE);
        gameView.restartGame();
    }

    private void checkAndUpdateHighScore(int currentScore) {
        int highScore = preferences.getInt(HIGH_SCORE_KEY, 0);
        if (currentScore > highScore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(HIGH_SCORE_KEY, currentScore);
            editor.apply();
            highScoreTextView.setText("High Score: " + currentScore);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resumeGame();
    }
}