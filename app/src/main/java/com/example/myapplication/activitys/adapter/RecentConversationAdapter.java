package com.example.myapplication.activitys.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.activitys.model.ChatMessage;
import com.example.myapplication.activitys.model.Usuario;
import com.example.myapplication.activitys.servicios.ConversionListener;
import com.example.myapplication.databinding.IntemContainerRecentConversionBinding;
import com.google.firebase.firestore.auth.User;

import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ConversionViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;

    public RecentConversationAdapter(List<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(IntemContainerRecentConversionBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {

        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder{

        IntemContainerRecentConversionBinding binding;

        ConversionViewHolder(IntemContainerRecentConversionBinding intemContainerRecentConversionBinding){
            super(intemContainerRecentConversionBinding.getRoot());
            binding = intemContainerRecentConversionBinding;
        }

        void setData(ChatMessage chatMessage){

            binding.textName.setText(chatMessage.conversionName);
            binding.textRecentMessage.setText(chatMessage.message);

            binding.getRoot().setOnClickListener(v ->{
                Usuario user = new Usuario();
                user.id = chatMessage.conversionId;
                user.nombre = chatMessage.conversionName;
                //user.image = chatMessage.converiosImage;
                conversionListener.onConversionClicked(user);
            });
        }
    }
}