package com.example.bottomnavactivity.DTO;

import com.google.gson.annotations.SerializedName;

public class UsuarioGetResponse {
    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("usuario")
    private UsuarioLoginDTO usuario;

    public String getMensaje() {
        return mensaje;
    }

    public UsuarioLoginDTO getUsuario() {
        return usuario;
    }
}
