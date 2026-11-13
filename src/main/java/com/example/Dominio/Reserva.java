package com.example.Dominio;

import java.sql.Date;

public class Reserva {
    private int id;
    private int usuarioId;
    private Integer usuarioRegistroId;
    private Date fechaSolicitud;

    public Reserva() {}

    public Reserva(int id, int usuarioId, Date fechaSolicitud) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.fechaSolicitud = fechaSolicitud;
    }

    public Reserva(int id, int usuarioId, Integer usuarioRegistroId, Date fechaSolicitud) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.usuarioRegistroId = usuarioRegistroId;
        this.fechaSolicitud = fechaSolicitud;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    // Alias para retrocompatibilidad con código existente
    public int getSolicitaId() { return usuarioId; }
    public void setSolicitaId(int solicitaId) { this.usuarioId = solicitaId; }

    public Integer getUsuarioRegistroId() { return usuarioRegistroId; }
    public void setUsuarioRegistroId(Integer usuarioRegistroId) { this.usuarioRegistroId = usuarioRegistroId; }

    public Date getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Date fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
}
