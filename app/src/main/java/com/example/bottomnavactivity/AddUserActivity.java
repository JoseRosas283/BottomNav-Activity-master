package com.example.bottomnavactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bottomnavactivity.DTO.UsuarioDTO;
import com.example.bottomnavactivity.DTO.UsuarioLoginDTO;
import com.example.bottomnavactivity.DTO.UsuarioRegisterResponse;
import com.example.bottomnavactivity.Services.ServiceClient;
import com.example.bottomnavactivity.Services.UsuarioService;
import com.example.bottomnavactivity.databinding.ActivityAddUserBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddUserActivity extends AppCompatActivity {

    ActivityAddUserBinding binding;

    ServiceClient client = new ServiceClient();
    Retrofit retrofit = client.BuildRetrofitClient();
    UsuarioService service = retrofit.create(UsuarioService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_add_user);
        binding = ActivityAddUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarUsuario();
            }
        });

        // Intent para ir a la pantalla anterior donde esta las tabla
        MaterialButton BackUsers = findViewById(R.id.btnBackUsers);

        BackUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddUserActivity.this, UsersActivity.class);
                startActivity(intent);
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
            } else if (itemId == R.id.bottom_reloj) {
                startActivity(new Intent(getApplicationContext(), RelojActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;

            } else if (itemId == R.id.bottom_profile) {
                return true;
            }

            return false;
        });
    }

    private void agregarUsuario() {
        String usuario = binding.etNombre.getText().toString().trim();
        String correo = binding.etCorreo.getText().toString().trim();
        String clave = binding.etClave.getText().toString().trim();

        // Llamar a las funciones de validación
        boolean nombreValido = validarNombre();
        boolean correoValido = validarCorreo();
        boolean claveValida = validarClave();
        boolean confirmarClaveValida = validarConfirmarClave();

        if (!nombreValido || !correoValido || !claveValida || !confirmarClaveValida) {
            Toast.makeText(this, "Corrige los errores antes de continuar", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioDTO nuevoUsuario = new UsuarioDTO(usuario, correo, clave);

        Call<UsuarioRegisterResponse> call = service.agregarUsuario(nuevoUsuario);
        call.enqueue(new Callback<UsuarioRegisterResponse>() {
            @Override
            public void onResponse(Call<UsuarioRegisterResponse> call, Response<UsuarioRegisterResponse> response) {
                Log.d("AddUserActivity", "Código HTTP: " + response.code());
                if(response.isSuccessful()) {
                    UsuarioRegisterResponse respuesta = response.body();
                    Log.d("AddUserActivity", "Usuario agregado con éxito" + respuesta.getMensaje());
                    Toast.makeText(AddUserActivity.this, "Usuario agregado correctamente", Toast.LENGTH_SHORT).show();
                    // volver a UsersActivity
                    startActivity(new Intent(AddUserActivity.this, UsersActivity.class));
                    finish();
                } else {
//                    Toast.makeText(AddUserActivity.this, "Error al agregar (response error)", Toast.LENGTH_SHORT).show();
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("AddUserActivity", "Error en respuesta: " + errorBody);
//                        Toast.makeText(AddUserActivity.this, "Error al agregar (HTTP " + response.code() + ")", Toast.LENGTH_SHORT).show();
                        Toast.makeText(AddUserActivity.this, "Error: el usuario ya existe.", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UsuarioRegisterResponse> call, Throwable t) {
                Log.e("AddUserActivity", "Fallo en conexión: " + t.getMessage());
                Toast.makeText(AddUserActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean validarNombre() {
        String usuario = binding.etNombre.getText().toString().trim();

        if (usuario.isEmpty()) {
            binding.etNombre.setError("El nombre completo es obligatorio");
            return false;
        } else if (usuario.length() < 3) {
            binding.etNombre.setError("Mínimo 3 caracteres");
            return false;
        } else if (usuario.length() > 100) {
            binding.etNombre.setError("Máximo 100 caracteres");
            return false;
        } else if (!Pattern.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", usuario)) {
            binding.etNombre.setError("Solo se permiten letras y espacios");
            return false;
        } else if (usuario.trim().split("\\s+").length < 2) {
            binding.etNombre.setError("Ingresa nombre y apellido");
            return false;
        } else {
            binding.etNombre.setError(null);
            return true;
        }
    }

    private boolean validarCorreo() {
        String correo = binding.etCorreo.getText().toString().trim();

        if (correo.isEmpty()) {
            binding.etCorreo.setError("El correo electrónico es obligatorio");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            binding.etCorreo.setError("Formato de correo inválido");
            return false;
        } else if (!correo.toLowerCase().contains("@gmail.com") && !correo.toLowerCase().contains("@hotmail.com") &&
                !correo.toLowerCase().contains("@yahoo.com") && !correo.toLowerCase().contains("@outlook.com")) {
            binding.etCorreo.setError("Usa un proveedor válido (@gmail.com, @hotmail.com, etc.)");
            return false;
        } else if (correo.length() > 100) {
            binding.etCorreo.setError("Máximo 100 caracteres");
            return false;
        } else {
            binding.etCorreo.setError(null);
            return true;
        }
    }

    private boolean validarClave() {
        String clave = binding.etClave.getText().toString().trim();

        if (clave.isEmpty()) {
            binding.etClave.setError("La contraseña es obligatoria");
            return false;
        } else if (clave.length() < 6) {
            binding.etClave.setError("Mínimo 6 caracteres");
            return false;
        } else if (clave.length() > 50) {
            binding.etClave.setError("Máximo 50 caracteres");
            return false;
        } else if (clave.contains(" ")) {
            binding.etClave.setError("No se permiten espacios");
            return false;
        } else if (!Pattern.matches(".*[a-zA-Z].*", clave)) {
            binding.etClave.setError("Debe contener al menos una letra");
            return false;
        } else if (!Pattern.matches(".*[0-9].*", clave)) {
            binding.etClave.setError("Debe contener al menos un número");
            return false;
        } else {
            binding.etClave.setError(null);
            return true;
        }
    }

    private boolean validarConfirmarClave() {
        String clave = binding.etClave.getText().toString().trim();
        String confirmarClave = binding.etConfirmarClave.getText().toString().trim();

        if (confirmarClave.isEmpty()) {
            binding.etConfirmarClave.setError("Debes confirmar la contraseña");
            return false;
        } else if (!clave.equals(confirmarClave)) {
            binding.etConfirmarClave.setError("Las contraseñas no coinciden");
            return false;
        } else {
            binding.etConfirmarClave.setError(null);
            return true;
        }
    }
}