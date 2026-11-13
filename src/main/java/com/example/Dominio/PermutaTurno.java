package com.example.Dominio;

import java.sql.Date;

public class PermutaTurno {
    private int id;
    private int usuarioSolicitanteId;
    private int usuarioReceptorId;
    private Date fechaAusencia;
    private String motivo;
    private String estado; // PENDIENTE, ACEPTADA, RECHAZADA
    private Date fechaSolicitud;

    public PermutaTurno() {
        this.estado = "PENDIENTE";
    }

    public PermutaTurno(int id, int usuarioSolicitanteId, int usuarioReceptorId, Date fechaAusencia,
                        String motivo, String estado, Date fechaSolicitud) {
        this.id = id;
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.usuarioReceptorId = usuarioReceptorId;
        this.fechaAusencia = fechaAusencia;
        this.motivo = motivo;
        this.estado = estado;
        this.fechaSolicitud = fechaSolicitud;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioSolicitanteId() { return usuarioSolicitanteId; }
    public void setUsuarioSolicitanteId(int usuarioSolicitanteId) { this.usuarioSolicitanteId = usuarioSolicitanteId; }

    public int getUsuarioReceptorId() { return usuarioReceptorId; }
    public void setUsuarioReceptorId(int usuarioReceptorId) { this.usuarioReceptorId = usuarioReceptorId; }

    // Aliases para retrocompatibilidad
    public int getSolicitanteId() { return usuarioSolicitanteId; }
    public void setSolicitanteId(int solicitanteId) { this.usuarioSolicitanteId = solicitanteId; }

    public int getReceptorId() { return usuarioReceptorId; }
    public void setReceptorId(int receptorId) { this.usuarioReceptorId = receptorId; }

    public Date getFechaAusencia() { return fechaAusencia; }
    public void setFechaAusencia(Date fechaAusencia) { this.fechaAusencia = fechaAusencia; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Date getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Date fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
}
