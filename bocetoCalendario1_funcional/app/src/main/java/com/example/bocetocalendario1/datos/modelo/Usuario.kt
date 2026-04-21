package com.example.bocetocalendario1.datos.modelo

data class Usuario(
    val id_usuario: Int = 0,
    val nombre: String,
    val email: String,
    val contrasena: String,
    val notificaciones_activas: Boolean = true
)