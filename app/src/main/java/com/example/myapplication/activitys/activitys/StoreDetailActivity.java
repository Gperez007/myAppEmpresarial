package com.example.myapplication.activitys.activitys;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.ProductoAdapter;
import com.example.myapplication.activitys.model.Producto;
import com.example.myapplication.activitys.network.ApiService;
import com.example.myapplication.activitys.network.ApiService;
import com.example.myapplication.activitys.network.Apiclient;
import com.google.android.material.navigation.NavigationView;
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

        // ----------------------
        // Drawer y navegación
        // ----------------------
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        ImageButton btnMenu = findViewById(R.id.btnMenu);

        // Abrir Drawer al pulsar el botón de menú
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // ----------------------
        // Carrito
        // ----------------------
        TextView txtCarritoCount = findViewById(R.id.txtCarritoCount);
        carrito = new ArrayList<>();

        txtCarritoCount.setOnClickListener(v -> {
            Intent intent = new Intent(StoreDetailActivity.this, CarritoActivity.class);
            intent.putExtra("carrito", new ArrayList<>(carrito));
            startActivity(intent);
        });

        // ----------------------
        // Coordenadas
        // ----------------------
        latUsuario = getIntent().getDoubleExtra("lat_usuario", 0.0);
        lngUsuario = getIntent().getDoubleExtra("lng_usuario", 0.0);
        latEmpresa = getIntent().getDoubleExtra("lat_empresa", 0.0);
        lngEmpresa = getIntent().getDoubleExtra("lng_empresa", 0.0);

        // ----------------------
        // RecyclerView de productos
        // ----------------------
        recyclerProductos = findViewById(R.id.recyclerProductos);
        listaProductos = new ArrayList<>();
        adapter = new ProductoAdapter(listaProductos, producto -> {
            carrito.add(producto);
            txtCarritoCount.setText("Productos en carrito: " + carrito.size());
            Toast.makeText(this, producto.getNombre() + " agregado al carrito", Toast.LENGTH_SHORT).show();
        });
        recyclerProductos.setAdapter(adapter);

        // Grid de 2 columnas
        recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));

        db = FirebaseFirestore.getInstance();

        // ----------------------
        // Información de la tienda
        // ----------------------
        String nombreEmpresa = getIntent().getStringExtra("nombre");
        String nit = getIntent().getStringExtra("nit");
        telefonoEmpresa = getIntent().getStringExtra("telefono");
        String direccion = getIntent().getStringExtra("direccion");
        String politicaReclamos = getIntent().getStringExtra("politicaReclamos");
        String politicaDevolucion = getIntent().getStringExtra("politicaDevolucion");

        TextView txtNombre = findViewById(R.id.storeName);
        TextView txtDireccion = findViewById(R.id.storeAddress);

        txtNombre.setText(nombreEmpresa != null ? nombreEmpresa : "Nombre no disponible");
        txtDireccion.setText(direccion != null ? direccion : "Dirección no disponible");

        // ----------------------
        // Políticas
        // ----------------------
        // Puedes usar el primer item del menú para "Chat/políticas" o un LinearLayout extra
        navigationView.getMenu().findItem(R.id.menu_chat).setOnMenuItemClickListener(item -> {
            String mensaje = "";
            mensaje += (politicaReclamos != null && !politicaReclamos.isEmpty()) ?
                    "Política de Reclamos:\n" + politicaReclamos + "\n\n" :
                    "Política de Reclamos: No disponible\n\n";
            mensaje += (politicaDevolucion != null && !politicaDevolucion.isEmpty()) ?
                    "Política de Devoluciones:\n" + politicaDevolucion :
                    "Política de Devoluciones: No disponible";

            new AlertDialog.Builder(StoreDetailActivity.this)
                    .setTitle("Políticas de la Empresa")
                    .setMessage(mensaje)
                    .setPositiveButton("Cerrar", null)
                    .show();

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // ----------------------
        // Menú lateral NavigationView
        // ----------------------
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_order) {
                Toast.makeText(this, "Función de pedir a domicilio aún no disponible", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.menu_map) {
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("lat_empresa", latEmpresa);
                intent.putExtra("lng_empresa", lngEmpresa);
                intent.putExtra("lat_usuario", latUsuario);
                intent.putExtra("lng_usuario", lngUsuario);
                intent.putExtra("nombre", nombreEmpresa);
                startActivity(intent);
            } else if (id == R.id.menu_favorite) {
                Toast.makeText(this, "Tienda guardada como favorita", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.menu_comments) {
                Toast.makeText(this, "Ver comentarios...", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.menu_share) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Te recomiendo visitar " + nombreEmpresa);
                startActivity(Intent.createChooser(sendIntent, "Compartir vía"));
            } else if (id == R.id.menu_chat) {
                Toast.makeText(this, "Abrir chat de atención al cliente...", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // ----------------------
        // Botones fuera del Drawer
        // ----------------------
        Button btnCall = findViewById(R.id.btnCall);
        Button btnPromociones = findViewById(R.id.btnPromociones);

        btnCall.setOnClickListener(v -> {
            if (telefonoEmpresa != null && !telefonoEmpresa.isEmpty()) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telefonoEmpresa));
                startActivity(callIntent);
            } else {
                Toast.makeText(this, "Teléfono no disponible", Toast.LENGTH_SHORT).show();
            }
        });

        btnPromociones.setOnClickListener(v -> {
            String empresaID = getIntent().getStringExtra("empresaID");
            if (empresaID != null && !empresaID.isEmpty()) {
                cargarPromociones(empresaID);
            } else {
                Toast.makeText(this, "ID de empresa no disponible", Toast.LENGTH_SHORT).show();
            }
        });

        // ----------------------
        // Cargar productos
        // ----------------------
        cargarProductos(nit);
    }

    // Método para abrir carrito
    private void btnCarritoClick(TextView txtCarritoCount) {
        txtCarritoCount.setOnClickListener(v -> {
            Intent intent = new Intent(StoreDetailActivity.this, CarritoActivity.class);
            intent.putExtra("carrito", new ArrayList<>(carrito));
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
