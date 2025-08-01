package com.example.bottomnavactivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.github.mikephil.charting.utils.Transformer;

public class MyXAxisRenderer extends XAxisRenderer {

    private final String[] meses;
    private int selectedIndex = -1;

    public MyXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans, String[] meses) {
        super(viewPortHandler, xAxis, trans);
        this.meses = meses;
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
    }

    @Override
    protected void drawLabel(Canvas c, String label, float x, float y, MPPointF anchor, float angleDegrees) {
        if (selectedIndex >= 0 && selectedIndex < meses.length && label.equals(meses[selectedIndex])) {
            // Fondo azul detrás del texto seleccionado
            Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bgPaint.setColor(Color.parseColor("#007AFF"));
            bgPaint.setStyle(Paint.Style.FILL);
            bgPaint.setShadowLayer(6f, 0f, 2f, Color.parseColor("#55000000")); // Sombra suave

            float padding = 16f;
            float textWidth = mAxisLabelPaint.measureText(label);
            Paint.FontMetrics fm = mAxisLabelPaint.getFontMetrics();

            float textHeight = fm.descent - fm.ascent;

            //  Ajuste visual para subir o bajar el texto
            float textoOffset = -2f;

            //  Ajuste visual para bajar el fondo sin afectar el texto
            float fondoOffset = 22f;

            // Posición del texto ajustada
            float textY = y - fm.descent + (textHeight / 2f) + textoOffset;

            // Cálculo del fondo ajustado independientemente del texto
            float rectLeft = x - textWidth / 2f - padding / 2f;
            float rectRight = x + textWidth / 2f + padding / 2f;
            float rectTop = textY + fm.ascent - padding / 2f + fondoOffset;
            float rectBottom = textY + fm.descent + padding / 2f + fondoOffset;

            c.drawRoundRect(rectLeft, rectTop, rectRight, rectBottom, 12f, 12f, bgPaint);

            mAxisLabelPaint.setColor(Color.WHITE);
            mAxisLabelPaint.setFakeBoldText(true);

            Utils.drawXAxisValue(c, label, x, textY, mAxisLabelPaint, anchor, angleDegrees);
        } else {
            mAxisLabelPaint.setColor(Color.DKGRAY);
            mAxisLabelPaint.setFakeBoldText(false);

            Utils.drawXAxisValue(c, label, x, y, mAxisLabelPaint, anchor, angleDegrees);
        }
    }
}
