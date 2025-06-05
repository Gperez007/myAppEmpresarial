package com.example.myapplication.activitys.activitys;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class PanelClienteActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private TextView tvPromociones, tvNegocios;
    private MaterialButton btnIrChat;

    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;

    private double userLat, userLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_cliente);

        tvPromociones = findViewById(R.id.tvPromociones);
        tvNegocios = findViewById(R.id.tvNegocios);
        btnIrChat = findViewById(R.id.btnIrChat);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        // Verificar y solicitar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            obtenerUbicacionYPromociones();
        }

        btnIrChat.setOnClickListener(view -> {
            // Implementa la lógica para ir al chat
        });
    }

    private void obtenerUbicacionYPromociones() {
        // Verificar permiso ANTES de acceder a la ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Si el permiso está concedido, acceder a la ubicación
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        userLat = location.getLatitude();
                        userLng = location.getLongitude();
                        cargarPromociones();
                    } else {
                        Toast.makeText(this, "No se pudo obtener la ubicación actual.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al obtener la ubicación: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void cargarPromociones() {
        db.collection("promocionesGlobales")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    StringBuilder promocionesBuilder = new StringBuilder();
                    StringBuilder negociosBuilder = new StringBuilder();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String titulo = document.getString("titulo");
                        String descripcion = document.getString("descripcion");
                        String fecha = document.getString("fecha");
                        Double latitud = document.getDouble("latitud");
                        Double longitud = document.getDouble("longitud");

                        promocionesBuilder.append("• ").append(titulo).append(" - ").append(descripcion).append(" (Válido hasta ").append(fecha).append(")\n");

                        if (latitud != null && longitud != null) {
                            float[] results = new float[1];
                            Location.distanceBetween(userLat, userLng, latitud, longitud, results);
                            float distanceInMeters = results[0];
                            String distanciaFormateada = formatDistance(distanceInMeters);
                            negociosBuilder.append("• ").append(titulo).append(" - a ").append(distanciaFormateada).append("\n");
                        } else {
                            negociosBuilder.append("• ").append(titulo).append(" - ubicación no disponible\n");
                        }
                    }

                    tvPromociones.setText(promocionesBuilder.toString());
                    tvNegocios.setText(negociosBuilder.toString());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar promociones: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private String formatDistance(float distanceInMeters) {
        if (distanceInMeters < 1000) {
            return Math.round(distanceInMeters) + " m";
        } else {
            return String.format("%.2f km", distanceInMeters / 1000);
        }
    }

    // Manejar la respuesta de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionYPromociones();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
