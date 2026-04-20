package com.example.bocetocalendario1.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.datos.basedatos.AppDatabase
import com.example.bocetocalendario1.datos.modelo.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.bocetocalendario1.datos.modelo.Calendario
import kotlinx.coroutines.withContext

class RegistroActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etEmail: EditText
    private lateinit var etContrasena: EditText
    private lateinit var switchNotificaciones: Switch
    private lateinit var btnRegistrarse: Button
    private lateinit var tvIrLogin: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        val db = AppDatabase.getDatabase(this)
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
            val contrasena = BCrypt.withDefaults().hashToString(12,etContrasena.text.toString().toCharArray())
            val notificaciones = switchNotificaciones.isChecked

            if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Registro en la BD
            val unUsuario = Usuario(nombre = nombre, email = email, contrasena = contrasena, notificaciones_activas = notificaciones)            // ahpra simulo un registro exitoso

            lifecycleScope.launch(Dispatchers.IO) {
                db.appDao().insertarUsuario(unUsuario)
                val usuarioInsertado = db.appDao().verificarUsuario(email)
                if (usuarioInsertado != null) {
                    val calendarioPersonal = Calendario(
                        nombre = "Mi calendario",
                        tipo = "PERSONAL",
                        id_propietario = usuarioInsertado.id_usuario,
                        id_grupo = null
                    )
                    //db.appDao().insertarCalendario(calendarioPersonal)
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistroActivity, "Registro exitoso!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }

        //login
        tvIrLogin.setOnClickListener {
            finish() // pa atras
        }
    }
}