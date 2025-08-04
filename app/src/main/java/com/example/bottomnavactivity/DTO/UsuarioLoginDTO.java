package com.example.bottomnavactivity.DTO;

public class UsuarioLoginDTO {
    private String usuarioId;

    private String usuario;

    private String correo;

    private String clave;

    public UsuarioLoginDTO() {
        // necesario para Retrofit o librerías de deserialización
    }

    public UsuarioLoginDTO(String usuarioId, String usuario, String correo, String clave) {
        this.usuarioId = usuarioId;
        this.usuario = usuario;
        this.correo = correo;
        this.clave = clave;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
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
