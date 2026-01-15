package com.example.bocetocalendario1.datos.modelo

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "usuarios_grupos",
    primaryKeys = ["id_usuario", "id_grupo"],
    foreignKeys = [
        ForeignKey(entity = Usuario::class, parentColumns = ["id_usuario"], childColumns = ["id_usuario"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Grupo::class, parentColumns = ["id_grupo"], childColumns = ["id_grupo"], onDelete = ForeignKey.CASCADE)
    ]
)
data class UsuarioGrupo(
    val id_usuario: Int,
    val id_grupo: Int
)