package com.example.Dominio;

public class Grupo {
    private int id;
    private String nombre;
    private int materiaId;
    private Integer usuarioId;
    private Integer periodoId;

    public Grupo() {}

    public Grupo(int id, String nombre, int materiaId, Integer usuarioId, Integer periodoId) {
        this.id = id;
        this.nombre = nombre;
        this.materiaId = materiaId;
        this.usuarioId = usuarioId;
        this.periodoId = periodoId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getMateriaId() { return materiaId; }
    public void setMateriaId(int materiaId) { this.materiaId = materiaId; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public Integer getPeriodoId() { return periodoId; }
    public void setPeriodoId(Integer periodoId) { this.periodoId = periodoId; }
}
