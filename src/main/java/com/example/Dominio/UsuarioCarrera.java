package com.example.Dominio;

public class UsuarioCarrera {
    private int usuarioId;
    private int carreraId;

    public UsuarioCarrera() {}

    public UsuarioCarrera(int usuarioId, int carreraId) {
        this.usuarioId = usuarioId;
        this.carreraId = carreraId;
    }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getCarreraId() { return carreraId; }
    public void setCarreraId(int carreraId) { this.carreraId = carreraId; }
}
