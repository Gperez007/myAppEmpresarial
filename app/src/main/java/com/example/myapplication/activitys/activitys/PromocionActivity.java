package com.example.myapplication.activitys.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.PromocionAdapter;
import com.example.myapplication.activitys.model.Promocion;
import com.example.myapplication.databinding.ActivityPromocionBinding;
import com.example.myapplication.databinding.ActivityPromocionBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PromocionActivity extends AppCompatActivity {

    private ActivityPromocionBinding binding;
    private FirebaseFirestore db;
    private final String empresaID = "empresa_123"; // ID de ejemplo
    private RecyclerView recyclerPromociones;
    private List<Promocion> promocionList = new ArrayList<>();
    private PromocionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPromocionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerPromociones = findViewById(R.id.recyclerPromociones);
        recyclerPromociones.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PromocionAdapter(promocionList);
        recyclerPromociones.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        binding.btnGuardarPromocion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarPromocion();
            }
        });
        obtenerPromociones();
    }

    private void guardarPromocion() {
        String titulo = binding.etTituloPromo.getText() != null ? binding.etTituloPromo.getText().toString().trim() : "";
        String descripcion = binding.etDescripcionPromo.getText() != null ? binding.etDescripcionPromo.getText().toString().trim() : "";
        String fecha = binding.etFechaPromo.getText() != null ? binding.etFechaPromo.getText().toString().trim() : "";

        if (titulo.isEmpty() || descripcion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> promocion = new HashMap<>();
        promocion.put("titulo", titulo);
        promocion.put("descripcion", descripcion);
        promocion.put("fecha", fecha);
        promocion.put("empresaID", empresaID);

        db.collection("empresas")
                .document(empresaID)
                .collection("promociones")
                .add(promocion)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(PromocionActivity.this, "Promoción guardada", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PromocionActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        obtenerPromociones();
    }

    private void limpiarCampos() {
        binding.etTituloPromo.setText("");
        binding.etDescripcionPromo.setText("");
        binding.etFechaPromo.setText("");
    }

    private void obtenerPromociones() {
        db.collection("empresas")
                .document(empresaID)
                .collection("promociones")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    promocionList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Promocion promo = doc.toObject(Promocion.class);
                        promocionList.add(promo);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar promociones", Toast.LENGTH_SHORT).show();
                });
    }
}