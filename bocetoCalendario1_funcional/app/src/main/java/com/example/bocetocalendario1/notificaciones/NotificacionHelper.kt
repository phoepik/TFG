package com.example.bocetocalendario1.notificaciones

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.bocetocalendario1.MainActivity
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.notificaciones.modelos.NotificacionesPayload
import com.example.bocetocalendario1.notificaciones.modelos.NotificacionesTipo

object NotificationHelper {

    fun show(context: Context, payload: NotificacionesPayload) {
        // Comprobar permiso en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                // Indicar qué tab abrir según el tipo
                putExtra("abrir_tab", when (payload.type) {
                    NotificacionesTipo.INVITATION -> "notificaciones"
                    NotificacionesTipo.REMINDER -> "inicio"
                    NotificacionesTipo.SYSTEM -> "notificaciones"
                })
                payload.extraData.forEach { (k, v) -> putExtra(k, v) }
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Icono según tipo
        val smallIcon = when (payload.type) {
            NotificacionesTipo.REMINDER -> android.R.drawable.ic_popup_reminder
            NotificacionesTipo.INVITATION -> android.R.drawable.ic_dialog_email
            NotificacionesTipo.SYSTEM -> android.R.drawable.ic_dialog_info
        }

        // Prioridad según tipo
        val priority = when (payload.type) {
            NotificacionesTipo.REMINDER -> NotificationCompat.PRIORITY_HIGH
            NotificacionesTipo.INVITATION -> NotificationCompat.PRIORITY_DEFAULT
            NotificacionesTipo.SYSTEM -> NotificationCompat.PRIORITY_LOW
        }

        val notification = NotificationCompat.Builder(context, payload.type.channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(payload.title)
            .setContentText(payload.body)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setPriority(priority)
            .build()

        NotificationManagerCompat.from(context).notify(generateId(payload), notification)
    }

    private fun generateId(payload: NotificacionesPayload): Int {
        return "${payload.type.name}_${System.currentTimeMillis()}".hashCode()
    }
}

