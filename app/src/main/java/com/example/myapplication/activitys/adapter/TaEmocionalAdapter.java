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
import com.example.myapplication.activitys.model.TaEmocional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaEmocionalAdapter extends RecyclerView.Adapter<TaEmocionalAdapter.RecyclerHolder>{

    private List<TaEmocional> items;

    private List<TaEmocional> originalItems;

    private RecyclerItemClickTaEmocional itemClick;

    int[] datosImage = {R.drawable.trabajo, R.drawable.marketing, R.drawable.educationpng, R.drawable.salud, R.drawable.the_fighter,R.drawable.avengers_infinity_war, R.drawable.contracara, R.drawable.the_wolf_of_wall_street, R.drawable.underworld, R.drawable.the_fighter};

    public TaEmocionalAdapter(List<TaEmocional> items) {
        this.items = items;
        this.originalItems = new ArrayList<>();
        originalItems.addAll(items);
    }

    @NonNull
    @Override
    public TaEmocionalAdapter.RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ta_emocional_list, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaEmocionalAdapter.RecyclerHolder holder, int position) {

        final TaEmocional item = items.get(position);
        holder.imgItem.setImageResource(datosImage[position]);
        holder.tvTitulo.setText(item.getEmocional());


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
        return items.size();
    }

    public void filter(final String strSearch) {
        if (strSearch.length() == 0) {
            items.clear();
            items.addAll(originalItems);
        }
        else {
            items.clear();
            items.addAll(originalItems);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                List<TaEmocional> collect = items.stream()
                        .filter(i -> i.getEmocional().toLowerCase().contains(strSearch))
                        .collect(Collectors.toList());

                items.clear();
                items.addAll(collect);
            }
            else {
                items.clear();
                for (TaEmocional i : originalItems) {
                    if (i.getEmocional().toLowerCase().contains(strSearch)) {
                        items.add(i);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder {
        private ImageView imgItem;
        private TextView tvTitulo;

        public RecyclerHolder(@NonNull View itemView_1) {
            super(itemView_1);

            imgItem = itemView.findViewById(R.id.imageShowEmocional);
            tvTitulo = itemView.findViewById(R.id.textNameEmocional);
        }
    }

    public interface RecyclerItemClickTaEmocional {
        void itemClick(TaEmocional item);
    }
}
