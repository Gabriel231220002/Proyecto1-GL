package com.example.Dominio;

import java.sql.Timestamp;

public class Chat {
    private int id;
    private int remitenteId;
    private int destinatarioId;
    private String contenido;
    private Timestamp fechaEnvio;
    private boolean estadoLectura;

    public Chat() {
        this.estadoLectura = false;
    }

    public Chat(int id, int remitenteId, int destinatarioId, String contenido,
                Timestamp fechaEnvio, boolean estadoLectura) {
        this.id = id;
        this.remitenteId = remitenteId;
        this.destinatarioId = destinatarioId;
        this.contenido = contenido;
        this.fechaEnvio = fechaEnvio;
        this.estadoLectura = estadoLectura;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRemitenteId() { return remitenteId; }
    public void setRemitenteId(int remitenteId) { this.remitenteId = remitenteId; }

    public int getDestinatarioId() { return destinatarioId; }
    public void setDestinatarioId(int destinatarioId) { this.destinatarioId = destinatarioId; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public Timestamp getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(Timestamp fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public boolean isEstadoLectura() { return estadoLectura; }
    public void setEstadoLectura(boolean estadoLectura) { this.estadoLectura = estadoLectura; }
}
