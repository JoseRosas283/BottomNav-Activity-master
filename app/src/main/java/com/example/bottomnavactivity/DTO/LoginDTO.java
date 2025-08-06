package com.example.bottomnavactivity.DTO;

import com.google.gson.annotations.SerializedName;

public class LoginDTO {
    @SerializedName("correo")
    private String correo;

    @SerializedName("clave")
    private String clave;

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
