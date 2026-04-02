package com.example.bocetocalendario1.notificaciones

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.bocetocalendario1.MainActivity
import com.example.bocetocalendario1.notificaciones.modelos.NotificacionesPayload

object NotificationHelper {

    fun show(context: Context, payload: NotificacionesPayload) {
        // Intent que abre la app al pulsar la notificación
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                payload.extraData.forEach { (k, v) -> putExtra(k, v) }
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, payload.type.channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(payload.title)
            .setContentText(payload.body)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(generateId(payload), notification)
    }

    // ID único por tipo + timestamp para evitar que notificaciones del mismo tipo se sobreescriban
    private fun generateId(payload: NotificacionesPayload): Int {
        return "${payload.type.name}_${System.currentTimeMillis()}".hashCode()
    }
}
