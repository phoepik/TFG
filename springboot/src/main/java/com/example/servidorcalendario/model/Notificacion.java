package com.example.servidorcalendario.model;

import jakarta.persistence.*;

@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNotificacion;

    private String titulo;
    private String mensaje;
    private Integer tiempoAnticipacion;
    private String tipo; // RECORDATORIO, INVITACION, SISTEMA
    private Integer idEvento;
    private Integer idUsuario;
    private Boolean leida = false;
    private Long fechaCreacion;
    private Integer idGrupoInvitacion;
    private String estadoInvitacion; // PENDIENTE, ACEPTADA, RECHAZADA

    public Notificacion() {}

    public Integer getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(Integer idNotificacion) { this.idNotificacion = idNotificacion; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Integer getTiempoAnticipacion() { return tiempoAnticipacion; }
    public void setTiempoAnticipacion(Integer tiempoAnticipacion) { this.tiempoAnticipacion = tiempoAnticipacion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getIdEvento() { return idEvento; }
    public void setIdEvento(Integer idEvento) { this.idEvento = idEvento; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public Boolean getLeida() { return leida; }
    public void setLeida(Boolean leida) { this.leida = leida; }

    public Long getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Long fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Integer getIdGrupoInvitacion() { return idGrupoInvitacion; }
    public void setIdGrupoInvitacion(Integer idGrupoInvitacion) { this.idGrupoInvitacion = idGrupoInvitacion; }

    public String getEstadoInvitacion() { return estadoInvitacion; }
    public void setEstadoInvitacion(String estadoInvitacion) { this.estadoInvitacion = estadoInvitacion; }
}
