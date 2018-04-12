package com.analyticalneko.marksmanassistant;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Nathaniel Simpson on 2/18/2018.
 */

public class DrawView extends View {

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }

    private boolean toggle = false;

    private float lastX;
    private float lastY;

    private float prevX;
    private float prevY;

    private int count = 0;

    Paint myPaint = new Paint();
    Paint myStrokePaint = new Paint();

    int step;

    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        lastX = event.getX();
        lastY = event.getY();

        invalidate();
        return super.onTouchEvent(event);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(toggle) {
            myPaint.setStyle(Paint.Style.FILL);
            myPaint.setColor(Color.RED);
            myPaint.setAntiAlias(true);
            myStrokePaint.setStyle(Paint.Style.STROKE);
            myStrokePaint.setColor(Color.BLACK);
            myStrokePaint.setStrokeWidth(3);
            myStrokePaint.setAntiAlias(true);
            if (step == 2) {
                myPaint.setColor(Color.GREEN);
                canvas.drawCircle(lastX, lastY, 30, myPaint);
                canvas.drawCircle(lastX, lastY, 32, myStrokePaint);
                prevX = lastX;
                prevY = lastY;
                count++;
            } else if(step == 3){
                myPaint.setColor(Color.GREEN);
                canvas.drawCircle(prevX, prevY, 30, myPaint);
                canvas.drawCircle(prevX, prevY, 32, myStrokePaint);
                myPaint.setColor(Color.RED);
                canvas.drawCircle(lastX, lastY, 20, myPaint);
                canvas.drawCircle(lastX, lastY, 22, myStrokePaint);
                count = 0;
            }
        } else {
            count = 0;
        }
    }

    public void setStep(int step) {
        this.step = step;
    }
}
