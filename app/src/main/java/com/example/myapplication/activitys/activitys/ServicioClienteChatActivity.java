package com.example.myapplication.activitys.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ServicioClienteChatActivity extends AppCompatActivity {

    private Spinner spinnerDepartamento, spinnerCiudad, spinnerEmpresa;
    private Button btnContinuar;

    private List<String> listaDepartamentos, listaCiudades, listaEmpresas;
    private final Map<String, String> empresaIDMap = new HashMap<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicio_cliente_chat);

        spinnerDepartamento = findViewById(R.id.spinnerDepartamentoChat);
        spinnerCiudad      = findViewById(R.id.spinnerCiudadChat);
        spinnerEmpresa     = findViewById(R.id.spinnerEmpresaChat);
        btnContinuar       = findViewById(R.id.btnContinuarChat);

        cargarDepartamentos();

        spinnerDepartamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String dep = listaDepartamentos.get(position);
                cargarCiudades(dep);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String dep = (String) spinnerDepartamento.getSelectedItem();
                String city = listaCiudades.get(position);
                cargarEmpresas(dep, city);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnContinuar.setOnClickListener(v -> {
            String empresaSel = (String) spinnerEmpresa.getSelectedItem();
            if (empresaSel != null && empresaIDMap.containsKey(empresaSel)) {
                String empresaID = empresaIDMap.get(empresaSel);
                Intent intent = new Intent(this, ChatAtencionClienteActivity.class);
                intent.putExtra("empresaID", empresaID);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Por favor selecciona una empresa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDepartamentos() {
        db.collection("ubicacionesEmpresas")
                .get()
                .addOnSuccessListener(snap -> {
                    Set<String> set = new HashSet<>();
                    for (DocumentSnapshot doc : snap) {
                        String d = doc.getString("departamento");
                        if (d != null) set.add(d);
                    }
                    listaDepartamentos = new ArrayList<>(set);
                    Collections.sort(listaDepartamentos);
                    spinnerDepartamento.setAdapter(new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item, listaDepartamentos));
                });
    }

    private void cargarCiudades(String departamento) {
        db.collection("ubicacionesEmpresas")
                .whereEqualTo("departamento", departamento)
                .get()
                .addOnSuccessListener(snap -> {
                    Set<String> set = new HashSet<>();
                    for (DocumentSnapshot doc : snap) {
                        String c = doc.getString("ciudad");
                        if (c != null) set.add(c);
                    }
                    listaCiudades = new ArrayList<>(set);
                    Collections.sort(listaCiudades);
                    spinnerCiudad.setAdapter(new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item, listaCiudades));
                });
    }

    private void cargarEmpresas(String departamento, String ciudad) {
        db.collection("ubicacionesEmpresas")
                .whereEqualTo("departamento", departamento)
                .whereEqualTo("ciudad", ciudad)
                .get()
                .addOnSuccessListener(snap -> {
                    listaEmpresas = new ArrayList<>();
                    empresaIDMap.clear();
                    for (DocumentSnapshot doc : snap) {
                        String name = doc.getString("razonSocial");
                        String id   = doc.getString("empresaID");
                        if (name != null && id != null) {
                            listaEmpresas.add(name);
                            empresaIDMap.put(name, id);
                        }
                    }
                    Collections.sort(listaEmpresas);
                    spinnerEmpresa.setAdapter(new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item, listaEmpresas));
                });
    }
}
