package com.example.myapplication.activitys.activitys;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.myapplication.R;

public class VentaRapidaDialog {
    public interface VentaListener {
        void onVentaGuardada(Venta venta);
    }

    public static class Venta {
        private double monto;
        private String descripcion;

        public Venta(double monto, String descripcion) {
            this.monto = monto;
            this.descripcion = descripcion;
        }

        public double getMonto() { return monto; }
        public String getDescripcion() { return descripcion; }
    }

    public VentaRapidaDialog(Context context, VentaListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_venta_rapida, null);
        EditText etMonto = view.findViewById(R.id.etMonto);
        EditText etDescripcion = view.findViewById(R.id.etDescripcion);

        new AlertDialog.Builder(context)
                .setTitle("Registrar venta rápida")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String montoStr = etMonto.getText().toString();
                    String descStr = etDescripcion.getText().toString();
                    if (!montoStr.isEmpty()) {
                        double monto = Double.parseDouble(montoStr);
                        listener.onVentaGuardada(new Venta(monto, descStr));
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
