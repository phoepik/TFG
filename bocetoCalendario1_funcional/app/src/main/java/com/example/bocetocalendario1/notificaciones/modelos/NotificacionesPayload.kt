package com.example.bocetocalendario1.notificaciones.modelos

data class NotificacionesPayload (
    val type: NotificacionesTipo,
    val title: String,
    val body: String,
    val triggerAtMillis: Long? = null,   // null = inmediata
    val extraData: Map<String, String> = emptyMap() // para datos adicionales (userId, eventId, etc.)
)