package com.example.bocetocalendario1.miniPantalla

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import java.util.logging.Handler

class MyService : Service() {
    private var serviceLooper: Looper? = null

    private var serviceHandler: Handler? = null

    private inner class ServiceHandeler(looper: Looper): Handler

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}