package com.example.bottomnavactivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;

public class ScannerProActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 101;

    private PreviewView previewView;
    private ScannerOverlayView overlayView; //  NUEVO: referencia al overlay
    private ProcessCameraProvider cameraProvider;
    private CameraSelector cameraSelector;
    private Preview preview;
    private ImageAnalysis imageAnalysis;
    private boolean escaneoActivo = false;
    private boolean detectedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scanner_pro);
        previewView = findViewById(R.id.previewView);
        overlayView = findViewById(R.id.scannerOverlay); // NUEVO: inicialización

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.bottom_settings);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id != R.id.bottom_settings) {
                navegarDesde(id);
                finish();
            }
            return true;
        });

        Drawable grayBg = ContextCompat.getDrawable(this, R.drawable.rounded_selector);
        Drawable redBg = ContextCompat.getDrawable(this, R.drawable.circle_red);

        View settingsItemView = bottomNav.findViewById(R.id.bottom_settings);
        if (settingsItemView != null) {
            settingsItemView.setBackground(grayBg);
            settingsItemView.setOnTouchListener((v, event) -> {
                if (cameraProvider == null) return false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        detectedOnce = false;
                        v.setBackground(redBg);
                        overlayView.startLaser();     //  NUEVO: inicia láser
                        activarEscaneo();
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.setBackground(grayBg);
                        overlayView.stopLaser();      //  NUEVO: detiene láser
                        detenerEscaneo();
                        return true;
                }
                return false;
            });
        }

        if (tieneCamara()) {
            solicitarPermisoCamara();
        } else {
            Toast.makeText(this,
                    "Este dispositivo no tiene cámara",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean tieneCamara() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void solicitarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.CAMERA)) {
                mostrarDialogoExplicativo();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION
                );
            }
        } else {
            iniciarCameraX();
        }
    }

    private void mostrarDialogoExplicativo() {
        new AlertDialog.Builder(this)
                .setTitle("Permiso de cámara requerido")
                .setMessage("Debe otorgar acceso a la cámara para escanear códigos.")
                .setPositiveButton("Continuar", (d, w) ->
                        ActivityCompat.requestPermissions(
                                this,
                                new String[]{Manifest.permission.CAMERA},
                                REQUEST_CAMERA_PERMISSION
                        ))
                .setNegativeButton("Cancelar", (d, w) ->
                        Toast.makeText(this,
                                "Permiso cancelado",
                                Toast.LENGTH_SHORT).show()
                )
                .show();
    }

    private void mostrarDialogoIrAConfiguracion() {
        new AlertDialog.Builder(this)
                .setTitle("Permiso denegado permanentemente")
                .setMessage("Habilita el permiso en Configuración.")
                .setPositiveButton("Ir a configuración", (d, w) -> {
                    Intent intent = new Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    );
                    Uri uri = Uri.fromParts(
                            "package", getPackageName(), null
                    );
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", (d, w) ->
                        Toast.makeText(this,
                                "No se puede escanear sin permiso",
                                Toast.LENGTH_SHORT).show()
                )
                .show();
    }

    private void iniciarCameraX() {
        ListenableFuture<ProcessCameraProvider> camProviderFuture =
                ProcessCameraProvider.getInstance(this);

        camProviderFuture.addListener(() -> {
            try {
                cameraProvider = camProviderFuture.get();

                preview = new Preview.Builder().build();
                cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(
                                ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                        ).build();

                imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(this),
                        new BarcodeAnalyzer(value -> runOnUiThread(() -> {
                            if (escaneoActivo && !detectedOnce) {
                                detectedOnce = true;

                                cameraProvider.unbindAll();

                                Intent intent = new Intent(
                                        ScannerProActivity.this,
                                        AddProductActivity.class
                                );
                                intent.putExtra("SCANNED_CODE", value);

                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                startActivity(intent);
                                finish();
                            }
                        }))
                );

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview
                );
            } catch (Exception e) {
                Toast.makeText(this,
                        "Error al iniciar la cámara",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void activarEscaneo() {
        escaneoActivo = true;
        cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalysis
        );
    }

    private void detenerEscaneo() {
        escaneoActivo = false;
        cameraProvider.unbind(imageAnalysis);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarCameraX();
            } else {
                boolean showRationale = ActivityCompat
                        .shouldShowRequestPermissionRationale(
                                this, Manifest.permission.CAMERA
                        );
                if (!showRationale) {
                    mostrarDialogoIrAConfiguracion();
                } else {
                    Toast.makeText(this,
                            "Permiso de cámara denegado",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void navegarDesde(int itemId) {
        Intent intent;
        if (itemId == R.id.bottom_home) {
            intent = new Intent(this, MainActivity.class);
        } else if (itemId == R.id.bottom_search) {
            intent = new Intent(this, SearchActivity.class);
        } else if (itemId == R.id.bottom_reloj) {
            intent = new Intent(this, RelojActivity.class);
        } else {
            intent = new Intent(this, ProfileActivity.class);
        }
        startActivity(intent);
        overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
        );
    }

}