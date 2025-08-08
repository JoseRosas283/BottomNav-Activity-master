package com.example.bottomnavactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottomnavactivity.DTO.DetalleVentaDTO;
import com.example.bottomnavactivity.DTO.ProductoVentaDTO;
import com.example.bottomnavactivity.DTO.VentaDTO;
import com.example.bottomnavactivity.Mappers.DetalleVentaMapper;
import com.example.bottomnavactivity.Mappers.ProductoMapper;
import com.example.bottomnavactivity.Mappers.VentaMapper;
import com.example.bottomnavactivity.Models.DetalleVentaModel;
import com.example.bottomnavactivity.Models.ProductoModel;
import com.example.bottomnavactivity.Models.VentaModel;
import com.example.bottomnavactivity.Services.DetalleVentaService;
import com.example.bottomnavactivity.Services.ProductoVentaService;
import com.example.bottomnavactivity.Services.ServiceClient;
import com.example.bottomnavactivity.Services.VentaService;
import com.example.bottomnavactivity.utils.ProductoVentaAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private double totalVenta = 0.0;

    private RecyclerView recyclerView;
    private ProductoVentaAdapter productoVentaAdapter;
    private List<ProductoModel> listaProductos = new ArrayList<>();

    // Referencias para el estado vacío y resumen
    private View emptyStateLayout;
    private TextView tvSubtotal, tvTaxes, tvDiscount, tvTotal;

    // Servicios
    ProductoVentaService productoVentaService;
    VentaService ventaService;
    DetalleVentaService detalleVentaService;

    // Variable para evitar múltiples llamadas simultáneas
    private boolean processingBarcode = false;
    private boolean updatingResumen = false;
    private boolean processingVenta = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_settings);

        Button scanButton = findViewById(R.id.btnEscanearVenta);
        scanButton.setOnClickListener(v -> openScanner());

        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productoVentaAdapter = new ProductoVentaAdapter(listaProductos);
        recyclerView.setAdapter(productoVentaAdapter);

        // Configurar callback simple con protección contra múltiples llamadas
        productoVentaAdapter.setOnCambioCallback(() -> {
            if (!updatingResumen) {
                updatingResumen = true;
                // Usar Handler para ejecutar después de que todas las operaciones del adapter terminen
                new Handler().post(() -> {
                    actualizarResumen();
                    updatingResumen = false;
                });
            }
        });

        ServiceClient client = new ServiceClient();
        productoVentaService = client.BuildRetrofitClient().create(ProductoVentaService.class);
        ventaService = client.BuildRetrofitClient().create(VentaService.class);
        detalleVentaService = client.BuildRetrofitClient().create(DetalleVentaService.class);

        // REFERENCIAS DEL LAYOUT
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTaxes = findViewById(R.id.tvTaxes);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvTotal = findViewById(R.id.tvTotal);

        setupBottomNavigation(bottomNavigationView);
        setupSearchButton();
        setupFinalizarVentaButton();
    }

    private void setupBottomNavigation(BottomNavigationView bottomNavigationView) {
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

    private void setupSearchButton() {
        EditText editTextBuscar = findViewById(R.id.etSearch);
        ImageView iconoCerrar = findViewById(R.id.iconoCerrar);

        iconoCerrar.setOnClickListener(v -> {
            editTextBuscar.getText().clear();
            editTextBuscar.clearFocus();
        });
    }

    private void setupFinalizarVentaButton() {
        Button btnFinalizarVenta = findViewById(R.id.btnFinalizarVenta);
        btnFinalizarVenta.setOnClickListener(v -> mostrarDialogConfirmarVenta());
    }

    private void openScanner() {
        if (processingBarcode) {
            Toast.makeText(this, "Procesando código anterior, espera un momento", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            IntentIntegrator integrator = new IntentIntegrator(this);

            // CONFIGURACION DEL SCANNER
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Apunta al código de barras del producto");
            integrator.setCameraId(0);  // Cámara trasera
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(false); // Desactivar para mejor rendimiento
            integrator.setOrientationLocked(true);
            integrator.setTimeout(30000); // 30 segundos timeout

            // CONFIGURAR EL TAMAÑO DEL MARCO DEL SCANNER
            integrator.addExtra("SCAN_WIDTH", 640);
            integrator.addExtra("SCAN_HEIGHT", 480);

            integrator.initiateScan();

        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir el escáner: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Escáner cancelado", Toast.LENGTH_SHORT).show();
                processingBarcode = false;
            } else {
                String barcode = result.getContents().trim();
                processBarcodeResult(barcode);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            processingBarcode = false;
        }
    }

    private void processBarcodeResult(String barcode) {
        if (processingBarcode) {
            return; // EVITAR QUE SE PROCESE MULTIPLE
        }

        processingBarcode = true;

        // SE VALIDA QUE EL CODIGO  NO SEA NULL O ESTE VACIO
        if (barcode == null || barcode.trim().isEmpty()) {
            Toast.makeText(this, "Código de barras inválido", Toast.LENGTH_SHORT).show();
            processingBarcode = false;
            return;
        }

        productoVentaService.getProductoPorCodigo(barcode).enqueue(new Callback<ProductoVentaDTO>() {
            @Override
            public void onResponse(Call<ProductoVentaDTO> call, Response<ProductoVentaDTO> response) {
                processingBarcode = false;

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        ProductoVentaDTO dto = response.body();

                        // CONVIERTES LA RESPUESTA A PRODUCTO MODEL
                        ProductoModel producto = ProductoMapper.fromResponse(dto);

                        producto.setCantidad(1);

                        if (producto != null) {
                            productoVentaAdapter.agregarProducto(producto);

                            // SI SE AGREGA UN PRODUCTO QUITAR EL ESTADO VACIO(LA IMAGEN DEL CARRITO)
                            if (emptyStateLayout.getVisibility() == View.VISIBLE) {
                                emptyStateLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }

                        } else {
                            Toast.makeText(SettingsActivity.this,
                                    "Error al procesar el producto",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(SettingsActivity.this,
                                "Error al agregar producto: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Producto no encontrado";
                    if (response.code() == 404) {
                        errorMsg = "Producto no encontrado en la base de datos";
                    } else if (response.code() >= 500) {
                        errorMsg = "Error del servidor, intenta más tarde";
                    }
                    Toast.makeText(SettingsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductoVentaDTO> call, Throwable t) {
                processingBarcode = false;

                String errorMsg = "Error de conexión";
                if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Tiempo de espera agotado, verifica tu conexión";
                } else if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "Sin conexión a internet";
                }

                Toast.makeText(SettingsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    //A CONTINUCION SE DEFINIRA Y SE HARA USO DE DIALOG PARA MOSTRAR UNA VENTANA EMERGENTE QUE TRABAJA CON UN LAYOUT
    private void mostrarDialogConfirmarVenta() {
        if (listaProductos.isEmpty()) {
            Toast.makeText(this, "No hay productos en la venta", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirmar_venta);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        TextView tvTotal = dialog.findViewById(R.id.tvTotalVenta);
        Button btnConfirmar = dialog.findViewById(R.id.btnConfirmarVenta);
        Button btnCancelar = dialog.findViewById(R.id.btnCancelar);

        // MOSTRAR EL TOTAL
        tvTotal.setText(String.format("$%.2f", totalVenta));

        btnConfirmar.setOnClickListener(v -> {
            dialog.dismiss();
            //  LLAMAMOS AL METODO PARA ENVIAR LA VENTA
            enviarVentaAAPI();
        });

        btnCancelar.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(SettingsActivity.this, "Venta cancelada", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    // METODO PARA ENVIAR LA VENTA ALA API
    private void enviarVentaAAPI() {
        if (processingVenta) {
            Toast.makeText(this, "Procesando venta, por favor espera...", Toast.LENGTH_SHORT).show();
            return;
        }


        processingVenta = true;
        Toast.makeText(this, "Enviando venta...", Toast.LENGTH_SHORT).show();



        // Crear la venta principal
        VentaModel ventaModel = new VentaModel();
        // ENVIAR TOTAL
        ventaModel.setTotal(BigDecimal.valueOf(totalVenta));
        // ENVIAR FECHA
        ventaModel.setFechaVenta(OffsetDateTime.now().toString());

        // CONVERTIR LOS MODELOS A DTO NUEVAMENTE PARA ENVIARLOS A ALA API
        VentaDTO.Venta ventaRequest = VentaMapper.toRequest(ventaModel);

        // DEBUG: VERIFICAR QUE SE HAYA CONVERTIDO BIEN
        if (ventaRequest == null) {
            processingVenta = false;
            Toast.makeText(this, "Error al preparar datos de venta", Toast.LENGTH_LONG).show();
            return;
        }


        // 2. UNA VEZ QUE SE CONVIERTON LOS DATOS (MODELO A DTO) PASARSELOS ALA API
        ventaService.registrarVenta(ventaRequest).enqueue(new Callback<VentaDTO>() {
            @Override
            public void onResponse(Call<VentaDTO> call, Response<VentaDTO> response) {

                if (response.isSuccessful() && response.body() != null) {

                    // OBTENER EL ID DE LA VENTA LO NECESITO PORQUE SE LO DEBEMOS DE MANDAR TAMBIEN ALA TABLA DETALLE DE VENTAS
                    VentaDTO responseBody = response.body();
                    VentaDTO.Venta ventaCreada = responseBody.getPrimeraVenta();

                    if (ventaCreada != null && ventaCreada.getVentaId() != null) {
                        String ventaIdGenerado = ventaCreada.getVentaId();

                        // 3. ENVIARLE EL ID ALOS DETALLES DE VENTA
                        enviarDetallesVenta(ventaIdGenerado);
                    } else {
                        processingVenta = false;
                        Toast.makeText(SettingsActivity.this, "Error: No se pudo obtener ID de venta", Toast.LENGTH_LONG).show();
                    }

                } else {
                    processingVenta = false;

                    // ERROR DEL SERVIDOR O AL CREAR VENTA

                    String errorMsg = "Error al crear la venta (Código: " + response.code() + ")";
                    if (response.code() == 400) {
                        errorMsg = "Datos de venta inválidos - Verifica fecha y total";
                    } else if (response.code() == 401) {
                        errorMsg = "Error de autenticación";
                    } else if (response.code() >= 500) {
                        errorMsg = "Error del servidor";
                    }

                    Toast.makeText(SettingsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<VentaDTO> call, Throwable t) {
                processingVenta = false;

                String errorMsg = "Error de conexión al crear venta";
                if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Tiempo de espera agotado";
                } else if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "Sin conexión a internet";
                } else if (t instanceof java.net.ConnectException) {
                    errorMsg = "No se pudo conectar al servidor";
                }

                Toast.makeText(SettingsActivity.this, errorMsg + ": " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // UNA VEZ QUE OBTUVMOS EL ID DE LA VENTA SE LO MANDAMOS A DETALLE DE VENTAS
    private void enviarDetallesVenta(String ventaId) {


        if (listaProductos.isEmpty()) {
            processingVenta = false;
            mostrarDialogVentaCompletada();
            return;
        }

        //AQUI MANEJAMOS COMO SE ENVIA EL DETALLE DE VENTA YA QUE SE MANEJA POR PRODUCTO
        enviarSiguienteDetalle(ventaId, 0);
    }

    // ENVÍO INDIVIDUAL DE CADA DETALLE DE VENTA
    private void enviarSiguienteDetalle(String ventaId, int index) {
        if (index >= listaProductos.size()) {
            processingVenta = false;
            mostrarDialogVentaCompletada();
            return;
        }

        ProductoModel producto = listaProductos.get(index);
        int cantidad = productoVentaAdapter.getCantidadProducto(producto.getProductoId());


        // VALIDACIONES CRÍTICAS
        if (ventaId == null || ventaId.trim().isEmpty()) {
            processingVenta = false;
            Toast.makeText(this, "Error: ID de venta inválido", Toast.LENGTH_LONG).show();
            return;
        }

        if (producto.getProductoId() == null) {
            processingVenta = false;
            Toast.makeText(this, "Error: ID de producto inválido", Toast.LENGTH_LONG).show();
            return;
        }

        if (cantidad <= 0) {
            enviarSiguienteDetalle(ventaId, index + 1);
            return;
        }

        // CREAR DETALLE DE VENTA
        DetalleVentaModel detalleModel = new DetalleVentaModel(
                producto.getProductoId(),
                ventaId,
                cantidad
        );

        DetalleVentaDTO.DetalleVenta detalleRequest = DetalleVentaMapper.toRequest(detalleModel);

        if (detalleRequest == null) {
            processingVenta = false;
            Toast.makeText(this, "Error al preparar detalle de venta", Toast.LENGTH_LONG).show();
            return;
        }


        detalleVentaService.registrarDetalleVenta(detalleRequest).enqueue(new Callback<DetalleVentaDTO>() {

            @Override
            public void onResponse(Call<DetalleVentaDTO> call, Response<DetalleVentaDTO> response) {
                if (response.isSuccessful()) {
                    enviarSiguienteDetalle(ventaId, index + 1);
                } else {
                    processingVenta = false;

                    //MANEJAR ERRORES
                    String errorMsg;
                    switch (response.code()) {
                        case 400:
                            errorMsg = "Datos inválidos en detalle de venta - Verifica productoId, ventaId, cantidad y estado";
                            break;
                        case 404:
                            errorMsg = "No se encontró la venta o el producto";
                            break;
                        case 500:
                            errorMsg = "Error interno del servidor";
                            break;
                        default:
                            errorMsg = "Error desconocido";
                    }

                    Toast.makeText(SettingsActivity.this,
                            errorMsg + " - " + producto.getNombreProducto() + " (Código: " + response.code() + ")",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DetalleVentaDTO> call, Throwable t) {
                processingVenta = false;

                Toast.makeText(SettingsActivity.this,
                        "Error de conexión al guardar detalle: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private void mostrarDialogVentaCompletada() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_venta_completada);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        TextView tvTotalProcesado = dialog.findViewById(R.id.tvTotalProcesado);
        Button btnAceptar = dialog.findViewById(R.id.btnAceptar);

        // MOSTRAR EL TOTAL DE LA VENTA
        tvTotalProcesado.setText(String.format("$%.2f", totalVenta));

        btnAceptar.setOnClickListener(v -> {
            dialog.dismiss();
            limpiarVenta();
            Toast.makeText(SettingsActivity.this, "¡Venta enviada y procesada con éxito!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    //LIMPIAR TODO
    private void limpiarVenta() {
        listaProductos.clear();
        productoVentaAdapter.notifyDataSetChanged();

        emptyStateLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        totalVenta = 0.0;
        actualizarResumen();
    }

    private void actualizarResumen() {
        BigDecimal total = productoVentaAdapter.calcularTotal();

        // MOSTRAR SUBTOTAL,IMPUESTO Y DESCUENTO EN 0
        tvSubtotal.setText("$0.00");
        tvTaxes.setText("$0.00");
        tvDiscount.setText("$0.00");

        // MOSTRAR EL TOTAL EN 0
        tvTotal.setText(String.format("$%.2f", total.doubleValue()));
        totalVenta = total.doubleValue();

        // VERIFICAR SI LA LISA ESTA VACIA PARA MOSTRAR NUEVAMENTE EL ESTADO VACIO
        if (productoVentaAdapter.estaVacia()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            // Asegurar que el RecyclerView esté visible si hay productos
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


    //onResume() para resetear estados y asegurar que tu aplicación funcione correctamente cada vez que el usuario regresa a ella.
    @Override
    protected void onResume() {
        super.onResume();
        processingBarcode = false; // Reset al volver a la actividad
        processingVenta = false;   // Reset de procesamiento de venta
    }
}
