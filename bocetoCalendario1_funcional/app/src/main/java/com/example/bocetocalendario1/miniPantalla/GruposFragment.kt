package com.example.bocetocalendario1.miniPantalla

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.activities.CrearGrupoActivity
import com.example.bocetocalendario1.activities.DetalleGrupoActivity
import com.example.bocetocalendario1.adaptadores.GrupoAdapter
import com.example.bocetocalendario1.datos.modelo.Grupo
import com.example.bocetocalendario1.network.RetrofitClient
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

        gestorSesion = GestorSesion(requireContext())
        rvGrupos = view.findViewById(R.id.rvGrupos)
        btnNuevoGrupo = view.findViewById(R.id.btnNuevoGrupo)
        rvGrupos.layoutManager = LinearLayoutManager(context)

        cargarGrupos()

        btnNuevoGrupo.setOnClickListener {
            startActivity(Intent(context, CrearGrupoActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarGrupos()
    }

    private fun cargarGrupos() {
        val idUsuario = gestorSesion.obtenerIdUsuario() ?: return

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.obtenerGruposDeUsuario(idUsuario)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val gruposServidor = response.body()!!
                        // Convertir GrupoResponse a datos.modelo.Grupo para el adaptador
                        val grupos = gruposServidor.map { g ->
                            Grupo(
                                id_grupo = g.idGrupo ?: 0,
                                nombre = g.nombre,
                                descripcion = g.descripcion,
                                id_admin = g.idAdmin
                            )
                        }
                        rvGrupos.adapter = GrupoAdapter(grupos) { grupo ->
                            val intent = Intent(context, DetalleGrupoActivity::class.java)
                            intent.putExtra("GRUPO_ID", grupo.id_grupo)
                            intent.putExtra("GRUPO_NOMBRE", grupo.nombre)
                            intent.putExtra("GRUPO_DESCRIPCION", grupo.descripcion)
                            startActivity(intent)
                        }
                    } else {
                        rvGrupos.adapter = GrupoAdapter(emptyList()) { }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("GRUPOS", "Error: ${e.message}")
                    Toast.makeText(context, "Error al cargar grupos", Toast.LENGTH_SHORT).show()
                    rvGrupos.adapter = GrupoAdapter(emptyList()) { }
                }
            }
        }
    }
}