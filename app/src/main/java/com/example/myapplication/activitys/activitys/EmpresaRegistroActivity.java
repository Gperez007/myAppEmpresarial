package com.example.myapplication.activitys.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import java.util.HashMap;

public class EmpresaRegistroActivity extends AppCompatActivity {

    private EditText editNit, editRazonSocial, editCorreo;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_registro);

        db = FirebaseFirestore.getInstance();
        editNit = findViewById(R.id.editNit);
        editRazonSocial = findViewById(R.id.editRazonSocial);
        editCorreo = findViewById(R.id.editCorreo);

        findViewById(R.id.btnRegistrarEmpresa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nit = editNit.getText().toString().trim();
                String razonSocial = editRazonSocial.getText().toString().trim();
                String correo = editCorreo.getText().toString().trim();

                if (nit.isEmpty() || razonSocial.isEmpty() || correo.isEmpty()) {
                    Toast.makeText(EmpresaRegistroActivity.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Obtener el último ID autoincrementable registrado
                    getLastCompanyId(nit, razonSocial, correo);
                }
            }
        });
    }

    private void getLastCompanyId(final String nit, final String razonSocial, final String correo) {
        db.collection("empresas")
                .orderBy("id", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int lastId = 0;

                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Obtener el último ID registrado
                        lastId = queryDocumentSnapshots.getDocuments().get(0).getLong("id").intValue();
                    }

                    int newId = lastId + 1; // El siguiente ID autoincrementable

                    // Crear un nuevo objeto HashMap con los nuevos campos
                    HashMap<String, Object> empresa = new HashMap<>();
                    empresa.put("id", newId);
                    empresa.put("nit", nit);
                    empresa.put("razonSocial", razonSocial);
                    empresa.put("correo", correo);

                    // Registrar la empresa en Firestore
                    db.collection("empresas")
                            .add(empresa)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(EmpresaRegistroActivity.this, "Empresa registrada exitosamente", Toast.LENGTH_SHORT).show();
                                finish(); // Regresa a la pantalla anterior
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(EmpresaRegistroActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // En caso de error al obtener el último ID
                    Toast.makeText(EmpresaRegistroActivity.this, "Error al obtener el ID", Toast.LENGTH_SHORT).show();
                });
    }
}
