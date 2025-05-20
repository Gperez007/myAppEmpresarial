package com.example.myapplication.activitys.activitys;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.UserAdapter;
import com.example.myapplication.activitys.listener.UserListener;
import com.example.myapplication.activitys.model.Usuario;
import com.example.myapplication.activitys.util.Constant;
import com.example.myapplication.activitys.util.PreferenseManager;
import com.example.myapplication.databinding.ActivityUserBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends BaseActivity implements UserListener {

    ActivityUserBinding binding;
    private PreferenseManager preferenseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenseManager = new PreferenseManager(getApplicationContext());
        setListener();
        getUser();

    }

    private void setListener(){

        binding.imageBack.setOnClickListener(v-> onBackPressed());
    }

    private void getUser() {
        loading(true);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String currentUserId = preferenseManager.getString(Constant.KEY_USER_ID);

        // Referencia al usuario actual
        DocumentReference currentUserRef = database.collection(Constant.KEY_COLLECTION_USER).document(currentUserId);
        currentUserRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Long> temasActual = (List<Long>) documentSnapshot.get("temas"); // Asegúrate que tus temas son tipo Long

                database.collection(Constant.KEY_COLLECTION_USER).get().addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Usuario> users = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            if (currentUserId.equals(doc.getId())) {
                                continue;
                            }

                            List<Long> temasUsuario = (List<Long>) doc.get("temas");

                            if (temasUsuario != null && temasActual != null) {
                                // Verificar si hay algún tema en común
                                boolean tieneTemaEnComun = false;
                                for (Long tema : temasUsuario) {
                                    if (temasActual.contains(tema)) {
                                        tieneTemaEnComun = true;
                                        break;
                                    }
                                }

                                if (tieneTemaEnComun) {
                                    Usuario user = new Usuario();
                                    user.nombre = doc.getString("Nombre");
                                    user.apellido = doc.getString("Apellido");
                                    user.telefono = doc.getString("Telefono");
                                    user.genero = doc.getString("Genero");
                                    user.fechaNacimiento = doc.getString("Fecha de nacimiento");
                                    user.token = doc.getString(Constant.KEY_FCM_TOKEN);
                                    user.id = doc.getId();
                                    users.add(user);
                                }
                            }
                        }

                        if (users.size() > 0) {
                            UserAdapter userAdapter = new UserAdapter(users, this);
                            binding.userRecicleView.setAdapter(userAdapter);
                            binding.userRecicleView.setVisibility(View.VISIBLE);
                        } else {
                            ShowErrorMessage();
                        }
                    } else {
                        ShowErrorMessage();
                    }
                });
            }
        }).addOnFailureListener(e -> {
            loading(false);
            ShowErrorMessage();
        });
    }

    private void ShowErrorMessage(){

        binding.textErroresMessage.setText(String.format("%s", "No use Available"));
        binding.textErroresMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){

        if(isLoading){
            binding.progessBar.setVisibility(View.INVISIBLE);
        }else {

            binding.progessBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUserClicked(Usuario user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constant.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}