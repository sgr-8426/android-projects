package com.sagar.graphics;

import android.content.Context;
import android.graphics.*;
import android.view.View;

public class CustomView extends View {
    private Paint paint;

    public CustomView(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Background color
        canvas.drawColor(Color.WHITE);

        // Draw a line
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        canvas.drawLine(100, 100, 400, 100, paint);

        // Draw a rectangle
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        canvas.drawRect(100, 150, 400, 300, paint);

        // Draw a filled circle
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(250, 450, 80, paint);

        // Draw text
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        canvas.drawText("Hello Graphics!", 100, 600, paint);
    }
}
