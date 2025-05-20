package com.example.myapplication.activitys.activitys;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityAnalisisBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class AnalisisActivity extends AppCompatActivity {

    String[] meses = new String[]{"Ene", "Feb", "Mar", "Abr", "May"};
    private ActivityAnalisisBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnalisisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvTotalVentas.setText("$12,450");
        binding.tvGanancias.setText("$4,230");
        binding.tvProductosVendidos.setText("387");

        setupBarChart();
    }

    private void setupBarChart() {
        BarChart chart = binding.barChart;

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 800));
        entries.add(new BarEntry(1, 1200));
        entries.add(new BarEntry(2, 900));
        entries.add(new BarEntry(3, 1500));
        entries.add(new BarEntry(4, 1700));

        BarDataSet dataSet = new BarDataSet(entries, "Ventas por mes");
        dataSet.setColors(new int[]{R.color.purple_500}, this);
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);
        chart.setData(data);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < meses.length) {
                    return meses[index];
                } else {
                    return "";
                }
            }
        });
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.setFitBars(true);
        chart.getDescription().setEnabled(false);
        chart.invalidate();
    }
}
