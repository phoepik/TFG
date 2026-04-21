package com.example.bocetocalendario1.datos.modelo

data class Grupo(
    val id_grupo: Int = 0,
    val nombre: String,
    val descripcion: String?,
    val id_admin: Int?
)