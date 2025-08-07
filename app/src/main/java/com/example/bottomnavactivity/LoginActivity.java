package com.example.bottomnavactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bottomnavactivity.DTO.LoginDTO;
import com.example.bottomnavactivity.DTO.UsuarioLoginDTO;
import com.example.bottomnavactivity.Services.ServiceClient;
import com.example.bottomnavactivity.Services.UsuarioService;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout txtUsuario;

    private TextInputLayout txtPassword;

    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        txtUsuario = (TextInputLayout) findViewById(R.id.txtUsuario);
        txtPassword = (TextInputLayout) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnIniciarSesion);

        TextView tvOlvidar = findViewById(R.id.tvOlvidarContrasena);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validar campos antes de hacer la petición
                boolean correoValido = validarCorreo();
                boolean claveValida = validarClave();

                if (!correoValido || !claveValida) {
                    Toast.makeText(LoginActivity.this, "Corrige los errores antes de continuar", Toast.LENGTH_SHORT).show();
                    return;
                }

                ServiceClient client = new ServiceClient();
                Retrofit retrofit = client.BuildRetrofitClient();
                UsuarioService service = retrofit.create(UsuarioService.class);

                LoginDTO loginDTO = new LoginDTO();
                loginDTO.setCorreo(txtUsuario.getEditText().getText().toString());
                loginDTO.setClave(txtPassword.getEditText().getText().toString());

                Call<UsuarioLoginDTO> usuarioCall = service.Login(loginDTO);

                usuarioCall.enqueue(new Callback<UsuarioLoginDTO>() {
                    @Override
                    public void onResponse(Call<UsuarioLoginDTO> call, Response<UsuarioLoginDTO> response) {
                        if (response.isSuccessful()) {
                            UsuarioLoginDTO responseBody = response.body();

                            if (responseBody == null) {
                                Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (responseBody.getUsuario() == null) {
                                Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (responseBody.getUsuario() != null) {
                                Toast.makeText(LoginActivity.this, "Bienvenido " + responseBody.getUsuario(), Toast.LENGTH_SHORT).show();


                                // GUARDAR EL USUARIO ID EN SHARED PREFERENCES
                                SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("usuarioId", responseBody.getUsuarioId());
                                editor.putString("usuario", responseBody.getUsuario());
                                editor.putString("correo", responseBody.getCorreo());
                                editor.putString("clave", responseBody.getClave());
                                editor.apply();

                                // Redirigir al MainActivity
                                Intent inicio = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(inicio);
                                finish();
                            }
                        } else {
                            String errorMessage = "Error del servidor";

                            if (response.code() == 404) {
                                errorMessage = "Correo o contraseña incorrectos";
                            } else if (response.code() == 500) {
                                errorMessage = "Error en el servidor, intenta más tarde";
                            } else if (response.code() == 502) {
                                errorMessage = "Servidor no disponible, intenta en unos minutos";
                            }

                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UsuarioLoginDTO> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Ocurrió un error en el servidor: Conexión perdida con el servidor.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });


            }
        });

        tvOlvidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, OlvidarContrasenaActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private boolean validarCorreo() {
        String correo = txtUsuario.getEditText().getText().toString().trim();

        if (correo.isEmpty()) {
            txtUsuario.setError("El correo electrónico es obligatorio");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            txtUsuario.setError("Formato de correo inválido");
            return false;
        } else if (!correo.toLowerCase().contains("@gmail.com") && !correo.toLowerCase().contains("@hotmail.com") &&
                !correo.toLowerCase().contains("@yahoo.com") && !correo.toLowerCase().contains("@outlook.com")) {
            txtUsuario.setError("Usa un proveedor válido (@gmail.com, @hotmail.com, etc.)");
            return false;
        } else if (correo.length() > 100) {
            txtUsuario.setError("Máximo 100 caracteres");
            return false;
        } else {
            txtUsuario.setError(null);
            return true;
        }
    }

    private boolean validarClave() {
        String clave = txtPassword.getEditText().getText().toString().trim();

        if (clave.isEmpty()) {
            txtPassword.setError("La contraseña es obligatoria");
            return false;
        } else if (clave.length() < 6) {
            txtPassword.setError("Mínimo 6 caracteres");
            return false;
        } else if (clave.length() > 50) {
            txtPassword.setError("Máximo 50 caracteres");
            return false;
        } else if (clave.contains(" ")) {
            txtPassword.setError("No se permiten espacios");
            return false;
        } else if (!Pattern.matches(".*[a-zA-Z].*", clave)) {
            txtPassword.setError("Debe contener al menos una letra");
            return false;
        } else if (!Pattern.matches(".*[0-9].*", clave)) {
            txtPassword.setError("Debe contener al menos un número");
            return false;
        } else {
            txtPassword.setError(null);
            return true;
        }
    }
}