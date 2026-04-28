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
import com.example.bocetocalendario1.MainActivity
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
    private lateinit var btnNotificacion: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvEventos       = view.findViewById(R.id.rvEventos)
        btnNuevoEvento  = view.findViewById(R.id.btnNuevoEvento)
        btnNotificacion = view.findViewById(R.id.btnNotificacion)

        rvEventos.layoutManager = LinearLayoutManager(context)

        cargarEventos()

        btnNuevoEvento.setOnClickListener {
            startActivity(Intent(context, CrearEventoActivity::class.java))
        }
        btnNotificacion.setOnClickListener {
            (activity as? MainActivity)?.navegarANotificaciones()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarEventos()
    }

    private fun cargarEventos() {
        val gestor = GestorSesion(requireContext())
        val idUsuario = gestor.obtenerIdUsuario() ?: -1

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val eventos = mutableListOf<Evento>()

                // Cargar desde calendarios del usuario
                if (idUsuario != -1) {
                    try {
                        val calResp = RetrofitClient.api.obtenerCalendariosDeUsuario(idUsuario)
                        if (calResp.isSuccessful && !calResp.body().isNullOrEmpty()) {
                            calResp.body()!!.forEach { cal ->
                                val idCal = cal.idCalendario ?: return@forEach
                                val evResp = RetrofitClient.api.obtenerEventosDeCalendario(idCal)
                                if (evResp.isSuccessful) {
                                    evResp.body()?.forEach { e ->
                                        eventos.add(Evento(
                                            id = e.idEvento ?: 0, titulo = e.titulo,
                                            descripcion = e.descripcion ?: "",
                                            fechaInicio = e.fechaInicio ?: "",
                                            fechaFin = e.fechaFin ?: "",
                                            ubicacion = e.ubicacion ?: "",
                                            estado = e.estado ?: "PENDIENTE",
                                            idCalendario = e.idCalendario ?: 0
                                        ))
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.w("INICIO", "Calendarios no disponibles, usando fallback")
                    }
                }

                // Fallback: cargar por id de usuario directo
                if (eventos.isEmpty() && idUsuario != -1) {
                    val resp = RetrofitClient.api.obtenerEventosDeCalendario(idUsuario)
                    if (resp.isSuccessful) {
                        resp.body()?.forEach { e ->
                            eventos.add(Evento(
                                id = e.idEvento ?: 0, titulo = e.titulo,
                                descripcion = e.descripcion ?: "",
                                fechaInicio = e.fechaInicio ?: "",
                                fechaFin = e.fechaFin ?: "",
                                ubicacion = e.ubicacion ?: "",
                                estado = e.estado ?: "PENDIENTE",
                                idCalendario = e.idCalendario ?: 0
                            ))
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    // EventoAdapter agrupa automáticamente por día
                    rvEventos.adapter = EventoAdapter(eventos) {}
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("INICIO", "Error: ${e.message}")
                    Toast.makeText(context, "Error al cargar eventos", Toast.LENGTH_SHORT).show()
                    rvEventos.adapter = EventoAdapter(emptyList()) {}
                }
            }
        }
    }
}
