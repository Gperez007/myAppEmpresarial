package com.example.myapplication.activitys.activitys;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class SearchCategoryActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private Button btnBuscar;
    public static double latitudUsuario;
    public static double longitudUsuario;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_category);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnBuscar = findViewById(R.id.btnBuscar);
        progressBar = findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance();

        // Obtener ubicación del usuario
        latitudUsuario = getIntent().getDoubleExtra("lat", 0.0);
        longitudUsuario = getIntent().getDoubleExtra("lng", 0.0);

        // Datos para el Spinner
        List<String> categorias = new ArrayList<>();
        categorias.add("Postres");
        categorias.add("Licores");
        categorias.add("Calzado");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categorias
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Botón buscar
        btnBuscar.setOnClickListener(v -> {
            String categoria = spinnerCategory.getSelectedItem().toString();
            buscarEmpresasPorCategoria(categoria);
        });
    }

    private void buscarEmpresasPorCategoria(String categoria) {
        progressBar.setVisibility(View.VISIBLE); // Mostrar loading

        db.collection("ubicacionesEmpresas")
                .whereEqualTo("descripcionEmpresa", categoria)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE); // Ocultar loading
                    if (!queryDocumentSnapshots.isEmpty()) {
                        mostrarModalDeEmpresas(queryDocumentSnapshots.getDocuments());
                    } else {
                        Toast.makeText(this, "No se encontraron empresas", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE); // Ocultar loading
                    Toast.makeText(this, "Error al buscar empresas", Toast.LENGTH_SHORT).show();
                });
    }

    private void mostrarModalDeEmpresas(List<DocumentSnapshot> empresas) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Empresas encontradas");

        String[] items = new String[empresas.size()];
        for (int i = 0; i < empresas.size(); i++) {
            DocumentSnapshot doc = empresas.get(i);
            String nombre = doc.getString("razonSocial");
            String descripcion = doc.getString("descripcionEmpresa");
            String ciudad = doc.getString("ciudad");
            String departamento = doc.getString("departamento");
            String direccion = doc.getString("direccion");
            items[i] = nombre + " - " + descripcion + "\n" + ciudad + ", " + departamento + "\n" + direccion;
        }

        final int[] seleccion = { -1 };
        builder.setSingleChoiceItems(items, -1, (dialog, which) -> seleccion[0] = which);

        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            if (seleccion[0] != -1) {
                DocumentSnapshot empresaSeleccionada = empresas.get(seleccion[0]);

                Intent intent = new Intent(SearchCategoryActivity.this, StoreDetailActivity.class);
                intent.putExtra("lat_empresa", empresaSeleccionada.getDouble("latitud"));
                intent.putExtra("lng_empresa", empresaSeleccionada.getDouble("longitud"));
                intent.putExtra("lat_usuario", latitudUsuario);
                intent.putExtra("lng_usuario", longitudUsuario);
                intent.putExtra("empresaID", empresaSeleccionada.getString("empresaID"));
                intent.putExtra("nombre", empresaSeleccionada.getString("razonSocial"));
                intent.putExtra("nit", empresaSeleccionada.getString("nit"));
                intent.putExtra("telefono", empresaSeleccionada.getString("telefono"));
                intent.putExtra("politicaReclamos", empresaSeleccionada.getString("politicaReclamos"));
                intent.putExtra("politicaDevolucion", empresaSeleccionada.getString("politicaDevolucion"));
                intent.putExtra("direccion", empresaSeleccionada.getString("direccion"));
                intent.putExtra("departamento", empresaSeleccionada.getString("departamento"));
                intent.putExtra("ciudad", empresaSeleccionada.getString("ciudad"));

                startActivity(intent);
            } else {
                Toast.makeText(this, "Debes seleccionar una empresa", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}