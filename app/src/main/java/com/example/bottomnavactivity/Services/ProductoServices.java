package com.example.bottomnavactivity.Services;

import com.example.bottomnavactivity.DTO.ProductoDTO;
import com.example.bottomnavactivity.DTO.ProductoGetResponse;
import com.example.bottomnavactivity.DTO.ProductoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductoServices {
    @GET("Producto")
    Call<ProductoResponse> ObtenerProductos();


    //Obtner lista de productos por id

    @GET("Producto/getProducto/{productoId}")
    Call<ProductoGetResponse> ObtenerProductos(@Path("productoId") String productoId);

    @POST("Producto")
        // Asegúrate que esta ruta sea la correcta en tu backend
    Call<Void> agregarProducto(@Body ProductoDTO producto);


    @PUT("Producto/putProductos/{productoId}")
    Call<ProductoDTO.ProductoUpdateResponse> editarProducto(@Path("productoId") String productoId, @Body ProductoDTO producto);


    @DELETE("Producto/deleteProductos/{productoId}")
    Call<Void> eliminarProducto(@Path("productoId") String productoId);
}
