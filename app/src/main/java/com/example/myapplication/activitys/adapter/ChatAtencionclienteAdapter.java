package com.example.myapplication.activitys.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.model.Mensaje;

import java.util.List;

public class ChatAtencionclienteAdapter extends RecyclerView.Adapter<ChatAtencionclienteAdapter.ViewHolder> {

    private List<Mensaje> mensajes;

    public ChatAtencionclienteAdapter(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public int getItemViewType(int position) {
        return mensajes.get(position).getEmisor().equals("usuario") ? 0 : 1;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == 0 ? R.layout.item_usuario : R.layout.item_bot;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mensajes.get(position));
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMensaje;

        ViewHolder(View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.textMensaje);
        }

        void bind(Mensaje mensaje) {
            txtMensaje.setText(mensaje.getContenido());
        }
    }

}
