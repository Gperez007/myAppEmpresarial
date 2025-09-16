package com.example.myapplication.activitys.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DomiciliarioLoginActivity extends AppCompatActivity {

    private EditText editCedula, editCorreo;
    private Button btnLogin, btnIrRegistro;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domiciliario_login);

        db = FirebaseFirestore.getInstance();

        editCedula = findViewById(R.id.editCedula);
        editCorreo = findViewById(R.id.editCorreo);
        btnLogin = findViewById(R.id.btnLogin);
        btnIrRegistro = findViewById(R.id.btnIrRegistro);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDomiciliario();
            }
        });

        btnIrRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, DomiciliarioRegistroActivity.class));
        });
    }

    private void loginDomiciliario() {
        String cedula = editCedula.getText().toString().trim();
        String correo = editCorreo.getText().toString().trim();

        if (cedula.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("domiciliarios").document(cedula).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String correoDB = documentSnapshot.getString("correo");

                        if (correo.equalsIgnoreCase(correoDB)) {
                            String estado = documentSnapshot.getString("estado");
                            if ("pendiente".equals(estado)) {
                                Toast.makeText(this, "Tu cuenta está en revisión", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Bienvenido " + documentSnapshot.getString("nombre"), Toast.LENGTH_SHORT).show();

                                // 👉 Pasamos la cédula en el Intent
                                Intent intent = new Intent(this, PpalDomiciliarioActivity.class);
                                intent.putExtra("cedula", cedula);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Toast.makeText(this, "Correo incorrecto", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No existe un domiciliario con esa cédula", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
