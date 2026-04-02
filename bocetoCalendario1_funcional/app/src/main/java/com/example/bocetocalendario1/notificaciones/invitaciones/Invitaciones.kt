package com.example.bocetocalendario1.notificaciones.invitaciones

import android.content.Context
import com.example.bocetocalendario1.notificaciones.NotificationHelper
import com.example.bocetocalendario1.notificaciones.modelos.NotificacionesPayload
import com.example.bocetocalendario1.notificaciones.modelos.NotificacionesTipo

class Invitaciones (
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
        val body = inputData.getString(KEY_BODY) ?: ""

        val payload = NotificacionesPayload(
            type = NotificacionesTipo.INVITATION,
            title = title,
            body = body,
            extraData = mapOf(
                "senderId" to (inputData.getString(KEY_SENDER_ID) ?: "")
            )
        )

        NotificationHelper.show(context, payload)
        return Result.success()
    }

    companion object {
        const val KEY_TITLE = "key_title"
        const val KEY_BODY = "key_body"
        const val KEY_SENDER_ID = "key_sender_id"
    }
}