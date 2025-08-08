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

import com.example.bottomnavactivity.DTO.ProductoDTO;
import com.example.bottomnavactivity.DTO.ProductoGetResponse;
import com.example.bottomnavactivity.Services.ProductoServices;
import com.example.bottomnavactivity.Services.ServiceClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProductActivity extends AppCompatActivity {

    private static final String TAG = "EditProduct";

    private TextInputEditText etNombre, etCodigo, etCantidad, etPrecio;
    private MaterialButton btnGuardar, btnBack;
    private BottomNavigationView bottomNavigationView;
    private String productoId;
    private ProductoServices productoServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_product);

        initViews();

        productoServices = new ServiceClient()
                .BuildRetrofitClient()
                .create(ProductoServices.class);

        productoId = getIntent().getStringExtra("productoId");
        Log.d(TAG, "ID recibido: " + productoId);

        if (productoId != null && !productoId.isEmpty()) {
            cargarProducto();
        } else {
            mostrarErrorYSalir("ID de producto no válido");
        }

        setupListeners();
        setupBottomNavigation();

        MaterialButton BackUsers = findViewById(R.id.btnBackProduct);
        BackUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProductActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        etNombre = findViewById(R.id.etNombre);
        etCodigo = findViewById(R.id.etCodigo);
        etCantidad = findViewById(R.id.etCantidad);
        etPrecio = findViewById(R.id.etPrecio);
        btnGuardar = findViewById(R.id.btnGuardarProducto);
        btnBack = findViewById(R.id.btnBackProduct);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnGuardar != null) {
            btnGuardar.setOnClickListener(v -> validarYGuardarProducto());
        }
    }

    private void setupBottomNavigation() {
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

    private void cargarProducto() {
        productoServices.ObtenerProductos(productoId).enqueue(new Callback<ProductoGetResponse>() {
            @Override
            public void onResponse(Call<ProductoGetResponse> call, Response<ProductoGetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductoDTO producto = response.body().getProducto();
                    if (producto != null) {
                        mostrarDatosProducto(producto);
                    } else {
                        Toast.makeText(EditProductActivity.this, "No se encontró el producto", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProductActivity.this, "Error al cargar producto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductoGetResponse> call, Throwable t) {
                Toast.makeText(EditProductActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDatosProducto(ProductoDTO producto) {
        etNombre.setText(producto.getNombreProducto());
        etCodigo.setText(producto.getCodigoProducto());
        etCantidad.setText(String.valueOf(producto.getCantidad()));
        etPrecio.setText(String.valueOf(producto.getPrecio()));
    }

    private void validarYGuardarProducto() {
        String nombre = etNombre.getText().toString().trim();
        String codigo = etCodigo.getText().toString().trim();
        String strCantidad = etCantidad.getText().toString().trim();
        String strPrecio = etPrecio.getText().toString().trim();

        boolean isValid = true;

        // ✅ Validación nombre
        if (nombre.isEmpty()) {
            etNombre.setError("El nombre es obligatorio");
            isValid = false;
        } else if (!Pattern.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{3,}$", nombre)) {
            etNombre.setError("Solo letras, mínimo 3 caracteres");
            isValid = false;
        } else {
            etNombre.setError(null);
        }

        // ✅ Validación código
        if (codigo.isEmpty()) {
            etCodigo.setError("El código es obligatorio");
            isValid = false;
        } else if (!Pattern.matches("^[a-zA-Z0-9-]{4,}$", codigo)) {
            etCodigo.setError("Código inválido (mínimo 4, sin espacios)");
            isValid = false;
        } else {
            etCodigo.setError(null);
        }

        int cantidad = 0;
        if (strCantidad.isEmpty()) {
            etCantidad.setError("La cantidad es obligatoria");
            isValid = false;
        } else {
            try {
                cantidad = Integer.parseInt(strCantidad);
                if (cantidad <= 0) {
                    etCantidad.setError("Debe ser mayor a 0");
                    isValid = false;
                } else {
                    etCantidad.setError(null);
                }
            } catch (NumberFormatException e) {
                etCantidad.setError("Debe ser un número válido");
                isValid = false;
            }
        }

        double precio = 0.0;
        if (strPrecio.isEmpty()) {
            etPrecio.setError("El precio es obligatorio");
            isValid = false;
        } else {
            try {
                precio = Double.parseDouble(strPrecio);
                if (precio <= 0) {
                    etPrecio.setError("Debe ser mayor a 0");
                    isValid = false;
                } else if (!Pattern.matches("^\\d+(\\.\\d{1,2})?$", strPrecio)) {
                    etPrecio.setError("Máximo 2 decimales");
                    isValid = false;
                } else {
                    etPrecio.setError(null);
                }
            } catch (NumberFormatException e) {
                etPrecio.setError("Debe ser un número válido");
                isValid = false;
            }
        }

        if (!isValid) {
            Toast.makeText(this, "Corrige los errores antes de continuar", Toast.LENGTH_SHORT).show();
            return;
        }

        ProductoDTO productoActualizado = new ProductoDTO(nombre, codigo, cantidad, precio);
        guardarProducto(productoActualizado);
    }

    private void guardarProducto(ProductoDTO producto) {
        productoServices.editarProducto(productoId, producto).enqueue(new Callback<ProductoDTO.ProductoUpdateResponse>() {
            @Override
            public void onResponse(Call<ProductoDTO.ProductoUpdateResponse> call, Response<ProductoDTO.ProductoUpdateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditProductActivity.this,
                            response.body().getMensaje(), Toast.LENGTH_SHORT).show();
                    volverAListaProductos();
                } else {
                    Toast.makeText(EditProductActivity.this,
                            "Error al actualizar producto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductoDTO.ProductoUpdateResponse> call, Throwable t) {
                Toast.makeText(EditProductActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void volverAListaProductos() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void mostrarErrorYSalir(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        finish();
    }
}
