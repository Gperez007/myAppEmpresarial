package com.example.myapplication.activitys.activitys;

import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.ProductoAdapter;
import com.example.myapplication.activitys.model.Producto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarritoActivity extends AppCompatActivity {

    private RecyclerView recyclerCarrito;
    private TextView txtTotal;
    private Button btnMetodoPago;

    private List<Producto> carrito;
    private ProductoAdapter adapter;
    double latEmpresa;
    double lngEmpresa;
    double latUsuario;
    double lngUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        recyclerCarrito = findViewById(R.id.recyclerCarrito);
        txtTotal = findViewById(R.id.txtTotal);
        btnMetodoPago = findViewById(R.id.btnMetodoPago);

        latEmpresa = getIntent().getDoubleExtra("lat_empresa", 0.0);
        lngEmpresa = getIntent().getDoubleExtra("lng_empresa", 0.0);
        latUsuario = getIntent().getDoubleExtra("lat_usuario", 0.0);
        lngUsuario = getIntent().getDoubleExtra("lng_usuario", 0.0);


        // Recibir los productos del carrito desde StoreDetailActivity
        carrito = (List<Producto>) getIntent().getSerializableExtra("carrito");

        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        // Configurar RecyclerView
        recyclerCarrito.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductoAdapter(carrito, null); // Aquí no necesitamos listener
        recyclerCarrito.setAdapter(adapter);

        // Calcular y mostrar total
        double total = 0;
        for (Producto p : carrito) {
            total += p.getPrecio(); // Asegúrate de que getPrecio devuelva un double
        }
        txtTotal.setText("Total: $" + total);

        // Botón para método de pago
        btnMetodoPago.setOnClickListener(v -> {
            confirmarPedido("contra_entrega");
            // Aquí podrías abrir un diálogo o nueva Activity para seleccionar pago
        });
    }

    private void confirmarPedido(String metodoPago) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String pedidoId = db.collection("pedidos").document().getId();

        asignarDomiciliarioMasCercano(latEmpresa, lngEmpresa, cedulaDomiciliario -> {
            if (cedulaDomiciliario == null) {
                Toast.makeText(this, "No hay domiciliarios disponibles", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> pedido = new HashMap<>();
            pedido.put("pedidoId", pedidoId);
            pedido.put("clienteId", FirebaseAuth.getInstance().getUid());
            pedido.put("clienteNombre", "Nombre del cliente");
            pedido.put("direccionEntrega", "Dirección del cliente");
            pedido.put("total", calcularTotal());
            pedido.put("estado", "pendiente");
            pedido.put("metodoPago", metodoPago);
            pedido.put("timestamp", System.currentTimeMillis());

            // 👉 Asignar domiciliario más cercano
            pedido.put("domiciliarioId", cedulaDomiciliario);

            // 👉 Guardar también coordenadas de empresa y cliente
            pedido.put("latEmpresa", latEmpresa);
            pedido.put("lngEmpresa", lngEmpresa);
            pedido.put("latCliente", latUsuario);
            pedido.put("lngCliente", lngUsuario);

            db.collection("pedidos").document(pedidoId).set(pedido)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Pedido confirmado ✅", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al crear pedido: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }

    private void asignarDomiciliarioMasCercano(double latEmpresa, double lngEmpresa, OnDomiciliarioEncontrado callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("domiciliarios")
                .whereEqualTo("estado", "activo") // solo domiciliarios disponibles
                .get()
                .addOnSuccessListener(query -> {
                    String cedulaMasCercano = null;
                    float menorDistancia = Float.MAX_VALUE;

                    for (DocumentSnapshot doc : query) {
                        Double latDom = doc.getDouble("latitud");
                        Double lngDom = doc.getDouble("longitud");
                        String cedula = doc.getString("cedula");

                        if (latDom != null && lngDom != null) {
                            float[] resultado = new float[1];
                            Location.distanceBetween(latEmpresa, lngEmpresa, latDom, lngDom, resultado);

                            if (resultado[0] < menorDistancia) {
                                menorDistancia = resultado[0];
                                cedulaMasCercano = cedula;
                            }
                        }
                    }

                    callback.onEncontrado(cedulaMasCercano);
                });
    }

    interface OnDomiciliarioEncontrado {
        void onEncontrado(String cedula);
    }

    private double calcularTotal() {
        double total = 0;
        for (Producto p : carrito) {
            total += p.getPrecio();
        }
        return total;
    }

}
