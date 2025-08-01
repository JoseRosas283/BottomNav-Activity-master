package com.example.bottomnavactivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class MonthMarkerView extends MarkerView {

    private final Paint outerPaint; // 🔵 Azul exterior
    private final Paint innerPaint; // ⚪ Blanco interior

    public MonthMarkerView(Context context, int layoutResource, String[] meses) {
        super(context, layoutResource);

        // Pintura para el círculo azul
        outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerPaint.setColor(0xFF007AFF); // Azul
        outerPaint.setStyle(Paint.Style.FILL);

        // Pintura para el centro blanco
        innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerPaint.setColor(0xFFFFFFFF); // Blanco
        innerPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // Ya no mostramos texto flotante
        // Dejar vacío para evitar layout
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        // 🔵 Dibuja círculo azul sobre el punto
        canvas.drawCircle(posX, posY, 33f, outerPaint);

        // ⚪ Dibuja centro blanco
        canvas.drawCircle(posX, posY, 14f, innerPaint);
    }

    @Override
    public MPPointF getOffset() {
        // Centra el dibujo sobre el punto
        return new MPPointF(-14f, -14f);
    }
}
