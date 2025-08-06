package com.example.bottomnavactivity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottomnavactivity.DTO.UsuarioGetResponse;
import com.example.bottomnavactivity.DTO.UsuarioLoginDTO;
import com.example.bottomnavactivity.Services.ServiceClient;
import com.example.bottomnavactivity.Services.UsuarioService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvNombre, tvCorreo;
    private MaterialButton btnCerrarSesion;
    private UsuarioService service;
    private String usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvNombre = findViewById(R.id.tvNombre);
        tvCorreo = findViewById(R.id.tvCorreo);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        LinearLayout EditProfile = findViewById(R.id.btnPerfil);
        LinearLayout Users = findViewById(R.id.btnUsuarios);

        service = new ServiceClient().BuildRetrofitClient().create(UsuarioService.class);

        cargarDatosUsuario();

        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        Users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, UsersActivity.class);
                startActivity(intent);
            }
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
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

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        usuarioId = prefs.getString("usuarioId", null);
        String usuario = prefs.getString("usuario", "Usuario");
        String correo = prefs.getString("correo", "correo@ejemplo.com");

        Log.d("ProfileActivity", "usuarioId: " + usuarioId + ", usuario: " + usuario + ", correo: " + correo);

        if (usuarioId != null) {
            tvNombre.setText(usuario);
            tvCorreo.setText(correo);

            obtenerDatosActualizadosDelServidor(usuarioId);
        } else {
            tvNombre.setText("Usuario no logueado");
            tvCorreo.setText("No disponible");

            Toast.makeText(this, "No hay usuario logueado", Toast.LENGTH_SHORT).show();
            redirigirAlLogin();
        }
    }

    private void obtenerDatosActualizadosDelServidor(String usuarioId) {
        Call<UsuarioGetResponse> call = service.obtenerUsuario(usuarioId);
        call.enqueue(new Callback<UsuarioGetResponse>() {
            @Override
            public void onResponse(Call<UsuarioGetResponse> call, Response<UsuarioGetResponse> response) {
                Log.d("ProfileActivity", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    UsuarioGetResponse respuesta = response.body();
                    UsuarioLoginDTO usuario = respuesta.getUsuario();

                    if (usuario != null) {
                        Log.d("ProfileActivity", "Datos actualizados del servidor: " + usuario.getUsuario() + ", " + usuario.getCorreo());

                        tvNombre.setText(usuario.getUsuario());
                        tvCorreo.setText(usuario.getCorreo());

                        actualizarSharedPreferences(usuario);
                    }
                } else {
                    Log.e("ProfileActivity", "Response no exitosa: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UsuarioGetResponse> call, Throwable t) {
                Log.e("ProfileActivity", "Fallo al obtener datos del servidor: " + t.getMessage());
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

    private void cerrarSesion() {
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        redirigirAlLogin();
    }

    private void redirigirAlLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
