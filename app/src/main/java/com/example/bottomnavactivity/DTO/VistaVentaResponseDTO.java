package com.example.bottomnavactivity.DTO;

import java.util.List;

public class VistaVentaResponseDTO {
    private String mensaje;
    private List<VistaDetalleVentaDTO> detalles;

    // Getters y Setters
    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<VistaDetalleVentaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<VistaDetalleVentaDTO> detalles) {
        this.detalles = detalles;
    }
}
