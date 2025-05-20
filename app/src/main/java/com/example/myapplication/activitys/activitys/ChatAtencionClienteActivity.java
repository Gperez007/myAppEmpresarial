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
import com.example.myapplication.activitys.model.Mensaje;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatAtencionClienteActivity extends AppCompatActivity {

    private EditText inputChat;
    private ImageButton btnEnviar, btnMicrofono;
    private RecyclerView recyclerView;
    private ChatAtencionclienteAdapter chatAdapter;
    private List<Mensaje> listaMensajes;

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_atencion_cliente);

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
                responderIA(mensaje);  // <<<< Asegúrate de tener esto
            }
        });

        btnMicrofono.setOnClickListener(v -> iniciarReconocimientoVoz());
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
        String respuesta = "No entendí eso, intenta otra vez.";

        if (entradaUsuario.toLowerCase().contains("telefono")) {
            respuesta = "El teléfono de la empresa es: 123-456-789";
        }

        agregarMensaje("bot", respuesta);  // <<<< Esto muestra la respuesta
    }
}