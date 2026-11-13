package com.example.Dominio;

import java.sql.Date;

public class Usuario {
    private int id;
    private String nombres;
    private String apellidoP;
    private String apellidoM;
    private String genero;
    private String foto;
    private int ci;
    private Date fechaNacimiento;
    private int telefono;
    private String rol;
    private String email;
    private String passwordHash;
    private int registroInstitucional;
    private boolean estado;

    public Usuario() {
        this.estado = true;
    }

    public Usuario(int id, String nombres, String apellidoP, String apellidoM, String genero,
                   String foto, int ci, Date fechaNacimiento, int telefono, String rol,
                   String email, String passwordHash, int registroInstitucional, boolean estado) {
        this.id = id;
        this.nombres = nombres;
        this.apellidoP = apellidoP;
        this.apellidoM = apellidoM;
        this.genero = genero;
        this.foto = foto;
        this.ci = ci;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.rol = rol;
        this.email = email;
        this.passwordHash = passwordHash;
        this.registroInstitucional = registroInstitucional;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidoP() { return apellidoP; }
    public void setApellidoP(String apellidoP) { this.apellidoP = apellidoP; }

    public String getApellidoM() { return apellidoM; }
    public void setApellidoM(String apellidoM) { this.apellidoM = apellidoM; }

    public String getNombreCompleto() {
        return nombres + " " + (apellidoP != null ? apellidoP : "") + (apellidoM != null ? " " + apellidoM : "");
    }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public int getCi() { return ci; }
    public void setCi(int ci) { this.ci = ci; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public int getTelefono() { return telefono; }
    public void setTelefono(int telefono) { this.telefono = telefono; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public int getRegistroInstitucional() { return registroInstitucional; }
    public void setRegistroInstitucional(int registroInstitucional) { this.registroInstitucional = registroInstitucional; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
}
