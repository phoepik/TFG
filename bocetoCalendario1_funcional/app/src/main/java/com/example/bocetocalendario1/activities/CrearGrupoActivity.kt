package com.example.bocetocalendario1.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bocetocalendario1.R
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

        etNombreGrupo = findViewById(R.id.etNombreGrupo)
        etDescripcionGrupo = findViewById(R.id.etDescripcionGrupo)
        btnCrearGrupo = findViewById(R.id.btnCrearGrupo)
        btnCancelar = findViewById(R.id.btnCancelar)

        btnCrearGrupo.setOnClickListener {
            val nombre = etNombreGrupo.text.toString()
            val descripcion = etDescripcionGrupo.text.toString()

            if (nombre.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val grupoRequest = GrupoResponse(
                nombre = nombre,
                descripcion = descripcion,
                idAdmin = gestorSesion.obtenerIdUsuario()
            )

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = RetrofitClient.api.crearGrupo(grupoRequest)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@CrearGrupoActivity, "Grupo creado!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
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

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}