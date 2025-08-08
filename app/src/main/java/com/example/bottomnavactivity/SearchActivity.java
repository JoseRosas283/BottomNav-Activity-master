package com.example.bottomnavactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.bottomnavactivity.DTO.ProductoDTO;
import com.example.bottomnavactivity.DTO.ProductoResponse;
import com.example.bottomnavactivity.Services.ProductoServices;
import com.example.bottomnavactivity.Services.ServiceClient;
import com.example.bottomnavactivity.databinding.ActivitySearchBinding;
import com.example.bottomnavactivity.utils.ProductoAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity implements ProductoAdapter.OnItemClicked {

    ActivitySearchBinding binding;

    ProductoAdapter adaptador;
    ArrayList<ProductoDTO> listaProductos = new ArrayList<>();
    ArrayList<ProductoDTO> listaFiltrada = new ArrayList<>();

    ServiceClient client = new ServiceClient();
    Retrofit retrofit = client.BuildRetrofitClient();
    ProductoServices services = retrofit.create(ProductoServices.class);

    FloatingActionButton btnAgregar;
    private ProductoServices productoServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rvProductos.setLayoutManager(new LinearLayoutManager(this));
        setupRecyclerView();
        obtenerProductos();

        // Botón Agregar Producto
        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, AddProductActivity.class);
            startActivity(intent);
        });

        // Barra de búsqueda con ícono para borrar texto
        EditText searchInput = findViewById(R.id.searchInput);
        searchInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
                } else {
                    searchInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
                filtrarProductos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        searchInput.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable[] drawables = searchInput.getCompoundDrawables();
                if (drawables[DRAWABLE_RIGHT] != null) {
                    int drawableStart = searchInput.getRight() - searchInput.getPaddingRight()
                            - drawables[DRAWABLE_RIGHT].getBounds().width();
                    if (event.getRawX() >= drawableStart) {
                        searchInput.setText("");
                        return true;
                    }
                }
            }
            return false;
        });

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_search);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_search) {
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
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;

            }

            return false;
        });
    }

    public void obtenerProductos() {
        Call<ProductoResponse> call = services.ObtenerProductos();
        call.enqueue(new Callback<ProductoResponse>() {
            @Override
            public void onResponse(Call<ProductoResponse> call, Response<ProductoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProductos = response.body().getListaProductos();
                    listaFiltrada.clear();
                    listaFiltrada.addAll(listaProductos);
                    adaptador.notifyDataSetChanged();
                    binding.tvNoResultados.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getApplicationContext(), "Error: respuesta vacía o no exitosa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductoResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error al consultar productos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setupRecyclerView() {
        adaptador = new ProductoAdapter(this, listaFiltrada);
        adaptador.setOnClick(this);
        binding.rvProductos.setAdapter(adaptador);
    }

    private void filtrarProductos(String texto) {
        listaFiltrada.clear();
        if (texto.isEmpty()) {
            listaFiltrada.addAll(listaProductos);
        } else {
            for (ProductoDTO producto : listaProductos) {
                if (producto.getNombreProducto().toLowerCase().contains(texto.toLowerCase()) ||
                        producto.getCodigoProducto().toLowerCase().contains(texto.toLowerCase())) {
                    listaFiltrada.add(producto);
                }
            }
        }

        if (listaFiltrada.isEmpty()) {
            binding.tvNoResultados.setVisibility(android.view.View.VISIBLE);
        } else {
            binding.tvNoResultados.setVisibility(android.view.View.GONE);
        }

        adaptador.notifyDataSetChanged();
    }

    @Override
    public void editarProducto(ProductoDTO producto) {
        Intent intent = new Intent(SearchActivity.this, EditProductActivity.class);
        intent.putExtra("productoId", producto.getProductoId());
        startActivity(intent);
    }

    @Override
    public void eliminarProducto(String productoId) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Seguro que deseas eliminar este producto?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    eliminarProductoApi(productoId);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void eliminarProductoApi(String productoId) {
        productoServices = new ServiceClient()
                .BuildRetrofitClient()
                .create(ProductoServices.class);

        Call<Void> call = productoServices.eliminarProducto(productoId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SearchActivity.this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                    adaptador.eliminarProductoDeLista(productoId);
                } else {
                    Toast.makeText(SearchActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Fallo la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerProductos();
    }
}
