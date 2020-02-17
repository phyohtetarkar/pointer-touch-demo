package com.phyohtet.pointertouchdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class DrawingView extends SurfaceView {

    public enum Direction {
        TOP, RIGHT, BOTTOM, LEFT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
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

    private Direction direction;

    private Vibrator vibrator;

    public void setOnFinalActionListener(OnFinalActionListener onFinalActionListener) {
        this.onFinalActionListener = onFinalActionListener;
    }

    public void setTriggerFinalAction(boolean triggerFinalAction) {
        this.triggerFinalAction = triggerFinalAction;
    }

    public DrawingView(Context context) {
        super(context);
        this.surfaceHolder = getHolder();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        setZOrderOnTop(true);

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
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
            /*if (((x > touchX) && (x >= tXP)) || ((y > touchY) && (y >= tYP))) {
                //Toast.makeText(getContext(), "Triggered Next", Toast.LENGTH_SHORT).show();
                clearCanvas();
                nextControl = true;
                drawControl(x, y);
            } else if ((x < touchX) && (x <= tXN) || ((y < touchY) && (y <= tYN))) {
                //Toast.makeText(getContext(), "Triggered Next", Toast.LENGTH_SHORT).show();
                clearCanvas();
                nextControl = true;
                drawControl(x, y);
            }*/

            if (((x < touchX) && (x <= tXN)) && ((y < touchY) && (y <= tYN))) {
                direction = Direction.TOP_LEFT;
                drawNextControl(x, y);
            } else if (((x > touchX) && (x >= tXP)) && ((y < touchY) && (y <= tYN))) {
                direction = Direction.TOP_RIGHT;
                drawNextControl(x, y);
            } else if((x < touchX) && (x <= tXN) && ((y > touchY) && (y >= tYP))) {
                direction = Direction.BOTTOM_LEFT;
                drawNextControl(x, y);
            } else if (((x > touchX) && (x >= tXP)) && ((y > touchY) && (y >= tYP))) {
                direction = Direction.BOTTOM_RIGHT;
                drawNextControl(x, y);
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
                vibrate();
                triggerFinalAction = true;
                onFinalActionListener.onAction(Direction.TOP);
            } else if (x >= tXP) {
                vibrate();
                triggerFinalAction = true;
                onFinalActionListener.onAction(Direction.RIGHT);
            } else if (y >= tYP) {
                vibrate();
                triggerFinalAction = true;
                onFinalActionListener.onAction(Direction.BOTTOM);
            } else if (x <= tXN) {
                vibrate();
                triggerFinalAction = true;
                onFinalActionListener.onAction(Direction.LEFT);
            }
        }
    }

    private void drawNextControl(float x, float y) {
        clearCanvas();
        nextControl = true;
        drawControl(x, y);

        vibrate();
    }

    private void drawControl(float x, float y) {
        touchX = x;
        touchY = y;
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            canvas.drawCircle(x, y, RADIUS, paint);
            if (nextControl) {

                if (direction != null) {
                    Toast.makeText(getContext(), direction.name(), Toast.LENGTH_SHORT).show();

                    // TODO implement next remote control by using Direction
                }

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

    private void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
    }

}
