package com.example.Dominio;

import java.sql.Date;

public class GrupoHorario {
    private int id; // Retrocompatibilidad en memoria
    private int grupoId;
    private int horarioId;
    private Integer laboratorioId; // Puede derivarse de horario
    private boolean estado;
    private boolean esParcial;
    private Date fechaParcial;

    public GrupoHorario() {
        this.estado = true;
        this.esParcial = false;
    }

    public GrupoHorario(int grupoId, int horarioId, boolean estado, boolean esParcial, Date fechaParcial) {
        this.grupoId = grupoId;
        this.horarioId = horarioId;
        this.estado = estado;
        this.esParcial = esParcial;
        this.fechaParcial = fechaParcial;
    }

    public GrupoHorario(int id, int grupoId, int horarioId, Integer laboratorioId,
                        boolean estado, boolean esParcial, Date fechaParcial) {
        this.id = id;
        this.grupoId = grupoId;
        this.horarioId = horarioId;
        this.laboratorioId = laboratorioId;
        this.estado = estado;
        this.esParcial = esParcial;
        this.fechaParcial = fechaParcial;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGrupoId() { return grupoId; }
    public void setGrupoId(int grupoId) { this.grupoId = grupoId; }

    public int getHorarioId() { return horarioId; }
    public void setHorarioId(int horarioId) { this.horarioId = horarioId; }

    public Integer getLaboratorioId() { return laboratorioId; }
    public void setLaboratorioId(Integer laboratorioId) { this.laboratorioId = laboratorioId; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public boolean isEsParcial() { return esParcial; }
    public void setEsParcial(boolean esParcial) { this.esParcial = esParcial; }

    public Date getFechaParcial() { return fechaParcial; }
    public void setFechaParcial(Date fechaParcial) { this.fechaParcial = fechaParcial; }
}
