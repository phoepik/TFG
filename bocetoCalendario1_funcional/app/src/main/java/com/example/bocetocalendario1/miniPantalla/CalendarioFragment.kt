package com.example.bocetocalendario1.miniPantalla

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.activities.EventDetalleBottomSheet
import com.example.bocetocalendario1.activities.SearchBottomSheet
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
    private lateinit var tvConteoEventos: TextView
    private lateinit var rvCalendario: RecyclerView
    private lateinit var rvEventosDia: RecyclerView
    private lateinit var layoutDayEmpty: LinearLayout    // empty state in month view
    private lateinit var btnAnterior: ImageButton
    private lateinit var btnHoy: Button
    private lateinit var btnSiguiente: ImageButton
    private lateinit var btnTabMes: Button
    private lateinit var btnTabSemana: Button
    private lateinit var btnTabDia: Button

    // View containers
    private lateinit var viewMes: LinearLayout
    private lateinit var viewSemana: LinearLayout
    private lateinit var viewDia: LinearLayout

    // Week view
    private lateinit var layoutDayColumns: LinearLayout
    private lateinit var layoutHourRows: LinearLayout
    private lateinit var layoutEventColumns: LinearLayout

    // Day view
    private lateinit var tvDayViewTitle: TextView
    private lateinit var layoutDayHourRows: LinearLayout
    private lateinit var layoutDayEvents: FrameLayout
    private lateinit var layoutDayViewEmpty: LinearLayout   // empty state in day view

    private var cursor = Calendar.getInstance()
    private var diaSeleccionado = Calendar.getInstance()
    private var todosEventos: List<Evento> = emptyList()
    private var vistaActual = "mes"

    private val MESES = arrayOf("Enero","Febrero","Marzo","Abril","Mayo","Junio",
        "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre")
    private val MESES_GEN = arrayOf("enero","febrero","marzo","abril","mayo","junio",
        "julio","agosto","septiembre","octubre","noviembre","diciembre")
    private val DIAS_ABREV = arrayOf("Lun","Mar","Mié","Jue","Vie","Sáb","Dom")
    private val DIAS_NOMBRE_LARGO = arrayOf("lunes","martes","miércoles","jueves","viernes","sábado","domingo")

    private val COLORES_EVENTOS = listOf(
        Color.parseColor("#0B5FFF"), Color.parseColor("#E94B7B"),
        Color.parseColor("#22C55E"), Color.parseColor("#8B5CF6"),
        Color.parseColor("#F97316"), Color.parseColor("#14B8A6")
    )

    private val HORA_INICIO = 7
    private val HORA_FIN = 23
    private val HOUR_HEIGHT_DP = 56

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvMesAnio         = view.findViewById(R.id.tvMesAnio)
        tvEtiquetaHoy     = view.findViewById(R.id.tvEtiquetaHoy)
        tvTituloDia       = view.findViewById(R.id.tvTituloDia)
        tvConteoEventos   = view.findViewById(R.id.tvConteoEventos)
        rvCalendario      = view.findViewById(R.id.rvCalendario)
        rvEventosDia      = view.findViewById(R.id.rvEventosDia)
        layoutDayEmpty    = view.findViewById(R.id.layoutDayEmpty)
        btnAnterior       = view.findViewById(R.id.btnAnteriorMes)
        btnHoy            = view.findViewById(R.id.btnHoy)
        btnSiguiente      = view.findViewById(R.id.btnSiguienteMes)
        btnTabMes         = view.findViewById(R.id.btnTabMes)
        btnTabSemana      = view.findViewById(R.id.btnTabSemana)
        btnTabDia         = view.findViewById(R.id.btnTabDia)

        viewMes           = view.findViewById(R.id.viewMes)
        viewSemana        = view.findViewById(R.id.viewSemana)
        viewDia           = view.findViewById(R.id.viewDia)

        layoutDayColumns      = view.findViewById(R.id.layoutDayColumns)
        layoutHourRows        = view.findViewById(R.id.layoutHourRows)
        layoutEventColumns    = view.findViewById(R.id.layoutEventColumns)

        tvDayViewTitle        = view.findViewById(R.id.tvDayViewTitle)
        layoutDayHourRows     = view.findViewById(R.id.layoutDayHourRows)
        layoutDayEvents       = view.findViewById(R.id.layoutDayEvents)
        layoutDayViewEmpty    = view.findViewById(R.id.layoutDayViewEmpty)

        val fab = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabNuevoEvento)
        val btnSearch = view.findViewById<ImageButton>(R.id.btnSearch)

        rvCalendario.layoutManager = GridLayoutManager(context, 7)
        rvEventosDia.layoutManager = LinearLayoutManager(context)

        btnAnterior.setOnClickListener  { cambiarPeriodo(-1) }
        btnSiguiente.setOnClickListener { cambiarPeriodo(+1) }
        btnHoy.setOnClickListener {
            cursor = Calendar.getInstance()
            diaSeleccionado = Calendar.getInstance()
            actualizarUI()
        }

        btnTabMes.setOnClickListener    { cambiarVista("mes") }
        btnTabSemana.setOnClickListener { cambiarVista("semana") }
        btnTabDia.setOnClickListener    { cambiarVista("día") }

        fab.setOnClickListener {
            startActivity(Intent(context, com.example.bocetocalendario1.activities.CrearEventoActivity::class.java))
        }

        btnSearch?.setOnClickListener {
            val sheet = SearchBottomSheet.newInstance(todosEventos)
            sheet.show(parentFragmentManager, "Search")
        }

        // Apply initial tab state
        aplicarEstadoTabs("mes")
        actualizarHeader()
        cargarEventos()
    }

    override fun onResume() {
        super.onResume()
        cargarEventos()
    }

    private fun cargarEventos() {
        val gestor = GestorSesion(requireContext())
        val idUsuario = gestor.obtenerIdUsuario() ?: -1
        if (idUsuario == -1) return

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
                    actualizarUI()
                }
            } catch (e: Exception) {
                Log.w("CALENDARIO", "Error cargando eventos", e)
                withContext(Dispatchers.Main) {
                    todosEventos = emptyList()
                    actualizarUI()
                }
            }
        }
    }

    private fun actualizarUI() {
        actualizarHeader()
        when (vistaActual) {
            "mes" -> {
                construirGrid()
                mostrarEventosDia(diaSeleccionado)
            }
            "semana" -> construirVistaSemana()
            "día" -> construirVistaDia()
        }
    }

    private fun actualizarHeader() {
        tvMesAnio.text = "${MESES[cursor.get(Calendar.MONTH)]} ${cursor.get(Calendar.YEAR)}"

        val hoy = Calendar.getInstance()
        val esHoy = diaSeleccionado.get(Calendar.DAY_OF_MONTH) == hoy.get(Calendar.DAY_OF_MONTH) &&
                diaSeleccionado.get(Calendar.MONTH) == hoy.get(Calendar.MONTH) &&
                diaSeleccionado.get(Calendar.YEAR) == hoy.get(Calendar.YEAR)

        if (esHoy) {
            tvEtiquetaHoy.text = "Hoy"
        } else {
            // Show day of week for selected day
            val dow = (diaSeleccionado.get(Calendar.DAY_OF_WEEK) + 5) % 7  // Mon=0
            tvEtiquetaHoy.text = DIAS_NOMBRE_LARGO[dow]
        }
        tvEtiquetaHoy.visibility = View.VISIBLE
    }

    private fun construirGrid() {
        val celdas = mutableListOf<DiaCelda>()
        val hoy = Calendar.getInstance()

        val primerDia = cursor.clone() as Calendar
        primerDia.set(Calendar.DAY_OF_MONTH, 1)
        // Monday = 0 offset
        val dow = (primerDia.get(Calendar.DAY_OF_WEEK) + 5) % 7
        // Fill leading empty cells
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

        // Fill trailing empty cells
        while (celdas.size % 7 != 0) {
            celdas.add(DiaCelda(0, false, false, false, emptyList()))
        }

        val adapter = CalendarDayAdapter(celdas) { dia ->
            diaSeleccionado = cursor.clone() as Calendar
            diaSeleccionado.set(Calendar.DAY_OF_MONTH, dia)
            actualizarHeader()
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

        tvTituloDia.text = if (esHoy) "Hoy" else
            "${dia.get(Calendar.DAY_OF_MONTH)} de ${MESES_GEN[dia.get(Calendar.MONTH)]}"
        tvConteoEventos.text = "${eventos.size} ${if (eventos.size == 1) "evento" else "eventos"}"

        if (eventos.isEmpty()) {
            rvEventosDia.visibility = View.GONE
            layoutDayEmpty.visibility = View.VISIBLE
        } else {
            layoutDayEmpty.visibility = View.GONE
            rvEventosDia.visibility = View.VISIBLE
            rvEventosDia.adapter = EventoAdapter(eventos, onClick = { evento ->
                val sheet = EventDetalleBottomSheet.newInstance(evento)
                sheet.show(parentFragmentManager, "EventDetalle")
            })
        }
    }

    // ── WEEK VIEW ─────────────────────────────────────────────────────────

    private fun construirVistaSemana() {
        val ctx = requireContext()
        val hourHeightPx = dpToPx(HOUR_HEIGHT_DP)

        val lunes = diaSeleccionado.clone() as Calendar
        val dow = lunes.get(Calendar.DAY_OF_WEEK)
        val delta = if (dow == Calendar.SUNDAY) -6 else -(dow - Calendar.MONDAY)
        lunes.add(Calendar.DAY_OF_MONTH, delta)

        val hoy = Calendar.getInstance()

        layoutDayColumns.removeAllViews()
        for (i in 0..6) {
            val dayCal = lunes.clone() as Calendar
            dayCal.add(Calendar.DAY_OF_MONTH, i)
            val esHoy = dayCal.get(Calendar.DAY_OF_MONTH) == hoy.get(Calendar.DAY_OF_MONTH) &&
                    dayCal.get(Calendar.MONTH) == hoy.get(Calendar.MONTH) &&
                    dayCal.get(Calendar.YEAR) == hoy.get(Calendar.YEAR)
            val esSel = dayCal.get(Calendar.DAY_OF_MONTH) == diaSeleccionado.get(Calendar.DAY_OF_MONTH) &&
                    dayCal.get(Calendar.MONTH) == diaSeleccionado.get(Calendar.MONTH) &&
                    dayCal.get(Calendar.YEAR) == diaSeleccionado.get(Calendar.YEAR)

            val dayCol = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            }

            val tvAbrev = TextView(ctx).apply {
                text = DIAS_ABREV[i]
                textSize = 10f
                setTextColor(ContextCompat.getColor(ctx, R.color.muted))
                gravity = Gravity.CENTER
                setTypeface(null, android.graphics.Typeface.BOLD)
                letterSpacing = 0.04f
            }

            val circlePx = dpToPx(30)
            val tvNum = TextView(ctx).apply {
                text = dayCal.get(Calendar.DAY_OF_MONTH).toString()
                textSize = 14f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(circlePx, circlePx).apply {
                    topMargin = dpToPx(4)
                }
                setTypeface(null, android.graphics.Typeface.BOLD)
                when {
                    esHoy -> {
                        setTextColor(Color.WHITE)
                        background = ContextCompat.getDrawable(ctx, R.drawable.bg_cal_cell_today)
                    }
                    esSel -> {
                        setTextColor(ContextCompat.getColor(ctx, R.color.brand))
                        background = ContextCompat.getDrawable(ctx, R.drawable.bg_cal_cell_selected)
                    }
                    else -> {
                        setTextColor(ContextCompat.getColor(ctx, R.color.ink))
                        background = null
                    }
                }
            }

            dayCol.addView(tvAbrev)
            dayCol.addView(tvNum)

            val finalDayCal = dayCal.clone() as Calendar
            dayCol.setOnClickListener {
                diaSeleccionado = finalDayCal
                construirVistaSemana()
            }

            layoutDayColumns.addView(dayCol)
        }

        layoutHourRows.removeAllViews()
        for (h in HORA_INICIO..HORA_FIN) {
            val row = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, hourHeightPx)
            }
            val tvHour = TextView(ctx).apply {
                text = String.format("%02d:00", h)
                textSize = 10f
                setTextColor(ContextCompat.getColor(ctx, R.color.muted_2))
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(dpToPx(48), LinearLayout.LayoutParams.MATCH_PARENT)
                setPadding(dpToPx(2), dpToPx(3), dpToPx(4), 0)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            val rowContent = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            }
            val divider = View(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1))
                setBackgroundColor(ContextCompat.getColor(ctx, R.color.line_2))
            }
            rowContent.addView(divider)
            row.addView(tvHour)
            row.addView(rowContent)
            layoutHourRows.addView(row)
        }

        layoutEventColumns.removeAllViews()
        val totalHours = HORA_FIN - HORA_INICIO + 1
        val totalHeightPx = totalHours * hourHeightPx

        for (i in 0..6) {
            val dayCal = lunes.clone() as Calendar
            dayCal.add(Calendar.DAY_OF_MONTH, i)
            val dayEventos = eventosEnDia(dayCal.get(Calendar.DAY_OF_MONTH),
                dayCal.get(Calendar.MONTH), dayCal.get(Calendar.YEAR))

            val colFrame = FrameLayout(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(0, totalHeightPx, 1f)
            }

            dayEventos.forEachIndexed { idx, evento ->
                val (topPx, heightPx) = calcEventPositionPx(evento, hourHeightPx)
                val color = COLORES_EVENTOS[idx % COLORES_EVENTOS.size]

                val eventView = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        maxOf(heightPx, dpToPx(24))
                    ).apply {
                        topMargin = topPx
                        marginStart = dpToPx(2)
                        marginEnd = dpToPx(2)
                    }
                    setBackgroundColor(Color.argb(34, Color.red(color), Color.green(color), Color.blue(color)))
                }

                val colorBar = View(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(dpToPx(3), LinearLayout.LayoutParams.MATCH_PARENT)
                    setBackgroundColor(color)
                }

                val tvTitle = TextView(ctx).apply {
                    text = evento.titulo
                    textSize = 10f
                    setTextColor(ContextCompat.getColor(ctx, R.color.ink))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setPadding(dpToPx(3), dpToPx(2), dpToPx(2), dpToPx(2))
                    maxLines = 2
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                }

                eventView.addView(colorBar)
                eventView.addView(tvTitle)
                eventView.setOnClickListener {
                    val sheet = EventDetalleBottomSheet.newInstance(evento)
                    sheet.show(parentFragmentManager, "EventDetalle")
                }
                colFrame.addView(eventView)
            }
            layoutEventColumns.addView(colFrame)
        }
    }

    // ── DAY VIEW ──────────────────────────────────────────────────────────

    private fun construirVistaDia() {
        val ctx = requireContext()
        val hourHeightPx = dpToPx(64)

        val hoy = Calendar.getInstance()
        val esHoy = diaSeleccionado.get(Calendar.DAY_OF_MONTH) == hoy.get(Calendar.DAY_OF_MONTH) &&
                diaSeleccionado.get(Calendar.MONTH) == hoy.get(Calendar.MONTH) &&
                diaSeleccionado.get(Calendar.YEAR) == hoy.get(Calendar.YEAR)

        val dow = (diaSeleccionado.get(Calendar.DAY_OF_WEEK) + 5) % 7
        val prefix = if (esHoy) "Hoy · " else ""
        tvDayViewTitle.text = "${prefix}${diaSeleccionado.get(Calendar.DAY_OF_MONTH)} " +
                "${MESES_GEN[diaSeleccionado.get(Calendar.MONTH)]} · ${DIAS_NOMBRE_LARGO[dow]}"

        val eventosDelDia = eventosEnDia(
            diaSeleccionado.get(Calendar.DAY_OF_MONTH),
            diaSeleccionado.get(Calendar.MONTH),
            diaSeleccionado.get(Calendar.YEAR)
        )

        layoutDayHourRows.removeAllViews()
        for (h in HORA_INICIO..HORA_FIN) {
            val row = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, hourHeightPx)
            }
            val tvHour = TextView(ctx).apply {
                text = String.format("%02d:00", h)
                textSize = 11f
                setTextColor(ContextCompat.getColor(ctx, R.color.muted))
                setTypeface(null, android.graphics.Typeface.BOLD)
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(dpToPx(52), LinearLayout.LayoutParams.MATCH_PARENT)
                setPadding(dpToPx(8), dpToPx(6), dpToPx(4), 0)
            }
            val lineContainer = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            }
            val line = View(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1))
                setBackgroundColor(ContextCompat.getColor(ctx, R.color.line_2))
            }
            lineContainer.addView(line)
            row.addView(tvHour)
            row.addView(lineContainer)
            layoutDayHourRows.addView(row)
        }

        layoutDayEvents.removeAllViews()
        val totalHeightPx = (HORA_FIN - HORA_INICIO + 1) * hourHeightPx
        layoutDayEvents.layoutParams?.let { it.height = totalHeightPx }

        if (eventosDelDia.isEmpty()) {
            layoutDayViewEmpty.visibility = View.VISIBLE
        } else {
            layoutDayViewEmpty.visibility = View.GONE

            eventosDelDia.forEachIndexed { idx, evento ->
                val (topPx, heightPx) = calcEventPositionPx(evento, hourHeightPx)
                val color = COLORES_EVENTOS[idx % COLORES_EVENTOS.size]

                val eventView = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        maxOf(heightPx, dpToPx(40))
                    ).apply {
                        topMargin = topPx
                        marginStart = dpToPx(56)
                        marginEnd = dpToPx(12)
                    }
                    background = android.graphics.drawable.GradientDrawable().apply {
                        setColor(Color.argb(26, Color.red(color), Color.green(color), Color.blue(color)))
                        cornerRadius = dpToPx(12).toFloat()
                    }
                    setPadding(dpToPx(2), dpToPx(6), dpToPx(8), dpToPx(6))
                }

                val colorBar = View(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(dpToPx(4), LinearLayout.LayoutParams.MATCH_PARENT).apply {
                        marginEnd = dpToPx(8)
                    }
                    background = android.graphics.drawable.GradientDrawable().apply {
                        setColor(color)
                        cornerRadius = dpToPx(4).toFloat()
                    }
                }

                val infoLayout = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val tvTitle = TextView(ctx).apply {
                    text = evento.titulo
                    textSize = 14f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setTextColor(ContextCompat.getColor(ctx, R.color.ink))
                    maxLines = 2
                }

                val hora = evento.fechaInicio.substringAfter(" ", "").trim()
                val horaFin = evento.fechaFin.substringAfter(" ", "").trim()
                val tvHora = TextView(ctx).apply {
                    text = if (hora.isNotEmpty() && horaFin.isNotEmpty()) "$hora – $horaFin" else hora
                    textSize = 12f
                    setTextColor(ContextCompat.getColor(ctx, R.color.muted))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }

                infoLayout.addView(tvTitle)
                if (hora.isNotEmpty()) infoLayout.addView(tvHora)

                eventView.addView(colorBar)
                eventView.addView(infoLayout)
                eventView.setOnClickListener {
                    val sheet = EventDetalleBottomSheet.newInstance(evento)
                    sheet.show(parentFragmentManager, "EventDetalle")
                }
                layoutDayEvents.addView(eventView)
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private fun calcEventPositionPx(evento: Evento, hourHeightPx: Int): Pair<Int, Int> {
        return try {
            val horaStr = evento.fechaInicio.substringAfter(" ", "").trim()
            val partes = horaStr.split(":")
            val hora = partes[0].toIntOrNull() ?: HORA_INICIO
            val min = partes.getOrNull(1)?.toIntOrNull() ?: 0

            val horaFinStr = evento.fechaFin.substringAfter(" ", "").trim()
            val partesF = horaFinStr.split(":")
            val horaF = partesF[0].toIntOrNull() ?: (hora + 1)
            val minF = partesF.getOrNull(1)?.toIntOrNull() ?: 0

            val topOffset = ((hora - HORA_INICIO) * 60 + min).toFloat() / 60f * hourHeightPx
            val durationMin = ((horaF - hora) * 60 + (minF - min)).coerceAtLeast(30)
            val height = durationMin.toFloat() / 60f * hourHeightPx

            Pair(topOffset.toInt(), height.toInt().coerceAtLeast(dpToPx(24)))
        } catch (e: Exception) {
            Pair(0, hourHeightPx)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
        ).toInt()
    }

    private fun eventosEnDia(dia: Int): List<Evento> =
        eventosEnDia(dia, cursor.get(Calendar.MONTH), cursor.get(Calendar.YEAR))

    private fun eventosEnDia(dia: Int, mes: Int, anio: Int): List<Evento> {
        return todosEventos.filter { evento ->
            try { parsearFechaEvento(evento.fechaInicio, dia, mes, anio) }
            catch (e: Exception) { false }
        }
    }

    private fun parsearFechaEvento(fechaInicio: String, dia: Int, mes: Int, anio: Int): Boolean {
        val raw = fechaInicio.trim()
        return if (raw.contains("/")) {
            val fecha = raw.substringBefore(" ")
            val partes = fecha.split("/")
            if (partes.size < 3) return false
            partes[0].toInt() == dia && partes[1].toInt() - 1 == mes && partes[2].toInt() == anio
        } else if (raw.contains("-")) {
            val fecha = raw.substringBefore("T").substringBefore(" ")
            val partes = fecha.split("-")
            if (partes.size < 3) return false
            partes[2].toInt() == dia && partes[1].toInt() - 1 == mes && partes[0].toInt() == anio
        } else false
    }

    private fun cambiarPeriodo(delta: Int) {
        when (vistaActual) {
            "mes" -> cursor.add(Calendar.MONTH, delta)
            "semana" -> diaSeleccionado.add(Calendar.WEEK_OF_YEAR, delta)
            "día" -> {
                diaSeleccionado.add(Calendar.DAY_OF_MONTH, delta)
                cursor = diaSeleccionado.clone() as Calendar
            }
        }
        actualizarUI()
    }

    private fun cambiarVista(vista: String) {
        vistaActual = vista
        aplicarEstadoTabs(vista)
        viewMes.visibility    = if (vista == "mes")    View.VISIBLE else View.GONE
        viewSemana.visibility = if (vista == "semana") View.VISIBLE else View.GONE
        viewDia.visibility    = if (vista == "día")    View.VISIBLE else View.GONE
        actualizarUI()
    }

    private fun aplicarEstadoTabs(vista: String) {
        val ctx = requireContext()
        val tabButtons = listOf(btnTabMes to "mes", btnTabSemana to "semana", btnTabDia to "día")
        tabButtons.forEach { (btn, v) ->
            val isActive = v == vista
            btn.backgroundTintList = android.content.res.ColorStateList.valueOf(
                if (isActive) Color.WHITE else Color.TRANSPARENT
            )
            btn.setTextColor(ContextCompat.getColor(ctx, if (isActive) R.color.ink else R.color.muted))
            btn.elevation = if (isActive) dpToPx(2).toFloat() else 0f
        }
    }
}
