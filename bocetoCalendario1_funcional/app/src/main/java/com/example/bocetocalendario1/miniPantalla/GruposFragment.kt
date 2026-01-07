package com.example.bocetocalendario1.miniPantalla

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.activities.CrearGrupoActivity
import com.example.bocetocalendario1.activities.DetalleGrupoActivity
import com.example.bocetocalendario1.adaptadores.GrupoAdapter
import com.example.bocetocalendario1.models.Grupo

class GruposFragment : Fragment() {

    private lateinit var rvGrupos: RecyclerView
    private lateinit var btnNuevoGrupo: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_grupos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvGrupos = view.findViewById(R.id.rvGrupos)
        btnNuevoGrupo = view.findViewById(R.id.btnNuevoGrupo)

        // datos de ejemplo
        val gruposEjemplo = listOf(
            Grupo(1, "Trabajo DAM", "Grupo de clase de desarrollo", 1, 5),
            Grupo(2, "Familia", "Eventos familiares", 1, 8),
            Grupo(3, "Amigos", "Quedadas y planes", 2, 12)
        )

        // confirmacion RecyclerView
        rvGrupos.layoutManager = LinearLayoutManager(context)
        rvGrupos.adapter = GrupoAdapter(gruposEjemplo) { grupo ->
            // detalles del grupo
            val intent = Intent(context, DetalleGrupoActivity::class.java)
            intent.putExtra("GRUPO_ID", grupo.id)
            intent.putExtra("GRUPO_NOMBRE", grupo.nombre)
            intent.putExtra("GRUPO_DESCRIPCION", grupo.descripcion)
            startActivity(intent)
        }

        // boton de nuevo grupo
        btnNuevoGrupo.setOnClickListener {
            startActivity(Intent(context, CrearGrupoActivity::class.java))
        }
    }
}
