package com.example.myapplication.activitys.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.model.Promocion;

import java.util.List;

public class PromocionAdapter extends RecyclerView.Adapter<PromocionAdapter.ViewHolder> {

    private List<Promocion> promocionList;

    public PromocionAdapter(List<Promocion> promocionList) {
        this.promocionList = promocionList;
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
    }

    @Override
    public int getItemCount() {
        return promocionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion, tvFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}
