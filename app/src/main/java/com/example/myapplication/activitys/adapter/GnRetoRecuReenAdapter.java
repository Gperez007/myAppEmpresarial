package com.example.myapplication.activitys.adapter;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.activitys.MainActivity;
import com.example.myapplication.activitys.model.GnRetoRecuReen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GnRetoRecuReenAdapter extends RecyclerView.Adapter<GnRetoRecuReenAdapter.RecyclerHolder> {

    private List<GnRetoRecuReen> taEstadoAnimoList;
    private List<GnRetoRecuReen> originalItems;
    private GnRetoRecuReenAdapter.RecyclerItemClick itemClick;

    int[] datosImage = {R.drawable.trabajo, R.drawable.marketing, R.drawable.educationpng, R.drawable.salud, R.drawable.the_fighter,R.drawable.avengers_infinity_war, R.drawable.contracara, R.drawable.the_wolf_of_wall_street, R.drawable.underworld, R.drawable.the_fighter};

    public GnRetoRecuReenAdapter(List<GnRetoRecuReen> taEstadoAnimoList) {
        this.taEstadoAnimoList = taEstadoAnimoList;
        this.originalItems = new ArrayList<>();
        originalItems.addAll(taEstadoAnimoList);
    }

    @NonNull
    @Override
    public GnRetoRecuReenAdapter.RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gn_reto_recu_reen_list, parent, false);
        return new GnRetoRecuReenAdapter.RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GnRetoRecuReenAdapter.RecyclerHolder holder, int position) {

        final GnRetoRecuReen item = taEstadoAnimoList.get(position);
        holder.imgItem.setImageResource(datosImage[position]);
        holder.tvTitulo.setText(item.getAsunto());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.itemClick(item);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                //intent.putExtra("itemDetail", item);
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return taEstadoAnimoList.size();
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder {
        private ImageView imgItem;
        private TextView tvTitulo;

        public RecyclerHolder(@NonNull View itemView_1) {
            super(itemView_1);

            imgItem = itemView.findViewById(R.id.imageShowGnRetoRecuReen);
            tvTitulo = itemView.findViewById(R.id.textNameGnRetoRecuReen);
        }
    }

    public interface RecyclerItemClick {
        void itemClick(GnRetoRecuReen item);
    }

    public void filter(final String strSearch) {
        if (strSearch.length() == 0) {
            taEstadoAnimoList.clear();
            taEstadoAnimoList.addAll(originalItems);
        }
        else {
            taEstadoAnimoList.clear();
            taEstadoAnimoList.addAll(originalItems);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                List<GnRetoRecuReen> collect = taEstadoAnimoList.stream()
                        .filter(i -> i.getAsunto().toLowerCase().contains(strSearch))
                        .collect(Collectors.toList());

                taEstadoAnimoList.clear();
                taEstadoAnimoList.addAll(collect);
            }
            else {
                taEstadoAnimoList.clear();
                for (GnRetoRecuReen i : originalItems) {
                    if (i.getAsunto().toLowerCase().contains(strSearch)) {
                        taEstadoAnimoList.add(i);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }


}
