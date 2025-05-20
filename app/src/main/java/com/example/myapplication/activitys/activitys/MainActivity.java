package com.example.myapplication.activitys.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.RecentConversationAdapter;
import com.example.myapplication.activitys.model.ChatMessage;
import com.example.myapplication.activitys.model.Usuario;
import com.example.myapplication.activitys.network.NetworkUtils;
import com.example.myapplication.activitys.servicios.ConversionListener;
import com.example.myapplication.activitys.util.Constant;
import com.example.myapplication.activitys.util.PreferenseManager;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements ConversionListener, Serializable {

    private static final long INACTIVITY_TIMEOUT = 10 * 60 * 1000; // 2 minutos
    private static final long WARNING_TIME = 8 * 60 * 1000; // 1 minuto antes de cerrar la sesión

    private long lastInteractionTime;
    private Handler inactivityHandler;
    private boolean isWarningShown = false;

    private ActivityMainBinding binding;
    private PreferenseManager preferenseManager;
    private List<ChatMessage> conversation;
    private RecentConversationAdapter conversationAdapter;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar la conexión a Internet
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No tienes conexión a internet, por favor revisa tu conexión", Toast.LENGTH_LONG).show();
        }

        // Inicializar vista y binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar el gestor de preferencias
        preferenseManager = new PreferenseManager(getApplicationContext());

        int temaId = getIntent().getIntExtra("temaId", -1);
        if (temaId != -1) {
            String userId = preferenseManager.getString("usuarioID"); // Asegúrate que tienes el ID del documento del usuario
            preguntarSuscripcion(temaId, userId);
        }

        // Llamadas a métodos de inicialización
        init();
        loadUserDetails();
        getToken();
        setListeiner();
        listenConversation();


        // Ahora ponle un Listener
        AppCompatImageView imageSignOut = findViewById(R.id.imageSingOutFound);
        // Ahora ponle un Listener
        imageSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí llamas a tu actividad de votación
                Intent intent = new Intent(MainActivity.this, VoteTopicsActivity.class);
                startActivity(intent);
            }
        });

        lastInteractionTime = System.currentTimeMillis();
        inactivityHandler = new Handler(Looper.getMainLooper());
        startInactivityChecker();
    }

    public class VoteTopicsActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
           // setContentView(R.layout.activity_vote_topics);

            // Aquí cargas tus subtemas (puedes usar un RecyclerView o una lista sencilla)
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        lastInteractionTime = System.currentTimeMillis();
        isWarningShown = false;  // Restablecer la advertencia si el usuario interactúa
    }

    private void startInactivityChecker() {
        inactivityHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                long timeElapsed = now - lastInteractionTime;

                if (timeElapsed >= INACTIVITY_TIMEOUT) {
                    Toast.makeText(MainActivity.this, "Sesión cerrada por inactividad", Toast.LENGTH_SHORT).show();
                    SingOut();
                    return; // Detiene la ejecución, no se vuelve a llamar al handler
                }

                if (timeElapsed >= INACTIVITY_TIMEOUT - WARNING_TIME && !isWarningShown) {
                    Toast.makeText(MainActivity.this, "Tu sesión se cerrará en 2 minutos por inactividad", Toast.LENGTH_SHORT).show();
                    isWarningShown = true;
                }

                // Siempre vuelve a revisar en 1 minuto
                inactivityHandler.postDelayed(this, 60 * 1000);
            }
        }, 60 * 1000); // Comienza la revisión al minuto
    }

    private void preguntarSuscripcion(int temaId, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("user").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Long> temas = (List<Long>) documentSnapshot.get("temas");

                // Si el tema ya está en la lista, no hacemos nada
                if (temas != null && temas.contains((long) temaId)) {
                    Log.d("FIRESTORE", "Usuario ya está suscrito al tema " + temaId);
                    return;
                }

                // Mostrar diálogo de suscripción si no está en la lista
                new AlertDialog.Builder(this)
                        .setTitle("Suscribirse al tema")
                        .setMessage("¿Deseas suscribirte a este tema?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            agregarTemaAFirebase(temaId, userId);
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        }).addOnFailureListener(e -> {
            Log.e("FIRESTORE", "Error al verificar temas del usuario", e);
        });
    }
    private void agregarTemaAFirebase(int temaId, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("user").document(userId);

        userRef.update("temas", FieldValue.arrayUnion(temaId))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Tema agregado correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al agregar tema", Toast.LENGTH_SHORT).show();
                    Log.e("FIRESTORE", "Error:", e);
                });
    }

    private void init() {

        conversation = new ArrayList<>();
        conversationAdapter = new RecentConversationAdapter(conversation, this);
        binding.userRecicleView.setAdapter(conversationAdapter);
        database = FirebaseFirestore.getInstance();

    }

    private void setListeiner() {
        binding.imageSingOutFound.setOnClickListener(v -> SingOut());
        binding.fabNewChat.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), UserActivity.class)));
    }

    private void loadUserDetails() {

        binding.TextName.setText(preferenseManager.getString("Nombre"));


    }

    private void ShowTast(String mesagge) {

        Toast.makeText(getApplicationContext(), mesagge, Toast.LENGTH_SHORT).show();
    }

    private void listenConversation(){

        database.collection(Constant.KEY_COLECTION_CONVERSATION)
                .whereEqualTo(Constant.KEY_SENDER_ID, preferenseManager.getString(Constant.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constant.KEY_COLECTION_CONVERSATION)
                .whereEqualTo(Constant.KEY_RECEIVER_ID, preferenseManager.getString(Constant.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener <QuerySnapshot> eventListener = (value, error) ->
    {

        if(error != null){

            return;
        }

        if(value != null){
            for (DocumentChange documentChange : value.getDocumentChanges()){

                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    String senderId = documentChange.getDocument().getString(Constant.KEY_SENDER_ID);
                    String receivedId = documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receivedId = receivedId;

                    if(preferenseManager.getString(Constant.KEY_USER_ID).equals(senderId)){

                        //chatMessage.conversionName = documentChange.getDocument().getString(Constant.KEY_RECEIVER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constant.KEY_RECEIVER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID);

                    } else {
                        chatMessage.conversionName = documentChange.getDocument().getString(Constant.KEY_SENDER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constant.KEY_SENDER_ID);
                    }

                    chatMessage.message = documentChange.getDocument().getString(Constant.KEY_LAST_MESAGGE);
                    chatMessage.dateObjet = documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP);
                    conversation.add(chatMessage);

                }else if(documentChange.getType() == DocumentChange.Type.MODIFIED) {

                    for ( int i = 0; i < conversation.size(); i++){

                        String senderId = documentChange.getDocument().getString(Constant.KEY_SENDER_ID);
                        String receivedId = documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID);
                        if(conversation.get(i).senderId.equals(senderId) && conversation.get(i).receivedId.equals(receivedId)){
                            conversation.get(i).message = documentChange.getDocument().getString(Constant.KEY_LAST_MESAGGE);
                            conversation.get(i).dateObjet = documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }

            Collections.sort(conversation, (obj1, obj2) -> obj2.dateObjet.compareTo(obj1.dateObjet));
            conversationAdapter.notifyDataSetChanged();
            binding.userRecicleView.smoothScrollToPosition(0);
            binding.userRecicleView.setVisibility(View.VISIBLE);
            binding.progessBar.setVisibility(View.GONE);
        }
    };

    private void getToken() {

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {

        preferenseManager.putString(Constant.KEY_FCM_TOKEN, token);

        FirebaseFirestore datebasse = FirebaseFirestore.getInstance();
        DocumentReference documentReference = datebasse.collection("user").document(
                preferenseManager.getString("usuarioID")
        );

        documentReference.update(Constant.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(e -> ShowTast("Unable To Update Token"));
    }

    private void SingOut() {
        ShowTast("Sing out...");
        FirebaseFirestore datebasse = FirebaseFirestore.getInstance();
        DocumentReference documentReference = datebasse.collection("user").document(
                preferenseManager.getString("usuarioID")
        );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constant.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unused -> {

                    preferenseManager.clear();
                    startActivity(new Intent(getApplicationContext(), LoginActivty.class));
                    finish();
                })
                .addOnFailureListener(e -> ShowTast("unable to sing out Error"));

    }

    @Override
    public void onConversionClicked(Usuario user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constant.KEY_USER,user);
        startActivity(intent);
    }
}