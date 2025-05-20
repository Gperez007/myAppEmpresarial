package com.example.myapplication.activitys.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.activitys.util.Constant;
import com.example.myapplication.activitys.util.PreferenseManager;

public class SelectRoleActivity extends AppCompatActivity {
    private PreferenseManager preferenseManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_role);

        // Verificar si el usuario ya está logeado
        //preferenseManager = new PreferenseManager(getApplicationContext());
        // if (preferenseManager.getBoolean(Constant.KEY_IS_SIGNED_IN)) {
        //    startActivity(new Intent(getApplicationContext(), PpalTemaActivity.class));
        //     finish();
        //       return; // Para evitar seguir con la inicialización innecesaria
        //  }


        findViewById(R.id.btnCliente).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Puedes pasar el rol como extra si es necesario
                Intent intent = new Intent(SelectRoleActivity.this, LoginActivty.class);
                intent.putExtra("user_role", "cliente");
                startActivity(intent);
            }
        });

        findViewById(R.id.btnEmpresa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectRoleActivity.this, EmpresaLoginActivity.class);
                intent.putExtra("user_role", "empresa");
                startActivity(intent);
            }
        });
    }
}
