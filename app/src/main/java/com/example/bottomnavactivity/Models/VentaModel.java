package com.example.bottomnavactivity.Models;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class VentaModel {
    private String ventaId;
    private String fechaVenta;
    private BigDecimal total;
    private String estado;

    // Constante para formato de fecha consistente
    private static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public VentaModel() {
        this.fechaVenta = OffsetDateTime.now().format(FECHA_FORMATTER);
        this.total = BigDecimal.ZERO;
    }
    public void setVentaId(String ventaId) { this.ventaId = ventaId; }

    public String getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(String fechaVenta) { this.fechaVenta = fechaVenta; }

    public BigDecimal getTotal() { return total != null ? total : BigDecimal.ZERO; }
    public void setTotal(BigDecimal total) { this.total = total; }


    @Override
    public String toString() {
        return "VentaModel{" +
                "ventaId='" + ventaId + '\'' +
                ", fechaVenta='" + fechaVenta + '\'' +
                ", total=" + total +
                ", estado='" + estado + '\'' +
                '}';
    }
}
