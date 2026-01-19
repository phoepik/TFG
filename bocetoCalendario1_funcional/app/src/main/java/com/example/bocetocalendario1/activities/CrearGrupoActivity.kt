package com.example.bocetocalendario1.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.datos.basedatos.AppDatabase
import com.example.bocetocalendario1.datos.modelo.Grupo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrearGrupoActivity : AppCompatActivity() {

    private lateinit var etNombreGrupo: EditText
    private lateinit var etDescripcionGrupo: EditText
    private lateinit var btnCrearGrupo: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)

        setContentView(R.layout.activity_crear_grupo)

        // inicializar vistas
        etNombreGrupo = findViewById(R.id.etNombreGrupo)
        etDescripcionGrupo = findViewById(R.id.etDescripcionGrupo)
        btnCrearGrupo = findViewById(R.id.btnCrearGrupo)
        btnCancelar = findViewById(R.id.btnCancelar)

        // botón Crear
        btnCrearGrupo.setOnClickListener {
            val nombre = etNombreGrupo.text.toString()
            val descripcion = etDescripcionGrupo.text.toString()



            if (nombre.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val unGrupo = Grupo(nombre = nombre, descripcion = descripcion, id_admin = 1)

            //GUARDADO EN LA BASE DE DATOS
            lifecycleScope.launch(Dispatchers.IO) {
                db.appDao().insertarGrupo(unGrupo)
            }
            Toast.makeText(this, "Grupo creado!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // cancelar
        btnCancelar.setOnClickListener {
            finish()
        }
    }
}
