package com.example.bocetocalendario1.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.network.CalendarioResponse
import com.example.bocetocalendario1.network.RegistroRequest
import com.example.bocetocalendario1.utilidades.PasswordUtils
import com.example.bocetocalendario1.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistroActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etEmail: EditText
    private lateinit var etContrasena: EditText
    private lateinit var switchNotificaciones: Switch
    private lateinit var checkTerminos: CheckBox
    private lateinit var btnRegistrarse: Button
    private lateinit var btnSiguienteStep: Button
    private lateinit var tvIrLogin: TextView
    private lateinit var layoutStep1: LinearLayout
    private lateinit var layoutStep2: LinearLayout
    private lateinit var progressStep1: View
    private lateinit var progressStep2: View
    private lateinit var btnBack: TextView
    private lateinit var btnTogglePasswordReg: TextView

    private var pasoActual = 1
    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        etNombre           = findViewById(R.id.etNombre)
        etEmail            = findViewById(R.id.etEmail)
        etContrasena       = findViewById(R.id.etContrasena)
        switchNotificaciones = findViewById(R.id.switchNotificaciones)
        checkTerminos      = findViewById(R.id.checkTerminos)
        btnRegistrarse     = findViewById(R.id.btnRegistrarse)
        btnSiguienteStep   = findViewById(R.id.btnSiguienteStep)
        tvIrLogin          = findViewById(R.id.tvIrLogin)
        layoutStep1        = findViewById(R.id.layoutStep1)
        layoutStep2        = findViewById(R.id.layoutStep2)
        progressStep1      = findViewById(R.id.progressStep1)
        progressStep2      = findViewById(R.id.progressStep2)
        btnBack            = findViewById(R.id.btnBack)
        btnTogglePasswordReg = findViewById(R.id.btnTogglePasswordReg)

        btnBack.setOnClickListener {
            if (pasoActual == 2) irAPaso(1) else finish()
        }

        btnTogglePasswordReg.setOnClickListener {
            passwordVisible = !passwordVisible
            etContrasena.transformationMethod =
                if (passwordVisible) HideReturnsTransformationMethod.getInstance()
                else PasswordTransformationMethod.getInstance()
            btnTogglePasswordReg.text = if (passwordVisible) "🙈" else "👁"
            etContrasena.setSelection(etContrasena.text.length)
        }

        btnSiguienteStep.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val email  = etEmail.text.toString().trim()
            if (nombre.isEmpty()) {
                Toast.makeText(this, "Introduce tu nombre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Introduce un email válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            irAPaso(2)
        }

        btnRegistrarse.setOnClickListener {
            val nombre     = etNombre.text.toString().trim()
            val email      = etEmail.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (contrasena.length < 6) {
                Toast.makeText(this, "La contraseña debe tener mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!checkTerminos.isChecked) {
                Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = RetrofitClient.api.registro(RegistroRequest(nombre, email, PasswordUtils.hash(contrasena)))

                    if (response.isSuccessful && response.body() != null) {
                        val usuario = response.body()!!
                        val idUsuario = usuario.idUsuario

                        // Crear calendario personal automáticamente
                        try {
                            RetrofitClient.api.crearCalendario(
                                CalendarioResponse(
                                    nombre       = "Mi calendario",
                                    tipo         = "PERSONAL",
                                    idPropietario = idUsuario,
                                    idGrupo      = null
                                )
                            )
                        } catch (e: Exception) {
                            Log.e("REGISTRO", "Error creando calendario personal: ${e.message}")
                        }

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegistroActivity, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegistroActivity, "El email ya está registrado", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("REGISTRO", "Error: ${e.message}")
                        Toast.makeText(this@RegistroActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        tvIrLogin.setOnClickListener { finish() }
    }

    private fun irAPaso(paso: Int) {
        pasoActual = paso
        if (paso == 1) {
            layoutStep1.visibility    = View.VISIBLE
            layoutStep2.visibility    = View.GONE
            btnSiguienteStep.visibility = View.VISIBLE
            btnRegistrarse.visibility = View.GONE
            progressStep1.setBackgroundResource(R.drawable.bg_btn_primary)
            progressStep2.setBackgroundColor(getColor(R.color.line))
        } else {
            layoutStep1.visibility    = View.GONE
            layoutStep2.visibility    = View.VISIBLE
            btnSiguienteStep.visibility = View.GONE
            btnRegistrarse.visibility = View.VISIBLE
            progressStep1.setBackgroundResource(R.drawable.bg_btn_primary)
            progressStep2.setBackgroundResource(R.drawable.bg_btn_primary)
        }
    }
}