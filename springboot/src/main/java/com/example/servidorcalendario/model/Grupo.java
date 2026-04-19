package com.example.servidorcalendario.model;

import jakarta.persistence.*;

@Entity
@Table(name = "grupos")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGrupo;

    private String nombre;
    private String descripcion;
    private Integer idAdmin;

    // ── Constructores ──

    public Grupo() {}

    public Grupo(String nombre, String descripcion, Integer idAdmin) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idAdmin = idAdmin;
    }

    // ── Getters y Setters ──

    public Integer getIdGrupo() { return idGrupo; }
    public void setIdGrupo(Integer idGrupo) { this.idGrupo = idGrupo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getIdAdmin() { return idAdmin; }
    public void setIdAdmin(Integer idAdmin) { this.idAdmin = idAdmin; }
}
