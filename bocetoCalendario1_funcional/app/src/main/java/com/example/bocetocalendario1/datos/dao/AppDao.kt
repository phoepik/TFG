package com.example.bocetocalendario1.datos.dao

import androidx.room.*
import com.example.bocetocalendario1.datos.modelo.Calendario
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

    @Insert
    suspend fun insertarCalendario(calendario: Calendario)


    //Obtener

    @Query("SELECT * FROM usuarios")
    suspend fun obtenerUsuarios(): List<Usuario>

    @Query("SELECT * FROM eventos WHERE id_calendario = :idCal")
    suspend fun obtenerEventosDeCalendario(idCal: Int): List<Evento>

    //ObtenerUsuariosGrupo(devolver lista id/Usuario)
    @Query("SELECT * FROM grupos WHERE id_admin = :idUsuario")
    suspend fun obtenerGruposDeUsuario(idUsuario: Int): List<Grupo>

    //Verificar

    @Query("SELECT * FROM usuarios WHERE email = :emailRecibido LIMIT 1")
    suspend fun verificarUsuario(emailRecibido: String): Usuario?
    @Query("DELETE FROM grupos WHERE id_grupo = :grupoId")
    suspend fun eliminarGrupoPorId(grupoId: Int)

    @Query("UPDATE usuarios SET notificaciones_activas = :activas WHERE id_usuario = :idUsuario")
    suspend fun actualizarNotificaciones(idUsuario: Int, activas: Boolean)


}