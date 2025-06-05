package com.example.myapplication.activitys.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.model.Promocion;

import java.util.List;

public class PromocionAdapter extends RecyclerView.Adapter<PromocionAdapter.ViewHolder> {

    private List<Promocion> promocionList;
    private OnPromocionClickListener listener;

    public PromocionAdapter(List<Promocion> promocionList, OnPromocionClickListener listener) {
        this.promocionList = promocionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promocion, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Promocion promo = promocionList.get(position);
        holder.tvTitulo.setText(promo.getTitulo());
        holder.tvDescripcion.setText(promo.getDescripcion());
        holder.tvFecha.setText(promo.getFecha());

        holder.btnEditar.setOnClickListener(v -> listener.onEditarClick(promo));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(promo));
    }

    @Override
    public int getItemCount() {
        return promocionList.size();
    }

    public void actualizarLista(List<Promocion> nuevaLista) {
        this.promocionList = nuevaLista;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion, tvFecha;
        Button btnEditar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

    public interface OnPromocionClickListener {
        void onEditarClick(Promocion promocion);
        void onEliminarClick(Promocion promocion);
    }
}
