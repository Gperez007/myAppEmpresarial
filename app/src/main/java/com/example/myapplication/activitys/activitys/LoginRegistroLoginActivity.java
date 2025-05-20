package com.example.myapplication.activitys.activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class LoginRegistroLoginActivity extends AppCompatActivity {

    EditText telefono, password;

    Button Registrarte;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registro_login);
        telefono = findViewById(R.id.textTelefono);
        password = findViewById(R.id.textPassword);
        Registrarte = findViewById(R.id.buttonRegistrate);
        


        Registrarte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CargarDatos()) {


                    MostrarDatos();

                    Intent intent = new Intent(LoginRegistroLoginActivity.this, LoginActivty.class);

                    startActivity(intent);

                }

            }


        });

    }


    public static class PasswordEncryption {

        public static String encryptPassword(String password) {
            try {
                // Obtén una instancia de MessageDigest con el algoritmo SHA-256
                MessageDigest digest = MessageDigest.getInstance("SHA-256");

                // Convierte la contraseña en bytes y calcula el hash
                byte[] encodedHash = digest.digest(password.getBytes());

                // Convierte el hash en una representación hexadecimal
                StringBuilder hexString = new StringBuilder();
                for (byte b : encodedHash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }

                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    private void MostrarDatos() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Bundle datos = this.getIntent().getExtras();
        String nombre = datos.getString("nombre");
        String apellido = datos.getString("apellido");
        String fechaDate = datos.getString("fechaDate");
        String sex = datos.getString("sex");
        String tele = telefono.getText().toString();
        BigDecimal telefono = new BigDecimal(tele);
        String contrasena = password.getText().toString();
        String correoPersona = "giopersie09@gmmail.com";

        String encryptedPassword = PasswordEncryption.encryptPassword(contrasena);
        Log.d("Encrypted Password", encryptedPassword);


        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put("Nombre",nombre);
        user.put("apellido",apellido);
        user.put("Fecha de nacimiento",fechaDate);
        user.put("Genero",sex);
        user.put("Telefono",tele);
        user.put("Contraseña",encryptedPassword);

        Task<DocumentReference> usuario = firebaseFirestore.collection("user")
                .add(user)
                .addOnSuccessListener(documentReference ->{
                    Toast.makeText(LoginRegistroLoginActivity.this, "Usuario insert", Toast.LENGTH_SHORT).show();

                }).addOnFailureListener(exception ->{
                    Toast.makeText(LoginRegistroLoginActivity.this, "fail", Toast.LENGTH_SHORT).show();


                });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private Boolean CargarDatos() {


        if (telefono.getText().toString().trim().isEmpty()) {
            showToast("ingresa el telefono o correo electonico");
            return false;
        } else if (password.getText().toString().trim().isEmpty()) {
            showToast("ingresa una contaseña");
            return false;
        } else {
            return true;
        }
    }
}