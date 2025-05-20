package com.example.myapplication.activitys.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.activitys.model.ChatMessage;
import com.example.myapplication.databinding.ItemContainerReceivedMessageBinding;
import com.example.myapplication.databinding.ItemContainerSentMessageBinding;


import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> chatMessages;
    private Bitmap recevedProfileImage;
    private final String senderId;

    public void setRecevedProfileImage(Bitmap bitmap){

        recevedProfileImage = bitmap;
    }
    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap recevedProfileImage, String senderId) {
        this.chatMessages = chatMessages;
        this.recevedProfileImage = recevedProfileImage;
        this.senderId = senderId;
    }

    private static final int VIEW_TIPE_SENT = 1;
    private static final int VIEW_TIPE_RECEIVED = 2;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TIPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.getContext()),
                            parent,
                            false)
            );
        } else {

            return new ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false

            ));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) == VIEW_TIPE_SENT){

            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        }else{
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position));

        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(senderId)) {

            return VIEW_TIPE_SENT;
        } else {

            return VIEW_TIPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding intemContainerUserBinding) {

            super(intemContainerUserBinding.getRoot());
            binding = intemContainerUserBinding;
        }

        void setData(ChatMessage chatMessage) {

            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText((chatMessage.dateTime));
        }


    }

    public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedMessageBinding binding;


       ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {

            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage) {



            binding.textMesagge.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);

            if(recevedProfileImage == null){

               binding.imageProfile.setImageBitmap(recevedProfileImage);

            }

        }


    }


}
