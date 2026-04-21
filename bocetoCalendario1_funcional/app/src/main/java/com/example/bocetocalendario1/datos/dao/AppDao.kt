package com.example.bocetocalendario1.datos.dao

import androidx.room.*
import com.example.bocetocalendario1.datos.modelo.Notificacion

// Room solo gestiona notificaciones locales.
// El resto de datos (usuarios, eventos, grupos) se obtienen vía Retrofit.
@Dao
interface AppDao {

    // ── Notificaciones ──

    @Insert
    suspend fun insertarNotificacion(notificacion: Notificacion): Long

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

    // Para BootReciver: obtener recordatorios futuros pendientes
    @Query("SELECT * FROM notificaciones WHERE tipo = 'RECORDATORIO' AND leida = 0 AND trigger_at_millis > :ahora")
    suspend fun obtenerRecordatoriosPendientes(ahora: Long): List<Notificacion>
}
