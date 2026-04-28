package com.example.bocetocalendario1.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.utilidades.GestorSesion

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Si ya hay sesión activa, ir directamente a MainActivity
        val gestorSesion = GestorSesion(this)
        if (gestorSesion.estaLogueado()) {
            startActivity(Intent(this, com.example.bocetocalendario1.MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_splash)

        findViewById<Button>(R.id.btnCrearCuenta).setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        findViewById<Button>(R.id.btnYaTengoCuenta).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
