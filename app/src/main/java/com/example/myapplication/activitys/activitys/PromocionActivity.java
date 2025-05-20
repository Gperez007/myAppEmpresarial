package com.example.myapplication.activitys.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityPromocionBinding;

public class PromocionActivity extends AppCompatActivity {

    private ActivityPromocionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPromocionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnGuardarPromocion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulo = binding.etTituloPromo.getText().toString().trim();
                String descripcion = binding.etDescripcionPromo.getText().toString().trim();
                String fecha = binding.etFechaPromo.getText().toString().trim();

                if (titulo.isEmpty() || descripcion.isEmpty() || fecha.isEmpty()) {
                    Toast.makeText(PromocionActivity.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Aquí podrías guardar en base de datos o enviar al servidor
                    Toast.makeText(PromocionActivity.this, "Promoción guardada", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                }
            }
        });
    }

    private void limpiarCampos() {
        binding.etTituloPromo.setText("");
        binding.etDescripcionPromo.setText("");
        binding.etFechaPromo.setText("");
    }
}
