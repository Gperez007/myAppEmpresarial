package com.example.myapplication.activitys.activitys;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.activitys.util.PreferenseManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class FormularioEmpresaActivity extends AppCompatActivity {

    private EditText etRazonSocial, etDireccion, etTelefono;
    private Button btnGuardarDatos;
    private PreferenseManager preferenseManager;
    private FirebaseFirestore db;
    private String empresaID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_empresa);

        preferenseManager = new PreferenseManager(getApplicationContext());
        empresaID = preferenseManager.getString("empresaNit");
        db = FirebaseFirestore.getInstance();

        etRazonSocial = findViewById(R.id.etRazonSocial);
        etDireccion = findViewById(R.id.etDireccion);
        etTelefono = findViewById(R.id.etTelefono);
        btnGuardarDatos = findViewById(R.id.btnGuardarDatos);

        if (empresaID == null || empresaID.isEmpty()) {
            Toast.makeText(this, "No se encontró el NIT de la empresa", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnGuardarDatos.setOnClickListener(v -> guardarDatosEmpresa());

        cargarDatosExistentes();
    }

    private void cargarDatosExistentes() {
        String datosEmpresaJson = preferenseManager.getString("datosEmpresa");
        if (datosEmpresaJson != null && !datosEmpresaJson.isEmpty()) {
            Map<String, Object> empresaData = new Gson().fromJson(datosEmpresaJson, Map.class);
            etRazonSocial.setText((String) empresaData.get("razonSocial"));
            etDireccion.setText((String) empresaData.get("direccion"));
            etTelefono.setText((String) empresaData.get("telefono"));
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

        Map<String, Object> empresaData = new HashMap<>();
        empresaData.put("razonSocial", razonSocial);
        empresaData.put("direccion", direccion);
        empresaData.put("telefono", telefono);

        // Guardar localmente los datos en preferencias
        String json = new Gson().toJson(empresaData);
        preferenseManager.putString("datosEmpresa", json);

        // Si no hay empresaID aún, generamos uno nuevo
        if (empresaID == null || empresaID.isEmpty()) {
            empresaID = db.collection("empresas").document().getId();
            preferenseManager.putString("empresaNit", empresaID);
        }

        // Guardar o actualizar el documento en Firestore
        db.collection("empresas").document(empresaID).collection("DatosEmpresa")
                .document(empresaID)
                .set(empresaData, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
