package com.phyohtet.pointertouchdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawingView extends SurfaceView {

    public enum Direction {
        TOP, RIGHT, BOTTOM, LEFT
    }

    public interface OnFinalActionListener {
        void onAction(Direction direction);
    }

    private static final int RADIUS = 300;
    private static final int THRESHOLD = 180;

    private final SurfaceHolder surfaceHolder;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float touchX;
    private float touchY;

    private boolean nextControl;
    private boolean triggerFinalAction;

    private OnFinalActionListener onFinalActionListener;

    public DrawingView(Context context) {
        super(context);
        this.surfaceHolder = getHolder();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        setZOrderOnTop(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                drawControl(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();

                //Log.d("TAG", "X : " + x);
                //Log.d("TAG", "Y : " + y);

                checkMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                clearCanvas();
                break;
        }

        return true;
    }

    private void checkMove(float x, float y) {
        float tXP = touchX + THRESHOLD;
        float tXN = touchX - THRESHOLD;
        float tYP = touchY + THRESHOLD;
        float tYN = touchY - THRESHOLD;

        if (nextControl) {
            determineDirection(x, y);
        } else {
            if (((x > touchX) && (x >= tXP)) || ((y > touchY) && (y >= tYP))) {
                //Toast.makeText(getContext(), "Triggered Next", Toast.LENGTH_SHORT).show();
                clearCanvas();
                nextControl = true;
                drawControl(x, y);
            } else if ((x < touchX) && (x <= tXN) || ((y < touchY) && (y <= tYN))) {
                //Toast.makeText(getContext(), "Triggered Next", Toast.LENGTH_SHORT).show();
                clearCanvas();
                nextControl = true;
                drawControl(x, y);
            }
        }
    }

    private void determineDirection(float x, float y) {
        float tXP = touchX + THRESHOLD;
        float tXN = touchX - THRESHOLD;
        float tYP = touchY + THRESHOLD;
        float tYN = touchY - THRESHOLD;

        if (onFinalActionListener != null && !triggerFinalAction) {
            if (y <= tYN) {
                triggerFinalAction = true;
                onFinalActionListener.onAction(Direction.TOP);
            } else if (x >= tXP) {
                triggerFinalAction = true;
                onFinalActionListener.onAction(Direction.RIGHT);
            } else if (y >= tYP) {
                triggerFinalAction = true;
                onFinalActionListener.onAction(Direction.BOTTOM);
            } else if (x <= tXN) {
                triggerFinalAction = true;
                onFinalActionListener.onAction(Direction.LEFT);
            }
        }
    }

    private void drawControl(float x, float y) {
        touchX = x;
        touchY = y;
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            canvas.drawCircle(x, y, RADIUS, paint);
            if (nextControl) {
                float half = RADIUS / 1.4f;
                canvas.drawLine(x - half, y - half, x + half, y + half, paint);
                canvas.drawLine(x - half, y + half, x + half, y - half, paint);
            } else {
                canvas.drawLine(x - RADIUS, y, x + RADIUS, y, paint);
                canvas.drawLine(x, y - RADIUS, x, y + RADIUS, paint);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void clearCanvas() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
        touchX = 0;
        touchY = 0;
        nextControl = false;
        triggerFinalAction = false;
    }

    public void setOnFinalActionListener(OnFinalActionListener onFinalActionListener) {
        this.onFinalActionListener = onFinalActionListener;
    }

    public void setTriggerFinalAction(boolean triggerFinalAction) {
        this.triggerFinalAction = triggerFinalAction;
    }
}
