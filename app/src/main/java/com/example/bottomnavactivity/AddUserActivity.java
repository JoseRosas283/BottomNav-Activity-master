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

        if(usuario.isEmpty() || correo.isEmpty() || clave.isEmpty()) {
            Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
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
}