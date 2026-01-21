package com.example.bocetocalendario1.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.bocetocalendario1.MainActivity
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.datos.basedatos.AppDatabase
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvIrRegistro: TextView

    private var idCuenta: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val db = AppDatabase.getDatabase(this)
        val gestorSesion = GestorSesion(this)

        if(gestorSesion.estaLogueado()){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()    // para que no puedas volver a esta pestaña echando hacia atrás
            return  // sale de la actividad
        }

        etEmail = findViewById(R.id.etEmail)
        etContrasena = findViewById(R.id.etContrasena)
        btnLogin = findViewById(R.id.btnLogin)
        tvIrRegistro = findViewById(R.id.tvIrRegistro)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val usuarioEncontrado = db.appDao().verificarUsuario(email)
                    idCuenta = usuarioEncontrado?.id_usuario ?: 0
                    val res= BCrypt.verifyer().verify(contrasena.toCharArray(),
                        usuarioEncontrado?.contrasena
                    )
                    withContext(Dispatchers.Main) {
                        if (usuarioEncontrado != null && res.verified) {
                            Toast.makeText(this@LoginActivity, "¡Bienvenido ${usuarioEncontrado.nombre}!", Toast.LENGTH_SHORT).show()
                            gestorSesion.guardarSesion(usuarioEncontrado)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@LoginActivity, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("DB_PRUEBA", "Error: ${e.message}")
                        Toast.makeText(this@LoginActivity, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Link a registro
        tvIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }
}
