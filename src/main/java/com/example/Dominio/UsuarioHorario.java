package com.example.Dominio;

public class UsuarioHorario {
    private int id;
    private int usuarioId;
    private int horarioId;
    private int tipoTurno;

    public UsuarioHorario() {
        this.tipoTurno = 1;
    }

    public UsuarioHorario(int id, int usuarioId, int horarioId, int tipoTurno) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.horarioId = horarioId;
        this.tipoTurno = tipoTurno;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getHorarioId() { return horarioId; }
    public void setHorarioId(int horarioId) { this.horarioId = horarioId; }

    public int getTipoTurno() { return tipoTurno; }
    public void setTipoTurno(int tipoTurno) { this.tipoTurno = tipoTurno; }
}
