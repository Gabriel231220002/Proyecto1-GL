package com.example.Dominio;

public class CarreraMateria {
    private int carreraId;
    private int materiaId;
    private int nivelAcademico;

    public CarreraMateria() {}

    public CarreraMateria(int carreraId, int materiaId, int nivelAcademico) {
        this.carreraId = carreraId;
        this.materiaId = materiaId;
        this.nivelAcademico = nivelAcademico;
    }

    public int getCarreraId() { return carreraId; }
    public void setCarreraId(int carreraId) { this.carreraId = carreraId; }

    public int getMateriaId() { return materiaId; }
    public void setMateriaId(int materiaId) { this.materiaId = materiaId; }

    public int getNivelAcademico() { return nivelAcademico; }
    public void setNivelAcademico(int nivelAcademico) { this.nivelAcademico = nivelAcademico; }
}
