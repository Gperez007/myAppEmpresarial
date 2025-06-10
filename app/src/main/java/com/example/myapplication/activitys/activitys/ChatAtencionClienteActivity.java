package com.example.myapplication.activitys.activitys;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.ChatAtencionclienteAdapter;
import com.example.myapplication.activitys.model.Empresa;
import com.example.myapplication.activitys.model.Mensaje;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatAtencionClienteActivity extends AppCompatActivity {

    private EditText inputChat;
    private ImageButton btnEnviar, btnMicrofono;
    private RecyclerView recyclerView;
    private ChatAtencionclienteAdapter chatAdapter;
    private List<Mensaje> listaMensajes;
    private Empresa datosEmpresa; // Variable global para guardar los datos
    private String empresaID;

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_atencion_cliente);

        String empresaID = getIntent().getStringExtra("empresaID");

        inputChat = findViewById(R.id.inputChat);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnMicrofono = findViewById(R.id.btnMicrofono);
        recyclerView = findViewById(R.id.recyclerChat);

        listaMensajes = new ArrayList<>();
        chatAdapter = new ChatAtencionclienteAdapter(listaMensajes);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnEnviar.setOnClickListener(v -> {
            String mensaje = inputChat.getText().toString().trim();
            if (!mensaje.isEmpty()) {
                agregarMensaje("usuario", mensaje);
                inputChat.setText("");
                responderIA(mensaje);
            }
        });
        empresaID = getIntent().getStringExtra("empresaID");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ubicacionesEmpresas")
                .document(empresaID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        datosEmpresa = documentSnapshot.toObject(Empresa.class);
                    }
                });

        btnMicrofono.setOnClickListener(v -> iniciarReconocimientoVoz());

        Toast.makeText(this, "empresaID: " + empresaID, Toast.LENGTH_SHORT).show(); // Puedes quitar esto luego
    }
    private void iniciarReconocimientoVoz() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla tu mensaje...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Tu dispositivo no soporta voz", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String texto = result.get(0);
            agregarMensaje("usuario", texto);
            responderIA(texto);
        }
    }

    private void agregarMensaje(String emisor, String mensaje) {
        listaMensajes.add(new Mensaje(emisor, mensaje));
        chatAdapter.notifyItemInserted(listaMensajes.size() - 1);
        recyclerView.scrollToPosition(listaMensajes.size() - 1);
    }

    private void responderIA(String entradaUsuario) {
        if (datosEmpresa == null) {
            agregarMensaje("bot", "Cargando datos de la empresa, por favor espera...");
            return;
        }

        String mensaje = entradaUsuario.toLowerCase();
        String respuesta = "Lo siento, no entendí tu pregunta. Puedes preguntar por dirección, teléfono, devolución o reclamos.";

        if (mensaje.contains("teléfono") || mensaje.contains("telefono")) {
            respuesta = "El teléfono de la empresa " + datosEmpresa.razonSocial + " es: " + datosEmpresa.telefono;
        } else if (mensaje.contains("dirección") || mensaje.contains("direccion")) {
            respuesta = "La dirección de " + datosEmpresa.razonSocial + " es: " + datosEmpresa.direccion;
        } else if (mensaje.contains("devolución") || mensaje.contains("devoluciones")) {
            respuesta = "Política de devoluciones: " + datosEmpresa.politicaDevolucion;
        } else if (mensaje.contains("reclamo") || mensaje.contains("reclamos")) {
            respuesta = "Política de reclamos: " + datosEmpresa.politicaReclamos;
        } else if (mensaje.contains("nit")) {
            respuesta = "El NIT de la empresa es: " + datosEmpresa.nit;
        }

        agregarMensaje("bot", respuesta);
    }

}