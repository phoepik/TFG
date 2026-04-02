package com.example.bocetocalendario1.notificaciones.modelos

enum class NotificacionesTipo(val channelId: String) {
    REMINDER("channel_reminders"),
    INVITATION("channel_invitations"),
    SYSTEM("channel_system")
}