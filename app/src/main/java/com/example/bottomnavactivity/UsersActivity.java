package com.example.bottomnavactivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bottomnavactivity.DTO.UsuarioLoginDTO;
import com.example.bottomnavactivity.DTO.UsuarioResponse;
import com.example.bottomnavactivity.Services.ServiceClient;
import com.example.bottomnavactivity.Services.UsuarioService;
import com.example.bottomnavactivity.databinding.ActivityUsersBinding;
import com.example.bottomnavactivity.utils.UsuarioAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UsersActivity extends AppCompatActivity implements UsuarioAdapter.OnItemClicked {

    ActivityUsersBinding binding;

    UsuarioAdapter adaptador;

    ArrayList<UsuarioLoginDTO> listaUsuarios = new ArrayList<>();

    UsuarioLoginDTO usuario = new UsuarioLoginDTO("-1", "", "", "");

    Boolean isEditando = false;

    ServiceClient client = new ServiceClient();
    Retrofit retrofit = client.BuildRetrofitClient();
    UsuarioService service = retrofit.create(UsuarioService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        EdgeToEdge.enable(this);
        //        setContentView(R.layout.activity_users);

        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rvUsuarios.setLayoutManager(new LinearLayoutManager(this));
        setupRecyclerView();

        obtenerUsuarios();


        // Intents
        FloatingActionButton AddUser = findViewById(R.id.btnAddUser);
        MaterialButton UserBackProfile = findViewById(R.id.btnUserBackProfile);

        AddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersActivity.this, AddUserActivity.class);
                startActivity(intent);
            }
        });

        UserBackProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        EditText searchInput = findViewById(R.id.searchInput);

        // Ocultar la X al iniciar la app
        searchInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        // Listener para detectar cambios en el texto
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    // Mostrar la X
                    searchInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
                } else {
                    // Quitar la X
                    searchInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Detectar clic en el drawable (la X)
        searchInput.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2; // índice del drawableEnd
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable[] drawables = searchInput.getCompoundDrawables();
                if (drawables[DRAWABLE_RIGHT] != null) {
                    int drawableStart = searchInput.getRight() - searchInput.getPaddingRight()
                            - drawables[DRAWABLE_RIGHT].getBounds().width();
                    if (event.getRawX() >= drawableStart) {
                        searchInput.setText(""); // Borrar texto
                        return true;
                    }
                }
            }
            return false;
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

//    binding.btnAddUpdate.setOnClickListener(view -> {
//        Boolean isValido = validarCampos();
//
//        if(isValido) {
//            if (!isEditando) {
//                agregarUsuario();
//            } else {
//                actualizarUsuario();
//            }
//        } else {
//            Toast.makeText(this, "Se deben llenar los campos", Toast.LENGTH_SHORT).show();
//        }
//    });

//    public void agregarUsuario() {
//        this.usuario.setUsuario(binding.tilNombre.getText().toString());
//        this.usuario.setCorreo(binding.tilCorreo.getText().toString());
//        this.usuario.setClave(binding.tilClave.getText().toString());
//
//        Call<String> call = service.agregarUsuario(this.usuario);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                Toast.makeText(getApplicationContext(), response.body().toString(), Toast.LENGTH_SHORT).show();
//                obtenerUsuarios();
//                limpiarCampos();
//                limpiarObjeto();
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Error ADD", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    public void actualizarUsuario() {
//        this.usuario.setUsuario(binding.tilNombre.getText().toString());
//        this.usuario.setCorreo(binding.tilCorreo.getText().toString());
//        this.usuario.setClave(binding.tilClave.getText().toString());
//
//        Call<String> call = service.actualizarUsuario(this.usuario.getUsuarioId(), this.usuario);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                Toast.makeText(getApplicationContext(), response.body().toString(), Toast.LENGTH_SHORT).show();
//                obtenerUsuarios();
//                limpiarCampos();
//                limpiarObjeto();
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Error ADD", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    public void limpiarCampos() {
//        binding.tilNombre.setText("");
//        binding.tilCorreo.setText("");
//        binding.tilClave.setText("");
//    }

//    public void limpiarObjeto() {
//        this.usuario.setUsuarioId("-1");
//        this.usuario.setUsuario("");
//        this.usuario.setCorreo("");
//        this.usuario.setClave("");
//    }

//    public Boolean validarCampos() {
//        return !(binding.tilNombre.getText().toString().equals("") || binding.tilCorreo.getText().toSring().equals(""));
//    }

    public void obtenerUsuarios() {
        Call<UsuarioResponse> call = service.obtenerUsuarios();
        call.enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaUsuarios = response.body().getListaUsuarios();
                    setupRecyclerView();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: respuesta vacía o no exitosa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error al consultar todos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setupRecyclerView() {
        adaptador = new UsuarioAdapter(this, listaUsuarios);
        adaptador.setOnClick(this);
        binding.rvUsuarios.setAdapter(adaptador);
    }

    @Override
    public void editarUsuario(UsuarioLoginDTO usuario) {

    }

    @Override
    public void eliminarUsuario(String usuarioId) {
//        Call<String> call = service.borrarUsuario(usuarioId);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                Toast.makeText(getApplicationContext(), response.body().toString(), Toast.LENGTH_SHORT).show();
//                obtenerUsuarios();
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Error al eliminar el usuario", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}