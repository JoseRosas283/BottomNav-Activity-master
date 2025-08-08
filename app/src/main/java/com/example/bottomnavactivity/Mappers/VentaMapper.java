package com.example.bottomnavactivity.Mappers;

import com.example.bottomnavactivity.DTO.DetalleVentaDTO;
import com.example.bottomnavactivity.DTO.VentaDTO;
import com.example.bottomnavactivity.Models.DetalleVentaModel;
import com.example.bottomnavactivity.Models.VentaModel;

public class VentaMapper {
    /**
     *  Convierte un VentaModel a VentaDTO.Venta para requests
     */
    public static VentaDTO.Venta toRequest(VentaModel model) {
        if (model == null) return null;

        VentaDTO.Venta venta = new VentaDTO.Venta();

        // ENVIAR SOLO FECHA Y TOTAL
        venta.setFechaVenta(model.getFechaVenta()); // CRÍTICO: INCLUIR FECHA
        venta.setTotal(model.getTotal());

        return venta;
    }
}
