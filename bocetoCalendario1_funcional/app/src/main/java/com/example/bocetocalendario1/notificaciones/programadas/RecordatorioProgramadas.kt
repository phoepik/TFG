package com.example.bocetocalendario1.notificaciones.programadas

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.bocetocalendario1.notificaciones.modelos.NotificacionesPayload
import com.example.bocetocalendario1.notificaciones.reciver.RecordatorioReciver

object RecordatorioProgramadas {
    fun schedule(context: Context, payload: NotificacionesPayload, requestCode: Int) {
    val triggerAt = payload.triggerAtMillis ?: return

    val intent = Intent(context, RecordatorioReciver::class.java).apply {
        putExtra(RecordatorioReciver.EXTRA_TITLE, payload.title)
        putExtra(RecordatorioReciver.EXTRA_BODY, payload.body)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(AlarmManager::class.java)
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerAt,
        pendingIntent
    )
}

    fun cancel(context: Context, requestCode: Int) {
        val intent = Intent(context, RecordatorioReciver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        context.getSystemService(AlarmManager::class.java).cancel(pendingIntent)
    }
}
