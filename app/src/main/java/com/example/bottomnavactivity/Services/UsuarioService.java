package com.example.bottomnavactivity.Services;

import com.example.bottomnavactivity.DTO.LoginDTO;
import com.example.bottomnavactivity.DTO.UsuarioLoginDTO;
import com.example.bottomnavactivity.DTO.UsuarioResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioService {
    // Login
    @POST("Usuario/login")
    Call<UsuarioLoginDTO> Login(@Body LoginDTO usuario);

    // Obtener lista de usuarios
    @GET("Usuario")
    Call<UsuarioResponse> obtenerUsuarios();

    // Obtener usuario especifico por ID
    @GET("Usuario/getUsuario/{usuarioId}")
    Call<UsuarioLoginDTO> obtenerUsuario(
            @Path("usuarioId") String usuarioId
    );

    // Crear nuevo usuario
    @POST("Usuario")
    Call<String> agregarUsuario(
            @Body UsuarioLoginDTO usuario
    );

    // Actualizar usuario
    @PUT("Usuario/putUsuarios/{usuarioId}")
    Call<String> actualizarUsuario(
            @Path("usuarioId") String usuarioId,
            @Body UsuarioLoginDTO usuario
    );

    // Eliminar usuario
    @DELETE("Usuario/deleteUsuarios/{usuarioId}")
    Call<String> borrarUsuario(
            @Path("usuarioId") String usuarioId
    );
}
