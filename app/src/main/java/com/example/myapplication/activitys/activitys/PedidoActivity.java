package com.example.myapplication.activitys.activitys;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class PedidoActivity extends AppCompatActivity {

    private TextView tvCantidad;
    private TextView tvTotal;
    private TextView tvCarrito;
    private int cantidad = 1;
    private double precioUnitario = 10.0;
    private List<String> carrito = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedido_activity);

        tvCantidad = findViewById(R.id.tvCantidad);
        tvTotal = findViewById(R.id.tvTotal);
        tvCarrito = findViewById(R.id.tvCarrito);

        Button btnMenos = findViewById(R.id.btnMenos);
        Button btnMas = findViewById(R.id.btnMas);
        Button btnAgregar = findViewById(R.id.btnAgregar);
        Button btnConfirmar = findViewById(R.id.btnConfirmar);

        btnMenos.setOnClickListener(v -> {
            if (cantidad > 1) {
                cantidad--;
                actualizarTotal();
            }
        });

        btnMas.setOnClickListener(v -> {
            cantidad++;
            actualizarTotal();
        });

        btnAgregar.setOnClickListener(v -> {
            String item = "Café Especial x" + cantidad + " - $" + String.format("%.2f", cantidad * precioUnitario);
            carrito.add(item);
            actualizarCarrito();
        });

        btnConfirmar.setOnClickListener(v -> {
            if (!carrito.isEmpty()) {
                Toast.makeText(PedidoActivity.this, "Pedido confirmado", Toast.LENGTH_SHORT).show();
                carrito.clear();
                actualizarCarrito();
            } else {
                Toast.makeText(PedidoActivity.this, "Agrega productos al carrito", Toast.LENGTH_SHORT).show();
            }
        });

        actualizarTotal();
    }

    private void actualizarTotal() {
        tvCantidad.setText(String.valueOf(cantidad));
        double total = cantidad * precioUnitario;
        tvTotal.setText("Total: $" + String.format("%.2f", total));
    }

    private void actualizarCarrito() {
        if (carrito.isEmpty()) {
            tvCarrito.setText("Carrito vacío");
        } else {
            StringBuilder contenido = new StringBuilder("Carrito:\n");
            for (String item : carrito) {
                contenido.append(item).append("\n");
            }
            tvCarrito.setText(contenido.toString());
        }
    }
}
