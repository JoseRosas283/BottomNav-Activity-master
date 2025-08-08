package com.example.bottomnavactivity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ScannerOverlayView extends View {
    private Paint backgroundPaint;
    private Paint clearPaint;
    private Paint laserPaint;
    private RectF holeRect;

    private float laserY;
    private ValueAnimator laserAnimator;

    public ScannerOverlayView(Context context) {
        super(context);
        init();
    }

    public ScannerOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScannerOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_HARDWARE, null);

        // Pintura para la sombra
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#80000000")); // sombra negra con opacidad
        backgroundPaint.setStyle(Paint.Style.FILL);

        // Pintura para recortar el hueco
        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        clearPaint.setAntiAlias(true);

        // Pintura para la línea láser
        laserPaint = new Paint();
        laserPaint.setColor(Color.RED);
        laserPaint.setStrokeWidth(4f);
        laserPaint.setAlpha(180); // semitransparente
        laserPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Dibuja sombra completa
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        // Define hueco en el centro
        float holeWidth = 800f;
        float holeHeight = 800f;
        float left = (getWidth() - holeWidth) / 2;
        float top = (getHeight() - holeHeight) / 2;
        float right = left + holeWidth;
        float bottom = top + holeHeight;

        holeRect = new RectF(left, top, right, bottom);

        // Recorta el hueco con bordes redondeados
        canvas.drawRoundRect(holeRect, 32f, 32f, clearPaint);

        // Dibuja línea láser si está activa
        if (laserAnimator != null && laserAnimator.isRunning()) {
            canvas.drawLine(holeRect.left + 20, laserY, holeRect.right - 20, laserY, laserPaint);
        }
    }

    //
    public void startLaser() {
        if (holeRect == null || (laserAnimator != null && laserAnimator.isRunning())) return;

        laserAnimator = ValueAnimator.ofFloat(holeRect.top, holeRect.bottom);
        laserAnimator.setDuration(2000);
        laserAnimator.setRepeatCount(ValueAnimator.INFINITE);
        laserAnimator.setRepeatMode(ValueAnimator.RESTART);
        laserAnimator.addUpdateListener(animation -> {
            laserY = (float) animation.getAnimatedValue();
            invalidate();
        });
        laserAnimator.start();
    }

    //
    public void stopLaser() {
        if (laserAnimator != null) {
            laserAnimator.cancel();
            laserAnimator = null;
            invalidate(); // limpia la línea
        }
    }
}
