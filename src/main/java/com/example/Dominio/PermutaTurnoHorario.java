package com.example.Dominio;

public class PermutaTurnoHorario {
    private int permutaTurnoId;
    private int horarioId;

    public PermutaTurnoHorario() {}

    public PermutaTurnoHorario(int permutaTurnoId, int horarioId) {
        this.permutaTurnoId = permutaTurnoId;
        this.horarioId = horarioId;
    }

    public int getPermutaTurnoId() { return permutaTurnoId; }
    public void setPermutaTurnoId(int permutaTurnoId) { this.permutaTurnoId = permutaTurnoId; }

    public int getHorarioId() { return horarioId; }
    public void setHorarioId(int horarioId) { this.horarioId = horarioId; }
}
