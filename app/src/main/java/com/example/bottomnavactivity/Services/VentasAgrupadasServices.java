package com.example.bottomnavactivity.Services;

import com.example.bottomnavactivity.DTO.VentaAgrupadaResponseDTO;

import retrofit2.Call;
import retrofit2.http.GET;

public interface VentasAgrupadasServices {
    @GET("VentasAgrupadas/agrupadas")
    Call<VentaAgrupadaResponseDTO> obtenerVentasAgrupadas();
}
