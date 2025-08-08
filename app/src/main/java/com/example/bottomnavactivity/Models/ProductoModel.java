package com.example.bottomnavactivity.Models;

import java.math.BigDecimal;

public class ProductoModel {
    private String productoId;
    private String nombreProducto;
    private String codigoProducto;
    private int cantidad;
    private BigDecimal precio;

    public ProductoModel() {}

    public ProductoModel(String productoId, String nombreProducto, String codigoProducto, int cantidad, BigDecimal precio) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.codigoProducto = codigoProducto;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public String getProductoId() { return productoId; }
    public void setProductoId(String productoId) { this.productoId = productoId; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public BigDecimal getTotalPrecio() {
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }
}
