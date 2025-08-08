package com.example.bottomnavactivity.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetalleVentaDTO {
    @SerializedName("mensaje")
    private String mensaje;

    // Para respuesta de UN solo detalle
    @SerializedName("detalleVenta")  // ← PARA UNO
    private DetalleVenta detalleVenta;

    // Para respuesta de MÚLTIPLES detalles
    @SerializedName("detalleVentas")  // ← PARA MUCHOS
    private List<DetalleVenta> detalleVentas;

    // Constructores
    public DetalleVentaDTO() {}


    // METODO PARA OBTENER EL DETALLE DE VENTA SIN IMPORTAE LA ESTRUCTURA
    public DetalleVenta getPrimerDetalle() {
        // Si viene como objeto singular, devolverlo
        if (detalleVenta != null) return detalleVenta;

        // Si viene como array, devolver el primero
        if (detalleVentas != null && !detalleVentas.isEmpty()) return detalleVentas.get(0);

        return null;
    }

    // Clase interna para el detalle individual
    public static class DetalleVenta {
        @SerializedName("productoId")
        private String productoId;

        @SerializedName("ventaId")
        private String ventaId;

        @SerializedName("cantidad")
        private int cantidad;

        @SerializedName("estado")
        private String estado;

        // Campos adicionales que podrían venir en el futuro
        @SerializedName("precioUnitario")
        private Double precioUnitario;

        @SerializedName("subtotal")
        private Double subtotal;

        // Constructores
        public DetalleVenta() {}

        public DetalleVenta(String productoId, String ventaId, int cantidad, String estado) {
            this.productoId = productoId;
            this.ventaId = ventaId;
            this.cantidad = cantidad;
            this.estado = estado;
        }

        // Getters y setters
        public String getProductoId() { return productoId; }
        public void setProductoId(String productoId) { this.productoId = productoId; }

        public String getVentaId() { return ventaId; }
        public void setVentaId(String ventaId) { this.ventaId = ventaId; }

        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }

        public Double getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

        public Double getSubtotal() { return subtotal; }
        public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    }
}
