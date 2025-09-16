package com.example.myapplication.activitys.activitys;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    boolean mostrarEmpresas = false;
    // Coordenadas fijas (Sogamoso, Boyacá)
    private static final LatLng UBICACION_FIJA = new LatLng(5.7146, -72.9335);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mostrarEmpresas = getIntent().getBooleanExtra("mostrarEmpresas", false);

        if(mostrarEmpresas){
            inicializarMapa();
        }else {
            inicializarMapaPromociones();

        }


    }

    private void inicializarMapaPromociones() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Error al cargar el mapa", Toast.LENGTH_SHORT).show();
        }
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
        mMap = googleMap;  // Esto lo puedes poner arriba, para no repetir en los dos if

        if (!mostrarEmpresas) if (!mostrarEmpresas) {
            // Obtener coordenadas del cliente y empresa desde el Intent
            double clienteLat = getIntent().getDoubleExtra("lat_usuario", 0.0);
            double clienteLng = getIntent().getDoubleExtra("lng_usuario", 0.0);
            double empresaLat = getIntent().getDoubleExtra("lat_empresa", 0.0);
            double empresaLng = getIntent().getDoubleExtra("lng_empresa", 0.0);
            String nombreEmpresa = getIntent().getStringExtra("nombre");

            LatLng ubicacionCliente = new LatLng(clienteLat, clienteLng);
            LatLng ubicacionEmpresa = new LatLng(empresaLat, empresaLng);

            // Añadir marcador del cliente
            mMap.addMarker(new MarkerOptions()
                    .position(ubicacionCliente)
                    .title("Tu ubicación"));

            // Añadir marcador de la empresa
            mMap.addMarker(new MarkerOptions()
                    .position(ubicacionEmpresa)
                    .title(nombreEmpresa != null ? nombreEmpresa : "Empresa"));

            // Dibujar ruta
            obtenerRuta(ubicacionCliente, ubicacionEmpresa);

            // Ajustar cámara para mostrar ambos puntos
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(ubicacionCliente);
            boundsBuilder.include(ubicacionEmpresa);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null && mapFragment.getView() != null) {
                mapFragment.getView().post(() -> {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));
                });
            }

            // Calcular distancia
            float[] resultados = new float[1];
            Location.distanceBetween(clienteLat, clienteLng, empresaLat, empresaLng, resultados);
            float distanciaMetros = resultados[0];
            Log.d("DISTANCIA", "Distancia a empresa: " + distanciaMetros + " metros");
        } else if (mostrarEmpresas) {
            // Coordenadas del cliente desde el Intent
            double clienteLat = getIntent().getDoubleExtra("lat", 0.0);
            double clienteLng = getIntent().getDoubleExtra("lng", 0.0);
            LatLng ubicacionCliente = new LatLng(clienteLat, clienteLng);

            // Coordenadas de la promoción desde el Intent
            String titulo = getIntent().getStringExtra("titulo");
            String descripcion = getIntent().getStringExtra("descripcion");
            double promoLat = getIntent().getDoubleExtra("latitud", 0.0);
            double promoLng = getIntent().getDoubleExtra("longitud", 0.0);
            LatLng ubicacionPromo = new LatLng(promoLat, promoLng);

            // Crear bounds para ajustar la cámara
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(ubicacionCliente); // Incluir cliente
            boundsBuilder.include(ubicacionPromo);   // Incluir promoción

            // Añadir marcador del cliente
            mMap.addMarker(new MarkerOptions()
                    .position(ubicacionCliente)
                    .title("Tu ubicación"));

            // Añadir marcador de la promoción
            mMap.addMarker(new MarkerOptions()
                    .position(ubicacionPromo)
                    .title(titulo != null ? titulo : "Promoción")
                    .snippet(descripcion != null ? descripcion : ""));

            // Dibujar ruta desde cliente hasta promoción
            obtenerRuta(ubicacionCliente, ubicacionPromo);

            // Calcular distancia
            float[] resultados = new float[1];
            Location.distanceBetween(clienteLat, clienteLng, promoLat, promoLng, resultados);
            float distanciaMetros = resultados[0];

            Log.d("DISTANCIA", (titulo != null ? titulo : "Promoción") + ": " + distanciaMetros + " metros");

            // Ajustar cámara para mostrar ambos puntos DE MANERA SEGURA
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null && mapFragment.getView() != null) {
                mapFragment.getView().post(() -> {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));
                });
            }
        }
    }


    private void obtenerRuta(LatLng origen, LatLng destino) {
        String url = getDirectionsUrl(origen, destino);
        new ObtenerRutaTask().execute(url);
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Reemplaza con tu propia API KEY
        String apiKey = "AIzaSyC0N9XHR9B-ezYm50C3Lnlrkuw7Rq3FZ-8";
        return "https://maps.googleapis.com/maps/api/directions/json?origin="
                + origin.latitude + "," + origin.longitude
                + "&destination=" + dest.latitude + "," + dest.longitude
                + "&sensor=false&mode=driving&key=" + apiKey;
    }

    private class ObtenerRutaTask extends AsyncTask<String, Void, List<LatLng>> {
        @Override
        protected List<LatLng> doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                JSONObject jsonObject = new JSONObject(json.toString());
                JSONArray routes = jsonObject.getJSONArray("routes");
                if (routes.length() == 0) return null;
                JSONObject route = routes.getJSONObject(0);
                JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                String encodedPoints = overviewPolyline.getString("points");
                return decodePoly(encodedPoints);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<LatLng> puntos) {
            if (puntos != null) {
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(puntos)
                        .color(Color.BLUE)
                        .width(10);
                mMap.addPolyline(polylineOptions);
            }
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(((double) lat / 1E5), ((double) lng / 1E5));
            poly.add(p);
        }

        return poly;
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
