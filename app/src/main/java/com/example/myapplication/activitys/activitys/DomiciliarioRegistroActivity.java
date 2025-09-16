package com.example.myapplication.activitys.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DomiciliarioRegistroActivity extends AppCompatActivity {

    private EditText editNombre, editCedula, editTelefono, editCorreo, editPlaca;
    private Button btnRegistrar;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitud = 0.0, longitud = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domiciliario_registro);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        editNombre = findViewById(R.id.editNombre);
        editCedula = findViewById(R.id.editCedula);
        editTelefono = findViewById(R.id.editTelefono);
        editCorreo = findViewById(R.id.editCorreo);
        editPlaca = findViewById(R.id.editPlaca);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        obtenerUbicacionActual();

        btnRegistrar.setOnClickListener(v -> registrarDomiciliario());
    }

    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitud = location.getLatitude();
                        longitud = location.getLongitude();
                    }
                });
    }

    private void registrarDomiciliario() {
        String nombre = editNombre.getText().toString().trim();
        String cedula = editCedula.getText().toString().trim();
        String telefono = editTelefono.getText().toString().trim();
        String correo = editCorreo.getText().toString().trim();
        String placa = editPlaca.getText().toString().trim();

        if (nombre.isEmpty() || cedula.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> domiciliario = new HashMap<>();
        domiciliario.put("nombre", nombre);
        domiciliario.put("cedula", cedula);
        domiciliario.put("telefono", telefono);
        domiciliario.put("correo", correo);
        domiciliario.put("placaVehiculo", placa);
        domiciliario.put("estado", "pendiente");
        domiciliario.put("latitud", latitud);
        domiciliario.put("longitud", longitud);

        db.collection("domiciliarios").document(cedula).set(domiciliario)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, DomiciliarioLoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}