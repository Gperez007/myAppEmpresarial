package com.example.myapplication.activitys.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.model.PromocionVistaCliente;

import java.util.List;

public class PromocionClienteAdapter extends RecyclerView.Adapter<PromocionClienteAdapter.ViewHolder> {

    private List<PromocionVistaCliente> promocionList;
    private OnPromocionClickListener listener;

    // Constructor CORRECTO: recibe lista + listener
    public PromocionClienteAdapter(List<PromocionVistaCliente> promocionList, OnPromocionClickListener listener) {
        this.promocionList = promocionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_promocion_cliente, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PromocionVistaCliente promo = promocionList.get(position);
        holder.tvTitulo.setText(promo.getTitulo());
        holder.tvDescripcion.setText(promo.getDescripcion());
        holder.tvFecha.setText(promo.getFecha());

        // Click del botón "Ir al mapa" llama a la interfaz
        holder.btnIrMapa.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIrMapaClick(promo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return promocionList.size();
    }

    public void actualizarLista(List<PromocionVistaCliente> nuevaLista) {
        this.promocionList = nuevaLista;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion, tvFecha;
        Button btnIrMapa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloPromocion);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionPromocion);
            tvFecha = itemView.findViewById(R.id.tvFechaPromocion);
            btnIrMapa = itemView.findViewById(R.id.btnIrMapa);
        }
    }

    // Interfaz que expone el click del botón
    public interface OnPromocionClickListener {
        void onIrMapaClick(PromocionVistaCliente promocion);
    }
}
