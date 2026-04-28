package com.example.bocetocalendario1.miniPantalla

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.activities.CrearEventoActivity
import com.example.bocetocalendario1.adaptadores.CalendarDayAdapter
import com.example.bocetocalendario1.adaptadores.DiaCelda
import com.example.bocetocalendario1.adaptadores.EventoAdapter
import com.example.bocetocalendario1.models.Evento
import com.example.bocetocalendario1.network.RetrofitClient
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CalendarioFragment : Fragment() {

    private lateinit var tvMesAnio: TextView
    private lateinit var tvEtiquetaHoy: TextView
    private lateinit var tvTituloDia: TextView
    private lateinit var rvCalendario: RecyclerView
    private lateinit var rvEventosDia: RecyclerView
    private lateinit var btnAnterior: Button
    private lateinit var btnHoy: Button
    private lateinit var btnSiguiente: Button
    private lateinit var btnTabMes: Button
    private lateinit var btnTabSemana: Button
    private lateinit var btnTabDia: Button
    private var cursor = Calendar.getInstance()   // mes visible
    private var diaSeleccionado = Calendar.getInstance()  // día seleccionado
    private var todosEventos: List<Evento> = emptyList()
    private var vistaActual = "mes"  // mes | semana | día

    private val MESES = arrayOf("Enero","Febrero","Marzo","Abril","Mayo","Junio",
        "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre")
    private val DIAS_SEMANA = arrayOf("Dom","Lun","Mar","Mié","Jue","Vie","Sáb")
    private val COLORES_EVENTOS = listOf(
        Color.parseColor("#0B5FFF"), Color.parseColor("#E94B7B"),
        Color.parseColor("#22C55E"), Color.parseColor("#8B5CF6"),
        Color.parseColor("#F97316"), Color.parseColor("#14B8A6")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvMesAnio      = view.findViewById(R.id.tvMesAnio)
        tvEtiquetaHoy  = view.findViewById(R.id.tvEtiquetaHoy)
        tvTituloDia    = view.findViewById(R.id.tvTituloDia)
        rvCalendario   = view.findViewById(R.id.rvCalendario)
        rvEventosDia   = view.findViewById(R.id.rvEventosDia)
        btnAnterior    = view.findViewById(R.id.btnAnteriorMes)
        btnHoy         = view.findViewById(R.id.btnHoy)
        btnSiguiente   = view.findViewById(R.id.btnSiguienteMes)
        btnTabMes      = view.findViewById(R.id.btnTabMes)
        btnTabSemana   = view.findViewById(R.id.btnTabSemana)
        btnTabDia      = view.findViewById(R.id.btnTabDia)
        val fab        = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabNuevoEvento)

        rvCalendario.layoutManager = GridLayoutManager(context, 7)
        rvEventosDia.layoutManager = LinearLayoutManager(context)

        btnAnterior.setOnClickListener  { cambiarMes(-1) }
        btnSiguiente.setOnClickListener { cambiarMes(+1) }
        btnHoy.setOnClickListener {
            cursor = Calendar.getInstance()
            diaSeleccionado = Calendar.getInstance()
            actualizarUI()
        }

        btnTabMes.setOnClickListener    { cambiarVista("mes") }
        btnTabSemana.setOnClickListener { cambiarVista("semana") }
        btnTabDia.setOnClickListener    { cambiarVista("día") }

        fab.setOnClickListener {
            startActivity(Intent(context, CrearEventoActivity::class.java))
        }

        actualizarHeader()
        cargarEventos()
    }

    override fun onResume() {
        super.onResume()
        cargarEventos()
    }

    // ── Carga eventos desde el servidor ──────────────────────────────────
    private fun cargarEventos() {
        val gestor = GestorSesion(requireContext())
        val idUsuario = gestor.obtenerIdUsuario() ?: -1
        if (idUsuario == -1) return

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. Obtener calendarios del usuario
                val calResp = RetrofitClient.api.obtenerCalendariosDeUsuario(idUsuario)
                val calendarios = if (calResp.isSuccessful) calResp.body() ?: emptyList() else emptyList()

                // 2. Cargar eventos de cada calendario
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
                    // Fallback: intentar con calendario 1
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
                    actualizarUI()
                }
            } catch (e: Exception) {
                Log.e("CALENDARIO", "Error cargando eventos: ${e.message}")
            }
        }
    }

    // ── UI ───────────────────────────────────────────────────────────────
    private fun actualizarUI() {
        actualizarHeader()
        construirGrid()
        mostrarEventosDia(diaSeleccionado)
    }

    private fun actualizarHeader() {
        tvMesAnio.text = "${MESES[cursor.get(Calendar.MONTH)]} ${cursor.get(Calendar.YEAR)}"

        val hoy = Calendar.getInstance()
        val esHoyVisible = cursor.get(Calendar.YEAR) == hoy.get(Calendar.YEAR) &&
                cursor.get(Calendar.MONTH) == hoy.get(Calendar.MONTH)
        tvEtiquetaHoy.visibility = if (esHoyVisible) View.VISIBLE else View.GONE
    }

    private fun construirGrid() {
        val celdas = mutableListOf<DiaCelda>()
        val hoy = Calendar.getInstance()

        val primerDia = cursor.clone() as Calendar
        primerDia.set(Calendar.DAY_OF_MONTH, 1)
        // Día de la semana del primer día (0=Lun … 6=Dom)
        var dow = primerDia.get(Calendar.DAY_OF_WEEK) - 2  // Calendar: Dom=1, Lun=2
        if (dow < 0) dow = 6  // Domingo → posición 6

        // Celdas vacías iniciales
        repeat(dow) { celdas.add(DiaCelda(0, false, false, false, emptyList())) }

        val diasEnMes = primerDia.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (d in 1..diasEnMes) {
            val esHoy = d == hoy.get(Calendar.DAY_OF_MONTH) &&
                    cursor.get(Calendar.MONTH) == hoy.get(Calendar.MONTH) &&
                    cursor.get(Calendar.YEAR) == hoy.get(Calendar.YEAR)
            val esSel = d == diaSeleccionado.get(Calendar.DAY_OF_MONTH) &&
                    cursor.get(Calendar.MONTH) == diaSeleccionado.get(Calendar.MONTH) &&
                    cursor.get(Calendar.YEAR) == diaSeleccionado.get(Calendar.YEAR)

            val colores = eventosEnDia(d).mapIndexed { i, _ -> COLORES_EVENTOS[i % COLORES_EVENTOS.size] }
            celdas.add(DiaCelda(d, true, esHoy, esSel, colores))
        }

        // Rellenar hasta múltiplo de 7
        while (celdas.size % 7 != 0) celdas.add(DiaCelda(0, false, false, false, emptyList()))

        val adapter = CalendarDayAdapter(celdas) { dia ->
            diaSeleccionado = cursor.clone() as Calendar
            diaSeleccionado.set(Calendar.DAY_OF_MONTH, dia)
            construirGrid()
            mostrarEventosDia(diaSeleccionado)
        }
        rvCalendario.adapter = adapter
    }

    private fun mostrarEventosDia(dia: Calendar) {
        val eventos = eventosEnDia(dia.get(Calendar.DAY_OF_MONTH),
            dia.get(Calendar.MONTH), dia.get(Calendar.YEAR))

        val hoy = Calendar.getInstance()
        val esHoy = dia.get(Calendar.DAY_OF_MONTH) == hoy.get(Calendar.DAY_OF_MONTH) &&
                dia.get(Calendar.MONTH) == hoy.get(Calendar.MONTH) &&
                dia.get(Calendar.YEAR) == hoy.get(Calendar.YEAR)

        val etiqueta = if (esHoy) "Hoy" else
            "${dia.get(Calendar.DAY_OF_MONTH)} de ${MESES[dia.get(Calendar.MONTH)].lowercase()}"
        tvTituloDia.text = "$etiqueta · ${eventos.size} ${if (eventos.size == 1) "evento" else "eventos"}"

        if (eventos.isEmpty()) {
            rvEventosDia.adapter = EventoAdapter(emptyList()) {}
        } else {
            rvEventosDia.adapter = EventoAdapter(eventos) {}
        }
    }

    // Eventos del día dentro del mes/año del cursor
    private fun eventosEnDia(dia: Int): List<Evento> =
        eventosEnDia(dia, cursor.get(Calendar.MONTH), cursor.get(Calendar.YEAR))

    private fun eventosEnDia(dia: Int, mes: Int, anio: Int): List<Evento> {
        return todosEventos.filter { evento ->
            try {
                val fecha = evento.fechaInicio.substringBefore(" ").trim()
                val partes = fecha.split("/")
                if (partes.size < 3) return@filter false
                partes[0].toInt() == dia &&
                        partes[1].toInt() - 1 == mes &&
                        partes[2].toInt() == anio
            } catch (e: Exception) { false }
        }
    }

    private fun cambiarMes(delta: Int) {
        cursor.add(Calendar.MONTH, delta)
        actualizarUI()
    }

    private fun cambiarVista(vista: String) {
        vistaActual = vista
        val activo = Color.parseColor("#FFFFFF")
        val inactivo = Color.TRANSPARENT
        val textoActivo = Color.parseColor("#0A1330")
        val textoInactivo = Color.parseColor("#6B7494")

        btnTabMes.backgroundTintList = android.content.res.ColorStateList.valueOf(
            if (vista == "mes") activo else inactivo)
        btnTabSemana.backgroundTintList = android.content.res.ColorStateList.valueOf(
            if (vista == "semana") activo else inactivo)
        btnTabDia.backgroundTintList = android.content.res.ColorStateList.valueOf(
            if (vista == "día") activo else inactivo)

        btnTabMes.setTextColor(if (vista == "mes") textoActivo else textoInactivo)
        btnTabSemana.setTextColor(if (vista == "semana") textoActivo else textoInactivo)
        btnTabDia.setTextColor(if (vista == "día") textoActivo else textoInactivo)

        // En semana/día mostramos solo el grid de la semana/día actual
        // (vista simplificada: muestra eventos del día seleccionado)
        actualizarUI()
    }
}
