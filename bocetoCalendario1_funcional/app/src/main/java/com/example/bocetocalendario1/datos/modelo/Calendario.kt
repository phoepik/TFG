package com.example.bocetocalendario1.datos.modelo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "calendarios",
    foreignKeys = [
        ForeignKey(entity = Usuario::class, parentColumns = ["id_usuario"], childColumns = ["id_propietario"]),
        ForeignKey(entity = Grupo::class, parentColumns = ["id_grupo"], childColumns = ["id_grupo"])
    ]
)
data class Calendario(
    @PrimaryKey(autoGenerate = true) val id_calendario: Int = 0,
    val nombre: String,
    val tipo: String, // 'PERSONAL' o 'GRUPAL'
    val id_propietario: Int?,
    val id_grupo: Int?
)