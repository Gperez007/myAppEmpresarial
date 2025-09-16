package com.example.myapplication.activitys.activitys;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.ProductoAdapter;
import com.example.myapplication.activitys.model.Producto;
import com.example.myapplication.activitys.network.ApiService;
import com.example.myapplication.activitys.network.ApiService;
import com.example.myapplication.activitys.network.Apiclient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreDetailActivity extends AppCompatActivity {

    private double lngUsuario, latUsuario;
    private double latEmpresa, lngEmpresa;
    private RecyclerView recyclerProductos;
    private String telefonoEmpresa;

    private FirebaseFirestore db;
    private ProductoAdapter adapter;
    private List<Producto> listaProductos;
    private List<Producto> carrito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        TextView txtCarritoCount = findViewById(R.id.txtCarritoCount);
        // Obtener coordenadas
        latUsuario = getIntent().getDoubleExtra("lat_usuario", 0.0);
        lngUsuario = getIntent().getDoubleExtra("lng_usuario", 0.0);
        latEmpresa = getIntent().getDoubleExtra("lat_empresa", 0.0);
        lngEmpresa = getIntent().getDoubleExtra("lng_empresa", 0.0);
        recyclerProductos = findViewById(R.id.recyclerProductos);
        listaProductos = new ArrayList<>();
        carrito = new ArrayList<>();
        adapter = new ProductoAdapter(listaProductos, producto -> {
            // Agregar al carrito
            carrito.add(producto);

            // Actualizar el contador en el TextView
            txtCarritoCount.setText("Productos en carrito: " + carrito.size());

            Toast.makeText(this, producto.getNombre() + " agregado al carrito", Toast.LENGTH_SHORT).show();
        });
        recyclerProductos.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        // Obtener datos de la empresa
        String nombreEmpresa = getIntent().getStringExtra("nombre");
        String nit = getIntent().getStringExtra("nit");
        telefonoEmpresa = getIntent().getStringExtra("telefono");
        String direccion = getIntent().getStringExtra("direccion");
        String politicaReclamos = getIntent().getStringExtra("politicaReclamos");
        String politicaDevolucion = getIntent().getStringExtra("politicaDevolucion");
        // Enlazar vistas
        TextView txtNombre = findViewById(R.id.storeName);
        TextView txtDireccion = findViewById(R.id.storeAddress);
        TextView txtTelefono = findViewById(R.id.storePhone);
        LinearLayout politicasLayout = findViewById(R.id.politicasLayout);
        Button btnChatCliente = findViewById(R.id.btnChatCliente);
        cargarProductos(nit);
        RecyclerView recyclerProductos = findViewById(R.id.recyclerProductos);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));

        // Asignar datos
        txtNombre.setText(nombreEmpresa != null ? nombreEmpresa : "Nombre no disponible");
        txtDireccion.setText(direccion != null ? direccion : "Dirección no disponible");
        txtTelefono.setText("Teléfono: " + (telefonoEmpresa != null ? telefonoEmpresa : "No disponible"));

        // Mostrar políticas en un modal al hacer clic
        politicasLayout.setOnClickListener(v -> {
            String mensaje = "";

            if (politicaReclamos != null && !politicaReclamos.isEmpty()) {
                mensaje += "Política de Reclamos:\n" + politicaReclamos + "\n\n";
            } else {
                mensaje += "Política de Reclamos: No disponible\n\n";
            }

            if (politicaDevolucion != null && !politicaDevolucion.isEmpty()) {
                mensaje += "Política de Devoluciones:\n" + politicaDevolucion;
            } else {
                mensaje += "Política de Devoluciones: No disponible";
            }

            new AlertDialog.Builder(StoreDetailActivity.this)
                    .setTitle("Políticas de la Empresa")
                    .setMessage(mensaje)
                    .setPositiveButton("Cerrar", null)
                    .show();
        });

        // Botón Chat de atención al cliente
        btnChatCliente.setOnClickListener(v -> {
            // En el futuro puedes abrir una actividad de chat real aquí
            Toast.makeText(this, "Abrir chat de atención al cliente...", Toast.LENGTH_SHORT).show();
        });

        // Botones restantes
        Button btnCall = findViewById(R.id.btnCall);
        Button btnOrder = findViewById(R.id.btnOrder);
        Button btnMap = findViewById(R.id.btnMap);
        Button btnFavorite = findViewById(R.id.btnFavorite);
        Button btnShare = findViewById(R.id.btnShare);
        Button btnComments = findViewById(R.id.btnComments);

        Button btnPromociones = findViewById(R.id.btnPromociones); // Debes ponerlo en tu XML
        btnPromociones.setOnClickListener(v -> {
            String empresaID = getIntent().getStringExtra("empresaID");
            if (empresaID != null && !empresaID.isEmpty()) {
                cargarPromociones(empresaID);
            } else {
                Toast.makeText(this, "ID de empresa no disponible", Toast.LENGTH_SHORT).show();
            }
        });

        btnCall.setOnClickListener(v -> {
            if (telefonoEmpresa != null && !telefonoEmpresa.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + telefonoEmpresa));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Teléfono no disponible", Toast.LENGTH_SHORT).show();
            }
        });

        btnOrder.setOnClickListener(v -> {
            Toast.makeText(this, "Función de pedir a domicilio aún no disponible", Toast.LENGTH_SHORT).show();
        });

        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("lat_empresa", latEmpresa);
            intent.putExtra("lng_empresa", lngEmpresa);
            intent.putExtra("lat_usuario", latUsuario);
            intent.putExtra("lng_usuario", lngUsuario);
            intent.putExtra("nombre", nombreEmpresa);
            startActivity(intent);
        });

        btnFavorite.setOnClickListener(v -> {
            Toast.makeText(this, "Tienda guardada como favorita", Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Te recomiendo visitar " + nombreEmpresa);
            startActivity(Intent.createChooser(sendIntent, "Compartir vía"));
        });

        btnComments.setOnClickListener(v -> {
            Toast.makeText(this, "Ver comentarios...", Toast.LENGTH_SHORT).show();
        });

        txtCarritoCount.setOnClickListener(v -> {
            Intent intent = new Intent(StoreDetailActivity.this, CarritoActivity.class);
            intent.putExtra("carrito", new ArrayList<>(carrito)); // productos
            intent.putExtra("lat_empresa", latEmpresa);
            intent.putExtra("lng_empresa", lngEmpresa);
            intent.putExtra("lat_usuario", latUsuario);
            intent.putExtra("lng_usuario", lngUsuario);
            startActivity(intent);
        });
    }

    private void cargarProductos(String nit) {
        db.collection("empresas")
                .whereEqualTo("nit", nit)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot empresaDoc : task.getResult()) {
                            empresaDoc.getReference()
                                    .collection("productos")
                                    .get()
                                    .addOnCompleteListener(productosTask -> {
                                        if (productosTask.isSuccessful()) {
                                            listaProductos.clear();
                                            for (QueryDocumentSnapshot productoDoc : productosTask.getResult()) {
                                                Producto producto = productoDoc.toObject(Producto.class);
                                                listaProductos.add(producto);
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    } else {
                        Log.e("Firebase", "No se encontró empresa con ese NIT");
                    }
                });
    }


    private void cargarPromociones(String empresaID) {
        db.collection("promocionesGlobales")
                .whereEqualTo("empresaUID", empresaID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            StringBuilder mensaje = new StringBuilder();
                            for (DocumentSnapshot promoDoc : task.getResult()) {
                                String titulo = promoDoc.getString("titulo");
                                String descripcion = promoDoc.getString("descripcion");
                                String fecha = promoDoc.getString("fecha");

                                mensaje.append("📌 ").append(titulo != null ? titulo : "Sin título").append("\n")
                                        .append(descripcion != null ? descripcion : "Sin descripción").append("\n")
                                        .append("Fecha: ").append(fecha != null ? fecha : "No disponible").append("\n\n");
                            }

                            new AlertDialog.Builder(StoreDetailActivity.this)
                                    .setTitle("Promociones de la Empresa")
                                    .setMessage(mensaje.toString())
                                    .setPositiveButton("Cerrar", null)
                                    .show();
                        } else {
                            Toast.makeText(this, "No hay promociones disponibles", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Firebase", "Error al obtener promociones", task.getException());
                        Toast.makeText(this, "Error al cargar promociones", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
