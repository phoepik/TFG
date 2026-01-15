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
        )
    ]
)
data class Notificacion(
    @PrimaryKey(autoGenerate = true) val id_notificacion: Int = 0,
    val titulo: String,
    val mensaje: String?,
    val tiempo_anticipacion: Int?,
    val tipo: String, // 'RECORDATORIO', 'INVITACION', 'SISTEMA'
    val id_evento: Int?
)