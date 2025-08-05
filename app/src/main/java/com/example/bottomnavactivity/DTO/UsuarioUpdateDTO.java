package com.example.bottomnavactivity.DTO;

public class UsuarioUpdateDTO {
    private String usuario;
    private String correo;

    public UsuarioUpdateDTO(String usuario, String correo) {
        this.usuario = usuario;
        this.correo = correo;
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

}
