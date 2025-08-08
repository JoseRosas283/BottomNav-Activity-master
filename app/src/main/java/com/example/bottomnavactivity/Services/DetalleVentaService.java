package com.example.bottomnavactivity.Services;

import com.example.bottomnavactivity.DTO.DetalleVentaDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DetalleVentaService {
    @GET("DetalleVenta")
    Call<DetalleVentaDTO> getListaDetalleVentas();  // RETORNA MENSAJE MAS LISTA DETALLES

    @GET("DetalleVenta/getDetalleVenta/{productoId}/{ventaId}")
    Call<DetalleVentaDTO> getDetalleVentaPorIds(@Path("productoId") String productoId,
                                                @Path("ventaId") String ventaId);  // RETORNA UN DETALLE

    @POST("DetalleVenta")
    Call<DetalleVentaDTO> registrarDetalleVenta(@Body DetalleVentaDTO.DetalleVenta detalleVenta);

    @PUT("DetalleVenta/PutdetalleVentas/{productoId}/{ventaId}")
    Call<DetalleVentaDTO> actualizarDetalleVenta(@Path("productoId") String productoId,
                                                 @Path("ventaId") String ventaId,
                                                 @Body DetalleVentaDTO.DetalleVenta detalleVenta);

    @DELETE("DetalleVenta/deletedetalleVenta/{productoId}/{ventaId}")
    Call<Void> eliminarDetalleVenta(@Path("productoId") String productoId,
                                    @Path("ventaId") String ventaId);
}
