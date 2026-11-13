package com.example.Dominio;

import java.sql.Date;

public class Periodo {
    private int id;
    private int gestion;
    private int semestre;
    private boolean aprobado;
    private Date fechaApertura;
    private Date fechaFinalizacion;
    private boolean estado;

    public Periodo() {
        this.aprobado = false;
        this.estado = true;
    }

    public Periodo(int id, int gestion, int semestre) {
        this.id = id;
        this.gestion = gestion;
        this.semestre = semestre;
        this.aprobado = false;
        this.estado = true;
    }

    public Periodo(int id, int gestion, int semestre, boolean aprobado,
                   Date fechaApertura, Date fechaFinalizacion, boolean estado) {
        this.id = id;
        this.gestion = gestion;
        this.semestre = semestre;
        this.aprobado = aprobado;
        this.fechaApertura = fechaApertura;
        this.fechaFinalizacion = fechaFinalizacion;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGestion() { return gestion; }
    public void setGestion(int gestion) { this.gestion = gestion; }

    public int getSemestre() { return semestre; }
    public void setSemestre(int semestre) { this.semestre = semestre; }

    public boolean isAprobado() { return aprobado; }
    public void setAprobado(boolean aprobado) { this.aprobado = aprobado; }

    public Date getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(Date fechaApertura) { this.fechaApertura = fechaApertura; }

    public Date getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(Date fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public String getDescripcion() {
        return gestion + "-" + semestre;
    }
}
