package com.example.bocetocalendario1.datos.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notificaciones")
data class Notificacion(
    @PrimaryKey(autoGenerate = true) val id_notificacion: Int = 0,
    val titulo: String,
    val mensaje: String?,
    val tiempo_anticipacion: Int?,
    val tipo: String,                     // RECORDATORIO, INVITACION, SISTEMA
    val id_evento: Int?,
    val id_usuario: Int,
    val leida: Boolean = false,
    val fecha_creacion: Long = System.currentTimeMillis(),
    val id_grupo_invitacion: Int? = null,
    val estado_invitacion: String? = null, // PENDIENTE, ACEPTADA, RECHAZADA
    val trigger_at_millis: Long? = null    // momento exacto de la alarma
)