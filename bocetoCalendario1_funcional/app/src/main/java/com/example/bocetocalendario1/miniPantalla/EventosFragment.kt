package com.example.bocetocalendario1.miniPantalla

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.activities.CrearEventoActivity
import com.example.bocetocalendario1.activities.EventDetalleBottomSheet
import com.example.bocetocalendario1.activities.SearchBottomSheet
import com.example.bocetocalendario1.adaptadores.EventoAdapter
import com.example.bocetocalendario1.models.Evento
import com.example.bocetocalendario1.network.RetrofitClient
import com.example.bocetocalendario1.utilidades.GestorSesion
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class EventosFragment : Fragment() {

    private lateinit var rvEventos: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var layoutGroupChips: LinearLayout
    private lateinit var tabProximos: TextView
    private lateinit var tabPasados: TextView
    private lateinit var tabTodos: TextView

    private var todosEventos: List<Evento> = emptyList()
    private var nombresCalendarios: List<String> = emptyList()
    private var mapaCalendarios: Map<Int, String> = emptyMap()
    private var filtroActual = "proximos"   // proximos | pasados | todos
    private var grupoFiltroActual = -1      // -1 = todos; idCalendario otherwise

    private val GROUP_COLORS = listOf(
        "#0B5FFF", "#E94B7B", "#22C55E", "#8B5CF6", "#F97316", "#14B8A6", "#FFC700"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_eventos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvEventos = view.findViewById(R.id.rvEventos)
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
        layoutGroupChips = view.findViewById(R.id.layoutGroupChips)
        tabProximos = view.findViewById(R.id.tabProximos)
        tabPasados = view.findViewById(R.id.tabPasados)
        tabTodos = view.findViewById(R.id.tabTodos)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabNuevoEvento)

        rvEventos.layoutManager = LinearLayoutManager(context)

        tabProximos.setOnClickListener { cambiarFiltro("proximos") }
        tabPasados.setOnClickListener { cambiarFiltro("pasados") }
        tabTodos.setOnClickListener { cambiarFiltro("todos") }

        fab.setOnClickListener {
            startActivity(Intent(context, CrearEventoActivity::class.java))
        }

        cargarEventos()
    }

    override fun onResume() {
        super.onResume()
        if (isAdded) cargarEventos()
    }

    private fun cambiarFiltro(filtro: String) {
        filtroActual = filtro
        actualizarTabUI()
        mostrarEventosFiltrados()
    }

    private fun actualizarTabUI() {
        val colorActivo = requireContext().getColor(R.color.brand)
        val colorInactivo = requireContext().getColor(R.color.muted)
        val bgActivo = requireContext().getDrawable(R.drawable.bg_tab_active)
        val bgInactivo = requireContext().getDrawable(R.drawable.bg_tab_inactive)

        tabProximos.setTextColor(if (filtroActual == "proximos") colorActivo else colorInactivo)
        tabProximos.background = if (filtroActual == "proximos") bgActivo else bgInactivo

        tabPasados.setTextColor(if (filtroActual == "pasados") colorActivo else colorInactivo)
        tabPasados.background = if (filtroActual == "pasados") bgActivo else bgInactivo

        tabTodos.setTextColor(if (filtroActual == "todos") colorActivo else colorInactivo)
        tabTodos.background = if (filtroActual == "todos") bgActivo else bgInactivo
    }

    private fun cargarEventos() {
        val gestor = GestorSesion(requireContext())
        val idUsuario = gestor.obtenerIdUsuario() ?: return

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val calResp = RetrofitClient.api.obtenerCalendariosDeUsuario(idUsuario)
                val calendarios = if (calResp.isSuccessful) calResp.body() ?: emptyList() else emptyList()

                val eventos = mutableListOf<Evento>()
                if (calendarios.isNotEmpty()) {
                    calendarios.forEach { cal ->
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
                } else {
                    val evResp = RetrofitClient.api.obtenerEventosDeCalendario(idUsuario)
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

                withContext(Dispatchers.Main) {
                    todosEventos = eventos
                    nombresCalendarios = calendarios.map { it.nombre }
                    mapaCalendarios = calendarios
                        .filter { it.idCalendario != null }
                        .associate { it.idCalendario!! to it.nombre }
                    construirGroupChips(nombresCalendarios)
                    mostrarEventosFiltrados()
                }
            } catch (e: Exception) {
                Log.e("EVENTOS", "Error: ${e.message}")
            }
        }
    }

    private fun construirGroupChips(nombreCalendarios: List<String>) {
        layoutGroupChips.removeAllViews()

        // "✨ Todos" chip
        addChip("✨ Todos", -1, -1 == grupoFiltroActual)

        nombreCalendarios.forEachIndexed { index, nombre ->
            val colorHex = GROUP_COLORS[index % GROUP_COLORS.size]
            addChipColored(nombre, index, colorHex, index == grupoFiltroActual)
        }
    }

    private fun addChip(label: String, id: Int, activo: Boolean) {
        val chip = TextView(context).apply {
            text = label
            textSize = 12f
            setPadding(20, 10, 20, 10)
            setTextColor(if (activo) Color.parseColor("#0B5FFF") else Color.parseColor("#6B7494"))
            background = if (activo)
                context.getDrawable(R.drawable.bg_chip_brand)
            else
                context.getDrawable(R.drawable.bg_chip_muted)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.marginEnd = 8
            layoutParams = lp
            setOnClickListener {
                grupoFiltroActual = id
                construirGroupChips(getNombreCalendarios())
                mostrarEventosFiltrados()
            }
        }
        layoutGroupChips.addView(chip)
    }

    private fun addChipColored(label: String, id: Int, colorHex: String, activo: Boolean) {
        val color = try { Color.parseColor(colorHex) } catch (e: Exception) { Color.parseColor("#0B5FFF") }
        val chip = TextView(context).apply {
            text = label
            textSize = 12f
            setPadding(20, 10, 20, 10)
            setTextColor(if (activo) color else Color.parseColor("#6B7494"))
            background = if (activo) {
                android.graphics.drawable.GradientDrawable().apply {
                    setColor(color and 0x00FFFFFF or 0x22000000)
                    cornerRadius = 999f
                }
            } else {
                context.getDrawable(R.drawable.bg_chip_muted)
            }
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.marginEnd = 8
            layoutParams = lp
            setOnClickListener {
                grupoFiltroActual = id
                construirGroupChips(getNombreCalendarios())
                mostrarEventosFiltrados()
            }
        }
        layoutGroupChips.addView(chip)
    }

    private fun getNombreCalendarios(): List<String> {
        return if (nombresCalendarios.isNotEmpty()) nombresCalendarios
        else todosEventos.map { it.idCalendario }.distinct().mapIndexed { i, _ -> "Calendario ${i + 1}" }
    }

    private fun mostrarEventosFiltrados() {
        val hoy = Calendar.getInstance()
        hoy.set(Calendar.HOUR_OF_DAY, 0)
        hoy.set(Calendar.MINUTE, 0)
        hoy.set(Calendar.SECOND, 0)
        hoy.set(Calendar.MILLISECOND, 0)

        var eventos = todosEventos

        // Group filter
        if (grupoFiltroActual >= 0) {
            val calendarioIds = todosEventos.map { it.idCalendario }.distinct()
            val idCal = calendarioIds.getOrNull(grupoFiltroActual)
            if (idCal != null) {
                eventos = eventos.filter { it.idCalendario == idCal }
            }
        }

        // Time filter
        eventos = when (filtroActual) {
            "proximos" -> eventos.filter { esFuturo(it.fechaInicio, hoy) }
            "pasados" -> eventos.filter { !esFuturo(it.fechaInicio, hoy) }
            else -> eventos
        }

        if (eventos.isEmpty()) {
            rvEventos.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
        } else {
            rvEventos.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE
            rvEventos.adapter = EventoAdapter(eventos, { evento ->
                val sheet = EventDetalleBottomSheet.newInstance(evento)
                sheet.show(parentFragmentManager, "EventDetalle")
            }, mapaCalendarios)
        }
    }

    private fun esFuturo(fechaInicio: String, hoy: Calendar): Boolean {
        return try {
            val fecha = fechaInicio.substringBefore(" ").trim()
            val partes = fecha.split("/")
            if (partes.size < 3) return true
            val cal = Calendar.getInstance()
            cal.set(partes[2].toInt(), partes[1].toInt() - 1, partes[0].toInt(), 0, 0, 0)
            cal.set(Calendar.MILLISECOND, 0)
            !cal.before(hoy)
        } catch (e: Exception) { true }
    }
}
