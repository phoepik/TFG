package com.example.bocetocalendario1.miniPantalla

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.activities.CrearGrupoActivity
import com.example.bocetocalendario1.activities.DetalleGrupoActivity
import com.example.bocetocalendario1.adaptadores.GrupoAdapter
import com.example.bocetocalendario1.datos.basedatos.AppDatabase
import com.example.bocetocalendario1.datos.modelo.Grupo
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GruposFragment : Fragment() {

    private lateinit var rvGrupos: RecyclerView
    private lateinit var btnNuevoGrupo: Button
    private lateinit var gestorSesion: GestorSesion


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_grupos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gestorSesion= GestorSesion(requireContext())
        rvGrupos = view.findViewById(R.id.rvGrupos)
        btnNuevoGrupo = view.findViewById(R.id.btnNuevoGrupo)
        // confirmacion RecyclerView
        rvGrupos.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
            val idUsuario = gestorSesion.obtenerIdUsuario() ?: 0
            val db = AppDatabase.getDatabase(requireContext())
            val gruposBD : List<Grupo> = db.appDao().obtenerGruposDeUsuario(idUsuario)


            withContext(Dispatchers.Main){
                rvGrupos.adapter = GrupoAdapter(gruposBD) { grupo ->
                    // detalles del grupo
                    val intent = Intent(context, DetalleGrupoActivity::class.java)
                    intent.putExtra("GRUPO_ID", grupo.id_grupo)
                    intent.putExtra("GRUPO_NOMBRE", grupo.nombre)
                    intent.putExtra("GRUPO_DESCRIPCION", grupo.descripcion)
                    startActivity(intent)
                }
            }
        }


        // boton de nuevo grupo
        btnNuevoGrupo.setOnClickListener {
            startActivity(Intent(context, CrearGrupoActivity::class.java))
        }
    }
}
