package com.example.bocetocalendario1.notificaciones

import android.content.Context
import com.example.bocetocalendario1.datos.basedatos.AppDatabase
import com.example.bocetocalendario1.datos.modelo.Notificacion
import com.example.bocetocalendario1.network.RetrofitClient
import com.example.bocetocalendario1.notificaciones.modelos.NotificacionesPayload
import com.example.bocetocalendario1.notificaciones.modelos.NotificacionesTipo
import com.example.bocetocalendario1.notificaciones.programadas.RecordatorioProgramadas

object NotificacionService {

    suspend fun enviarInvitacionGrupo(
        context: Context,
        idUsuarioDestino: Int,
        nombreGrupo: String,
        idGrupo: Int,
        nombreQuienInvita: String
    ) {
        val db = AppDatabase.getDatabase(context)

        val notificacion = Notificacion(
            titulo = "Invitación a grupo",
            mensaje = "$nombreQuienInvita te ha invitado al grupo \"$nombreGrupo\"",
            tiempo_anticipacion = null,
            tipo = "INVITACION",
            id_evento = null,
            id_usuario = idUsuarioDestino,
            id_grupo_invitacion = idGrupo,
            estado_invitacion = "PENDIENTE"
        )
        db.appDao().insertarNotificacion(notificacion)

        val payload = NotificacionesPayload(
            type = NotificacionesTipo.INVITATION,
            title = "Invitación a grupo",
            body = "$nombreQuienInvita te ha invitado al grupo \"$nombreGrupo\"",
            extraData = mapOf(
                "id_grupo" to idGrupo.toString(),
                "id_usuario_origen" to nombreQuienInvita
            )
        )
        NotificationHelper.show(context, payload)
    }

    suspend fun programarRecordatorioEvento(
        context: Context,
        idUsuario: Int,
        idEvento: Int,
        tituloEvento: String,
        descripcionEvento: String?,
        fechaEventoMillis: Long,
        minutosAntes: Int
    ) {
        val db = AppDatabase.getDatabase(context)
        val triggerAt = fechaEventoMillis - (minutosAntes * 60 * 1000L)

        val notificacion = Notificacion(
            titulo = "Recordatorio: $tituloEvento",
            mensaje = descripcionEvento ?: "Tienes un evento próximamente",
            tiempo_anticipacion = minutosAntes,
            tipo = "RECORDATORIO",
            id_evento = idEvento,
            id_usuario = idUsuario,
            trigger_at_millis = triggerAt
        )
        val idNotif = db.appDao().insertarNotificacion(notificacion)

        if (triggerAt > System.currentTimeMillis()) {
            val payload = NotificacionesPayload(
                type = NotificacionesTipo.REMINDER,
                title = "Recordatorio: $tituloEvento",
                body = descripcionEvento ?: "Tienes un evento próximamente",
                triggerAtMillis = triggerAt,
                extraData = mapOf("id_evento" to idEvento.toString())
            )
            RecordatorioProgramadas.schedule(context, payload, idNotif.toInt())
        } else {
            val payload = NotificacionesPayload(
                type = NotificacionesTipo.REMINDER,
                title = "Recordatorio: $tituloEvento",
                body = descripcionEvento ?: "Tienes un evento próximamente"
            )
            NotificationHelper.show(context, payload)
        }
    }

    suspend fun enviarInvitacionEvento(
        context: Context,
        idUsuarioDestino: Int,
        idEvento: Int,
        tituloEvento: String,
        nombreQuienInvita: String
    ) {
        val db = AppDatabase.getDatabase(context)

        val notificacion = Notificacion(
            titulo = "Invitación a evento",
            mensaje = "$nombreQuienInvita te ha invitado al evento \"$tituloEvento\"",
            tiempo_anticipacion = null,
            tipo = "INVITACION",
            id_evento = idEvento,
            id_usuario = idUsuarioDestino,
            estado_invitacion = "PENDIENTE"
        )
        db.appDao().insertarNotificacion(notificacion)

        val payload = NotificacionesPayload(
            type = NotificacionesTipo.INVITATION,
            title = "Invitación a evento",
            body = "$nombreQuienInvita te ha invitado al evento \"$tituloEvento\"",
            extraData = mapOf("id_evento" to idEvento.toString())
        )
        NotificationHelper.show(context, payload)
    }

    suspend fun enviarNotificacionSistema(
        context: Context,
        idUsuario: Int,
        titulo: String,
        mensaje: String
    ) {
        val db = AppDatabase.getDatabase(context)

        val notificacion = Notificacion(
            titulo = titulo,
            mensaje = mensaje,
            tiempo_anticipacion = null,
            tipo = "SISTEMA",
            id_evento = null,
            id_usuario = idUsuario
        )
        db.appDao().insertarNotificacion(notificacion)

        val payload = NotificacionesPayload(
            type = NotificacionesTipo.SYSTEM,
            title = titulo,
            body = mensaje
        )
        NotificationHelper.show(context, payload)
    }

    suspend fun aceptarInvitacion(context: Context, notificacion: Notificacion) {
        val db = AppDatabase.getDatabase(context)
        db.appDao().actualizarEstadoInvitacion(notificacion.id_notificacion, "ACEPTADA")
        db.appDao().marcarComoLeida(notificacion.id_notificacion)

        // Añadir al grupo via servidor
        notificacion.id_grupo_invitacion?.let { idGrupo ->
            try {
                RetrofitClient.api.anadirMiembro(
                    mapOf("idUsuario" to notificacion.id_usuario, "idGrupo" to idGrupo)
                )
            } catch (_: Exception) {
                // Si falla la red, la invitación queda aceptada localmente
            }
        }
    }

    suspend fun rechazarInvitacion(context: Context, notificacion: Notificacion) {
        val db = AppDatabase.getDatabase(context)
        db.appDao().actualizarEstadoInvitacion(notificacion.id_notificacion, "RECHAZADA")
        db.appDao().marcarComoLeida(notificacion.id_notificacion)
    }
}