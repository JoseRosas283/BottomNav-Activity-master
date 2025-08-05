package com.example.bottomnavactivity.DTO;

public class UsuarioDTO {

    private String usuario;

    private String correo;

    private String clave;

    public UsuarioDTO(String usuario, String correo, String clave) {
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
