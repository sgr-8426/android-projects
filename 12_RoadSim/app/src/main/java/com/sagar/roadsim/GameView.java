package com.sagar.roadsim;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import java.util.*;

public class GameView extends View {
    private Car playerCar;
    private ArrayList<Car> enemyCars;
    private Paint roadPaint;
    private Paint linePaint;
    private boolean isPlaying;
    private int score;
    private float gameSpeed;
    private Random random;
    private static final int LANE_COUNT = 5;
    private float laneWidth;
    private ScoreUpdateListener scoreListener;
    private long lastEnemySpawn;
    private static final long SPAWN_DELAY = 1500; // 1.5 seconds
    private GameOverListener gameOverListener;

    public interface ScoreUpdateListener {
        void onScoreUpdate(int score);
    }

    public interface GameOverListener {
        void onGameOver(int finalScore);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        enemyCars = new ArrayList<>();
        random = new Random();
        score = 0;
        gameSpeed = 5;
        isPlaying = true;

        roadPaint = new Paint();
        roadPaint.setColor(Color.DKGRAY);

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(5);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        laneWidth = w / LANE_COUNT;
        
        // Initialize player car
        float carWidth = laneWidth * 0.8f;
        float carHeight = carWidth * 1.5f;
        playerCar = new Car(w/2, h*0.8f, carWidth, carHeight, Color.BLUE, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isPlaying) return;

        // Draw road
        canvas.drawRect(0, 0, getWidth(), getHeight(), roadPaint);

        // Draw lane lines
        for (int i = 1; i < LANE_COUNT; i++) {
            float x = i * laneWidth;
            canvas.drawLine(x, 0, x, getHeight(), linePaint);
        }

        // Update and draw player
        playerCar.draw(canvas);

        // Spawn new enemy cars
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEnemySpawn > SPAWN_DELAY) {
            spawnEnemyCar();
            lastEnemySpawn = currentTime;
        }

        // Update and draw enemy cars
        Iterator<Car> iterator = enemyCars.iterator();
        while (iterator.hasNext()) {
            Car enemyCar = iterator.next();
            enemyCar.update();
            enemyCar.draw(canvas);

            // Check collision
            if (playerCar.collidesWith(enemyCar)) {
                gameOver();
                return;
            }

            // Remove cars that are off screen and increase score
            if (enemyCar.getY() > getHeight()) {
                iterator.remove();
                incrementScore();
            }
        }

        // Continue game loop
        invalidate();
    }

    private void spawnEnemyCar() {
        int lane = random.nextInt(LANE_COUNT);
        float x = lane * laneWidth + laneWidth/2;
        float carWidth = laneWidth * 0.8f;
        float carHeight = carWidth * 1.5f;
        
        Car enemyCar = new Car(x, -carHeight, carWidth, carHeight, Color.RED, false);
        enemyCar.setSpeed(gameSpeed);
        enemyCars.add(enemyCar);
    }

    private void incrementScore() {
        score++;
        if (scoreListener != null) {
            scoreListener.onScoreUpdate(score);
        }
        // Increase game speed every 5 points
        if (score % 5 == 0) {
            gameSpeed += 0.5f;
        }
    }

    public void movePlayerLeft() {
        if (!isPlaying) return;
        float newX = playerCar.getX() - laneWidth;
        if (newX >= laneWidth/2) {
            playerCar.setPosition(newX, playerCar.getY());
        }
    }

    public void movePlayerRight() {
        if (!isPlaying) return;
        float newX = playerCar.getX() + laneWidth;
        if (newX <= getWidth() - laneWidth/2) {
            playerCar.setPosition(newX, playerCar.getY());
        }
    }

    public void setScoreUpdateListener(ScoreUpdateListener listener) {
        this.scoreListener = listener;
    }

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    public void pauseGame() {
        isPlaying = false;
    }

    public void resumeGame() {
        isPlaying = true;
        invalidate();
    }

    public void restartGame() {
        enemyCars.clear();
        score = 0;
        gameSpeed = 5;
        isPlaying = true;
        if (scoreListener != null) {
            scoreListener.onScoreUpdate(score);
        }
        invalidate();
    }

    private void gameOver() {
        isPlaying = false;
        if (gameOverListener != null) {
            gameOverListener.onGameOver(score);
        }
    }
}