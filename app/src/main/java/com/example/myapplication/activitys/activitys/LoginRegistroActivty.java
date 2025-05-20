package com.example.myapplication.activitys.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import java.util.Calendar;


public class LoginRegistroActivty extends AppCompatActivity {

    TextView fechaBtn;
    EditText efecha, nombre, apellidoP, fechaNac, genero;

    Button Siguiente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registro_activty);
        efecha = findViewById(R.id.textFecha);
        nombre = findViewById(R.id.textNombre);
        apellidoP = findViewById(R.id.textApellidoP);
        fechaNac = findViewById(R.id.textFecha);
        genero = findViewById(R.id.textGenero);
        Siguiente = findViewById(R.id.buttonSiguiente);
        fechaBtn = findViewById(R.id.imageButonFecha);
        //progressBar = findViewById(R.id.ProgressBar);
        Siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CargarDatos()) {
                    singUp();
                    String name = nombre.getText().toString();
                    String apellido = apellidoP.getText().toString();
                    String fechaDate = fechaNac.getText().toString();
                    String sex = genero.getText().toString();

                    Intent i = new Intent(LoginRegistroActivty.this, LoginRegistroLoginActivity.class);

                    i.putExtra("nombre", name);
                    i.putExtra("apellido", apellido);
                    i.putExtra("fechaDate", fechaDate);
                    i.putExtra("sex", sex);

                    startActivity(i);
                }


            }


        });

        fechaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fecha();
            }

        });

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }


    private void singUp() {

        //loading(true);
    }

    private Boolean CargarDatos() {

        if (nombre.getText().toString().trim().isEmpty()) {
            showToast("ingresa el nombre");
            return false;
        } else if (apellidoP.getText().toString().trim().isEmpty()) {
            showToast("Enter apellido");
            return false;
        } else if (fechaNac == null) {
            showToast("Select profile fecha de nacimiento");
            return false;
        } else if (genero.getText().toString().trim().isEmpty()) {
            showToast("Enter Genero");
            return false;
        } else {
            return true;
        }
    }

    private void fecha() {

        final Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                efecha.setText(dayOfMonth + "/" + month + "/" + year);
            }
        }
                , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }
}