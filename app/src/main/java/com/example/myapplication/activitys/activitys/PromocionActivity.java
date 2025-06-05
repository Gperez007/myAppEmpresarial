package com.example.myapplication.activitys.activitys;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.PromocionAdapter;
import com.example.myapplication.activitys.model.Promocion;
import com.example.myapplication.activitys.util.PreferenseManager;
import com.example.myapplication.databinding.ActivityPromocionBinding;
import com.example.myapplication.databinding.ActivityPromocionBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PromocionActivity extends AppCompatActivity {

    private ActivityPromocionBinding binding;
    private FirebaseFirestore db;
    private PreferenseManager preferenseManager;
    private final String empresaID = "empresa_123"; // ID de ejemplo
    private RecyclerView recyclerPromociones;
    private List<Promocion> promocionList = new ArrayList<>();
    private PromocionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPromocionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        preferenseManager = new PreferenseManager(getApplicationContext());

        // Recycler
        promocionList = new ArrayList<>();
        recyclerPromociones = binding.recyclerPromociones;
        recyclerPromociones.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PromocionAdapter(
                promocionList,
                new PromocionAdapter.OnPromocionClickListener() {
                    @Override
                    public void onEditarClick(Promocion p) { editarPromocion(p); }
                    @Override
                    public void onEliminarClick(Promocion p) { eliminarPromocion(p); }
                });
        recyclerPromociones.setAdapter(adapter);

        // Botón guardar
        String nit = preferenseManager.getString("empresaNit");
        binding.btnGuardarPromocion.setOnClickListener(v -> obtenerEmpresaIDYGuardarPromocion(nit));

        binding.btnBuscar.setOnClickListener(v -> {
            String titulo = binding.etBuscarTitulo.getText().toString().trim();
            if (!titulo.isEmpty()) {
                buscarPromocionPorTitulo(titulo);
            } else {
                Toast.makeText(this, "Ingresa un título para buscar", Toast.LENGTH_SHORT).show();
            }
        });

        // Cargar lista al arrancar
        obtenerPromociones();
    }

    private void obtenerEmpresaIDYGuardarPromocion(String nitIngresado) {
        String nitNormalizado = nitIngresado.trim().replaceAll("\\s+", "");

        db.collection("empresas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String nitFirestore = document.getString("nit");

                        if (nitFirestore != null && nitFirestore.trim().replaceAll("\\s+", "").equals(nitNormalizado)) {
                            guardarPromocion();
                            return; // salimos del bucle si ya se encontró
                        }
                    }

                    Toast.makeText(this, "Empresa no encontrada", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener la empresa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void guardarPromocion() {
        String empresaUID = preferenseManager.getString("empresaUID");

        if (empresaUID == null || empresaUID.isEmpty()) {
            Toast.makeText(this, "ID de empresa no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String titulo = binding.etTituloPromo.getText().toString().trim();
        String descripcion = binding.etDescripcionPromo.getText().toString().trim();
        String fecha = binding.etFechaPromo.getText().toString().trim();

        if (titulo.isEmpty() || descripcion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String promocionID = db.collection("empresas")
                .document(empresaUID)
                .collection("promociones")
                .document().getId();

        // Obtener lat/lng desde preferencias (ajústalo si los obtienes de otro lado)
        double latitud = 5.71786;
        double longitud = -72.92706;
        // O usa preferencias si ya los guardaste:
        // double latitud = Double.parseDouble(preferenseManager.getString("empresaLatitud"));
        // double longitud = Double.parseDouble(preferenseManager.getString("empresaLongitud"));

        // Crear un mapa con los datos (incluyendo ubicación)
        Map<String, Object> promocionData = new HashMap<>();
        promocionData.put("promocionID", promocionID);
        promocionData.put("titulo", titulo);
        promocionData.put("descripcion", descripcion);
        promocionData.put("fecha", fecha);
        promocionData.put("empresaUID", empresaUID);
        promocionData.put("latitud", latitud);
        promocionData.put("longitud", longitud);
        promocionData.put("timestamp", FieldValue.serverTimestamp());

        // Guardar en subcolección de promociones de la empresa
        db.collection("empresas")
                .document(empresaUID)
                .collection("promociones")
                .document(promocionID)
                .set(promocionData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Promoción guardada", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                    obtenerPromociones();

                    // También guardar en la colección global
                    db.collection("promocionesGlobales")
                            .document(promocionID)
                            .set(promocionData)
                            .addOnSuccessListener(v -> Log.d("Firestore", "Promoción global guardada"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error al guardar en global: " + e.getMessage()));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void limpiarCampos() {
        binding.etTituloPromo.setText("");
        binding.etDescripcionPromo.setText("");
        binding.etFechaPromo.setText("");
    }

    private void obtenerPromociones() {
        String empresaUID = preferenseManager.getString("empresaUID");

        if (empresaUID == null || empresaUID.isEmpty()) {
            Toast.makeText(this, "ID de empresa no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("empresas")
                .document(empresaUID)
                .collection("promociones")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    promocionList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Promocion promo = doc.toObject(Promocion.class);
                        promo.setId(doc.getId()); // importante
                        promocionList.add(promo);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar promociones", Toast.LENGTH_SHORT).show();
                });
    }

    private void eliminarPromocion(Promocion promocion) {
        String nit = preferenseManager.getString("empresaNit");

        db.collection("empresas")
                .whereEqualTo("nit", nit)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        String empresaId = snapshot.getDocuments().get(0).getId();

                        // Eliminar de la subcolección de promociones de la empresa
                        db.collection("empresas").document(empresaId)
                                .collection("promociones")
                                .document(promocion.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Ahora eliminar de la colección global
                                    db.collection("promocionesGlobales")
                                            .document(promocion.getId())
                                            .delete()
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(this, "Promoción eliminada", Toast.LENGTH_SHORT).show();
                                                obtenerPromociones();
                                            })
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(this, "Error al eliminar en global", Toast.LENGTH_SHORT).show());
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error al eliminar en empresa", Toast.LENGTH_SHORT).show());
                    }
                });
    }


    // Editar (sobrescribe la promo con los datos que haya en los EditText)
    private void editarPromocion(Promocion promocion) {
        // Rellenar los campos con datos existentes
        binding.etTituloPromo.setText(promocion.getTitulo());
        binding.etDescripcionPromo.setText(promocion.getDescripcion());
        binding.etFechaPromo.setText(promocion.getFecha());

        // Al tocar "Guardar"
        binding.btnGuardarPromocion.setOnClickListener(v -> {
            String newTitulo = binding.etTituloPromo.getText().toString().trim();
            String newDesc = binding.etDescripcionPromo.getText().toString().trim();
            String newFecha = binding.etFechaPromo.getText().toString().trim();

            if (newTitulo.isEmpty() || newDesc.isEmpty() || newFecha.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> cambios = new HashMap<>();
            cambios.put("titulo", newTitulo);
            cambios.put("descripcion", newDesc);
            cambios.put("fecha", newFecha);

            String nit = preferenseManager.getString("empresaNit");
            db.collection("empresas")
                    .whereEqualTo("nit", nit)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            String empresaId = snapshot.getDocuments().get(0).getId();

                            // Actualizar en la subcolección de promociones de la empresa
                            db.collection("empresas").document(empresaId)
                                    .collection("promociones")
                                    .document(promocion.getId())
                                    .update(cambios)
                                    .addOnSuccessListener(aVoid -> {
                                        // Luego actualizar en la colección global
                                        db.collection("promocionesGlobales")
                                                .document(promocion.getId())
                                                .update(cambios)
                                                .addOnSuccessListener(unused -> {
                                                    Toast.makeText(this, "Promoción actualizada", Toast.LENGTH_SHORT).show();
                                                    limpiarCampos();
                                                    obtenerPromociones();
                                                })
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(this, "Error al actualizar en global", Toast.LENGTH_SHORT).show());
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Error al actualizar en empresa", Toast.LENGTH_SHORT).show());
                        }
                    });
        });
    }

    private void buscarPromocionPorTitulo(String tituloBuscado) {
        String empresaUID = preferenseManager.getString("empresaUID");

        db.collection("empresas")
                .document(empresaUID)
                .collection("promociones")
                .whereEqualTo("titulo", tituloBuscado)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    promocionList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Promocion promo = doc.toObject(Promocion.class);
                        promo.setId(doc.getId());
                        promocionList.add(promo);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al buscar promoción", Toast.LENGTH_SHORT).show();
                });
    }
}