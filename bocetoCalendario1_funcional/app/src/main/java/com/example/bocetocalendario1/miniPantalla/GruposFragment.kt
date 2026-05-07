package com.example.bocetocalendario1.miniPantalla

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.activities.CreateGroupBottomSheet
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
    private lateinit var btnNuevoGrupo: TextView
    private lateinit var etBuscarGrupos: EditText
    private lateinit var gestorSesion: GestorSesion

    private var todosGrupos: List<Grupo> = emptyList()

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
        etBuscarGrupos = view.findViewById(R.id.etBuscarGrupos)
        rvGrupos.layoutManager = LinearLayoutManager(context)

        // Search filter
        etBuscarGrupos.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim()?.lowercase() ?: ""
                val filtrados = if (query.isEmpty()) todosGrupos
                else todosGrupos.filter { it.nombre.lowercase().contains(query) }
                mostrarGrupos(filtrados)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cargarGrupos()

        btnNuevoGrupo.setOnClickListener {
            CreateGroupBottomSheet().show(parentFragmentManager, "CreateGroup")
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
                        todosGrupos = gruposServidor.mapIndexed { idx, g ->
                            Grupo(
                                id_grupo = g.idGrupo ?: 0,
                                nombre = g.nombre,
                                descripcion = g.descripcion,
                                id_admin = g.idAdmin
                            )
                        }
                        mostrarGrupos(todosGrupos)
                    } else {
                        todosGrupos = emptyList()
                        mostrarGrupos(emptyList())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("GRUPOS", "Error: ${e.message}")
                    Toast.makeText(context, "Error al cargar grupos", Toast.LENGTH_SHORT).show()
                    todosGrupos = emptyList()
                    mostrarGrupos(emptyList())
                }
            }
        }
    }

    private fun mostrarGrupos(grupos: List<Grupo>) {
        rvGrupos.adapter = GrupoAdapter(grupos) { grupo ->
            val idx = todosGrupos.indexOf(grupo)
            val intent = Intent(context, DetalleGrupoActivity::class.java)
            intent.putExtra("GRUPO_ID", grupo.id_grupo)
            intent.putExtra("GRUPO_NOMBRE", grupo.nombre)
            intent.putExtra("GRUPO_DESCRIPCION", grupo.descripcion)
            intent.putExtra("GRUPO_IDX", idx.coerceAtLeast(0))
            startActivity(intent)
        }
    }
}
