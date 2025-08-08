package com.example.bottomnavactivity.Services;

import com.example.bottomnavactivity.DTO.ProductoVentaDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductoVentaService {
    @GET("Producto")
    Call<ProductoVentaDTO> getListaProductos();  //RETORRNA MENSAJE MAS LISTA PRODUCTOS

    @GET("Producto/getProducto/{id}")
    Call<ProductoVentaDTO> getProductoPorId(@Path("id") int id);  // RETORNA UN PRODUCTO

    @GET("Producto/getProductoPorCodigo/{codigo}")
    Call<ProductoVentaDTO> getProductoPorCodigo(@Path("codigo") String codigo);  //RETORNA UN PRODUCTO

    @POST("Producto")
    Call<ProductoVentaDTO> registrarProducto(@Body ProductoVentaDTO.Producto producto);

    @PUT("Producto/putProducto/{id}")
    Call<ProductoVentaDTO> actualizarProducto(@Path("id") int id, @Body ProductoVentaDTO.Producto producto);

    @DELETE("Producto/deleteProducto/{id}")
    Call<Void> eliminarProducto(@Path("id") int id);
}
