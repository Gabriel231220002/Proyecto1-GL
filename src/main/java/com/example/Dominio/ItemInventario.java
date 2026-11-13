package com.example.Dominio;

public class ItemInventario {
    private int id;
    private int laboratorioId;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String estadoFisico; // BUENO, REGULAR, MALO, EN_REPARACION, DE_BAJA
    private String foto;

    public ItemInventario() {
        this.estadoFisico = "BUENO";
    }

    public ItemInventario(int id, int laboratorioId, String codigo, String nombre,
                          String descripcion, String estadoFisico) {
        this.id = id;
        this.laboratorioId = laboratorioId;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estadoFisico = estadoFisico;
    }

    public ItemInventario(int id, int laboratorioId, String codigo, String nombre,
                          String descripcion, String estadoFisico, String foto) {
        this.id = id;
        this.laboratorioId = laboratorioId;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estadoFisico = estadoFisico;
        this.foto = foto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLaboratorioId() { return laboratorioId; }
    public void setLaboratorioId(int laboratorioId) { this.laboratorioId = laboratorioId; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstadoFisico() { return estadoFisico; }
    public void setEstadoFisico(String estadoFisico) { this.estadoFisico = estadoFisico; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}
