package com.example.bocetocalendario1.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.network.CalendarioResponse
import com.example.bocetocalendario1.network.GrupoResponse
import com.example.bocetocalendario1.network.RetrofitClient
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearGrupoActivity : AppCompatActivity() {

    private lateinit var etNombreGrupo: EditText
    private lateinit var etDescripcionGrupo: EditText
    private lateinit var btnCrearGrupo: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gestorSesion = GestorSesion(this)
        setContentView(R.layout.activity_crear_grupo)

        etNombreGrupo    = findViewById(R.id.etNombreGrupo)
        etDescripcionGrupo = findViewById(R.id.etDescripcionGrupo)
        btnCrearGrupo    = findViewById(R.id.btnCrearGrupo)
        btnCancelar      = findViewById(R.id.btnCancelar)

        btnCrearGrupo.setOnClickListener {
            val nombre      = etNombreGrupo.text.toString().trim()
            val descripcion = etDescripcionGrupo.text.toString().trim()

            if (nombre.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idAdmin = gestorSesion.obtenerIdUsuario()

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val grupoResp = RetrofitClient.api.crearGrupo(
                        GrupoResponse(nombre = nombre, descripcion = descripcion, idAdmin = idAdmin)
                    )

                    if (grupoResp.isSuccessful && grupoResp.body() != null) {
                        val idGrupo = grupoResp.body()!!.idGrupo

                        // Crear calendario grupal automáticamente
                        try {
                            RetrofitClient.api.crearCalendario(
                                CalendarioResponse(
                                    nombre        = nombre,
                                    tipo          = "GRUPAL",
                                    idPropietario = idAdmin,
                                    idGrupo       = idGrupo
                                )
                            )
                        } catch (e: Exception) {
                            Log.e("GRUPO", "Error creando calendario grupal: ${e.message}")
                        }

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@CrearGrupoActivity, "Grupo creado!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@CrearGrupoActivity, "Error al crear grupo", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("GRUPO", "Error: ${e.message}")
                        Toast.makeText(this@CrearGrupoActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnCancelar.setOnClickListener { finish() }
    }
}