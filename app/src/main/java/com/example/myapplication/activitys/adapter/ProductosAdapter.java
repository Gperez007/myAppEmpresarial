package com.example.myapplication.activitys.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.activitys.model.ProductoLocation;

import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {

    private List<ProductoLocation> productos;

    public ProductosAdapter(List<ProductoLocation> productos) {
        this.productos = productos;
    }

    public void setProductos(List<ProductoLocation> nuevosProductos) {
        productos = nuevosProductos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        tv.setPadding(20, 20, 20, 20);
        tv.setTextSize(16);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductoLocation p = productos.get(position);
        ((TextView) holder.itemView).setText(p.getNombre());
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
