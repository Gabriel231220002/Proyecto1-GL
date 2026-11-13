package com.example.Dominio;

import java.sql.Date;

public class ObjetoPerdido {
    private int id;
    private String nombre;
    private String descripcionObjeto;
    private boolean estadoDevolucion;
    private Date fechaHallazgo;
    private Date fechaEntrega;
    private String foto;
    private int usuarioRegistraId;
    private Integer usuarioReclamaId;
    private int laboratorioId;
    private String estado; // Retrocompatibilidad: REPORTADO, CUSTODIADO, ENTREGADO

    public ObjetoPerdido() {
        this.nombre = "Objeto extraviado";
        this.estadoDevolucion = false;
        this.estado = "REPORTADO";
    }

    public ObjetoPerdido(int id, String nombre, String descripcionObjeto, boolean estadoDevolucion,
                         Date fechaHallazgo, Date fechaEntrega, String foto,
                         int usuarioRegistraId, Integer usuarioReclamaId, int laboratorioId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcionObjeto = descripcionObjeto;
        this.estadoDevolucion = estadoDevolucion;
        this.fechaHallazgo = fechaHallazgo;
        this.fechaEntrega = fechaEntrega;
        this.foto = foto;
        this.usuarioRegistraId = usuarioRegistraId;
        this.usuarioReclamaId = usuarioReclamaId;
        this.laboratorioId = laboratorioId;
        this.estado = estadoDevolucion ? "ENTREGADO" : "REPORTADO";
    }

    public ObjetoPerdido(int id, int laboratorioId, int registraId, Integer reclamaId,
                         String descripcionObjeto, String estado, Date fechaHallazgo, Date fechaEntrega) {
        this.id = id;
        this.laboratorioId = laboratorioId;
        this.usuarioRegistraId = registraId;
        this.usuarioReclamaId = reclamaId;
        this.descripcionObjeto = descripcionObjeto;
        this.nombre = descripcionObjeto != null && descripcionObjeto.length() > 20 ? descripcionObjeto.substring(0, 20) : "Objeto";
        this.estado = estado;
        this.estadoDevolucion = "ENTREGADO".equalsIgnoreCase(estado);
        this.fechaHallazgo = fechaHallazgo;
        this.fechaEntrega = fechaEntrega;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcionObjeto() { return descripcionObjeto; }
    public void setDescripcionObjeto(String descripcionObjeto) { this.descripcionObjeto = descripcionObjeto; }

    public boolean isEstadoDevolucion() { return estadoDevolucion; }
    public void setEstadoDevolucion(boolean estadoDevolucion) {
        this.estadoDevolucion = estadoDevolucion;
        this.estado = estadoDevolucion ? "ENTREGADO" : "REPORTADO";
    }

    public Date getFechaHallazgo() { return fechaHallazgo; }
    public void setFechaHallazgo(Date fechaHallazgo) { this.fechaHallazgo = fechaHallazgo; }

    public Date getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(Date fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public int getUsuarioRegistraId() { return usuarioRegistraId; }
    public void setUsuarioRegistraId(int usuarioRegistraId) { this.usuarioRegistraId = usuarioRegistraId; }

    public Integer getUsuarioReclamaId() { return usuarioReclamaId; }
    public void setUsuarioReclamaId(Integer usuarioReclamaId) { this.usuarioReclamaId = usuarioReclamaId; }

    public int getLaboratorioId() { return laboratorioId; }
    public void setLaboratorioId(int laboratorioId) { this.laboratorioId = laboratorioId; }

    // Aliases para retrocompatibilidad
    public int getRegistraId() { return usuarioRegistraId; }
    public void setRegistraId(int registraId) { this.usuarioRegistraId = registraId; }

    public Integer getReclamaId() { return usuarioReclamaId; }
    public void setReclamaId(Integer reclamaId) { this.usuarioReclamaId = reclamaId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) {
        this.estado = estado;
        this.estadoDevolucion = "ENTREGADO".equalsIgnoreCase(estado);
    }
}
