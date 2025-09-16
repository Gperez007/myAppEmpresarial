package com.example.myapplication.activitys.activitys;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.PedidoAdapter;
import com.example.myapplication.activitys.model.Pedido;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class PpalDomiciliarioActivity extends AppCompatActivity implements PedidoAdapter.OnPedidoClickListener {

    private RecyclerView recyclerPedidos;
    private PedidoAdapter pedidoAdapter;
    private List<Pedido> listaPedidos;
    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;
    private Location ubicacionActual;
    private RecyclerView recyclerPedidosAceptados;
    private PedidoAdapter pedidoAceptadosAdapter;
    private List<Pedido> listaPedidosAceptados;
    String cedulaDomiciliario;
    private ListenerRegistration pedidosListener;

    private static final int LOCATION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppal_domiciliario);

        recyclerPedidos = findViewById(R.id.recyclerPedidos);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        recyclerPedidosAceptados = findViewById(R.id.recyclerPedidosAceptados);
        recyclerPedidosAceptados.setLayoutManager(new LinearLayoutManager(this));
        cedulaDomiciliario = getIntent().getStringExtra("cedula");
        if (cedulaDomiciliario != null) {
            // Ya tienes la cédula disponible para usar
            Toast.makeText(this, "Cédula recibida: " + cedulaDomiciliario, Toast.LENGTH_SHORT).show();
        }
        listaPedidosAceptados = new ArrayList<>();
        pedidoAceptadosAdapter = new PedidoAdapter(listaPedidosAceptados, this);
        recyclerPedidosAceptados.setAdapter(pedidoAceptadosAdapter);

        listaPedidos = new ArrayList<>();
        pedidoAdapter = new PedidoAdapter(listaPedidos, this);
        recyclerPedidos.setAdapter(pedidoAdapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        solicitarUbicacion();
        cargarPedidosDisponibles();

        // 👇 Esto faltaba
        cargarPedidosAceptados();
    }



    private void solicitarUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        ubicacionActual = location;
                        guardarUbicacionEnFirestore(location);
                        cargarPedidosDisponibles();
                    } else {
                        Toast.makeText(this, "No se pudo obtener ubicación", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onCancelarPedido(Pedido pedido) {
        db.collection("pedidos").document(pedido.getPedidoId())
                .update(
                        "estado", "pendiente",
                        "domiciliarioId", null
                )
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Pedido cancelado", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cancelar pedido", Toast.LENGTH_SHORT).show()
                );
    }
    private void guardarUbicacionEnFirestore(Location location) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            db.collection("domiciliarios").document(userId)
                    .update("latitud", location.getLatitude(),
                            "longitud", location.getLongitude(),
                            "estado", "disponible")
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Ubicación actualizada", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error guardando ubicación", Toast.LENGTH_SHORT).show());
        }
    }

    private void cargarPedidosDisponibles() {
        if (pedidosListener != null) {
            pedidosListener.remove(); // 👈 cancela el listener previo
        }

        if (cedulaDomiciliario == null) {
            Toast.makeText(this, "No se encontró la cédula del domiciliario", Toast.LENGTH_SHORT).show();
            return;
        }

        pedidosListener = db.collection("pedidos")
                .whereEqualTo("estado", "pendiente")
                .whereEqualTo("domiciliarioId", cedulaDomiciliario) // 👈 filtro por cedula
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("Pedidos", "Error escuchando pedidos", e);
                        return;
                    }

                    if (snapshots == null) return;

                    listaPedidos.clear(); // 👇 limpiamos lista antes de llenarla

                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Pedido pedido = doc.toObject(Pedido.class);
                        if (pedido != null) {
                            pedido.setPedidoId(doc.getId());
                            listaPedidos.add(pedido);
                        }
                    }

                    pedidoAdapter.notifyDataSetChanged();
                });
    }
    @Override
    public void onAceptarPedido(Pedido pedido) {
        // Obtener la cédula que llegó en el intent
        String cedula = getIntent().getStringExtra("cedula");
        if (cedula == null) {
            Toast.makeText(this, "No se encontró la cédula del domiciliario", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("pedidos").document(pedido.getPedidoId())
                .update(
                        "estado", "en curso",
                        "domiciliarioId", cedula  // 👈 guardamos la cédula como referencia
                )
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Pedido aceptado", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al aceptar pedido", Toast.LENGTH_SHORT).show()
                );
    }

    private void cargarPedidosAceptados() {
        String cedula = getIntent().getStringExtra("cedula");
        if (cedula == null) return;

        db.collection("pedidos")
                .whereEqualTo("estado", "en curso")
                .whereEqualTo("domiciliarioId", cedula)  // 👈 filtro por cédula
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("PedidosAceptados", "Error escuchando pedidos aceptados", e);
                        return;
                    }
                    if (snapshots == null) return;

                    listaPedidosAceptados.clear();
                    for (DocumentSnapshot docSnap : snapshots.getDocuments()) {
                        Pedido pedido = docSnap.toObject(Pedido.class);
                        if (pedido != null) {
                            pedido.setPedidoId(docSnap.getId());
                            listaPedidosAceptados.add(pedido);
                        }
                    }
                    pedidoAceptadosAdapter.notifyDataSetChanged();
                });
    }


    // Permisos de ubicación
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            solicitarUbicacion();
        }
    }
}
