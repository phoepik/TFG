package com.example.bocetocalendario1.notificaciones.canales

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.bocetocalendario1.notificaciones.modelos.NotificacionesTipo

object  NotificacionesManagerCanales {
    fun createAll(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)

        val channels = listOf(
            NotificationChannel(
                NotificacionesTipo.REMINDER.channelId,
                "Recordatorios",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios de eventos y citas"
                enableVibration(true)
            },

            NotificationChannel(
                NotificacionesTipo.INVITATION.channelId,
                "Invitaciones",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Invitaciones de amigos y grupos"
            },

            NotificationChannel(
                NotificacionesTipo.SYSTEM.channelId,
                "Sistema",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificaciones internas de la aplicación"
            }
        )

        channels.forEach { manager.createNotificationChannel(it) }
    }
}

