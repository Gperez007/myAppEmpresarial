package com.example.myapplication.activitys.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.media.MediaPlayer;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.activitys.util.Constant;
import com.example.myapplication.activitys.util.PreferenseManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class PpalEmpresaActivity extends AppCompatActivity {

    private VideoView videoView;
    private PreferenseManager preferenseManager;
    private ImageView avatarAsistente;
    private TextView mensajeAsistente;
    private TextView indicadorTocarAsistente;
    private Button btnAbrirFormulario, btnSaltarAsistente;
    private String nombreEmpresa = "tu empresa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppal_empresa);

        preferenseManager = new PreferenseManager(getApplicationContext());

        // Recuperar nombre de empresa
        String datosEmpresaJson = preferenseManager.getString("datosEmpresa");
        if (datosEmpresaJson != null && !datosEmpresaJson.isEmpty()) {
            Map<String, Object> empresaData = new Gson().fromJson(datosEmpresaJson, Map.class);
            if (empresaData.get("razonSocial") != null) {
                nombreEmpresa = (String) empresaData.get("razonSocial");
            }
        }

        // Inicializar vistas
        videoView = findViewById(R.id.videoView);
        avatarAsistente = findViewById(R.id.avatarAsistente);
        mensajeAsistente = findViewById(R.id.mensajeAsistente);
        indicadorTocarAsistente = findViewById(R.id.indicadorTocarAsistente);
        btnAbrirFormulario = findViewById(R.id.btnAbrirFormulario);
        btnSaltarAsistente = findViewById(R.id.btnSaltarAsistente);

        // Video fondo
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.empresa_fondo);
        videoView.setVideoURI(video);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f);
        });
        videoView.start();

        // Mensajes automáticos al iniciar
        mostrarMensajesIniciales();

        // Repetir mensajes al tocar avatar
        avatarAsistente.setOnClickListener(v -> mostrarMensajesAsistente());

        // Botón para abrir formulario
        btnAbrirFormulario.setOnClickListener(v -> abrirFormularioEmpresa());

        // Botón para saltar asistente
        btnSaltarAsistente.setOnClickListener(v -> ocultarAsistente());

        // Otros botones
        findViewById(R.id.btnAdminProdRegistro).setOnClickListener(v -> {
            startActivity(new Intent(this, EmpresaPanelActivity.class));
        });

        findViewById(R.id.imageSingOutFoundPpalEmpresa).setOnClickListener(v -> SingOut());
    }

    private void mostrarMensajesIniciales() {
        mensajeAsistente.setVisibility(View.VISIBLE);
        indicadorTocarAsistente.setVisibility(View.GONE);
        btnAbrirFormulario.setVisibility(View.GONE);

        String[] mensajesBienvenida = new String[]{
                "¡Hola " + nombreEmpresa + "! Bienvenido a tu asistente empresarial 🤖",
                //"Estoy aquí para ayudarte a organizar mejor tu negocio.",
                //"¿Te parece si comenzamos con algunos datos básicos?"
        };

        Handler handler = new Handler();
        for (int i = 0; i < mensajesBienvenida.length; i++) {
            int delay = i * 5000;
            int finalI = i;
            handler.postDelayed(() -> {
                mensajeAsistente.setText(mensajesBienvenida[finalI]);
                if (finalI == mensajesBienvenida.length - 1) {
                    indicadorTocarAsistente.setVisibility(View.VISIBLE);
                }
            }, delay);
        }
    }

    private void mostrarMensajesAsistente() {
        mensajeAsistente.setVisibility(View.VISIBLE);
        indicadorTocarAsistente.setVisibility(View.GONE);
        btnAbrirFormulario.setVisibility(View.GONE);

        final String[] mensajes = new String[]{
                "¡Hola " + nombreEmpresa + "!",
               // "Estoy aquí para ayudarte a organizar mejor tu negocio.",
               // "¿Te parece si comenzamos con algunos datos básicos?",
                //"Estoy aquí siempre que me necesites."
        };

        final Handler repeatHandler = new Handler();
        for (int i = 0; i < mensajes.length; i++) {
            int delay = i * 5000;
            int finalI = i;

            repeatHandler.postDelayed(() -> {
                mensajeAsistente.setText(mensajes[finalI]);
                if (finalI == mensajes.length - 1) {
                    // Después del último mensaje, esperar 5 segundos y mostrar botón formulario
                    repeatHandler.postDelayed(() -> {
                        mensajeAsistente.setVisibility(View.GONE);
                        indicadorTocarAsistente.setVisibility(View.VISIBLE);
                        btnAbrirFormulario.setVisibility(View.VISIBLE);
                    }, 5000);
                }
            }, delay);
        }
    }

    private void abrirFormularioEmpresa() {
        // Aquí lanzamos la actividad formulario
        Intent intent = new Intent(this, FormularioEmpresaActivity.class);
        startActivity(intent);
    }

    private void ocultarAsistente() {
        avatarAsistente.setVisibility(View.GONE);
        mensajeAsistente.setVisibility(View.GONE);
        indicadorTocarAsistente.setVisibility(View.GONE);
        btnAbrirFormulario.setVisibility(View.GONE);
        btnSaltarAsistente.setVisibility(View.GONE);
    }

    private void ShowTast(String mesagge) {

        Toast.makeText(getApplicationContext(), mesagge, Toast.LENGTH_SHORT).show();
    }

    private void SingOut() {
        ShowTast("Cerrando sesión...");

        String nit = preferenseManager.getString("empresaNit");

        if (nit == null || nit.isEmpty()) {
            ShowTast("No se encontró el NIT de la empresa. No se puede cerrar sesión.");
            return;
        }

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        // Buscar empresa por NIT
        database.collection("empresas")
                .whereEqualTo("nit", nit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        ShowTast("No se encontró ninguna empresa con ese NIT.");
                        return;
                    }

                    DocumentSnapshot empresaDoc = queryDocumentSnapshots.getDocuments().get(0);
                    String empresaId = empresaDoc.getId(); // ID del documento

                    // Eliminar token FCM
                    empresaDoc.getReference()
                            .update(Constant.KEY_FCM_TOKEN, FieldValue.delete())
                            .addOnSuccessListener(unused -> {
                                preferenseManager.clear();

                                Intent intent = new Intent(getApplicationContext(), SelectRoleActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> ShowTast("Error al cerrar sesión: " + e.getMessage()));
                })
                .addOnFailureListener(e -> ShowTast("Error al buscar empresa: " + e.getMessage()));
    }
}
