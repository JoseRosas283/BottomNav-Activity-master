package com.example.bottomnavactivity.DTO;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class VentaDTO {
    @SerializedName("mensaje")
    private String mensaje;

    // Para respuesta de UNA sola venta
    @SerializedName("venta")
    private Venta venta;

    // Para respuesta de MÚLTIPLES ventas
    @SerializedName("ventas")
    private List<Venta> ventas;

    // Constructores
    public VentaDTO() {}


    // METODO PARA OBTENER PRIMERA VENTA
    public Venta getPrimeraVenta() {
        if (venta != null) return venta;
        if (ventas != null && !ventas.isEmpty()) return ventas.get(0);
        return null;
    }


    // CLASE DE VENTA INDIVIDUAL
    public static class Venta {
        @SerializedName("ventaId")
        private String ventaId;

        @SerializedName("fechaVenta")
        private String fechaVenta;

        @SerializedName("total")
        private BigDecimal total;

        @SerializedName("estado")
        private String estado;

        // Constructores
        public Venta() {}

        public Venta(String ventaId, String fechaVenta, BigDecimal total) {
            this.ventaId = ventaId;
            this.fechaVenta = fechaVenta;
            this.total = total != null ? total : BigDecimal.ZERO;
        }

        // Getters y setters
        public String getVentaId() { return ventaId; }
        public void setVentaId(String ventaId) { this.ventaId = ventaId; }

        public String getFechaVenta() { return fechaVenta; }
        public void setFechaVenta(String fechaVenta) { this.fechaVenta = fechaVenta; }

        public BigDecimal getTotal() { return total != null ? total : BigDecimal.ZERO; }
        public void setTotal(BigDecimal total) { this.total = total; }

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }

        // METODO PARA DEBUGING
        @Override
        public String toString() {
            return "Venta{" +
                    "ventaId='" + ventaId + '\'' +
                    ", fechaVenta='" + fechaVenta + '\'' +
                    ", total=" + total +
                    ", estado='" + estado + '\'' +
                    '}';
        }
    }
}
