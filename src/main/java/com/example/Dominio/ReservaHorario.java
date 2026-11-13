package com.example.Dominio;

import java.sql.Date;

public class ReservaHorario {
    private int id; // Retrocompatibilidad
    private int reservaId;
    private int horarioId;
    private int laboratorioId; // Derivable de horario
    private Date fechaUso;
    private String estado; // PENDIENTE, APROBADA, RECHAZADA, CANCELADA
    private String motivo;

    public ReservaHorario() {
        this.estado = "PENDIENTE";
    }

    public ReservaHorario(int reservaId, int horarioId, Date fechaUso, String estado, String motivo) {
        this.reservaId = reservaId;
        this.horarioId = horarioId;
        this.fechaUso = fechaUso;
        this.estado = estado;
        this.motivo = motivo;
    }

    public ReservaHorario(int id, int reservaId, int horarioId, int laboratorioId,
                          Date fechaUso, String estado, String motivo) {
        this.id = id;
        this.reservaId = reservaId;
        this.horarioId = horarioId;
        this.laboratorioId = laboratorioId;
        this.fechaUso = fechaUso;
        this.estado = estado;
        this.motivo = motivo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReservaId() { return reservaId; }
    public void setReservaId(int reservaId) { this.reservaId = reservaId; }

    public int getHorarioId() { return horarioId; }
    public void setHorarioId(int horarioId) { this.horarioId = horarioId; }

    public int getLaboratorioId() { return laboratorioId; }
    public void setLaboratorioId(int laboratorioId) { this.laboratorioId = laboratorioId; }

    public Date getFechaUso() { return fechaUso; }
    public void setFechaUso(Date fechaUso) { this.fechaUso = fechaUso; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
