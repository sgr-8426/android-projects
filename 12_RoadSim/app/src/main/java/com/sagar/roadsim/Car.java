package com.sagar.roadsim;

import android.graphics.*;

public class Car {
    private float x;
    private float y;
    private float width;
    private float height;
    private float speed;
    private Paint paint;
    private RectF rect;
    private boolean isPlayer;

    public Car(float x, float y, float width, float height, int color, boolean isPlayer) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isPlayer = isPlayer;
        
        paint = new Paint();
        paint.setColor(color);
        rect = new RectF();
        updateRect();
        
        // Set initial speed (enemies move downward)
        speed = isPlayer ? 0 : 5;
    }

    private void updateRect() {
        rect.left = x - width/2;
        rect.right = x + width/2;
        rect.top = y - height/2;
        rect.bottom = y + height/2;
    }

    public void update() {
        if (!isPlayer) {
            y += speed;
        }
        updateRect();
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(rect, paint);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateRect();
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public RectF getRect() { return rect; }
    public boolean isPlayer() { return isPlayer; }

    public boolean collidesWith(Car other) {
        return RectF.intersects(this.rect, other.getRect());
    }
}