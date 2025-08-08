package com.example.bottomnavactivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bottomnavactivity.DTO.UsuarioGetResponse;
import com.example.bottomnavactivity.DTO.UsuarioLoginDTO;
import com.example.bottomnavactivity.DTO.UsuarioUpdateDTO;
import com.example.bottomnavactivity.DTO.UsuarioUpdateResponse;
import com.example.bottomnavactivity.Services.ServiceClient;
import com.example.bottomnavactivity.Services.UsuarioService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etCorreo, etNuevaClave, etConfNuevaClave;
    private MaterialButton btnGuardar, btnBackProfile;
    private MaterialCardView cardPerfil;
    private ImageView imagePerfil;
    private UsuarioService service;
    private String usuarioId;

    // Para manejo de imágenes
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;
    private static final int PERMISSION_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etNuevaClave = findViewById(R.id.etNuevaClave);
        etConfNuevaClave = findViewById(R.id.etConfNuevaClave);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnBackProfile = findViewById(R.id.btnBackProfile);
        cardPerfil = findViewById(R.id.cardPerfil);
        imagePerfil = findViewById(R.id.imagePerfil);

        service = new ServiceClient().BuildRetrofitClient().create(UsuarioService.class);

        // Configurar launchers para galería y cámara
        setupImageLaunchers();
        setupPermissionLauncher();

        // Cargar foto de perfil guardada
        cargarFotoPerfilGuardada();

        obtenerDatosUsuarioLogueado();

        cardPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarOpcionesFoto();
            }
        });

        btnBackProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarUsuario();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }else if(itemId==R.id.bottom_reloj){
                startActivity(new Intent(getApplicationContext(), RelojActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;

            }else if (itemId == R.id.bottom_profile) {
                return true;
            }

            return false;
        });
    }

    private void setupImageLaunchers() {
        // Launcher para galería
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            procesarImagenSeleccionada(imageUri);
                        }
                    }
                }
        );

        // Launcher para cámara
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            if (imageBitmap != null) {
                                imagePerfil.setImageBitmap(imageBitmap);
                                guardarFotoPerfilEnSharedPreferences(imageBitmap);
                            }
                        }
                    }
                }
        );
    }

    private void setupPermissionLauncher() {
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allPermissionsGranted = true;
                    for (Boolean granted : result.values()) {
                        if (!granted) {
                            allPermissionsGranted = false;
                            break;
                        }
                    }

                    if (allPermissionsGranted) {
                        // Los permisos fueron otorgados, mostrar opciones de foto
                        mostrarOpcionesFoto();
                    } else {
                        Toast.makeText(this, "Se necesitan permisos para cambiar la foto de perfil", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void mostrarOpcionesFoto() {
        // Verificar permisos antes de mostrar opciones
        if (!verificarTodosLosPermisos()) {
            solicitarTodosLosPermisos();
            return;
        }

        String[] opciones = {"Galería", "Cámara", "Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar foto de perfil");
        builder.setItems(opciones, (dialog, which) -> {
            switch (which) {
                case 0:
                    abrirGaleria();
                    break;
                case 1:
                    abrirCamara();
                    break;
                case 2:
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    private boolean verificarTodosLosPermisos() {
        List<String> permisosNecesarios = getPermisosNecesarios();
        for (String permiso : permisosNecesarios) {
            if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void solicitarTodosLosPermisos() {
        List<String> permisosNecesarios = getPermisosNecesarios();
        List<String> permisosPorSolicitar = new ArrayList<>();

        for (String permiso : permisosNecesarios) {
            if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
                permisosPorSolicitar.add(permiso);
            }
        }

        if (!permisosPorSolicitar.isEmpty()) {
            permissionLauncher.launch(permisosPorSolicitar.toArray(new String[0]));
        }
    }

    private List<String> getPermisosNecesarios() {
        List<String> permisos = new ArrayList<>();

        // Permiso para cámara
        permisos.add(Manifest.permission.CAMERA);

        // Permisos para galería según la versión de Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            permisos.add(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            permisos.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        return permisos;
    }

    private void abrirGaleria() {
        if (verificarPermisosGaleria()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        } else {
            solicitarTodosLosPermisos();
        }
    }

    private void abrirCamara() {
        if (verificarPermisosCamara()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(intent);
        } else {
            solicitarTodosLosPermisos();
        }
    }

    private boolean verificarPermisosGaleria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private boolean verificarPermisosCamara() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void procesarImagenSeleccionada(Uri imageUri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

            // Redimensionar imagen para optimizar
            Bitmap bitmapRedimensionado = redimensionarBitmap(bitmap, 300, 300);

            imagePerfil.setImageBitmap(bitmapRedimensionado);
            guardarFotoPerfilEnSharedPreferences(bitmapRedimensionado);

        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            Log.e("EditProfile", "Error al procesar imagen: " + e.getMessage());
        }
    }

    private Bitmap redimensionarBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) maxWidth) / width;
        float scaleHeight = ((float) maxHeight) / height;
        float scaleFactor = Math.min(scaleWidth, scaleHeight);

        int newWidth = Math.round(width * scaleFactor);
        int newHeight = Math.round(height * scaleFactor);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private void guardarFotoPerfilEnSharedPreferences(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] byteArray = baos.toByteArray();
            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

            SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            // CAMBIO: Usar la misma clave que ProfileActivity
            editor.putString("profile_image" + usuarioId, encodedImage);
            editor.apply();

            Toast.makeText(this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("EditProfile", "Error al guardar imagen: " + e.getMessage());
        }
    }

    private void cargarFotoPerfilGuardada() {
        if (usuarioId == null) {
            imagePerfil.setImageResource(R.drawable.ic_avatar_img);
            return;
        }

        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        // CAMBIO: Usar la misma clave que ProfileActivity
        String encodedImage = prefs.getString("profile_image" + usuarioId, null);

        if (encodedImage != null) {
            try {
                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imagePerfil.setImageBitmap(decodedBitmap);
            } catch (Exception e) {
                Log.e("EditProfile", "Error al cargar imagen guardada: " + e.getMessage());
                // Si hay error, mantener la imagen por defecto
                imagePerfil.setImageResource(R.drawable.ic_avatar_img);
            }
        } else {
            // Si no hay imagen guardada, usar la imagen por defecto
            imagePerfil.setImageResource(R.drawable.ic_avatar_img);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                mostrarOpcionesFoto();
            } else {
                Toast.makeText(this, "Se necesitan permisos para cambiar la foto de perfil", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void obtenerDatosUsuarioLogueado() {
        // Obtener datos desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        usuarioId = prefs.getString("usuarioId", null);
        String usuario = prefs.getString("usuario", "");
        String correo = prefs.getString("correo", "");

        Log.d("EditProfile", "usuarioId: " + usuarioId + ", usuario: " + usuario + ", correo: " + correo);

        if (usuarioId != null) {
            // Llenar los campos con los datos guardados
            etNombre.setText(usuario);
            etCorreo.setText(correo);

            // Obtener datos actualizados del servidor
            obtenerUsuarioActualizadoDelServidor(usuarioId);
        } else {
            Toast.makeText(this, "No hay usuario logueado", Toast.LENGTH_SHORT).show();
            // Redirigir al login si no hay usuario logueado
            Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void obtenerUsuarioActualizadoDelServidor(String usuarioId) {
        Call<UsuarioGetResponse> call = service.obtenerUsuario(usuarioId);
        call.enqueue(new Callback<UsuarioGetResponse>() {
            @Override
            public void onResponse(Call<UsuarioGetResponse> call, Response<UsuarioGetResponse> response) {
                Log.d("EditProfile", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    UsuarioGetResponse respuesta = response.body();
                    UsuarioLoginDTO usuario = respuesta.getUsuario();

                    if (usuario != null) {
                        Log.d("EditProfile", "Usuario obtenido del servidor: " + usuario.getUsuario() + ", " + usuario.getCorreo());
                        // Actualizar los campos con los datos más recientes del servidor
                        etNombre.setText(usuario.getUsuario());
                        etCorreo.setText(usuario.getCorreo());

                        // Actualizar SharedPreferences con los datos más recientes
                        actualizarSharedPreferences(usuario);
                    }
                } else {
                    Log.e("EditProfile", "Response no exitosa: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UsuarioGetResponse> call, Throwable t) {
                Log.e("EditProfile", "Fallo al obtener datos del servidor: " + t.getMessage());
            }
        });
    }

    private void actualizarSharedPreferences(UsuarioLoginDTO usuario) {
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("usuario", usuario.getUsuario());
        editor.putString("correo", usuario.getCorreo());
        editor.apply();
    }

    private void actualizarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String nuevaClave = etNuevaClave.getText().toString().trim();
        String confirmarClave = etConfNuevaClave.getText().toString().trim();

        // Validar solo si hay cambios en los campos
        boolean nombreValido = true;
        boolean correoValido = true;
        boolean claveValida = true;
        boolean confirmarClaveValida = true;

        // Solo validar nombre si no está vacío (si el usuario quiere cambiarlo)
        if (!nombre.isEmpty()) {
            nombreValido = validarNombre();
        }

        // Solo validar correo si no está vacío (si el usuario quiere cambiarlo)
        if (!correo.isEmpty()) {
            correoValido = validarCorreo();
        }

        // Solo validar clave si no está vacía (si el usuario quiere cambiarla)
        if (!nuevaClave.isEmpty()) {
            claveValida = validarClave();
        }

        if (!nuevaClave.isEmpty() || !confirmarClave.isEmpty()) {
            confirmarClaveValida = validarConfirmarClave();
        }

        if (!nombreValido || !correoValido || !claveValida || !confirmarClaveValida) {
            Toast.makeText(this, "Corrige los errores antes de continuar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar que al menos un campo tenga datos para actualizar
        if (nombre.isEmpty() && correo.isEmpty() && nuevaClave.isEmpty()) {
            Toast.makeText(this, "Debe modificar al menos un campo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usuarioId == null) {
            Toast.makeText(this, "Error: usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto para actualizar con clave opcional
        UsuarioUpdateDTO usuarioActualizado;
        if (!nuevaClave.isEmpty()) {
            usuarioActualizado = new UsuarioUpdateDTO(nombre, correo, nuevaClave);
        } else {
            usuarioActualizado = new UsuarioUpdateDTO(nombre, correo);
        }

        Call<UsuarioUpdateResponse> call = service.actualizarUsuario(usuarioId, usuarioActualizado);
        call.enqueue(new Callback<UsuarioUpdateResponse>() {
            @Override
            public void onResponse(Call<UsuarioUpdateResponse> call, Response<UsuarioUpdateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UsuarioUpdateResponse respuesta = response.body();
                    Toast.makeText(EditProfileActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();

                    // Actualizar SharedPreferences con los nuevos datos
                    SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("usuario", nombre);
                    editor.putString("correo", correo);
                    if (!nombre.isEmpty()) {
                        editor.putString("usuario", nombre);
                    }
                    if (!correo.isEmpty()) {
                        editor.putString("correo", correo);
                    }
                    if (!nuevaClave.isEmpty()) {
                        editor.putString("clave", nuevaClave);
                    }
                    editor.apply();

                    // NOTA: NO redirige automáticamente, el usuario debe usar el botón Back
                    // El botón btnBackProfile ya tiene su funcionalidad para regresar a ProfileActivity
                } else {
                    Toast.makeText(EditProfileActivity.this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                    Log.e("EditProfile", "Error: " + response.code() + " - " + response.message());

                    try {
                        if (response.errorBody() != null) {
                            Log.e("EditProfile", "Error body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e("EditProfile", "Error leyendo error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<UsuarioUpdateResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("EditProfile", "Fallo: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private boolean validarNombre() {
        String usuario = etNombre.getText().toString().trim();

        if (usuario.length() < 3) {
            etNombre.setError("Mínimo 3 caracteres");
            return false;
        } else if (usuario.length() > 100) {
            etNombre.setError("Máximo 100 caracteres");
            return false;
        } else if (!Pattern.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", usuario)) {
            etNombre.setError("Solo se permiten letras y espacios");
            return false;
        } else if (usuario.trim().split("\\s+").length < 2) {
            etNombre.setError("Ingresa nombre y apellido");
            return false;
        } else {
            etNombre.setError(null);
            return true;
        }
    }

    private boolean validarCorreo() {
        String correo = etCorreo.getText().toString().trim();

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Formato de correo inválido");
            return false;
        } else if (!correo.toLowerCase().contains("@gmail.com") && !correo.toLowerCase().contains("@hotmail.com") &&
                !correo.toLowerCase().contains("@yahoo.com") && !correo.toLowerCase().contains("@outlook.com")) {
            etCorreo.setError("Usa un proveedor válido (@gmail.com, @hotmail.com, etc.)");
            return false;
        } else if (correo.length() > 100) {
            etCorreo.setError("Máximo 100 caracteres");
            return false;
        } else {
            etCorreo.setError(null);
            return true;
        }
    }

    private boolean validarClave() {
        String clave = etNuevaClave.getText().toString().trim();

        if (clave.length() < 6) {
            etNuevaClave.setError("Mínimo 6 caracteres");
            return false;
        } else if (clave.length() > 50) {
            etNuevaClave.setError("Máximo 50 caracteres");
            return false;
        } else if (clave.contains(" ")) {
            etNuevaClave.setError("No se permiten espacios");
            return false;
        } else if (!Pattern.matches(".*[a-zA-Z].*", clave)) {
            etNuevaClave.setError("Debe contener al menos una letra");
            return false;
        } else if (!Pattern.matches(".*[0-9].*", clave)) {
            etNuevaClave.setError("Debe contener al menos un número");
            return false;
        } else {
            etNuevaClave.setError(null);
            return true;
        }
    }

    private boolean validarConfirmarClave() {
        String nuevaClave = etNuevaClave.getText().toString().trim();
        String confirmarClave = etConfNuevaClave.getText().toString().trim();

        // Si no hay contraseña nueva, no es necesario confirmar
        if (nuevaClave.isEmpty() && confirmarClave.isEmpty()) {
            etConfNuevaClave.setError(null);
            return true;
        }

        // Si hay contraseña nueva pero no confirmación
        if (!nuevaClave.isEmpty() && confirmarClave.isEmpty()) {
            etConfNuevaClave.setError("Debes confirmar la nueva contraseña");
            return false;
        }

        // Si hay confirmación pero no contraseña nueva
        if (nuevaClave.isEmpty() && !confirmarClave.isEmpty()) {
            etConfNuevaClave.setError("Primero ingresa la nueva contraseña");
            return false;
        }

        // Si ambas tienen contenido, verificar que coincidan
        if (!nuevaClave.equals(confirmarClave)) {
            etConfNuevaClave.setError("Las contraseñas no coinciden");
            return false;
        }

        etConfNuevaClave.setError(null);
        return true;
    }
}