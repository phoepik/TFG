package com.example.bocetocalendario1.datos.modelo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "eventos",
    foreignKeys = [
        ForeignKey(
            entity = Calendario::class,
            parentColumns = ["id_calendario"],
            childColumns = ["id_calendario"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Evento(
    @PrimaryKey(autoGenerate = true) val id_evento: Int = 0,
    val titulo: String,
    val descripcion: String?,
    val fecha_inicio: String, // Formato "YYYY-MM-DD HH:MM:SS"
    val fecha_fin: String,
    val ubicacion: String?,
    val estado: String,     // 'PENDIENTE', 'CONFIRMADO'
    val id_calendario: Int
)