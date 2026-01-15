package com.example.bocetocalendario1.datos.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id_usuario: Int = 0,
    val nombre: String,
    val email: String,
    val contrasena: String,
    val notificaciones_activas: Boolean = true
)