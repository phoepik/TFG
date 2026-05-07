package com.example.bocetocalendario1.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bocetocalendario1.MainActivity
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.datos.modelo.Usuario
import com.example.bocetocalendario1.network.LoginRequest
import com.example.bocetocalendario1.network.RetrofitClient
import com.example.bocetocalendario1.notificaciones.canales.NotificacionesManagerCanales
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvIrRegistro: TextView
    private lateinit var btnTogglePassword: TextView
    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificacionesManagerCanales.createAll(this)

        setContentView(R.layout.activity_login)

        val gestorSesion = GestorSesion(this)

        if (gestorSesion.estaLogueado()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        etEmail = findViewById(R.id.etEmail)
        etContrasena = findViewById(R.id.etContrasena)
        btnLogin = findViewById(R.id.btnLogin)
        tvIrRegistro = findViewById(R.id.tvIrRegistro)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)

        // Back arrow
        val btnBack = findViewById<TextView>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        // Toggle password visibility
        btnTogglePassword.setOnClickListener {
            passwordVisible = !passwordVisible
            if (passwordVisible) {
                etContrasena.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnTogglePassword.text = "🙈"
            } else {
                etContrasena.transformationMethod = PasswordTransformationMethod.getInstance()
                btnTogglePassword.text = "👁"
            }
            etContrasena.setSelection(etContrasena.text.length)
        }

        // Forgot password
        val tvOlvide = findViewById<TextView>(R.id.tvOlvideContrasena)
        tvOlvide.setOnClickListener {
            ForgotPasswordBottomSheet().show(supportFragmentManager, "ForgotPassword")
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = RetrofitClient.api.login(LoginRequest(email, contrasena))

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body() != null) {
                            val u = response.body()!!
                            val usuarioLocal = Usuario(
                                id_usuario = u.idUsuario,
                                nombre = u.nombre,
                                email = u.email,
                                contrasena = u.contrasena,
                                notificaciones_activas = u.notificacionesActivas
                            )
                            gestorSesion.guardarSesion(usuarioLocal)
                            Toast.makeText(this@LoginActivity, "Bienvenido ${u.nombre}!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@LoginActivity, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("LOGIN", "Error: ${e.message}")
                        Toast.makeText(this@LoginActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        tvIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }
}
