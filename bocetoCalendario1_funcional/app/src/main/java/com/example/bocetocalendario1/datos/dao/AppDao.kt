package com.example.bocetocalendario1.datos.dao

import androidx.room.*
import com.example.bocetocalendario1.datos.modelo.Evento
import com.example.bocetocalendario1.datos.modelo.Grupo
import com.example.bocetocalendario1.datos.modelo.Usuario

@Dao
interface AppDao {

    //Insertar

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: Usuario)

    @Insert
    suspend fun insertarEvento(evento: Evento)

    @Insert
    suspend fun insertarGrupo(unGrupo: Grupo)


    //Obtener

    @Query("SELECT * FROM usuarios")
    suspend fun obtenerUsuarios(): List<Usuario>

    @Query("SELECT * FROM eventos WHERE id_calendario = :idCal")
    suspend fun obtenerEventosDeCalendario(idCal: Int): List<Evento>

    //ObtenerUsuariosGrupo(devolver lista id/Usuario)




    //Verificar

    @Query("SELECT * FROM usuarios WHERE email = :emailRecibido LIMIT 1")
    suspend fun verificarUsuario(emailRecibido: String): Usuario?




}