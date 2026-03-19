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
import com.example.bocetocalendario1.activities.CrearEventoActivity
import com.example.bocetocalendario1.adaptadores.EventoAdapter
import com.example.bocetocalendario1.datos.basedatos.AppDatabase
import com.example.bocetocalendario1.models.Evento
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InicioFragment : Fragment() {

    private lateinit var rvEventos: RecyclerView
    private lateinit var btnNuevoEvento: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvEventos = view.findViewById(R.id.rvEventos)
        btnNuevoEvento = view.findViewById(R.id.btnNuevoEvento)

        rvEventos.layoutManager = LinearLayoutManager(context)
        rvEventos.adapter = EventoAdapter(emptyList()) {}

        // boton de nuevo evcento
        btnNuevoEvento.setOnClickListener {
            startActivity(Intent(context, CrearEventoActivity::class.java))
        }
    }
    override fun onResume() {
        super.onResume()
        cargarEventos()
    }

    private fun cargarEventos() {
        val db = AppDatabase.getDatabase(requireContext())
        val gestor = GestorSesion(requireContext())
        val idUsuario = gestor.obtenerIdUsuario() ?: -1

        lifecycleScope.launch(Dispatchers.IO) {
            val eventosDB = if (idUsuario != -1) {
                db.appDao().obtenerEventosDeUsuario(idUsuario)
            } else emptyList()

            val eventosUI = eventosDB.map { e ->
                Evento(
                    id = e.id_evento,
                    titulo = e.titulo,
                    descripcion = e.descripcion ?: "",
                    fechaInicio = e.fecha_inicio,
                    fechaFin = e.fecha_fin,
                    ubicacion = e.ubicacion ?: "",
                    estado = e.estado,
                    idCalendario = e.id_calendario
                )
            }

            withContext(Dispatchers.Main) {
                rvEventos.adapter = EventoAdapter(eventosUI) { }
            }
        }
    }

}
