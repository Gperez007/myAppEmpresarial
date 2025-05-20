package com.example.myapplication.activitys.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class EmpresaPanelActivity extends AppCompatActivity {

    Button btnProductos, btnPromociones, btnAnalisis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_panel);

        btnProductos = findViewById(R.id.btnProductos);
        btnPromociones = findViewById(R.id.btnPromociones);
        btnAnalisis = findViewById(R.id.btnAnalisis);

        btnProductos.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminProductosActivity.class));
        });

        btnPromociones.setOnClickListener(v -> {
            startActivity(new Intent(this, PromocionActivity.class));
        });

        btnAnalisis.setOnClickListener(v -> {
            startActivity(new Intent(this, AnalisisActivity.class));
        });
    }
}