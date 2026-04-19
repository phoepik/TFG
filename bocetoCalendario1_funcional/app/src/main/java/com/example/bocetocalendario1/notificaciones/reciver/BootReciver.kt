package com.example.bocetocalendario1.notificaciones.reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.bocetocalendario1.datos.basedatos.AppDatabase
import com.example.bocetocalendario1.notificaciones.modelos.NotificacionesPayload
import com.example.bocetocalendario1.notificaciones.modelos.NotificacionesTipo
import com.example.bocetocalendario1.notificaciones.programadas.RecordatorioProgramadas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Tras reinicio del dispositivo, las alarmas de AlarmManager se pierden.
 * Este receiver las reprograma consultando las notificaciones de tipo RECORDATORIO
 * que aún no se han leído y cuyo evento no ha pasado.
 */
class BootReciver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                // Obtener todos los usuarios (simplificación; en producción se haría por usuario activo)
                val usuarios = db.appDao().obtenerUsuarios()
                val ahora = System.currentTimeMillis()

                for (usuario in usuarios) {
                    val notificaciones = db.appDao().obtenerNotificacionesDeUsuario(usuario.id_usuario)
                    for (notif in notificaciones) {
                        if (notif.tipo != "RECORDATORIO") continue
                        if (notif.leida) continue
                        if (notif.tiempo_anticipacion == null) continue
                        if (notif.id_evento == null) continue

                        val evento = db.appDao().obtenerEventoPorId(notif.id_evento) ?: continue

                        // Parsear fecha del evento (formato dd/MM/yyyy HH:mm)
                        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                        val fechaEvento = try { sdf.parse(evento.fecha_inicio)?.time ?: continue } catch (_: Exception) { continue }
                        val triggerAt = fechaEvento - (notif.tiempo_anticipacion * 60 * 1000L)

                        if (triggerAt > ahora) {
                            val payload = NotificacionesPayload(
                                type = NotificacionesTipo.REMINDER,
                                title = notif.titulo,
                                body = notif.mensaje ?: "",
                                triggerAtMillis = triggerAt
                            )
                            RecordatorioProgramadas.schedule(context, payload, notif.id_notificacion)
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
