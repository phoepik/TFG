package com.example.bocetocalendario1.datos.modelo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "notificaciones",
    foreignKeys = [
        ForeignKey(
            entity = Evento::class,
            parentColumns = ["id_evento"],
            childColumns = ["id_evento"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Notificacion(
    @PrimaryKey(autoGenerate = true) val id_notificacion: Int = 0,
    val titulo: String,
    val mensaje: String?,
    val tiempo_anticipacion: Int?,       // minutos antes del evento
    val tipo: String,                     // 'RECORDATORIO', 'INVITACION', 'SISTEMA'
    val id_evento: Int?,
    val id_usuario: Int,                  // destinatario
    val leida: Boolean = false,
    val fecha_creacion: Long = System.currentTimeMillis(),
    val id_grupo_invitacion: Int? = null, // solo para tipo INVITACION
    val estado_invitacion: String? = null  // 'PENDIENTE', 'ACEPTADA', 'RECHAZADA'
)
