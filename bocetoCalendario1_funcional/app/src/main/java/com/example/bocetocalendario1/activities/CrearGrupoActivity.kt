package com.example.bocetocalendario1.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bocetocalendario1.R

class CrearGrupoActivity : AppCompatActivity() {

    private lateinit var etNombreGrupo: EditText
    private lateinit var etDescripcionGrupo: EditText
    private lateinit var btnCrearGrupo: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_grupo)

        // inicializar vistas
        etNombreGrupo = findViewById(R.id.etNombreGrupo)
        etDescripcionGrupo = findViewById(R.id.etDescripcionGrupo)
        btnCrearGrupo = findViewById(R.id.btnCrearGrupo)
        btnCancelar = findViewById(R.id.btnCancelar)

        // botón Crear
        btnCrearGrupo.setOnClickListener {
            val nombre = etNombreGrupo.text.toString()
            
            if (nombre.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // RICARDO->>>>>>>>>> AQUI VA GUARDADO EN LA BASE DE DATOS
            Toast.makeText(this, "Grupo creado!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // cancelar
        btnCancelar.setOnClickListener {
            finish()
        }
    }
}
