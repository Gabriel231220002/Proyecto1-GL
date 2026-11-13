package com.example.Dominio;

import java.sql.Time;

public class Laboratorio {
    private int id;
    private String nombre;
    private Time horaApertura;
    private Time horaCierre;
    private int aula; // Mantenido para retrocompatibilidad

    public Laboratorio() {
        this.horaApertura = Time.valueOf("07:00:00");
        this.horaCierre = Time.valueOf("21:30:00");
    }

    public Laboratorio(int id, String nombre, Time horaApertura, Time horaCierre) {
        this.id = id;
        this.nombre = nombre;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
    }

    public Laboratorio(int id, String nombre, int aula) {
        this.id = id;
        this.nombre = nombre;
        this.aula = aula;
        this.horaApertura = Time.valueOf("07:00:00");
        this.horaCierre = Time.valueOf("21:30:00");
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Time getHoraApertura() { return horaApertura; }
    public void setHoraApertura(Time horaApertura) { this.horaApertura = horaApertura; }

    public Time getHoraCierre() { return horaCierre; }
    public void setHoraCierre(Time horaCierre) { this.horaCierre = horaCierre; }

    public int getAula() { return aula; }
    public void setAula(int aula) { this.aula = aula; }
}
