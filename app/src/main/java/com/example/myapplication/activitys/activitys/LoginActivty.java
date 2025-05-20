package com.example.myapplication.activitys.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activitys.util.Constant;
import com.example.myapplication.activitys.util.PreferenseManager;
import com.example.myapplication.databinding.ActivityLoginActivtyBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;


public class LoginActivty extends AppCompatActivity {

    private PreferenseManager preferenseManager;

    private ActivityLoginActivtyBinding binding;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar si el usuario ya está logeado
        preferenseManager = new PreferenseManager(getApplicationContext());
        if (preferenseManager.getBoolean(Constant.KEY_IS_SIGNED_IN)) {
            startActivity(new Intent(getApplicationContext(), PpalTemaActivity.class));
            finish();
            return; // Para evitar seguir con la inicialización innecesaria
        }

        if (preferenseManager.getBoolean(Constant.KEY_EMPRESA_ID)) {
            startActivity(new Intent(getApplicationContext(), PpalTemaActivity.class));
            finish();
            return; // Para evitar seguir con la inicialización innecesaria
        }

        // Inicializar ViewBinding
        binding = ActivityLoginActivtyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar Facebook CallbackManager
        callbackManager = CallbackManager.Factory.create();

        // Registrar el callback de Facebook
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = loginResult.getAccessToken();
                        GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response) -> {
                            try {
                                String name = object.getString("name");
                                String email = object.getString("email");

                                preferenseManager.putBoolean(Constant.KEY_IS_SIGNED_IN, true);
                                preferenseManager.putString(Constant.KEY_NAME, name);
                                preferenseManager.putString(Constant.KEY_EMAIL, email);

                                goToHome();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Login cancelado", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(getApplicationContext(), "Error en Facebook login", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Asignar listeners a los botones
        binding.imageFacebook.setOnClickListener(v -> signInWithFacebook());
        binding.imageGoogle.setOnClickListener(v -> signInWithGoogle());

        // Cualquier lógica adicional del login
        loginSingLogin();
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void loginSingLogin() {

        binding.buttonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginRegistroActivty.class));

            }
        });

        binding.buttonIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidSingInDetalis()) {
                    singIn();
                }
            }

        });

    }

    private void singIn() {

        loading(true);
        String tele = binding.TextUsuario.getText().toString();
        String contrasena = binding.TextContrasena.getText().toString();

        //loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String hashedPassword;

        hashedPassword = hashSHA256(contrasena);

        database.collection("user").
                whereEqualTo("Telefono", tele).
                whereEqualTo("Contraseña", hashedPassword)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0) {

                        HashMap<String, Object> data = new HashMap<>();
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenseManager.putBoolean(Constant.KEY_IS_SIGNED_IN, true);
                        preferenseManager.putString("usuarioID", documentSnapshot.getId());
                        preferenseManager.putString("Telefono", documentSnapshot.getString("Telefono"));
                        preferenseManager.putString("Contraseña", documentSnapshot.getString("Contraseña"));
                        preferenseManager.putString("Nombre", documentSnapshot.getString("Nombre"));


                        Intent intent = new Intent(getApplicationContext(), PpalTemaActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                    } else {

                        loading(false);
                        showToast("unable to sing in");
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("email", "public_profile")
        );
    }

    private void goToHome() {
        Intent intent = new Intent(getApplicationContext(), PpalTemaActivity.class);
        startActivity(intent);
        finish();
    }

    private void loading(Boolean isLoading){

        if(isLoading){

            binding.buttonIngresar.setVisibility(View.INVISIBLE);
            binding.ProgressBar.setVisibility(View.VISIBLE);
        }else {
            binding.ProgressBar.setVisibility(View.INVISIBLE);
            binding.buttonIngresar.setVisibility(View.VISIBLE);

        }
    }

    private Boolean isValidSingInDetalis() {

        if (binding.TextUsuario.getText().toString().trim().isEmpty()) {
            showToast("ingresa el Telefono o Email");
            return false;
        } else if (binding.TextContrasena.getText().toString().trim().isEmpty()) {
            showToast("Enter apellido");
            return false;
        } else {

        }
        return true;

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    public static String hashSHA256(String password) {
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