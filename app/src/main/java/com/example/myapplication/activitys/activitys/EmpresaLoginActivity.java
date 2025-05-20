package com.example.myapplication.activitys.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.activitys.util.Constant;
import com.example.myapplication.activitys.util.PreferenseManager;
import com.example.myapplication.databinding.ActivityLoginActivtyBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import com.google.firebase.auth.FirebaseAuth;
public class EmpresaLoginActivity extends AppCompatActivity {
    private EditText editNit, editRazonSocial;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private PreferenseManager preferenseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_login);
        Button btnIrARegistro = findViewById(R.id.btnIrARegistro);
        // Inicialización de Firebase Firestore
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        // Referencias a los campos de entrada
        editNit = findViewById(R.id.editNit);
        editRazonSocial = findViewById(R.id.editRazonSocial);
        btnIrARegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmpresaLoginActivity.this, EmpresaRegistroActivity.class);
                startActivity(intent);
            }
        });
        // Lógica del botón de inicio de sesión
        findViewById(R.id.btnLoginEmpresa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nit = editNit.getText().toString().trim();
                String razonSocial = editRazonSocial.getText().toString().trim();

                if (nit.isEmpty() || razonSocial.isEmpty()) {
                    Toast.makeText(EmpresaLoginActivity.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Llamada al método de autenticación
                    signIn(nit, razonSocial);
                }
            }
        });

        TextView tvOlvidasteContrasena = findViewById(R.id.tvOlvidasteContrasena);
        tvOlvidasteContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });

    }

    // Método de autenticación y validación en Firestore
    private void signIn(String nit, String razonSocial) {
        // Muestra un mensaje de carga mientras se verifica la información
        Toast.makeText(this, "Verificando empresa...", Toast.LENGTH_SHORT).show();

        // Realizamos una consulta en Firestore para verificar si el NIT y la razón social existen en la base de datos
        db.collection("empresas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean encontrada = false;
                for (DocumentSnapshot doc : task.getResult()) {
                    String n = doc.getString("nit");
                    String r = doc.getString("razonSocial");

                    if (n != null && r != null &&
                            n.trim().replaceAll("\\s+", "").equals(nit.replaceAll("\\s+", "")) &&
                            r.trim().replaceAll("\\s+", "").equalsIgnoreCase(razonSocial.replaceAll("\\s+", ""))) {

                        encontrada = true;
                        // Empresa encontrada, pasar al siguiente Activity
                        Toast.makeText(this, "Empresa autenticada: " + r, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, PpalTemaActivity.class));
                        preferenseManager = new PreferenseManager(getApplicationContext());
                        preferenseManager.putBoolean(Constant.KEY_IS_SIGNED_IN, true);
                        preferenseManager.putBoolean(Constant.KEY_EMPRESA_ID, true);
                        break;
                    }
                }

                if (!encontrada) {
                    Toast.makeText(this, "Empresa no encontrada", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showForgotPasswordDialog() {
        final EditText editEmail = new EditText(this);
        editEmail.setHint("Introduce tu correo electrónico");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperar Contraseña");
        builder.setView(editEmail);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = editEmail.getText().toString().trim();
                if (!email.isEmpty()) {
                    verifyEmailInFirestore(email);
                } else {
                    Toast.makeText(EmpresaLoginActivity.this, "Por favor, ingresa un correo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", null);

        builder.show();
    }


    private void verifyEmailInFirestore(String email) {
        db.collection("empresas")
                .whereEqualTo("correo", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        sendPasswordResetEmail(email);
                    } else {
                        Toast.makeText(EmpresaLoginActivity.this, "Correo no encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendPasswordResetEmail(String email) {
        // Realizamos una consulta en Firestore para verificar si el correo está registrado
        db.collection("empresas").whereEqualTo("correo", email.trim()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // Si no se encuentra el correo en Firestore
                            Toast.makeText(EmpresaLoginActivity.this, "Correo no registrado", Toast.LENGTH_SHORT).show();
                        } else {
                            // Si se encuentra el correo, proceder con el restablecimiento
                            auth.sendPasswordResetEmail(email.trim())
                                    .addOnCompleteListener(resetTask -> {
                                        if (resetTask.isSuccessful()) {
                                            Toast.makeText(EmpresaLoginActivity.this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(EmpresaLoginActivity.this, "Error al enviar correo", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Si hay un error en la consulta
                        Toast.makeText(EmpresaLoginActivity.this, "Error al verificar correo", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
