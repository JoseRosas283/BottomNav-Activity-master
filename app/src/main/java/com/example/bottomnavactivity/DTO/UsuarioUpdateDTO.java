package com.example.bottomnavactivity.DTO;

import com.google.gson.annotations.SerializedName;

public class UsuarioUpdateDTO {
    @SerializedName("usuario")
    private String usuario;

    @SerializedName("correo")
    private String correo;

    @SerializedName("clave")
    private String clave;

    // Constructor sin clave (para actualizar solo nombre y correo)
    public UsuarioUpdateDTO(String usuario, String correo) {
        this.usuario = usuario;
        this.correo = correo;
    }

    // Constructor con clave (para actualizar nombre, correo y clave)
    public UsuarioUpdateDTO(String usuario, String correo, String clave) {
        this.usuario = usuario;
        this.correo = correo;
        this.clave = clave;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

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
