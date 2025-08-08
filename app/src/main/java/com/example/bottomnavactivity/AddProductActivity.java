package com.example.bottomnavactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bottomnavactivity.DTO.ProductoDTO;
import com.example.bottomnavactivity.Services.ProductoServices;
import com.example.bottomnavactivity.Services.ServiceClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {

    private MaterialButton btnGuardarProducto;
    private TextInputEditText inputNombre, inputCodigo, inputCantidad, inputPrecio;
    private ActivityResultLauncher<Intent> scanLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);

        btnGuardarProducto = findViewById(R.id.btnGuardarProducto);
        inputNombre = findViewById(R.id.inputNombre);
        inputCodigo = findViewById(R.id.inputCodigo);
        inputCantidad = findViewById(R.id.inputCantidad);
        inputPrecio = findViewById(R.id.inputPrecio);

        String scannedCode = getIntent().getStringExtra("SCANNED_CODE");
        if (scannedCode != null) {
            inputCodigo.setText(scannedCode);
        }

        scanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String codigo = result.getData().getStringExtra("SCANNED_CODE");
                        if (codigo != null) {
                            inputCodigo.setText(codigo);
                        }
                    }
                }
        );

        Button btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(v -> {
            String codigoActual = inputCodigo.getText().toString().trim();
            if (!codigoActual.isEmpty()) {
                Toast.makeText(AddProductActivity.this, "Ya hay un código ingresado", Toast.LENGTH_SHORT).show();
            } else {
                Intent scanIntent = new Intent(AddProductActivity.this, ScannerProActivity.class);
                scanLauncher.launch(scanIntent);
            }
        });

        btnGuardarProducto.setOnClickListener(v -> {
            String nombreProducto = inputNombre.getText().toString().trim();
            String codigoProducto = inputCodigo.getText().toString().trim();
            String cantidadStr = inputCantidad.getText().toString().trim();
            String precioStr = inputPrecio.getText().toString().trim();

            boolean isValid = true;

            if (nombreProducto.isEmpty()) {
                inputNombre.setError("El nombre es obligatorio");
                isValid = false;
            } else if (!Pattern.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{3,}$", nombreProducto)) {
                inputNombre.setError("Solo letras, mínimo 3 caracteres");
                isValid = false;
            } else {
                inputNombre.setError(null);
            }

            if (codigoProducto.isEmpty()) {
                inputCodigo.setError("El código es obligatorio");
                isValid = false;
            } else if (!Pattern.matches("^[a-zA-Z0-9-]{4,}$", codigoProducto)) {
                inputCodigo.setError("Código inválido (mínimo 4, sin espacios)");
                isValid = false;
            } else {
                inputCodigo.setError(null);
            }

            int cantidad = 0;
            if (cantidadStr.isEmpty()) {
                inputCantidad.setError("La cantidad es obligatoria");
                isValid = false;
            } else {
                try {
                    cantidad = Integer.parseInt(cantidadStr);
                    if (cantidad <= 0) {
                        inputCantidad.setError("Debe ser mayor a 0");
                        isValid = false;
                    } else {
                        inputCantidad.setError(null);
                    }
                } catch (NumberFormatException e) {
                    inputCantidad.setError("Debe ser un número válido");
                    isValid = false;
                }
            }

            double precio = 0.0;
            if (precioStr.isEmpty()) {
                inputPrecio.setError("El precio es obligatorio");
                isValid = false;
            } else {
                try {
                    precio = Double.parseDouble(precioStr);
                    if (precio <= 0) {
                        inputPrecio.setError("Debe ser mayor a 0");
                        isValid = false;
                    } else if (!Pattern.matches("^\\d+(\\.\\d{1,2})?$", precioStr)) {
                        inputPrecio.setError("Máximo 2 decimales");
                        isValid = false;
                    } else {
                        inputPrecio.setError(null);
                    }
                } catch (NumberFormatException e) {
                    inputPrecio.setError("Debe ser un número válido");
                    isValid = false;
                }
            }

            if (!isValid) {
                Toast.makeText(AddProductActivity.this, "Corrige los errores antes de continuar", Toast.LENGTH_SHORT).show();
                return;
            }

            ProductoDTO nuevoProducto = new ProductoDTO(nombreProducto, codigoProducto, cantidad, precio);

            ServiceClient serviceClient = new ServiceClient();
            ProductoServices productoService = serviceClient.BuildRetrofitClient().create(ProductoServices.class);

            Call<Void> call = productoService.agregarProducto(nuevoProducto);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddProductActivity.this, "Producto agregado con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddProductActivity.this, "Error al agregar producto", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(AddProductActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        MaterialButton BackUsers = findViewById(R.id.btnBackProduct);
        BackUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AddProductActivity.this, SearchActivity.class);
            startActivity(intent);
        });

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        String scannedCode = intent.getStringExtra("SCANNED_CODE");
        if (scannedCode != null && inputCodigo != null) {
            inputCodigo.setText(scannedCode);
        }
    }
}