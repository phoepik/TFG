package com.example.servidorcalendario.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    private String nombre;

    @Column(unique = true)
    private String email;

    private String contrasena;

    private Boolean notificacionesActivas = true;

    // ── Constructores ──

    public Usuario() {}

    public Usuario(String nombre, String email, String contrasena) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.notificacionesActivas = true;
    }

    // ── Getters y Setters ──

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Boolean getNotificacionesActivas() { return notificacionesActivas; }
    public void setNotificacionesActivas(Boolean notificacionesActivas) { this.notificacionesActivas = notificacionesActivas; }
}
