package com.example.bottomnavactivity;

import android.util.Log;

import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

public class BarcodeAnalyzer implements ImageAnalysis.Analyzer {
    private final BarcodeScanner scanner = BarcodeScanning.getClient();
    private final BarcodeListener listener;

    public interface BarcodeListener {
        void onBarcodeDetected(String value);
    }

    public BarcodeAnalyzer(BarcodeListener listener) {
        this.listener = listener;
    }

    @ExperimentalGetImage
    @Override
    public void analyze(ImageProxy imageProxy) {
        if (imageProxy == null || imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        int format = barcode.getFormat();
                        String rawValue = barcode.getRawValue();

                        // ✅ Solo aceptar códigos de productos
                        if (rawValue != null && (
                                format == Barcode.FORMAT_EAN_13 ||
                                        format == Barcode.FORMAT_UPC_A ||
                                        format == Barcode.FORMAT_CODE_128)) {

                            listener.onBarcodeDetected(rawValue);
                            Log.d("BarcodeAnalyzer", "Producto detectado: " + rawValue);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("BarcodeAnalyzer", "Error al escanear", e))
                .addOnCompleteListener(task -> imageProxy.close());
    }
}
