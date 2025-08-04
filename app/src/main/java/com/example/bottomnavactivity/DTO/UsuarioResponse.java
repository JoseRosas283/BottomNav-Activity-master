package com.example.bottomnavactivity.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UsuarioResponse {
    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("usuarios")
    ArrayList<UsuarioLoginDTO> listaUsuarios;

    public String getMensaje() {
        return mensaje;
    }

    public ArrayList<UsuarioLoginDTO> getListaUsuarios() {
        return listaUsuarios;
    }
}
