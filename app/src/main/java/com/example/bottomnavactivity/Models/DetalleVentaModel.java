package com.example.bottomnavactivity.Models;

public class DetalleVentaModel {
    private String productId;
    private String ventaId;
    private int cantidad;
    private String estado;

    public DetalleVentaModel() {
        this.cantidad = 1;
        this.estado = "Completada";
    }

    public DetalleVentaModel(String productId, String ventaId, int cantidad) {
        this.productId = productId;
        this.ventaId = ventaId;
        this.cantidad = cantidad;
        this.estado = "Completada";
    }

    // Getters y Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getVentaId() { return ventaId; }
    public void setVentaId(String ventaId) { this.ventaId = ventaId; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
