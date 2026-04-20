package com.example.servidorcalendario.model;

import jakarta.persistence.*;

@Entity
@Table(name = "calendarios")
public class Calendario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCalendario;

    private String nombre;
    private String tipo; // PERSONAL o GRUPAL
    private Integer idPropietario;
    private Integer idGrupo;

    public Calendario() {}

    public Calendario(String nombre, String tipo, Integer idPropietario, Integer idGrupo) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.idPropietario = idPropietario;
        this.idGrupo = idGrupo;
    }

    public Integer getIdCalendario() { return idCalendario; }
    public void setIdCalendario(Integer idCalendario) { this.idCalendario = idCalendario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getIdPropietario() { return idPropietario; }
    public void setIdPropietario(Integer idPropietario) { this.idPropietario = idPropietario; }

    public Integer getIdGrupo() { return idGrupo; }
    public void setIdGrupo(Integer idGrupo) { this.idGrupo = idGrupo; }
}
