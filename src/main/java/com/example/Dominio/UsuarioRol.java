package com.example.Dominio;

public class UsuarioRol {
    private int usuarioId;
    private int rolId;
    private boolean esPrincipal;

    public UsuarioRol() {
        this.esPrincipal = false;
    }

    public UsuarioRol(int usuarioId, int rolId, boolean esPrincipal) {
        this.usuarioId = usuarioId;
        this.rolId = rolId;
        this.esPrincipal = esPrincipal;
    }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getRolId() { return rolId; }
    public void setRolId(int rolId) { this.rolId = rolId; }

    public boolean isEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(boolean esPrincipal) { this.esPrincipal = esPrincipal; }
}

