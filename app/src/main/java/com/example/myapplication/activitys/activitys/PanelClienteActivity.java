package com.example.myapplication.activitys.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;

public class PanelClienteActivity extends AppCompatActivity {

    TextView tvPromociones, tvNegocios;
    MaterialButton btnIrChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_cliente);

        tvPromociones = findViewById(R.id.tvPromociones);
        tvNegocios = findViewById(R.id.tvNegocios);
        btnIrChat = findViewById(R.id.btnIrChat);

        // Puedes cargar promociones dinámicamente desde Firebase o un ArrayList
        tvPromociones.setText("• Descuento 2x1 en café\n• 15% en panadería artesanal");

        // Puedes usar Google Maps API o base de datos local para negocios cercanos
        tvNegocios.setText("• Café Juanito - a 300m\n• Panadería El Trigo - a 500m");

        btnIrChat.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChatAtencionClienteActivity.class);
            startActivity(intent);
        });
    }
}
