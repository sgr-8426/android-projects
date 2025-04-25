package com.sagar.animationball;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.view.View;

public class BallView extends View {
    private Paint paint;
    private float angle = 0;
    private float centerX, centerY, radiusOrbit = 300;
    private int currentPhase = 0;
    private int[] sizes = {60, 120, 180}; // Radius: 2x, 4x, 6x (scaled)
    private int[] colors = {Color.RED, Color.BLUE, Color.GREEN};
    private long interval = 1000 / 60; // 60 FPS
    private Handler handler = new Handler();

    public BallView(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
        startRotation();
    }

    private void startRotation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                angle += 2;
                if (angle >= 360) angle = 0;
                invalidate();
                handler.postDelayed(this, interval);
            }
        }, interval);

        // Change phase every 1 minute
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentPhase = (currentPhase + 1) % sizes.length;
                handler.postDelayed(this, 60000);
            }
        }, 60000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        centerX = getWidth() / 2f;
        centerY = getHeight() / 2f;

        float x = (float) (centerX + radiusOrbit * Math.cos(Math.toRadians(angle)));
        float y = (float) (centerY + radiusOrbit * Math.sin(Math.toRadians(angle)));

        paint.setColor(colors[currentPhase]);
        canvas.drawCircle(x, y, sizes[currentPhase], paint);
    }
}
