package com.example.myapplication.activitys.activitys;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.ProductoAdapter;
import com.example.myapplication.activitys.model.Producto;
import com.example.myapplication.activitys.util.PreferenseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AdminProductosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductoAdapter productoAdapter;
    private List<Producto> listaProductos;
    private FloatingActionButton fabAgregar;
    private FirebaseFirestore db;
    private PreferenseManager preferenseManager;
    private String empresaID;

    private static final int REQUEST_GALLERY = 1001;
    private static final int REQUEST_CAMERA = 1002;
    private Bitmap imagenSeleccionada;
    private AlertDialog dialogActual;
    private String nombreTmp, descripcionTmp, categoriaTmp;
    private double precioTmp;

    private static final int PERMISSION_CAMERA_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_productos);

        recyclerView = findViewById(R.id.recyclerViewProductos);
        fabAgregar = findViewById(R.id.fabAgregarProducto);
        db = FirebaseFirestore.getInstance();

        preferenseManager = new PreferenseManager(getApplicationContext());
        empresaID = preferenseManager.getString("empresaNit");

        if (empresaID == null || empresaID.isEmpty()) {
            Toast.makeText(this, "No se encontró el NIT de la empresa", Toast.LENGTH_LONG).show();
            Log.e("AdminProductos", "empresaID (NIT) es null o vacío");
            finish();
            return;
        }

        listaProductos = new ArrayList<>();
        productoAdapter = new ProductoAdapter(listaProductos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(productoAdapter);

        cargarProductos();

        fabAgregar.setOnClickListener(v -> mostrarDialogoAgregarProducto());
    }

    // Carga productos desde Firestore
    private void cargarProductos() {
        String empresaUID = preferenseManager.getString("empresaUID"); // Obtener el UID guardado

        db.collection("empresas")
                .document(empresaUID)
                .collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaProductos.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Producto producto = doc.toObject(Producto.class);
                        if (producto != null) {
                            producto.setId(doc.getId()); // Asigna ID del documento al modelo
                            listaProductos.add(producto);
                        }
                    }
                    productoAdapter.notifyDataSetChanged(); // Actualiza el RecyclerView
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                );
    }

    // Dialogo para agregar producto
    private void mostrarDialogoAgregarProducto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar Producto");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_producto, null);
        final EditText etNombre = viewInflated.findViewById(R.id.etNombreProducto);
        final EditText etDescripcion = viewInflated.findViewById(R.id.etDescripcionProducto);
        final EditText etPrecio = viewInflated.findViewById(R.id.etPrecioProducto);
        final ImageView ivImagen = viewInflated.findViewById(R.id.ivImagenProducto);

        ivImagen.setOnClickListener(v -> mostrarOpcionesImagen());

        builder.setView(viewInflated);
        builder.setPositiveButton("Guardar", null); // Control manual del botón guardar
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        dialogActual = builder.create();
        dialogActual.show();

        dialogActual.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            nombreTmp = etNombre.getText().toString().trim();
            descripcionTmp = etDescripcion.getText().toString().trim();
            categoriaTmp = descripcionTmp.toLowerCase(); // usar descripción para categoria si quieres
            String precioStr = etPrecio.getText().toString().trim();

            if (nombreTmp.isEmpty() || descripcionTmp.isEmpty() || precioStr.isEmpty() || imagenSeleccionada == null) {
                Toast.makeText(this, "Completa todos los campos y selecciona una imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                precioTmp = Double.parseDouble(precioStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            subirImagenAFirebase(imagenSeleccionada, categoriaTmp);
        });
    }

    // Subir imagen a Firebase Storage y guardar URL en Firestore por el momento sin imagen
    private void subirImagenAFirebase(Bitmap bitmap, String categoria) {
        if (bitmap == null) {
            Toast.makeText(this, "No se pudo cargar la imagen correctamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap imagenConFondo = agregarFondoACategoria(bitmap, categoria);
        if (imagenConFondo == null) {
            Toast.makeText(this, "Error al procesar la imagen con fondo.", Toast.LENGTH_SHORT).show();
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean comprime = imagenConFondo.compress(Bitmap.CompressFormat.PNG, 100, baos);
        if (!comprime) {
            Toast.makeText(this, "Error al comprimir la imagen.", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] data = baos.toByteArray();

        if (data.length == 0) {
            Toast.makeText(this, "La imagen está vacía y no se puede subir.", Toast.LENGTH_SHORT).show();
            Log.e("FirebaseUpload", "Byte array de imagen vacío.");
            return;
        }

        String nombreArchivo = "mifoto_" + System.currentTimeMillis() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("imagenes/" + nombreArchivo);
        guardarProductoEnFirestore();
//        storageRef.putBytes(data)
//                .addOnSuccessListener(taskSnapshot -> {
//                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                        String urlDescarga = uri.toString();
//                        Log.d("FirebaseStorage", "URL descarga: " + urlDescarga);
//                        Toast.makeText(this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
//
//                        // Aquí llamas a la función para guardar el producto con la URL
//                        guardarProductoEnFirestore(urlDescarga);
//
//                    }).addOnFailureListener(e -> {
//                        Log.e("FirebaseUpload", "Error al obtener URL", e);
//                        Toast.makeText(this, "Error al obtener URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("FirebaseUpload", "Error al subir imagen", e);
//                    Toast.makeText(this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                });
    }

    // Método ejemplo para usar la URL (lo defines tú según tu necesidad)
    private void usarUrlDeImagen(String url) {
        // Por ejemplo, actualizar un ImageView con Glide/Picasso o guardar URL en Firestore/Realtime DB
        Log.d("FirebaseStorage", "Usando URL en app: " + url);
    }

    // Guardar producto con URL imagen en Firestore
    private void guardarProductoEnFirestore() {
        // Obtener empresaUID desde PreferenseManager
        String empresaUID = preferenseManager.getString("empresaUID");

        // Crear objeto producto
        Producto producto = new Producto(nombreTmp, descripcionTmp, precioTmp);

        // Guardar en Firestore dentro de la colección correspondiente
        db.collection("empresas")
                .document(empresaUID)
                .collection("productos")
                .add(producto)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Producto agregado sin imagen", Toast.LENGTH_SHORT).show();
                    cargarProductos(); // Recarga la lista
                    if (dialogActual != null) dialogActual.dismiss(); // Cierra el diálogo
                    imagenSeleccionada = null; // Limpia imagen seleccionada
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar producto", Toast.LENGTH_SHORT).show()
                );
    }

    private void mostrarOpcionesImagen() {
        String[] opciones = {"Galería", "Cámara"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar imagen");
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) {
                // Galería
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_GALLERY);
            } else {
                // Cámara: verificar permiso
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_CAMERA_CODE);
                } else {
                    abrirCamara();
                }
            }
        });
        builder.show();
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY && data != null) {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    imagenSeleccionada = bitmap;
                    if (dialogActual != null) {
                        ImageView iv = dialogActual.findViewById(R.id.ivImagenProducto);
                        iv.setImageBitmap(bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CAMERA && data != null) {
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                imagenSeleccionada = bitmap;
                if (dialogActual != null) {
                    ImageView iv = dialogActual.findViewById(R.id.ivImagenProducto);
                    iv.setImageBitmap(bitmap);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CAMERA_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Agrega un fondo de color según categoría (o nombre de producto)
    private Bitmap agregarFondoACategoria(Bitmap original, String categoria) {
        int width = original.getWidth();
        int height = original.getHeight();

        Bitmap resultado = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultado);

        // Selecciona color o drawable según categoría
        int colorFondo = 0xFFE0E0E0; // color gris claro por defecto

        switch (categoria.toLowerCase()) {
            case "frutas":
                colorFondo = 0xFFFFF3E0; // naranja claro
                break;
            case "verduras":
                colorFondo = 0xFFE0FFE0; // verde claro
                break;
            // agrega más casos según necesites
            default:
                colorFondo = 0xFFF0F0F0;
        }

        canvas.drawColor(colorFondo);
        canvas.drawBitmap(original, 0, 0, null);

        return resultado;
    }
}
