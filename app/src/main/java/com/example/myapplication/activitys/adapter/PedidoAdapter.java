package com.example.myapplication.activitys.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.model.Pedido;

import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    public interface OnPedidoClickListener {
        void onCancelarPedido(Pedido pedido);

        void onAceptarPedido(Pedido pedido);
    }

    private List<Pedido> listaPedidos;
    private OnPedidoClickListener listener;

    public PedidoAdapter(List<Pedido> listaPedidos, OnPedidoClickListener listener) {
        this.listaPedidos = listaPedidos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = listaPedidos.get(position);
        holder.tvInfo.setText("Cliente: " + pedido.getClienteNombre() +
                "\nDirección: " + pedido.getDireccionEntrega() +
                "\nTotal: $" + pedido.getTotal());

        holder.btnAceptar.setOnClickListener(v -> listener.onAceptarPedido(pedido));
        holder.btnCancelar.setOnClickListener(v -> listener.onCancelarPedido(pedido));
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvInfo;
        Button btnAceptar;
        Button btnCancelar;

        PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInfo = itemView.findViewById(R.id.tvInfoPedido);
            btnAceptar = itemView.findViewById(R.id.btnAceptarPedido);   // 👈 ID correcto
            btnCancelar = itemView.findViewById(R.id.btnCancelarPedido); // 👈 ID correcto
        }
    }
}
