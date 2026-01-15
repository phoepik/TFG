package com.example.bocetocalendario1.datos.dao

import androidx.room.*
import com.example.bocetocalendario1.datos.modelo.Evento
import com.example.bocetocalendario1.datos.modelo.Usuario

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios")
    suspend fun obtenerUsuarios(): List<Usuario>

    @Insert
    suspend fun insertarEvento(evento: Evento)

    @Query("SELECT * FROM eventos WHERE id_calendario = :idCal")
    suspend fun obtenerEventosDeCalendario(idCal: Int): List<Evento>

    @Query("SELECT * FROM usuarios WHERE email = :emailRecibido AND contrasena = :contrasenaRecibida LIMIT 1")
    suspend fun verificarUsuario(emailRecibido: String, contrasenaRecibida: String): Usuario?
}