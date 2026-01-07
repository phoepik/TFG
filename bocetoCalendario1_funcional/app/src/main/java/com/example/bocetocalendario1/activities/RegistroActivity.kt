package com.example.bocetocalendario1.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bocetocalendario1.MainActivity
import com.example.bocetocalendario1.R

class RegistroActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etEmail: EditText
    private lateinit var etContrasena: EditText
    private lateinit var switchNotificaciones: Switch
    private lateinit var btnRegistrarse: Button
    private lateinit var tvIrLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etContrasena = findViewById(R.id.etContrasena)
        switchNotificaciones = findViewById(R.id.switchNotificaciones)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)
        tvIrLogin = findViewById(R.id.tvIrLogin)

        // Botón Registrarse
        btnRegistrarse.setOnClickListener {
            val nombre = etNombre.text.toString()
            val email = etEmail.text.toString()
            val contrasena = etContrasena.text.toString()
            val notificaciones = switchNotificaciones.isChecked

            if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // RICARDO->>>>>>>Aqui ira el registro en la BD
            // ahpra simulo un registro exitoso
            Toast.makeText(this, "Registro exitoso!", Toast.LENGTH_SHORT).show()
            
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        //login
        tvIrLogin.setOnClickListener {
            finish() // pa atras
        }
    }
}
