package com.example.Dominio;

import java.sql.Date;
import java.sql.Time;

public class CierreLaboratorio {
    private int id;
    private String motivo;
    private Date fecha;
    private Time horaInicio;
    private Time horaFin;

    public CierreLaboratorio() {}

    public CierreLaboratorio(int id, String motivo, Date fecha, Time horaInicio, Time horaFin) {
        this.id = id;
        this.motivo = motivo;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public Time getHoraInicio() { return horaInicio; }
    public void setHoraInicio(Time horaInicio) { this.horaInicio = horaInicio; }

    public Time getHoraFin() { return horaFin; }
    public void setHoraFin(Time horaFin) { this.horaFin = horaFin; }
}

