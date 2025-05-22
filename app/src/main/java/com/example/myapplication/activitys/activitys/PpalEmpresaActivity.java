package com.example.myapplication.activitys.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;
import android.media.MediaPlayer;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.activitys.util.Constant;
import com.example.myapplication.activitys.util.PreferenseManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class PpalEmpresaActivity extends AppCompatActivity {

    private VideoView videoView;
    private PreferenseManager preferenseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppal_empresa);
        preferenseManager = new PreferenseManager(getApplicationContext());
        videoView = findViewById(R.id.videoView);

        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.empresa_fondo);
        videoView.setVideoURI(video);
        videoView.start();

        findViewById(R.id.btnAdminProdRegistro).setOnClickListener(v -> {
            Intent intent = new Intent(PpalEmpresaActivity.this, EmpresaPanelActivity.class);
            startActivity(intent);
        });

        ImageView image = findViewById(R.id.imageSingOutFoundPpalEmpresa);
        image.setOnClickListener(v -> SingOut());

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f); // sin sonido
        });
    }

    private void ShowTast(String mesagge) {

        Toast.makeText(getApplicationContext(), mesagge, Toast.LENGTH_SHORT).show();
    }

    private void SingOut() {
        ShowTast("Sing out...");
        FirebaseFirestore datebasse = FirebaseFirestore.getInstance();
        DocumentReference documentReference = datebasse.collection("empresas").document(
                preferenseManager.getString(Constant.KEY_EMPRESA_ID)
        );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constant.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unused -> {

                    preferenseManager.clear();
                    startActivity(new Intent(getApplicationContext(), SelectRoleActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> ShowTast("unable to sing out Error"));

    }

}
