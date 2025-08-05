package com.example.bottomnavactivity.Services;

import com.example.bottomnavactivity.DTO.DeleteResponse;
import com.example.bottomnavactivity.DTO.LoginDTO;
import com.example.bottomnavactivity.DTO.UsuarioDTO;
import com.example.bottomnavactivity.DTO.UsuarioGetResponse;
import com.example.bottomnavactivity.DTO.UsuarioLoginDTO;
import com.example.bottomnavactivity.DTO.UsuarioRegisterResponse;
import com.example.bottomnavactivity.DTO.UsuarioResponse;
import com.example.bottomnavactivity.DTO.UsuarioUpdateDTO;
import com.example.bottomnavactivity.DTO.UsuarioUpdateResponse;

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
    Call<UsuarioGetResponse> obtenerUsuario(
            @Path("usuarioId") String usuarioId
    );

    // Crear nuevo usuario
    @POST("Usuario")
    Call<UsuarioRegisterResponse> agregarUsuario(
            @Body UsuarioDTO usuario
    );

    // Actualizar usuario
    @PUT("Usuario/putUsuarios/{usuarioId}")
    Call<UsuarioUpdateResponse> actualizarUsuario(
            @Path("usuarioId") String usuarioId,
            @Body UsuarioUpdateDTO usuario
    );

    // Eliminar usuario
    @DELETE("Usuario/deleteUsuarios/{usuarioId}")
    Call<DeleteResponse> borrarUsuario(
            @Path("usuarioId") String usuarioId
    );
}
