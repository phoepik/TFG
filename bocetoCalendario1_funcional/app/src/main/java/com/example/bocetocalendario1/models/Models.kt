package com.example.bocetocalendario1.models

// Usuario - tabla usuarios
data class Usuario(
    val id: Int = 0,
    val nombre: String = "",
    val email: String = "",
    val contrasena: String = "",
    val notificacionesActivas: Boolean = true
)

// Evento - tabla eventos
data class Evento(
    val id: Int = 0,
    val titulo: String = "",
    val descripcion: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val ubicacion: String = "",
    val estado: String = "PENDIENTE", // PENDIENTE o CONFIRMADO
    val idCalendario: Int = 0
)

// Notificacion - tabla notificaciones
data class Notificacion(
    val id: Int = 0,
    val titulo: String = "",
    val mensaje: String = "",
    val tiempoAnticipacion: Int = 15,
    val tipo: String = "RECORDATORIO", // RECORDATORIO, INVITACION, SISTEMA
    val idUsuario: Int = 0,
    val idEvento: Int = 0
)

// Calendario - tabla calendarios
data class Calendario(
    val id: Int = 0,
    val nombre: String = "",
    val tipo: String = "PERSONAL", // PERSONAL o GRUPAL
    val idPropietario: Int = 0,
    val idGrupo: Int? = null
)
