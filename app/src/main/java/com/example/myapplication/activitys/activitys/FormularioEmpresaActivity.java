    package com.example.myapplication.activitys.activitys;

    import android.Manifest;
    import android.content.pm.PackageManager;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Spinner;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;

    import com.example.myapplication.R;
    import com.example.myapplication.activitys.util.PreferenseManager;
    import com.google.android.gms.location.FusedLocationProviderClient;
    import com.google.android.gms.location.LocationServices;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.firestore.QueryDocumentSnapshot;
    import com.google.gson.Gson;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
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
        private EditText etPoliticaDevolucion, etPoliticaReclamos, etDetalleEmpresa;
        private Spinner spinnerDepartamento, spinnerCiudad;
        private Map<String, String> departamentosMap = new HashMap<>();
        private Map<String, String> ciudadesMap = new HashMap<>();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_formulario_empresa);

            // 1. Inicializaciones básicas
            preferenseManager = new PreferenseManager(getApplicationContext());
            db = FirebaseFirestore.getInstance();

            etPoliticaDevolucion = findViewById(R.id.etPoliticaDevolucion);
            etDetalleEmpresa = findViewById(R.id.etDetalleEmpresa);
            etPoliticaReclamos = findViewById(R.id.etPoliticaReclamos);
            etRazonSocial = findViewById(R.id.etRazonSocial);
            etDireccion = findViewById(R.id.etDireccion);
            etTelefono = findViewById(R.id.etTelefono);
            btnGuardarDatos = findViewById(R.id.btnGuardarDatos);
            spinnerDepartamento = findViewById(R.id.spinnerDepartamento);
            spinnerCiudad = findViewById(R.id.spinnerCiudad);

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            obtenerUbicacionActual();

            // 2. Cargar datos empresa
            String datosEmpresaJson = preferenseManager.getString("datosEmpresas");
            Map<String, Object> empresaData = null;
            if (datosEmpresaJson != null && !datosEmpresaJson.isEmpty()) {
                empresaData = new Gson().fromJson(datosEmpresaJson, Map.class);

                etRazonSocial.setText((String) empresaData.get("razonSocial"));
                etDireccion.setText((String) empresaData.get("direccion"));
                etTelefono.setText((String) empresaData.get("telefono"));
                etDetalleEmpresa.setText((String) empresaData.get("descripcionEmpresa"));
                etPoliticaDevolucion.setText((String) empresaData.get("politicaDevolucion"));
                etPoliticaReclamos.setText((String) empresaData.get("politicaReclamos"));
            }

            // 3. Cargar departamentos y luego ciudades con datos existentes
            if (empresaData != null) {
                cargarDepartamentosConDatosExistentes(empresaData);
            } else {
                cargarDatosDesdeFirestore();
                cargarDatosExistentes();
                cargarDepartamentos(); // flujo normal sin preselección
            }

            // 4. Acciones
            btnGuardarDatos.setOnClickListener(v -> guardarDatosEmpresa());
        }

        private void cargarDepartamentosConDatosExistentes(Map<String, Object> empresaData) {
            db.collection("ubicaciones")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<String> listaDepartamentos = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String nombre = doc.getString("nombre");
                            String id = doc.getId();
                            departamentosMap.put(nombre, id);
                            listaDepartamentos.add(nombre);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaDepartamentos);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerDepartamento.setAdapter(adapter);

                        String departamento = (String) empresaData.get("departamento");
                        String ciudad = (String) empresaData.get("ciudad");

                        if (departamento != null) {
                            int indexDepto = adapter.getPosition(departamento);
                            if (indexDepto >= 0) {
                                // Evitar que el listener se dispare automáticamente
                                spinnerDepartamento.setOnItemSelectedListener(null);
                                spinnerDepartamento.setSelection(indexDepto);
                                String idDepartamento = departamentosMap.get(departamento);

                                // Cargar ciudades con la ciudad ya guardada
                                cargarCiudadesConSeleccion(idDepartamento, ciudad);

                                // Restaurar el listener después de la precarga
                                spinnerDepartamento.postDelayed(() -> {
                                    spinnerDepartamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            String nombreDepartamento = listaDepartamentos.get(position);
                                            String idDepartamento = departamentosMap.get(nombreDepartamento);
                                            cargarCiudades(idDepartamento); // ya sin preselección
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {}
                                    });
                                }, 500); // Delay pequeño para evitar conflicto
                            }
                        }
                    });
        }

        private void cargarCiudadesConSeleccion(String idDepartamento, String ciudadSeleccionada) {
            db.collection("ubicaciones")
                    .document(idDepartamento)
                    .collection("ciudades")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<String> listaCiudades = new ArrayList<>();
                        ciudadesMap.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String nombre = doc.getString("nombre");
                            String id = doc.getId();
                            ciudadesMap.put(nombre, id);
                            listaCiudades.add(nombre);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCiudades);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCiudad.setAdapter(adapter);

                        // Seleccionar ciudad si está en la lista
                        if (ciudadSeleccionada != null) {
                            int indexCiudad = adapter.getPosition(ciudadSeleccionada);
                            if (indexCiudad >= 0) {
                                spinnerCiudad.setSelection(indexCiudad);
                            }
                        }
                    });
        }

        private void cargarDepartamentos() {
            db.collection("ubicaciones")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<String> listaDepartamentos = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String nombre = doc.getString("nombre");
                            String id = doc.getId();
                            departamentosMap.put(nombre, id);
                            listaDepartamentos.add(nombre);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaDepartamentos);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerDepartamento.setAdapter(adapter);

                        spinnerDepartamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String nombreDepartamento = listaDepartamentos.get(position);
                                String idDepartamento = departamentosMap.get(nombreDepartamento);
                                cargarCiudades(idDepartamento);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });
                    });
        }

        private void cargarCiudades(String idDepartamento) {
            db.collection("ubicaciones")
                    .document(idDepartamento)
                    .collection("ciudades")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<String> listaCiudades = new ArrayList<>();
                        ciudadesMap.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String nombre = doc.getString("nombre");
                            String id = doc.getId();
                            ciudadesMap.put(nombre, id);
                            listaCiudades.add(nombre);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCiudades);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCiudad.setAdapter(adapter);
                    });
        }

        private void cargarDatosExistentes() {
            String datosEmpresaJson = preferenseManager.getString("datosEmpresas");
            if (datosEmpresaJson != null && !datosEmpresaJson.isEmpty()) {
                Map<String, Object> empresaData = new Gson().fromJson(datosEmpresaJson, Map.class);

                etRazonSocial.setText((String) empresaData.get("razonSocial"));
                etDireccion.setText((String) empresaData.get("direccion"));
                etTelefono.setText((String) empresaData.get("telefono"));
                etDetalleEmpresa.setText((String) empresaData.get("descripcionEmpresa"));
                etPoliticaDevolucion.setText((String) empresaData.get("politicaDevolucion"));
                etPoliticaReclamos.setText((String) empresaData.get("politicaReclamos"));

                // 🔄 Cargar departamento y ciudad en los spinners
                //String departamento = (String) empresaData.get("departamento");
                //String ciudad = (String) empresaData.get("ciudad");

                //if (departamento != null) {
                  //  int indexDepto = ((ArrayAdapter<String>) spinnerDepartamento.getAdapter())
               //             .getPosition(departamento);
                  //  if (indexDepto >= 0) spinnerDepartamento.setSelection(indexDepto);
               // }

                //if (ciudad != null) {
                   // int indexCiudad = ((ArrayAdapter<String>) spinnerCiudad.getAdapter())
                   //        .getPosition(ciudad);
                    //if (indexCiudad >= 0) spinnerCiudad.setSelection(indexCiudad);
              //  }

                // 📝 Opcional: puedes guardar/usar departamentoID y ciudadID si los necesitas luego
                // String departamentoID = (String) empresaData.get("departamentoID");
                // String ciudadID = (String) empresaData.get("ciudadID");
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
                                etDetalleEmpresa.setText((String) data.get("descripcionEmpresa"));
                                etPoliticaDevolucion.setText((String) data.get("politicaDevolucion"));
                                etPoliticaReclamos.setText((String) data.get("politicaReclamos"));

                                // Guardar localmente
                                String json = new Gson().toJson(data);
                                preferenseManager.putString("datosEmpresas", json);

                                // Cargar departamento y ciudad en los spinners si existen
                                String departamento = (String) data.get("departamento");
                                String ciudad = (String) data.get("ciudad");


                                if (departamento != null && spinnerDepartamento.getAdapter() != null) {
                                    int indexDepto = ((ArrayAdapter<String>) spinnerDepartamento.getAdapter()).getPosition(departamento);
                                    if (indexDepto >= 0) spinnerDepartamento.setSelection(indexDepto);
                                }

                                if (ciudad != null && spinnerCiudad.getAdapter() != null) {
                                    int indexCiudad = ((ArrayAdapter<String>) spinnerCiudad.getAdapter()).getPosition(ciudad);
                                    if (indexCiudad >= 0) spinnerCiudad.setSelection(indexCiudad);
                                }
                                // Opcional: puedes usar departamentoID y ciudadID si deseas validar contra Firestore
                                // String departamentoID = (String) data.get("departamentoID");
                                // String ciudadID = (String) data.get("ciudadID");
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
            String detalleEmpresa = etDetalleEmpresa.getText().toString().trim();
            String politicaDevolucion = etPoliticaDevolucion.getText().toString().trim();
            String politicaReclamos = etPoliticaReclamos.getText().toString().trim();

            // ✅ Validación aquí:
            if (spinnerDepartamento.getSelectedItem() == null || spinnerCiudad.getSelectedItem() == null) {
                Toast.makeText(this, "Selecciona un departamento y una ciudad", Toast.LENGTH_SHORT).show();
                return;
            }


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
                empresaData.put("descripcionEmpresa", detalleEmpresa);
                empresaData.put("politicaDevolucion", politicaDevolucion);
                empresaData.put("politicaReclamos", politicaReclamos);

                // Agregar ubicación geográfica
                if (location != null) {
                    empresaData.put("latitud", location.getLatitude());
                    empresaData.put("longitud", location.getLongitude());
                } else {
                    empresaData.put("latitud", 0.0);
                    empresaData.put("longitud", 0.0);
                }

                // Agregar departamento y ciudad seleccionados
                String departamentoSeleccionado = spinnerDepartamento.getSelectedItem().toString();
                String ciudadSeleccionada = spinnerCiudad.getSelectedItem().toString();

                String departamentoID = departamentosMap.get(departamentoSeleccionado);
                String ciudadID = ciudadesMap.get(ciudadSeleccionada);

                empresaData.put("departamento", departamentoSeleccionado);
                empresaData.put("ciudad", ciudadSeleccionada);
                empresaData.put("departamentoID", departamentoID);
                empresaData.put("ciudadID", ciudadID);

                // Guardar en Preferencias
                String json = new Gson().toJson(empresaData);
                preferenseManager.putString("datosEmpresas", json);

                // 1. Subcolección dentro del documento de la empresa
                db.collection("empresas").document(empresaUID)
                        .collection("DatosEmpresas").document("informacion")
                        .set(empresaData)
                        .addOnSuccessListener(unused -> Log.d("Firestore", "DatosEmpresa guardado"));

                // 2. Colección global: ubicacionesEmpresas
                db.collection("ubicacionesEmpresas").document(empresaUID)
                        .set(empresaData)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                            finish();
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
