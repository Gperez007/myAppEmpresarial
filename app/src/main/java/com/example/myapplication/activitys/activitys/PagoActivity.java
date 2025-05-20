package com.example.myapplication.activitys.activitys;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.payments.paymentlauncher.PaymentLauncher;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class PagoActivity extends AppCompatActivity {

    private TextView tvMonto;
    private Button btnPagar;

    private PaymentSheet paymentSheet;
    private String paymentIntentClientSecret;
    private static final String PUBLISHABLE_KEY = "pk_test_51Hxxxxx..."; // ⚠️ Usa tu clave real

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pago_activity);

        tvMonto = findViewById(R.id.tvMonto);
        btnPagar = findViewById(R.id.btnPagar);

        tvMonto.setText("Total a pagar: $100");

        // 1. Inicializa Stripe
        PaymentConfiguration.init(getApplicationContext(), PUBLISHABLE_KEY);
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        // 2. Crear PaymentIntent desde tu servidor
        fetchPaymentIntent();

        // 3. Botón para mostrar PaymentSheet
        btnPagar.setOnClickListener(view -> {
            if (paymentIntentClientSecret != null) {
                PaymentSheet.Configuration config = new PaymentSheet.Configuration(
                        "Tu Empresa S.A.",
                        new PaymentSheet.CustomerConfiguration(
                                null, // customerId (opcional)
                                null  // ephemeralKey (opcional)
                        )
                );

                paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, config);
            } else {
                Toast.makeText(this, "Error al generar el intento de pago", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onPaymentSheetResult(PaymentSheetResult result) {
        if (result instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "¡Pago exitoso!", Toast.LENGTH_LONG).show();
        } else if (result instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Pago cancelado", Toast.LENGTH_SHORT).show();
        } else if (result instanceof PaymentSheetResult.Failed) {
            Toast.makeText(this, "Fallo en el pago", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchPaymentIntent() {
        new Thread(() -> {
            try {
                // ⚠️ Cambia por tu servidor
                URL url = new URL("https://tu-servidor.com/create-payment-intent");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("amount", 10000); // En centavos ($100.00)

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes());
                os.flush();

                Scanner scanner = new Scanner(conn.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }

                JSONObject json = new JSONObject(response.toString());
                paymentIntentClientSecret = json.getString("clientSecret");

                Log.d("PagoActivity", "clientSecret: " + paymentIntentClientSecret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}