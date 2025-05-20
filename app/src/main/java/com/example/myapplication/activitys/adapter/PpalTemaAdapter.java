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
import com.example.myapplication.activitys.model.Tema;

import java.util.List;


public class PpalTemaAdapter extends RecyclerView.Adapter<PpalTemaAdapter.ViewHolder>  {

    private List<Tema> ppalTemasList;
    private Context context;


    final PpalTemaAdapter.imageButtonPress listener;

    public interface imageButtonPress{

        void onItemClick(Tema item);
    }

    int[] datosImage = {R.drawable.trabajo, R.drawable.salud1, R.drawable.educaciones, R.drawable.marketingf, R.drawable.religion};


    public PpalTemaAdapter(List<Tema> ppalTemasList, Context context, imageButtonPress listener) {
        this.ppalTemasList = ppalTemasList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item_container,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setOnboardingData(ppalTemasList.get(position),position);
        holder.biData(ppalTemasList.get(position));

    }

    @Override
    public int getItemCount() {
        return ppalTemasList.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        TextView nameText;
        TextView nameTextDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.textTitle);
            nameTextDescription = itemView.findViewById(R.id.textDescription);
            imageView = itemView.findViewById(R.id.imageIcon);

        }

        public void setOnboardingData(Tema tema, int position) {

            nameText.setText(tema.getNombreTema());
            nameTextDescription.setText(tema.getNombreTema());
            imageView.setImageResource(datosImage[position]);
        }

        void biData (final Tema item ){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });

        }
    }

}
