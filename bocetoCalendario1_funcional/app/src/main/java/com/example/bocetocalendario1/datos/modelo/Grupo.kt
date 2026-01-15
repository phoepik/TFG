package com.example.bocetocalendario1.datos.modelo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "grupos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_admin"]
        )
    ]
)
data class Grupo(
    @PrimaryKey(autoGenerate = true) val id_grupo: Int = 0,
    val nombre: String,
    val descripcion: String?,
    val id_admin: Int?
)