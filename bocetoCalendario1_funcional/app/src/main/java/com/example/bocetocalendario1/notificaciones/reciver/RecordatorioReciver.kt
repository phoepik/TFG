package com.example.bocetocalendario1.notificaciones.reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.bocetocalendario1.notificaciones.NotificationHelper
import com.example.bocetocalendario1.notificaciones.modelos.NotificacionesPayload
import com.example.bocetocalendario1.notificaciones.modelos.NotificacionesTipo

class RecordatorioReciver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: return
        val body = intent.getStringExtra(EXTRA_BODY) ?: ""

        val payload = NotificacionesPayload(
            type = NotificacionesTipo.REMINDER,
            title = title,
            body = body
        )

        NotificationHelper.show(context, payload)
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
    }
}