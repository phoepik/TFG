package com.example.bocetocalendario1.miniPantalla

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import android.os.Message

class MyService : Service() {

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    // Handler que procesa los mensajes en el hilo del servicio
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            // Lógica del servicio en segundo plano (pendiente de implementar)
            stopSelf(msg.arg1)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}