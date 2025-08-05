package com.example.bottomnavactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bottomnavactivity.DTO.UsuarioGetResponse;
import com.example.bottomnavactivity.DTO.UsuarioLoginDTO;
import com.example.bottomnavactivity.DTO.UsuarioRegisterResponse;
import com.example.bottomnavactivity.DTO.UsuarioUpdateDTO;
import com.example.bottomnavactivity.DTO.UsuarioUpdateResponse;
import com.example.bottomnavactivity.Services.ServiceClient;
import com.example.bottomnavactivity.Services.UsuarioService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etCorreo;
    private MaterialButton btnGuardar;

    private UsuarioService service;
    private String usuarioId; // <-- viene del intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_user);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        btnGuardar = findViewById(R.id.btnGuardar);

        service = new ServiceClient().BuildRetrofitClient().create(UsuarioService.class);

        usuarioId = getIntent().getStringExtra("usuarioId");
        Log.d("EditUserActivity", "usuarioId recibido: " + usuarioId);

        if (usuarioId != null) {
            obtenerUsuarioPorId(usuarioId);
        } else {
            Toast.makeText(this, "Error: usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnGuardar.setOnClickListener(view -> actualizarUsuario());


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

    private void obtenerUsuarioPorId(String usuarioId) {
        Call<UsuarioGetResponse> call = service.obtenerUsuario(usuarioId);
        call.enqueue(new Callback<UsuarioGetResponse>() {
            @Override
            public void onResponse(Call<UsuarioGetResponse> call, Response<UsuarioGetResponse> response) {
                Log.d("EditUser", "Response code: " + response.code());

                if(response.isSuccessful() && response.body() != null) {
                    UsuarioGetResponse respuesta = response.body();
                    UsuarioLoginDTO usuario = respuesta.getUsuario();

                    if(usuario != null) {
                        Log.d("EditUser", "Usuario obtenido: " + usuario.getUsuario() + ", " + usuario.getCorreo());
                        etNombre.setText(usuario.getUsuario());
                        etCorreo.setText(usuario.getCorreo());
                    } else {
                        Toast.makeText(EditUserActivity.this, "Usuario no encontrado en la respuesta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditUserActivity.this, "No se pudo cargar el usuario", Toast.LENGTH_SHORT).show();
                    Log.e("EditUser", "Response no exitosa: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UsuarioGetResponse> call, Throwable t) {
                Toast.makeText(EditUserActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("EditUser", "Fallo: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void actualizarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();

        if (nombre.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioUpdateDTO usuarioActualizado = new UsuarioUpdateDTO(nombre, correo);

//        UsuarioUpdateDTO usuarioActualizado = new UsuarioUpdateDTO();
////        usuarioActualizado.setUsuarioId(usuarioId);
//        usuarioActualizado.setUsuario(nombre);
//        usuarioActualizado.setCorreo(correo);
////        usuarioActualizado.setClave(""); // Si no quiero cambiar la clave

        Call<UsuarioUpdateResponse> call = service.actualizarUsuario(usuarioId, usuarioActualizado);
        call.enqueue(new Callback<UsuarioUpdateResponse>() {
            @Override
            public void onResponse(Call<UsuarioUpdateResponse> call, Response<UsuarioUpdateResponse> response) {
                if (response.isSuccessful() && response.body() != null)  {
                    // Mostrar mensaje de éxito
                    UsuarioUpdateResponse respuesta = response.body();
                    Toast.makeText(EditUserActivity.this, respuesta.getMensaje(), Toast.LENGTH_SHORT).show();

                    // CAMBIO: Navegar a UsersActivity en lugar de hacer finish()
                    Intent intent = new Intent(EditUserActivity.this, UsersActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(EditUserActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    Log.e("EditUser", "Error: " + response.code() + " - " + response.message());


                    // Debug: Ver el cuerpo del error
                    try {
                        if (response.errorBody() != null) {
                            Log.e("EditUser", "Error body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e("EditUser", "Error leyendo error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<UsuarioUpdateResponse> call, Throwable t) {
                Toast.makeText(EditUserActivity.this, "Fallo en conexión", Toast.LENGTH_SHORT).show();
                Log.e("EditUser", "Fallo: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

}