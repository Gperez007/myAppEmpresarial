package com.example.myapplication.activitys.activitys;

import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.activitys.adapter.ChatAdapter;
import com.example.myapplication.activitys.model.ChatMessage;
import com.example.myapplication.activitys.model.Usuario;
import com.example.myapplication.activitys.network.ApiService;
import com.example.myapplication.activitys.network.Apiclient;
import com.example.myapplication.activitys.util.Constant;
import com.example.myapplication.activitys.util.PreferenseManager;
import com.example.myapplication.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {

    private ActivityChatBinding binding;
    private Usuario receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenseManager preferenseManager;
    FirebaseFirestore database = FirebaseFirestore.getInstance() ;
    private String conversionId = null;
    private Boolean isReceiverAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverDetails();
        init();
        listenMessages();
    }

    private void init() {

        preferenseManager = new PreferenseManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages, getBitmaFromEncodeString(receiverUser.image),
                preferenseManager.getString(Constant.KEY_USER_ID)

        );

        binding.reciclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();

    }

    private void sendMessage() {

        HashMap<String, Object> message = new HashMap<>();
        message.put(Constant.KEY_SENDER_ID, preferenseManager.getString(Constant.KEY_USER_ID));
        message.put(Constant.KEY_RECEIVER_ID, receiverUser.id);
        message.put(Constant.KEY_MESSAGE, binding.imputMessage.getText().toString());
        message.put(Constant.KEY_TIMESTAMP, new Date());
        database.collection(Constant.KEY_COLLECTION_CHAT).add(message);

        if (conversionId != null) {
            updateConversion(binding.imputMessage.getText().toString());
        } else {

            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constant.KEY_SENDER_ID, preferenseManager.getString(Constant.KEY_USER_ID));
            conversion.put(Constant.KEY_SENDER_NAME, preferenseManager.getString(Constant.KEY_NAME));
            //conversion.put(Constant.KEY_SENDER_IMAGE, preferenseManager.getString(Constant.KEY_SENDER_IMAGE));
            conversion.put(Constant.KEY_RECEIVER_ID, receiverUser.id);
            //conversion.put(Constant.KEY_RECEIVER_IMAGE, receiverUser.image);
            conversion.put(Constant.KEY_LAST_MESAGGE, binding.imputMessage.getText().toString());
            conversion.put(Constant.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }
        if (!isReceiverAvailable) {

            try {
                JSONArray tokens = new JSONArray();
                tokens.put(receiverUser.token);

                JSONObject data = new JSONObject();
                data.put(Constant.KEY_USER_ID, preferenseManager.getString(Constant.KEY_USER_ID));
                data.put(Constant.KEY_NAME, preferenseManager.getString(Constant.KEY_NAME));
                data.put(Constant.KEY_FCM_TOKEN, preferenseManager.getString(Constant.KEY_FCM_TOKEN));
                data.put(Constant.KEY_MESSAGE, binding.imputMessage.getText().toString());

                JSONObject body = new JSONObject();
                body.put(Constant.REMOTE_MGS_DATA, data);
                body.put(Constant.REMOTE_MGS_REGISTRATION_IDS, tokens);

                SendNotificacion(body.toString());

            } catch (Exception exception) {

                showToast(exception.getMessage());

            }
        }
        binding.imputMessage.setText(null);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void SendNotificacion(String messageBody) {

        Apiclient.getClient().create(ApiService.class).sendMessage(Constant.getRemoteMsgHeader(),
                messageBody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {

                if (response.isSuccessful()) {

                    try {

                        if (response.body() != null) {

                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray result = responseJson.getJSONArray("result");

                            showToast("Notificacion sucessfull");
                            if (responseJson.getInt("failure") == 1) {
                                JSONObject error = (JSONObject) result.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                    showToast("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {

                showToast(t.getMessage());
            }
        });
    }

    private void listenAvailibilityOfReceiver() {

        database.collection(Constant.KEY_COLLECTION_USER).document(
                receiverUser.id
        ).addSnapshotListener(ChatActivity.this, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null) {
                if (value.getLong(Constant.KEY_AVAILABILITY) != null) {
                    int avaibility = Objects.requireNonNull(
                            value.getLong(Constant.KEY_AVAILABILITY))
                            .intValue();
                    isReceiverAvailable = avaibility == 1;
                }
                receiverUser.token = value.getString(Constant.KEY_FCM_TOKEN);
                if (receiverUser.image == null) {

                    receiverUser.image = value.getString("imagen");
                    chatAdapter.setRecevedProfileImage(getBitmaFromEncodeString(receiverUser.image));
                    chatAdapter.notifyItemRangeChanged(0, chatMessages.size());
                }
            }
            if (isReceiverAvailable) {
                binding.textAviavility.setVisibility(View.VISIBLE);
            } else {

                binding.textAviavility.setVisibility(View.GONE);
            }
        });
    }

    private void listenMessages() {

        database.collection(Constant.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constant.KEY_SENDER_ID, preferenseManager.getString(Constant.KEY_USER_ID))
                .whereEqualTo(Constant.KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);
        database.collection(Constant.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constant.KEY_SENDER_ID, receiverUser.id)
                .whereEqualTo(Constant.KEY_RECEIVER_ID, preferenseManager.getString(Constant.KEY_USER_ID))
                .addSnapshotListener(eventListener);


    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constant.KEY_SENDER_ID);
                    chatMessage.receivedId = documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constant.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDataTime(documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP));
                    chatMessage.dateObjet = documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obje1, obje2) -> obje1.dateObjet.compareTo(obje2.dateObjet));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.reciclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.reciclerView.setVisibility(View.VISIBLE);
        }
        binding.progessBar.setVisibility(View.GONE);

        if (conversionId == null) {

            CheckForConversion();
        }

    };

    private Bitmap getBitmaFromEncodeString(String encodedImage) {

        if (encodedImage != null) {

            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }

    }

    private void loadReceiverDetails() {

        receiverUser = (Usuario) getIntent().getSerializableExtra(Constant.KEY_USER);
        binding.textName.setText(receiverUser.nombre);

    }

    private void setListeners() {

        //binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoudSend.setOnClickListener(v -> sendMessage());

    }

    private String getReadableDataTime(Date date) {

        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversion(HashMap<String, Object> conversion) {

        database.collection(Constant.KEY_COLECTION_CONVERSATION)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId());
    }

    private void updateConversion(String message){

        DocumentReference documentReference = database.collection(Constant.KEY_COLECTION_CONVERSATION)
                .document(conversionId);
        documentReference.update(Constant.KEY_LAST_MESAGGE,message,
                Constant.KEY_TIMESTAMP,new Date());
    }

    private void CheckForConversion(){

        if(chatMessages.size() != 0) {

            CheckForConversionRemoteOnly(preferenseManager.getString(Constant.KEY_USER_ID),receiverUser.id);
            CheckForConversionRemoteOnly(receiverUser.id, preferenseManager.getString(Constant.KEY_USER_ID));


        }

    }

    private void CheckForConversionRemoteOnly(String senderId, String receiverId){

        database.collection(Constant.KEY_COLECTION_CONVERSATION)
                .whereEqualTo(Constant.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constant.KEY_RECEIVER_ID,receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);

    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {

        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){

            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();

        }
    };

    @Override
    protected void onResume(){

        super.onResume();
        listenAvailibilityOfReceiver();
    }

}