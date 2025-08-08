package com.example.bottomnavactivity.Mappers;

import com.example.bottomnavactivity.DTO.DetalleVentaDTO;
import com.example.bottomnavactivity.Models.DetalleVentaModel;

public class DetalleVentaMapper {
    /**
     * Convierte un DetalleVentaModel a DetalleVentaDTO.DetalleVenta para requests
     */
    public static DetalleVentaDTO.DetalleVenta toRequest(DetalleVentaModel model) {
        if (model == null) return null;

        return new DetalleVentaDTO.DetalleVenta(
                model.getProductId(),
                model.getVentaId(),
                model.getCantidad(),
                model.getEstado()
        );
    }
}
