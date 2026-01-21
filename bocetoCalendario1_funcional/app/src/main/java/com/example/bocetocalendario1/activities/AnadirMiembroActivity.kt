package com.example.bocetocalendario1.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bocetocalendario1.R

class AnadirMiembroActivity : AppCompatActivity() {

    private lateinit var etIdUsuario: EditText
    private lateinit var btnAnadir: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anadir_miembro)

        val grupoId = intent.getIntExtra("GRUPO_ID", 0)

        etIdUsuario = findViewById(R.id.etIdUsuario)
        btnAnadir = findViewById(R.id.btnAnadir)
        btnCancelar = findViewById(R.id.btnCancelar)

        btnAnadir.setOnClickListener {
            val idUsuario = etIdUsuario.text.toString()

            if (idUsuario.isEmpty()) {
                Toast.makeText(this, "Introduce el ID del usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Aquí se conectará con la BD para añadir el miembro
            Toast.makeText(this, "Miembro añadido", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}