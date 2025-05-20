package com.example.myapplication.activitys.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.ChatAtencionclienteAdapter;
import com.example.myapplication.activitys.adapter.PpalTemaAdapter;
import com.example.myapplication.activitys.model.ProductoLocation;
import com.example.myapplication.activitys.model.Tema;
import com.example.myapplication.activitys.network.ApiRetrofit;
import com.example.myapplication.activitys.servicios.Services;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.button.MaterialButton;


import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PpalTemaActivity extends AppCompatActivity {

    private List<Tema> temaList;
    private MaterialButton botonBoardingAction;
    private PpalTemaAdapter ppalTemaAdapter;
    private LinearLayout layoutOnboardingIndicator;


    private ViewPager2 viewPager2;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppal_tema);
        viewPager2 = findViewById(R.id.onBoarddindViePager);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        findViewById(R.id.btnIrAlChat).setOnClickListener(v -> {
            Intent intent = new Intent(PpalTemaActivity.this, ChatAtencionClienteActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnAdminProdRegistro).setOnClickListener(v -> {
            Intent intent = new Intent(PpalTemaActivity.this, EmpresaPanelActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnclientePromocion).setOnClickListener(v -> {
            Intent intent = new Intent(PpalTemaActivity.this, PanelClienteActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnPedido).setOnClickListener(v -> {
            Intent intent = new Intent(PpalTemaActivity.this, PedidoActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnPagos).setOnClickListener(v -> {
            Intent intent = new Intent(PpalTemaActivity.this, PagoActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnProdLocation).setOnClickListener(v -> {
            Intent intent = new Intent(PpalTemaActivity.this, ProductosUbicacionActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnBuscarTiendasCercanas).setOnClickListener(v -> obtenerUbicacion());
        // Mostrar los temas
        ShowTemas();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion();  // Si el usuario concede el permiso, se obtiene la ubicación
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado. La aplicación no puede acceder a tu ubicación.", Toast.LENGTH_LONG).show();
                // Aquí puedes añadir una lógica adicional para llevar al usuario a la configuración de la app
            }
        }
    }

    private void obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
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
                    mostrarTiendasCercanas(latitud, longitud);
                } else {
                    Log.e("Ubicación", "No se pudo obtener la ubicación con requestLocationUpdates.");
                    Toast.makeText(getApplicationContext(), "No se pudo obtener tu ubicación. Asegúrate de tener el GPS activado.", Toast.LENGTH_LONG).show();
                }
            }
        }, Looper.getMainLooper());
    }
    private void mostrarTiendasCercanas(double lat, double lng) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        startActivity(intent);
    }

    private void ShowTemas() {
        Call<List<Tema>> call = ApiRetrofit.retrofit().create(Services.class).getAll();
        call.enqueue(new Callback<List<Tema>>() {
            @Override
            public void onResponse(Call<List<Tema>> call, Response<List<Tema>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    temaList = response.body();

                        // Aquí puedes asignar las imágenes manualmente (si no vienen del backend)
                    for (int i = 0; i < temaList.size(); i++) {
                        Tema tema = temaList.get(i);
                        if (i < 5) { // Asegúrate de que no exceda el array
                            int[] imagenes = {
                                    R.drawable.trabajo,
                                    R.drawable.salud1,
                                    R.drawable.educaciones,
                                    R.drawable.marketingf,
                                    R.drawable.religion
                            };
                            tema.setImagen(imagenes[i]);
                        }
                    }

                    ppalTemaAdapter = new PpalTemaAdapter(temaList, getApplicationContext(), new PpalTemaAdapter.imageButtonPress() {
                        @Override
                        public void onItemClick(Tema item) {
                            movetoDescription(item);
                        }

                    });

                    // 🚀 Aquí conectas el adapter al ViewPager
                    viewPager2.setAdapter(ppalTemaAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Tema>> call, Throwable t) {
                Toast.makeText(PpalTemaActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_ERROR", "Error al cargar los temas", t);
            }
        });
    }

    private void movetoDescription(Tema item) {

        if (item.getIdTema() == 1) {

            Intent intent = new Intent(this, GnMenteActivity.class);
            intent.putExtra("Temas", item);
            startActivity(intent);
        }

        if (item.getIdTema() == 2) {

            Intent intent = new Intent(this, GnRetoRecuReenActivity.class);
            intent.putExtra("Temas", item);
            startActivity(intent);
        }

        if (item.getIdTema() == 3) {

            Intent intent = new Intent(this, GnConcienciaActivity.class);
            intent.putExtra("Temas", item);
            startActivity(intent);
        }

        if (item.getIdTema() == 10014) {

            Intent intent = new Intent(this, GnImpresionActivity.class);
            intent.putExtra("Temas", item);
            startActivity(intent);
        }

        if (item.getIdTema() == 10016) {

            Intent intent = new Intent(this, TaEmocionalActivity.class);
            intent.putExtra("Temas", item);
            startActivity(intent);
        }

    }

    private void setupOnboardingIndicator(){

        ImageView[] indicators = new ImageView[ppalTemaAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(8,0,8,0);
        for(int i = 0; i < indicators.length; i++){

            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),R.drawable.indicator_inactive
            ));

            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicator.addView(indicators[i]);
        }

    }

    private void setCurrentOnBoardingIndicator(int index){

        int chldConut  = layoutOnboardingIndicator.getChildCount();
        for ( int i = 0; i < chldConut; i++){

            ImageView imageView = (ImageView) layoutOnboardingIndicator.getChildAt(i);
            if(i == index){

                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active));

            }else{

                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive ));

            }

        }

        if(index == ppalTemaAdapter.getItemCount() -1) {

            botonBoardingAction.setText("Start");
        }else {

            botonBoardingAction.setText("Next");
        }

    }

}