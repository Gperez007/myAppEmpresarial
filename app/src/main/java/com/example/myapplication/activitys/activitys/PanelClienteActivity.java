package com.example.myapplication.activitys.activitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.PromocionAdapter;
import com.example.myapplication.activitys.adapter.PromocionClienteAdapter;
import com.example.myapplication.activitys.model.Promocion;
import com.example.myapplication.activitys.model.PromocionVistaCliente;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PanelClienteActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private MaterialButton btnIrChat;
    private RecyclerView rvPromociones;

    private PromocionClienteAdapter promocionAdapter;
    private List<PromocionVistaCliente> promocionList;

    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;

    private double miLat = 0.0;
    private double miLng = 0.0;

    private PromocionVistaCliente promocionSeleccionada = null;
    private double userLat, userLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_cliente);

        // Inicializar views
        rvPromociones = findViewById(R.id.rvPromociones);
        btnIrChat = findViewById(R.id.btnIrChat);

        // Inicializar Firestore y Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        // Inicializar lista y adapter
        promocionList = new ArrayList<>();
        promocionAdapter = new PromocionClienteAdapter(
                promocionList,
                new PromocionClienteAdapter.OnPromocionClickListener() {
                    @Override
                    public void onIrMapaClick(PromocionVistaCliente promocion) {
                        // Aquí lanzas el MapaActivity
                        promocionSeleccionada = promocion; // guardas la promoción seleccionada
                        obtenerUbicacion();

                    }
                }
        );
        rvPromociones.setAdapter(promocionAdapter);

        // Configurar RecyclerView
        rvPromociones.setLayoutManager(new LinearLayoutManager(this));
        rvPromociones.setAdapter(promocionAdapter);

        // Manejar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            obtenerUbicacionYPromociones();
        }

        // Click en botón de ir al chat
        btnIrChat.setOnClickListener(view -> {
            // TODO: Implementar navegación al chat
            Toast.makeText(this, "Ir al Chat (por implementar)", Toast.LENGTH_SHORT).show();
        });

    }

    private void obtenerUbicacionYPromociones() {
        // Verificamos permisos antes de obtener ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Si no tenemos permisos, los solicitamos
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return; // IMPORTANTE: salimos y esperamos a que el usuario acepte los permisos
        }

        // Si tenemos permisos, pedimos la última ubicación conocida
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        userLat = location.getLatitude();
                        userLng = location.getLongitude();
                        cargarPromociones();
                    } else {
                        // Si no hay ubicación disponible, cargamos promociones igual (quizás sin calcular distancias)
                        Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                        cargarPromociones();
                    }
                })
                .addOnFailureListener(e -> {
                    // En caso de error, también cargamos promociones para no bloquear la app
                    Toast.makeText(this, "Error obteniendo la ubicación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    cargarPromociones();
                });
    }

    private void cargarPromociones() {
        db.collection("promocionesGlobales")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    promocionList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String titulo = document.getString("titulo");
                        String descripcion = document.getString("descripcion");
                        String fecha = document.getString("fecha");
                        String id = document.getId(); // si quieres usar el ID del documento
                        Double latitud = document.getDouble("latitud");
                        Double longitud = document.getDouble("longitud");


                        PromocionVistaCliente promocion = new PromocionVistaCliente(id, titulo, descripcion, fecha, latitud, longitud);
                        promocionList.add(promocion);
                    }

                    promocionAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar promociones: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionYPromociones();
                obtenerUbicacion();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado. La aplicación no puede acceder a tu ubicación.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000)
                .setNumUpdates(1);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            Toast.makeText(this, "El GPS está desactivado. Actívalo para continuar.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    double latitud = location.getLatitude();
                    double longitud = location.getLongitude();

                    miLat = latitud;
                    miLng = longitud;

                    mostrarUbicacion(latitud, longitud);
                } else {
                    Log.e("Ubicación", "No se pudo obtener la ubicación con requestLocationUpdates.");
                    Toast.makeText(getApplicationContext(), "No se pudo obtener tu ubicación. Asegúrate de tener el GPS activado.", Toast.LENGTH_LONG).show();
                }
            }
        }, Looper.getMainLooper());
    }
    private void mostrarUbicacion(double lat, double lng) {
        if (promocionSeleccionada == null) {
            Toast.makeText(this, "Error: no se seleccionó ninguna promoción.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("titulo", promocionSeleccionada.getTitulo());
        intent.putExtra("descripcion", promocionSeleccionada.getDescripcion());
        intent.putExtra("latitud", promocionSeleccionada.getLatitud());
        intent.putExtra("longitud", promocionSeleccionada.getLongitud());
        intent.putExtra("lat", lat);  // ubicación del cliente
        intent.putExtra("lng", lng);  // ubicación del cliente
        intent.putExtra("mostrarEmpresas", true); // o true si quieres

        startActivity(intent);
    }

}
