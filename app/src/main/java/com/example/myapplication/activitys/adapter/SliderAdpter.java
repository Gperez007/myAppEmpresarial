package com.example.myapplication.activitys.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.model.OnBoardItem;
import com.example.myapplication.activitys.model.Tema;

import java.util.List;

public class SliderAdpter extends RecyclerView.Adapter<SliderAdpter.ViewHolder> {

   List<Tema> temas;

    int[] datosImage = {R.drawable.avengers_infinity_war, R.drawable.contracara, R.drawable.the_wolf_of_wall_street, R.drawable.underworld, R.drawable.the_fighter};


    public SliderAdpter(List<Tema> temas) {
        this.temas = temas;
    }

    @NonNull
    @Override
    public SliderAdpter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item_container,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderAdpter.ViewHolder holder, int position) {
        holder.setOnboardingData(temas.get(position));

    }

    @Override
    public int getItemCount() {
        return temas.size();
    }

    class  ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView nameTextTitulo;
        private TextView nameTextDescripcion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextTitulo = itemView.findViewById(R.id.textTitle);
            nameTextDescripcion = itemView.findViewById(R.id.textDescription);
            imageView = itemView.findViewById(R.id.imageIcon);

        }

        public void setOnboardingData(Tema tema ) {

            nameTextTitulo.setText(tema.getNombreTema());
            nameTextDescripcion.setText(tema.getNombreTema());
            imageView.setImageResource(datosImage.length);
        }
    }
}
