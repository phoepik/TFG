package com.example.bocetocalendario1.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.adaptadores.MiembroAdapter
import com.example.bocetocalendario1.models.Usuario

class DetalleGrupoActivity : AppCompatActivity() {

    private lateinit var tvNombreGrupo: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var rvMiembros: RecyclerView
    private lateinit var btnAnadirMiembro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_grupo)

        // inicializar vistas
        tvNombreGrupo = findViewById(R.id.tvNombreGrupo)
        tvDescripcion = findViewById(R.id.tvDescripcion)
        rvMiembros = findViewById(R.id.rvMiembros)
        btnAnadirMiembro = findViewById(R.id.btnAnadirMiembro)

        // Obtener datos del intent
        val grupoId = intent.getIntExtra("GRUPO_ID", 0)
        val grupoNombre = intent.getStringExtra("GRUPO_NOMBRE") ?: "Grupo"
        val grupoDescripcion = intent.getStringExtra("GRUPO_DESCRIPCION") ?: ""

        tvNombreGrupo.text = grupoNombre
        tvDescripcion.text = grupoDescripcion

        // Miembros de ejemplo (después vendrán de la BD)
        val miembrosEjemplo = listOf(
            Usuario(1, "Tú (Admin)", "tu@email.com", "", true),
            Usuario(2, "Paco porras", "paquito@email.com", "", true),
            Usuario(3, "Ricardo", "ricardo@email.com", "", false),
            Usuario(4, "Jorge", "jorge@email.com", "", true)
        )

        // Configuracion de recicled view
        rvMiembros.layoutManager = LinearLayoutManager(this)
        rvMiembros.adapter = MiembroAdapter(miembrosEjemplo, 1) // 1 = id del admin

        //  RICARDO->>>>>>>>> añadir miembro
        btnAnadirMiembro.setOnClickListener {
            val intent = Intent(this, AnadirMiembroActivity::class.java)
            intent.putExtra("GRUPO_ID", grupoId)
            startActivity(intent)
        }
    }
}