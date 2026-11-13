package com.example.Dominio;

import java.sql.Date;

public class Asistencia {
    private int id;
    private int usuarioId;
    private int horarioId;
    private Integer grupoId; // Opcional / retrocompatibilidad
    private Date fecha;
    private String estadoAsistencia; // PRESENTE, FALTA, LICENCIA, RETRASO
    private String justificacion;
    private boolean esFaltaJustificada;

    public Asistencia() {
        this.esFaltaJustificada = true;
    }

    public Asistencia(int id, int usuarioId, int horarioId, Date fecha,
                      String estadoAsistencia, String justificacion, boolean esFaltaJustificada) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.horarioId = horarioId;
        this.fecha = fecha;
        this.estadoAsistencia = estadoAsistencia;
        this.justificacion = justificacion;
        this.esFaltaJustificada = esFaltaJustificada;
    }

    public Asistencia(int id, int usuarioId, Integer grupoId, Date fecha, String estadoAsistencia) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.grupoId = grupoId;
        this.horarioId = 0;
        this.fecha = fecha;
        this.estadoAsistencia = estadoAsistencia;
        this.esFaltaJustificada = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getHorarioId() { return horarioId; }
    public void setHorarioId(int horarioId) { this.horarioId = horarioId; }

    public Integer getGrupoId() { return grupoId; }
    public void setGrupoId(Integer grupoId) { this.grupoId = grupoId; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getEstadoAsistencia() { return estadoAsistencia; }
    public void setEstadoAsistencia(String estadoAsistencia) { this.estadoAsistencia = estadoAsistencia; }

    public String getJustificacion() { return justificacion; }
    public void setJustificacion(String justificacion) { this.justificacion = justificacion; }

    public boolean isEsFaltaJustificada() { return esFaltaJustificada; }
    public void setEsFaltaJustificada(boolean esFaltaJustificada) { this.esFaltaJustificada = esFaltaJustificada; }
}
