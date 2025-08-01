package com.example.bottomnavactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import androidx.core.content.ContextCompat;


import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home) {
                return true;
            } else if (itemId == R.id.bottom_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_reloj) {
                startActivity(new Intent(getApplicationContext(), RelojActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;

            }

            return false;
        });
        LineChart lineChart = findViewById(R.id.lineChart);

// 1. Datos de enero a diciembre
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 5000)); // Ene
        entries.add(new Entry(1, 3000)); // Feb
        entries.add(new Entry(2, 4500)); // Mar
        entries.add(new Entry(3, 8545)); // Abr
        entries.add(new Entry(4, 6000)); // May
        entries.add(new Entry(5, 7000)); // Jun
        entries.add(new Entry(6, 6200)); // Jul
        entries.add(new Entry(7, 7200)); // Ago
        entries.add(new Entry(8, 6800)); // Sep
        entries.add(new Entry(9, 5000)); // Oct
        entries.add(new Entry(10, 3000)); // Nov
        entries.add(new Entry(11, 4500)); // Dic

// 2. Configuración del DataSet
        LineDataSet dataSet = new LineDataSet(entries, "Ventas mensuales");
        dataSet.setColor(Color.parseColor("#007AFF"));
        dataSet.setCircleColor(Color.parseColor("#007AFF"));
        dataSet.setLineWidth(7f);
        dataSet.setCircleRadius(6f);
        dataSet.setValueTextSize(12f);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false); //  Oculta todos los puntos
        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(ContextCompat.getDrawable(this, R.drawable.fade_fill));
        dataSet.setFillAlpha(100);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawHorizontalHighlightIndicator(false);
        dataSet.setDrawVerticalHighlightIndicator(false);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                return "$" + String.format(Locale.US, "%,.0f", entry.getY());
            }
        });

// 3. Asignar datos al gráfico
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getLegend().setEnabled(false);
        lineChart.setDescription(null);

// 4. Etiquetas personalizadas para el eje X
        String[] meses = new String[]{"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(meses));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);//aca
        xAxis.setGridColor(Color.parseColor("#F5F5F5")); // Color suave
        xAxis.setGridLineWidth(1f);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setLabelRotationAngle(0f);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(entries.size() - 1);

// ACTIVACIÓN del renderer personalizado
        MyXAxisRenderer rendererPersonalizado = new MyXAxisRenderer(
                lineChart.getViewPortHandler(),
                xAxis,
                lineChart.getTransformer(YAxis.AxisDependency.LEFT),
                meses
        );
        rendererPersonalizado.setSelectedIndex(3); // Ej. Abril como seleccionado
        lineChart.setXAxisRenderer(rendererPersonalizado);

// 5. Eje Y izquierdo
        lineChart.getAxisLeft().setEnabled(false);

// 6. Estilo general
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.setScaleEnabled(false);
        lineChart.setExtraBottomOffset(10f);
        lineChart.animateY(1000);

// 7. Scroll horizontal: muestra solo Ene-Jun al inicio
        lineChart.setVisibleXRangeMaximum(6f);
        lineChart.setDragEnabled(true);
        lineChart.moveViewToX(0);
        lineChart.moveViewToAnimated(0, 0, YAxis.AxisDependency.LEFT, 1000L);

// 8. Activar el MarkerView personalizado
        MonthMarkerView marker = new MonthMarkerView(this, R.layout.marker_month, meses);
        marker.setChartView(lineChart);
        lineChart.setMarker(marker);

// 9. Mostrar ventas en el TextView al tocar un punto
        TextView tvResultado = findViewById(R.id.tvResultado);
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX();
                float valor = e.getY();
                rendererPersonalizado.setSelectedIndex(index);
                lineChart.invalidate();
                String texto = " $" + String.format(Locale.US, "%,.0f", valor);
                tvResultado.setText(texto);
            }

            @Override
            public void onNothingSelected() {
                tvResultado.setText("");
            }
        });
    }
}