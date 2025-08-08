package com.example.bottomnavactivity.DTO;

import com.google.gson.annotations.SerializedName;

public class ProductoGetResponse {

    // 🔧 CORRECCIÓN: Agregar "mensaje" en minúsculas como principal
    @SerializedName(value = "mensaje", alternate = {"Mensaje", "MENSAJE"})
    private String mensaje;

    // 🔧 CORRECCIÓN: Agregar "producto" en minúsculas como principal
    @SerializedName(value = "producto", alternate = {"Producto", "PRODUCTO"})
    private ProductoDTO producto;

    public String getMensaje() {
        return mensaje;
    }

    public ProductoDTO getProducto() {
        return producto;
    }
}
