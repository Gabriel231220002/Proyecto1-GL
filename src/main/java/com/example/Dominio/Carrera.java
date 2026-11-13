package com.example.Dominio;

public class Carrera {
    private int id;
    private String codigo;
    private String nombre;
    private String sigla;

    public Carrera() {}

    public Carrera(int id, String codigo, String nombre, String sigla) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.sigla = sigla;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSigla() { return sigla; }
    public void setSigla(String sigla) { this.sigla = sigla; }
}
