package com.example.Dominio;

import java.sql.Date;

public class TicketIncidencia {
    private int id;
    private int itemInventarioId;
    private int usuarioReportaId;
    private Integer usuarioAtiendeId;
    private String asunto;
    private String descripcion;
    private String estado; // Derivado o gestionado: ABIERTO, EN_PROCESO, RESUELTO, CERRADO
    private String solucionAplicada;
    private Date fechaIncidencia;
    private Date fechaSolucion;

    public TicketIncidencia() {
        this.estado = "ABIERTO";
    }

    public TicketIncidencia(int id, int itemInventarioId, int usuarioReportaId, Integer usuarioAtiendeId,
                            String asunto, String descripcion, String estado,
                            String solucionAplicada, Date fechaIncidencia, Date fechaSolucion) {
        this.id = id;
        this.itemInventarioId = itemInventarioId;
        this.usuarioReportaId = usuarioReportaId;
        this.usuarioAtiendeId = usuarioAtiendeId;
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.estado = estado;
        this.solucionAplicada = solucionAplicada;
        this.fechaIncidencia = fechaIncidencia;
        this.fechaSolucion = fechaSolucion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getItemInventarioId() { return itemInventarioId; }
    public void setItemInventarioId(int itemInventarioId) { this.itemInventarioId = itemInventarioId; }

    public int getUsuarioReportaId() { return usuarioReportaId; }
    public void setUsuarioReportaId(int usuarioReportaId) { this.usuarioReportaId = usuarioReportaId; }

    public Integer getUsuarioAtiendeId() { return usuarioAtiendeId; }
    public void setUsuarioAtiendeId(Integer usuarioAtiendeId) { this.usuarioAtiendeId = usuarioAtiendeId; }

    // Aliases para retrocompatibilidad
    public int getReportaId() { return usuarioReportaId; }
    public void setReportaId(int reportaId) { this.usuarioReportaId = reportaId; }

    public Integer getAtiendeId() { return usuarioAtiendeId; }
    public void setAtiendeId(Integer atiendeId) { this.usuarioAtiendeId = atiendeId; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getSolucionAplicada() { return solucionAplicada; }
    public void setSolucionAplicada(String solucionAplicada) { this.solucionAplicada = solucionAplicada; }

    public Date getFechaIncidencia() { return fechaIncidencia; }
    public void setFechaIncidencia(Date fechaIncidencia) { this.fechaIncidencia = fechaIncidencia; }

    public Date getFechaSolucion() { return fechaSolucion; }
    public void setFechaSolucion(Date fechaSolucion) { this.fechaSolucion = fechaSolucion; }
}
