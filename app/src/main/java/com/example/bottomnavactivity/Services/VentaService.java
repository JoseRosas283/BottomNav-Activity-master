package com.example.bottomnavactivity.Services;

import com.example.bottomnavactivity.DTO.VentaDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VentaService {
    @GET("Venta")
    Call<VentaDTO> getListaVentas();  // RETORNA MENSAJE MAS LISTA DE VENTAS

    @GET("Venta/getVentas/{ventaId}")
    Call<VentaDTO> getVentaPorId(@Path("ventaId") String ventaId);  // RETORNA UNA VENTA

    @POST("Venta")
    Call<VentaDTO> registrarVenta(@Body VentaDTO.Venta venta);  //ENVIA UNA SOLA VENTA

    @PUT("Venta/putVentas/{ventaId}")
    Call<VentaDTO> actualizarVenta(@Path("ventaId") String ventaId, @Body VentaDTO.Venta venta);

    @DELETE("Venta/deleteVentas/{ventaId}")
    Call<Void> eliminarVenta(@Path("ventaId") String ventaId);
}
