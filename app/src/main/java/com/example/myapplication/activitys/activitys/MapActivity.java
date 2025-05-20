package com.example.myapplication.activitys.activitys;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // Coordenadas fijas (Sogamoso, Boyacá)
    private static final LatLng UBICACION_FIJA = new LatLng(5.7146, -72.9335);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        inicializarMapa();
    }

    private void inicializarMapa() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Error al cargar el mapa", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Centrar el mapa en la ubicación fija y añadir marcador
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UBICACION_FIJA, 15));
        mMap.addMarker(new MarkerOptions().position(UBICACION_FIJA).title("Sogamoso, Boyacá"));

        // Buscar lugares cercanos
        buscarLugaresCercanos(UBICACION_FIJA.latitude, UBICACION_FIJA.longitude, "restaurant");
    }

    private void buscarLugaresCercanos(double latitud, double longitud, String tipoNegocio) {
        String apiKey = getString(R.string.google_maps_key);

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                + "?location=" + latitud + "," + longitud
                + "&radius=2000"
                + "&type=" + tipoNegocio
                + "&key=" + apiKey;

        Log.d("API_URL", url);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.optJSONArray("results");
                        if (results == null || results.length() == 0) {
                            Toast.makeText(this, "No se encontraron lugares cercanos", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject lugar = results.getJSONObject(i);
                            JSONObject location = lugar.getJSONObject("geometry").getJSONObject("location");
                            String nombre = lugar.getString("name");
                            double lat = location.getDouble("lat");
                            double lng = location.getDouble("lng");
                            LatLng pos = new LatLng(lat, lng);

                            mMap.addMarker(new MarkerOptions().position(pos).title(nombre));
                        }
                    } catch (JSONException e) {
                        Log.e("JSON_Error", "Error al procesar datos: " + e.getMessage());
                        Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Volley_Error", "Error en la petición: " + error.toString());
                    Toast.makeText(this, "Error al buscar lugares", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }
}
