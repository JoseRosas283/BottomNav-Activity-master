package com.example.bottomnavactivity.DTO;

import java.util.List;

public class VentaAgrupadaResponseDTO {
    private String mensaje;
    private List<VentaAgrupadaDTO> detalles;

    // Getters y Setters
    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<VentaAgrupadaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<VentaAgrupadaDTO> detalles) {
        this.detalles = detalles;
    }
}
