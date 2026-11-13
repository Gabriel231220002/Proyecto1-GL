package com.example.Dominio;

import java.sql.Date;

public class UsuarioPrivilegio {
    private int usuarioId;
    private int privilegioId;
    private Date fechaAsignacion;
    private boolean estado;

    public UsuarioPrivilegio() {
        this.estado = true;
    }

    public UsuarioPrivilegio(int usuarioId, int privilegioId, Date fechaAsignacion, boolean estado) {
        this.usuarioId = usuarioId;
        this.privilegioId = privilegioId;
        this.fechaAsignacion = fechaAsignacion;
        this.estado = estado;
    }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getPrivilegioId() { return privilegioId; }
    public void setPrivilegioId(int privilegioId) { this.privilegioId = privilegioId; }

    public Date getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(Date fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
}
