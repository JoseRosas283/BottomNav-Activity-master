package com.example.bottomnavactivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.bottomnavactivity.DTO.VentaAgrupadaAdapter;
import com.example.bottomnavactivity.DTO.VentaAgrupadaDTO;
import com.example.bottomnavactivity.DTO.VentaAgrupadaResponseDTO;
import com.example.bottomnavactivity.Services.ServiceClient;
import com.example.bottomnavactivity.Services.VentasAgrupadasServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RelojActivity extends AppCompatActivity {

    private RecyclerView recyclerVentas;
    private VentaAgrupadaAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reloj);

        // Inicializar RecyclerView
        initRecyclerView();

        // Obtener datos agrupados
        obtenerVentasAgrupadas();

        // Configurar bottom navigation
        setupBottomNavigation();
    }

    private void initRecyclerView() {
        recyclerVentas = findViewById(R.id.recyclerVentas);
        recyclerVentas.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_reloj);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else if (itemId == R.id.bottom_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            } else if (itemId == R.id.bottom_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            } else if (itemId == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            } else if (itemId == R.id.bottom_reloj) {
                return true;
            }

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
            return true;
        });
    }

    private void obtenerVentasAgrupadas() {
        Retrofit retrofit = new ServiceClient().BuildRetrofitClient();
        VentasAgrupadasServices service = retrofit.create(VentasAgrupadasServices.class);

        // ✅ Llamar al nuevo endpoint de ventas agrupadas
        service.obtenerVentasAgrupadas().enqueue(new Callback<VentaAgrupadaResponseDTO>() {
            @Override
            public void onResponse(Call<VentaAgrupadaResponseDTO> call, Response<VentaAgrupadaResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VentaAgrupadaResponseDTO ventasResponse = response.body();

                    // ✅ Extraer la lista de ventas agrupadas
                    List<VentaAgrupadaDTO> ventasAgrupadas = ventasResponse.getDetalles();

                    if (ventasAgrupadas != null && !ventasAgrupadas.isEmpty()) {
                        adapter = new VentaAgrupadaAdapter(ventasAgrupadas);
                        recyclerVentas.setAdapter(adapter);

                        // Mostrar mensaje de éxito
                        Log.d("RelojActivity", "Mensaje API: " + ventasResponse.getMensaje());
                        Toast.makeText(RelojActivity.this,
                                "Ventas agrupadas cargadas: " + ventasAgrupadas.size(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RelojActivity.this, "No hay ventas disponibles", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RelojActivity.this, "No se pudieron obtener las ventas", Toast.LENGTH_SHORT).show();
                    Log.e("RelojActivity", "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<VentaAgrupadaResponseDTO> call, Throwable t) {
                Toast.makeText(RelojActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("RelojActivity", "Error: ", t);
            }
        });
    }
}