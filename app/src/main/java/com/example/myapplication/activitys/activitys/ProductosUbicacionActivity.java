package com.example.myapplication.activitys.activitys;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.ProductosAdapter;
import com.example.myapplication.activitys.model.ProductoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class ProductosUbicacionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SeekBar seekBarRango;
    private TextView tvRangoValor;
    private RecyclerView rvProductos;

    private double rangoKm = 10.0;
    private Location ubicacionUsuario;

    private List<ProductoLocation> listaProductos = new ArrayList<>();
    private ProductosAdapter adapter;

    private MapView mapView;
    private GoogleMap googleMap;

    private static final LatLng UBICACION_USUARIO = new LatLng(5.7146, -72.9335);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_ubicacion);

        seekBarRango = findViewById(R.id.seekBarRango);
        tvRangoValor = findViewById(R.id.tvRangoValor);
        rvProductos = findViewById(R.id.rvProductos);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        seekBarRango.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rangoKm = progress;
                tvRangoValor.setText(progress + " km");
                if (googleMap != null) {
                    googleMap.clear();
                    mostrarMarcadores();
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UBICACION_USUARIO, 12f));
        mostrarMarcadores();
    }

    private void mostrarMarcadores() {
        googleMap.addMarker(new MarkerOptions()
                .position(UBICACION_USUARIO)
                .title("Tú estás aquí"));

        googleMap.addCircle(new CircleOptions()
                .center(UBICACION_USUARIO)
                .radius(rangoKm * 1000)
                .strokeColor(Color.BLUE)
                .fillColor(0x22007AFF)
                .strokeWidth(2f));

        for (ProductoLocation producto : listaProductos) {
            LatLng pos = new LatLng(producto.getLat(), producto.getLng());
            googleMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(producto.getNombre()));
        }
    }

    // Métodos ciclo de vida de MapView
    @Override protected void onStart() { super.onStart(); mapView.onStart(); }
    @Override protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override protected void onPause() { super.onPause(); mapView.onPause(); }
    @Override protected void onStop() { super.onStop(); mapView.onStop(); }
    @Override protected void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}