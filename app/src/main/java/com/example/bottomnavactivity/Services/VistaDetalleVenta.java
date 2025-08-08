package com.example.bottomnavactivity.Services;

import com.example.bottomnavactivity.DTO.VistaVentaResponseDTO;

import retrofit2.Call;
import retrofit2.http.GET;

public interface VistaDetalleVenta {
    @GET("VistaDetalleVenta")
    Call<VistaVentaResponseDTO> obtenerVentas();
}
