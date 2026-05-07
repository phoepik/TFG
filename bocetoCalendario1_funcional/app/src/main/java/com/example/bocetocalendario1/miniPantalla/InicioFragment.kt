package com.example.bocetocalendario1.miniPantalla

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class InicioFragment : Fragment() {

    private lateinit var rvEventos: RecyclerView
    private lateinit var btnNuevoEvento: TextView
    private lateinit var btnNotificacion: TextView
    private lateinit var tvFechaHoy: TextView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var fabNuevo: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvEventos        = view.findViewById(R.id.rvEventos)
        btnNuevoEvento   = view.findViewById(R.id.btnNuevoEvento)
        btnNotificacion  = view.findViewById(R.id.btnNotificacion)
        tvFechaHoy       = view.findViewById(R.id.tvFechaHoy)
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
        fabNuevo         = view.findViewById(R.id.fabNuevo)

        rvEventos.layoutManager = LinearLayoutManager(context)

        // Show today's date in header
        val sdf = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
        tvFechaHoy.text = sdf.format(Date()).replaceFirstChar { it.uppercaseChar() }

        cargarEventos()

        val abrirCrear = { startActivity(Intent(context, CrearEventoActivity::class.java)) }
        btnNuevoEvento.setOnClickListener { abrirCrear() }
        fabNuevo.setOnClickListener { abrirCrear() }

        btnNotificacion.setOnClickListener {
            // Reserved for notifications screen
        }
    }

    override fun onResume() {
        super.onResume()
        cargarEventos()
    }

    private fun eventosDemo(): List<Evento> {
        val hoy = Calendar.getInstance()
        val d = "%02d".format(hoy.get(Calendar.DAY_OF_MONTH))
        val m = "%02d".format(hoy.get(Calendar.MONTH) + 1)
        val y = hoy.get(Calendar.YEAR)
        val sig = "%02d".format((hoy.get(Calendar.DAY_OF_MONTH) + 1).coerceAtMost(28))
        return listOf(
            Evento(1, "Reunión de equipo",   "Revisión semanal del sprint", "$d/$m/$y 09:00",  "$d/$m/$y 10:00",  "Sala de reuniones", "CONFIRMADO", 1),
            Evento(2, "Clase de yoga",        "",                            "$d/$m/$y 18:00",  "$d/$m/$y 19:00",  "Gimnasio Central",  "CONFIRMADO", 2),
            Evento(3, "Revisión TFG",         "Entrega parcial",             "$sig/$m/$y 10:00","$sig/$m/$y 12:00","Facultad B-101",    "PENDIENTE",  1),
            Evento(4, "Cena con amigos",      "Cumpleaños de Ana",           "$sig/$m/$y 21:00","$sig/$m/$y 23:30","Restaurante Mar",   "CONFIRMADO", 3)
        )
    }

    private fun cargarEventos() {
        val gestor = GestorSesion(requireContext())
        val idUsuario = gestor.obtenerIdUsuario() ?: -1

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val eventos = mutableListOf<Evento>()

                // Load from user's calendars
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
                                            id = e.idEvento ?: 0,
                                            titulo = e.titulo,
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

                // Fallback: load directly by user id
                if (eventos.isEmpty() && idUsuario != -1) {
                    val resp = RetrofitClient.api.obtenerEventosDeCalendario(idUsuario)
                    if (resp.isSuccessful) {
                        resp.body()?.forEach { e ->
                            eventos.add(Evento(
                                id = e.idEvento ?: 0,
                                titulo = e.titulo,
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
                    val lista = if (eventos.isEmpty()) eventosDemo() else eventos
                    rvEventos.visibility = View.VISIBLE
                    layoutEmptyState.visibility = View.GONE
                    rvEventos.adapter = EventoAdapter(lista, onClick = {})
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    rvEventos.visibility = View.VISIBLE
                    layoutEmptyState.visibility = View.GONE
                    rvEventos.adapter = EventoAdapter(eventosDemo(), onClick = {})
                }
            }
        }
    }
}
