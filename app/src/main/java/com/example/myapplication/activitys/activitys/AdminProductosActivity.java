package com.example.myapplication.activitys.activitys;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.ProductoAdapter;
import com.example.myapplication.activitys.model.Producto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminProductosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductoAdapter productoAdapter;
    private List<Producto> listaProductos;
    private FloatingActionButton fabAgregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_productos);

        recyclerView = findViewById(R.id.recyclerViewProductos);
        fabAgregar = findViewById(R.id.fabAgregarProducto);

        listaProductos = new ArrayList<>();
        listaProductos.add(new Producto("Camiseta", "Camiseta de algodón", 29.99));
        listaProductos.add(new Producto("Pantalón", "Pantalón jeans azul", 49.99));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productoAdapter = new ProductoAdapter(listaProductos);
        recyclerView.setAdapter(productoAdapter);

        fabAgregar.setOnClickListener(v -> {
            Toast.makeText(this, "Agregar nuevo producto (por implementar)", Toast.LENGTH_SHORT).show();
        });
    }
}
