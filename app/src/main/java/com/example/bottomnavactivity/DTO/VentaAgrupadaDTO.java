package com.example.bottomnavactivity.DTO;

import java.util.List;

public class VentaAgrupadaDTO {
    private String ventaId;
    private String fechaVenta;
    private double total;
    private List<VistaDetalleVentaDTO> productos;

    // Getters y Setters
    public String getVentaId() {
        return ventaId;
    }

    public void setVentaId(String ventaId) {
        this.ventaId = ventaId;
    }

    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<VistaDetalleVentaDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<VistaDetalleVentaDTO> productos) {
        this.productos = productos;
    }
}
