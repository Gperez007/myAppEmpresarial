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

    private EditText etCategory;
    private Button btnBuscar;
    public static double latitudUsuario;
    public static double longitudUsuario;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_category);

        etCategory = findViewById(R.id.etCategory);
        btnBuscar = findViewById(R.id.btnBuscar);

        db = FirebaseFirestore.getInstance();

        // Obtener ubicación del usuario
        latitudUsuario = getIntent().getDoubleExtra("lat", 0.0);
        longitudUsuario = getIntent().getDoubleExtra("lng", 0.0);

        btnBuscar.setOnClickListener(v -> {
            String categoria = etCategory.getText().toString().trim();
            if (!categoria.isEmpty()) {
                buscarEmpresasPorCategoria(categoria);
            } else {
                Toast.makeText(this, "Por favor ingrese una categoría", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarEmpresasPorCategoria(String categoria) {
        db.collection("ubicacionesEmpresas")
                .whereEqualTo("descripcionEmpresa", categoria)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        mostrarModalDeEmpresas(queryDocumentSnapshots.getDocuments());
                    } else {
                        Toast.makeText(this, "No se encontraron empresas", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
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

        final int[] seleccion = { -1 }; // Para guardar la selección temporal

        builder.setSingleChoiceItems(items, -1, (dialog, which) -> {
            seleccion[0] = which;
        });

        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            if (seleccion[0] != -1) {
                DocumentSnapshot empresaSeleccionada = empresas.get(seleccion[0]);

                double latEmpresa = empresaSeleccionada.getDouble("latitud");
                double lngEmpresa = empresaSeleccionada.getDouble("longitud");

                double latUsuario = getIntent().getDoubleExtra("lat", 0.0);
                double lngUsuario = getIntent().getDoubleExtra("lng", 0.0);

                Intent intent = new Intent(SearchCategoryActivity.this, StoreDetailActivity.class);
                intent.putExtra("lat_empresa", latEmpresa);
                intent.putExtra("lng_empresa", lngEmpresa);
                intent.putExtra("lat_usuario", latUsuario);
                intent.putExtra("lng_usuario", lngUsuario);

                // Nuevos datos que quieres pasar
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

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
