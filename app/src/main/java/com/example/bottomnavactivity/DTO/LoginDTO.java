package com.example.bottomnavactivity.DTO;

import com.google.gson.annotations.SerializedName;

public class LoginDTO {
    @SerializedName("usuario")
    private String usuario;

    @SerializedName("clave")
    private String clave;

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
