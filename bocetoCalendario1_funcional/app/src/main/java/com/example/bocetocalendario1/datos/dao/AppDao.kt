package com.example.bocetocalendario1.datos.dao

import androidx.room.*
import com.example.bocetocalendario1.datos.modelo.Calendario
import com.example.bocetocalendario1.datos.modelo.Evento
import com.example.bocetocalendario1.datos.modelo.Grupo
import com.example.bocetocalendario1.datos.modelo.Notificacion
import com.example.bocetocalendario1.datos.modelo.Usuario
import com.example.bocetocalendario1.datos.modelo.UsuarioGrupo

@Dao
interface AppDao {

    // ── Usuarios ──

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios")
    suspend fun obtenerUsuarios(): List<Usuario>

    @Query("SELECT * FROM usuarios WHERE email = :emailRecibido LIMIT 1")
    suspend fun verificarUsuario(emailRecibido: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id_usuario = :id LIMIT 1")
    suspend fun obtenerUsuarioPorId(id: Int): Usuario?

    // ── Calendarios ──

    @Insert
    suspend fun insertarCalendario(calendario: Calendario)

    // ── Eventos ──

    @Insert
    suspend fun insertarEvento(evento: Evento): Long

    @Query("SELECT * FROM eventos WHERE id_calendario = :idCal")
    suspend fun obtenerEventosDeCalendario(idCal: Int): List<Evento>

    @Query("SELECT * FROM eventos WHERE id_evento = :id LIMIT 1")
    suspend fun obtenerEventoPorId(id: Int): Evento?

    // ── Grupos ──

    @Insert
    suspend fun insertarGrupo(unGrupo: Grupo): Long

    @Query("SELECT * FROM grupos WHERE id_admin = :idUsuario")
    suspend fun obtenerGruposDeUsuario(idUsuario: Int): List<Grupo>

    @Query("SELECT * FROM grupos WHERE id_grupo = :id LIMIT 1")
    suspend fun obtenerGrupoPorId(id: Int): Grupo?

    @Query("DELETE FROM grupos WHERE id_grupo = :grupoId")
    suspend fun eliminarGrupoPorId(grupoId: Int)

    // ── UsuarioGrupo ──

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuarioGrupo(usuarioGrupo: UsuarioGrupo)

    @Query("SELECT * FROM usuarios_grupos WHERE id_grupo = :idGrupo")
    suspend fun obtenerMiembrosDeGrupo(idGrupo: Int): List<UsuarioGrupo>

    @Query("SELECT EXISTS(SELECT 1 FROM usuarios_grupos WHERE id_usuario = :idUsuario AND id_grupo = :idGrupo)")
    suspend fun esMiembroDeGrupo(idUsuario: Int, idGrupo: Int): Boolean

    // ── Notificaciones ──

    @Insert
    suspend fun insertarNotificacion(notificacion: Notificacion): Long

    @Query("UPDATE usuarios SET notificaciones_activas = :activas WHERE id_usuario = :idUsuario")
    suspend fun actualizarNotificaciones(idUsuario: Int, activas: Boolean)

    @Query("SELECT * FROM notificaciones WHERE id_usuario = :idUsuario ORDER BY fecha_creacion DESC")
    suspend fun obtenerNotificacionesDeUsuario(idUsuario: Int): List<Notificacion>

    @Query("SELECT * FROM notificaciones WHERE id_usuario = :idUsuario AND leida = 0 ORDER BY fecha_creacion DESC")
    suspend fun obtenerNotificacionesNoLeidas(idUsuario: Int): List<Notificacion>

    @Query("SELECT COUNT(*) FROM notificaciones WHERE id_usuario = :idUsuario AND leida = 0")
    suspend fun contarNotificacionesNoLeidas(idUsuario: Int): Int

    @Query("UPDATE notificaciones SET leida = 1 WHERE id_notificacion = :idNotificacion")
    suspend fun marcarComoLeida(idNotificacion: Int)

    @Query("UPDATE notificaciones SET leida = 1 WHERE id_usuario = :idUsuario")
    suspend fun marcarTodasComoLeidas(idUsuario: Int)

    @Query("UPDATE notificaciones SET estado_invitacion = :estado WHERE id_notificacion = :idNotificacion")
    suspend fun actualizarEstadoInvitacion(idNotificacion: Int, estado: String)

    @Query("DELETE FROM notificaciones WHERE id_notificacion = :idNotificacion")
    suspend fun eliminarNotificacion(idNotificacion: Int)

    @Query("DELETE FROM notificaciones WHERE id_usuario = :idUsuario")
    suspend fun eliminarTodasNotificaciones(idUsuario: Int)
}
