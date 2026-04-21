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
import com.example.bocetocalendario1.activities.CrearEventoActivity
import com.example.bocetocalendario1.adaptadores.EventoAdapter
import com.example.bocetocalendario1.models.Evento
import com.example.bocetocalendario1.network.RetrofitClient
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

        cargarEventos()

        btnNuevoEvento.setOnClickListener {
            startActivity(Intent(context, CrearEventoActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarEventos()
    }

    private fun cargarEventos() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Cargar eventos del calendario 0 (por defecto)
                val response = RetrofitClient.api.obtenerEventosDeCalendario(0)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val eventosServidor = response.body()!!
                        // Convertir EventoResponse a models.Evento para el adaptador
                        val eventos = eventosServidor.map { e ->
                            Evento(
                                id = e.idEvento ?: 0,
                                titulo = e.titulo,
                                descripcion = e.descripcion ?: "",
                                fechaInicio = e.fechaInicio ?: "",
                                fechaFin = e.fechaFin ?: "",
                                ubicacion = e.ubicacion ?: "",
                                estado = e.estado ?: "PENDIENTE",
                                idCalendario = e.idCalendario ?: 0
                            )
                        }
                        rvEventos.adapter = EventoAdapter(eventos) { evento -> }
                    } else {
                        rvEventos.adapter = EventoAdapter(emptyList()) { }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("INICIO", "Error: ${e.message}")
                    Toast.makeText(context, "Error al cargar eventos", Toast.LENGTH_SHORT).show()
                    rvEventos.adapter = EventoAdapter(emptyList()) { }
                }
            }
        }
    }
}