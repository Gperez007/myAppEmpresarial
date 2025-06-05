package com.example.myapplication.activitys.activitys;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.R;
import com.example.myapplication.activitys.util.PreferenseManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class FormularioEmpresaActivity extends AppCompatActivity {

    private EditText etRazonSocial, etDireccion, etTelefono;
    private Button btnGuardarDatos;
    private PreferenseManager preferenseManager;
    private FirebaseFirestore db;
    private String idEmpresa;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitud = 0.0;
    private double longitud = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_empresa);
        setContentView(R.layout.activity_formulario_empresa);
        setContentView(R.layout.activity_formulario_empresa);

        preferenseManager = new PreferenseManager(getApplicationContext());
        db = FirebaseFirestore.getInstance();

        etRazonSocial = findViewById(R.id.etRazonSocial);
        etDireccion = findViewById(R.id.etDireccion);
        etTelefono = findViewById(R.id.etTelefono);
        btnGuardarDatos = findViewById(R.id.btnGuardarDatos);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        obtenerUbicacionActual();

        // Obtener el ID de la empresa desde las preferencias
        String datosEmpresaJson = preferenseManager.getString("datosEmpresa");
        if (datosEmpresaJson != null && !datosEmpresaJson.isEmpty()) {
            Map<String, Object> empresaData = new Gson().fromJson(datosEmpresaJson, Map.class);
            if (empresaData.get("id") != null) {
                idEmpresa = String.valueOf(((Double) empresaData.get("id")).intValue());
            }
        }

        if (idEmpresa == null || idEmpresa.isEmpty()) {
            Toast.makeText(this, "No se encontró el ID de la empresa", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        cargarDatosDesdeFirestore();
        btnGuardarDatos.setOnClickListener(v -> guardarDatosEmpresa());
        cargarDatosExistentes();
    }

    private void cargarDatosExistentes() {
        String datosEmpresaJson = preferenseManager.getString("datosEmpresas");
        if (datosEmpresaJson != null && !datosEmpresaJson.isEmpty()) {
            Map<String, Object> empresaData = new Gson().fromJson(datosEmpresaJson, Map.class);
            etRazonSocial.setText((String) empresaData.get("razonSocial"));
            etDireccion.setText((String) empresaData.get("direccion"));
            etTelefono.setText((String) empresaData.get("telefono"));
        }
    }

    private void cargarDatosDesdeFirestore() {
        String empresaUID = preferenseManager.getString("empresaUID");

        db.collection("empresas")
                .document(empresaUID)
                .collection("DatosEmpresas")
                .document("informacion")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {
                            etRazonSocial.setText((String) data.get("razonSocial"));
                            etDireccion.setText((String) data.get("direccion"));
                            etTelefono.setText((String) data.get("telefono"));

                            // Guardar localmente en preferencias también
                            String json = new Gson().toJson(data);
                            preferenseManager.putString("datosEmpresas", json);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                guardarDatosEmpresa(); // Reintenta guardar con permisos
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void guardarDatosEmpresa() {
        String razonSocial = etRazonSocial.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        if (razonSocial.isEmpty()) {
            etRazonSocial.setError("Campo requerido");
            return;
        }

        // Verificar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        Toast.makeText(this, "Obteniendo ubicación, por favor espera...", Toast.LENGTH_SHORT).show();

        String empresaUID = preferenseManager.getString("empresaUID");
        String nit = preferenseManager.getString("empresaNit");

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            Map<String, Object> empresaData = new HashMap<>();
            empresaData.put("razonSocial", razonSocial);
            empresaData.put("nit", nit);
            empresaData.put("empresaID", empresaUID);
            empresaData.put("direccion", direccion);
            empresaData.put("telefono", telefono);

            if (location != null) {
                empresaData.put("latitud", 5.71786);
                empresaData.put("longitud", -72.92706);
            }

            // Guardar localmente en preferencias
            String json = new Gson().toJson(empresaData);
            preferenseManager.putString("datosEmpresas", json);

            // ✅ 1. Guardar en subcolección DatosEmpresas de cada empresa
            db.collection("empresas").document(empresaUID)
                    .collection("DatosEmpresas").document("informacion")
                    .set(empresaData)
                    .addOnSuccessListener(unused -> Log.d("Firestore", "DatosEmpresa guardado"));

            // ✅ 2. Guardar en colección global "ubicacionesEmpresas"
            db.collection("ubicacionesEmpresas").document(empresaUID)
                    .set(empresaData)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                        finish(); // cerrar pantalla
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
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

}
