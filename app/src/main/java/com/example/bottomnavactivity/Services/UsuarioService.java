package com.example.bottomnavactivity.Services;

import com.example.bottomnavactivity.DTO.LoginDTO;
import com.example.bottomnavactivity.DTO.UsuarioLoginDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuarioService {
    @POST("Usuario/login")
    Call<UsuarioLoginDTO> Login(@Body LoginDTO usuario);
}
