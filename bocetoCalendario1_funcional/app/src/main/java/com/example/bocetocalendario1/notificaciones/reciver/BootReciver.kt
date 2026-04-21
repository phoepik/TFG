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

class BootReciver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val ahora = System.currentTimeMillis()
                val pendientes = db.appDao().obtenerRecordatoriosPendientes(ahora)

                for (notif in pendientes) {
                    val triggerAt = notif.trigger_at_millis ?: continue

                    val payload = NotificacionesPayload(
                        type = NotificacionesTipo.REMINDER,
                        title = notif.titulo,
                        body = notif.mensaje ?: "",
                        triggerAtMillis = triggerAt
                    )
                    RecordatorioProgramadas.schedule(context, payload, notif.id_notificacion)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}