package com.example.myapplication.activitys.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.activitys.listener.UserListener;
import com.example.myapplication.activitys.model.Usuario;
import com.example.myapplication.databinding.IntemContainerUserBinding;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List <Usuario> usersMetodos;
    IntemContainerUserBinding binding;

    private final UserListener userListener;

    public UserAdapter(List<Usuario> users, UserListener userListener) {

        this.usersMetodos = users;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         IntemContainerUserBinding intemContainerUserBinding = IntemContainerUserBinding.inflate(

                 LayoutInflater.from(parent.getContext()),parent,false

         );
         return  new UserViewHolder(intemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        holder.setUserData(usersMetodos.get(position));
    }

    @Override
    public int getItemCount() {
        return usersMetodos.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{


         UserViewHolder(IntemContainerUserBinding intemContainerUserBinding) {
            super(intemContainerUserBinding.getRoot());
            binding = intemContainerUserBinding;

        }

        void setUserData(Usuario user){

             binding.textName.setText(user.nombre);
             binding.textApellido.setText(user.apellido);

             binding.getRoot().setOnClickListener(v-> userListener.onUserClicked(user));

        }
    }
}
