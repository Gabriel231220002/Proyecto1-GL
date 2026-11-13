package com.example.Dominio;

import java.sql.Time;

public class Horario {
    private int id;
    private String dia;
    private Time horaInicio;
    private Time horaFin;
    private Integer laboratorioId;

    public Horario() {}

    public Horario(int id, String dia, Time horaInicio, Time horaFin) {
        this.id = id;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Horario(int id, String dia, Time horaInicio, Time horaFin, Integer laboratorioId) {
        this.id = id;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.laboratorioId = laboratorioId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }

    public Time getHoraInicio() { return horaInicio; }
    public void setHoraInicio(Time horaInicio) { this.horaInicio = horaInicio; }

    public Time getHoraFin() { return horaFin; }
    public void setHoraFin(Time horaFin) { this.horaFin = horaFin; }

    public Integer getLaboratorioId() { return laboratorioId; }
    public void setLaboratorioId(Integer laboratorioId) { this.laboratorioId = laboratorioId; }

    public Integer getLabarotarioId() { return laboratorioId; }
    public void setLabarotarioId(Integer labarotarioId) { this.laboratorioId = labarotarioId; }

    public String getDiaNombre() {
        if (dia == null) return "DESCONOCIDO";
        switch (dia.trim()) {
            case "1": return "LUNES";
            case "2": return "MARTES";
            case "3": return "MIERCOLES";
            case "4": return "JUEVES";
            case "5": return "VIERNES";
            case "6": return "SABADO";
            case "7": return "DOMINGO";
            default: return dia.toUpperCase();
        }
    }
}
