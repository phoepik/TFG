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
    private lateinit var rvCalendario: RecyclerView
    private lateinit var rvEventosDia: RecyclerView
    private lateinit var btnAnterior: Button
    private lateinit var btnHoy: Button
    private lateinit var btnSiguiente: Button
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
    private lateinit var layoutDayEmpty: LinearLayout

    private var cursor = Calendar.getInstance()
    private var diaSeleccionado = Calendar.getInstance()
    private var todosEventos: List<Evento> = emptyList()
    private var vistaActual = "mes"

    private val MESES = arrayOf("Enero","Febrero","Marzo","Abril","Mayo","Junio",
        "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre")
    private val MESES_GEN = arrayOf("enero","febrero","marzo","abril","mayo","junio",
        "julio","agosto","septiembre","octubre","noviembre","diciembre")
    private val DIAS_ABREV = arrayOf("L","M","X","J","V","S","D")
    private val DIAS_NOMBRE = arrayOf("Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo")
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

        viewMes        = view.findViewById(R.id.viewMes)
        viewSemana     = view.findViewById(R.id.viewSemana)
        viewDia        = view.findViewById(R.id.viewDia)

        layoutDayColumns   = view.findViewById(R.id.layoutDayColumns)
        layoutHourRows     = view.findViewById(R.id.layoutHourRows)
        layoutEventColumns = view.findViewById(R.id.layoutEventColumns)

        tvDayViewTitle    = view.findViewById(R.id.tvDayViewTitle)
        layoutDayHourRows = view.findViewById(R.id.layoutDayHourRows)
        layoutDayEvents   = view.findViewById(R.id.layoutDayEvents)
        layoutDayEmpty    = view.findViewById(R.id.layoutDayEmpty)

        val fab = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabNuevoEvento)
        val btnSearch = view.findViewById<android.widget.ImageButton?>(R.id.btnSearch)

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
                Log.e("CALENDARIO", "Error cargando eventos: ${e.message}")
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
        val esHoyVisible = cursor.get(Calendar.YEAR) == hoy.get(Calendar.YEAR) &&
                cursor.get(Calendar.MONTH) == hoy.get(Calendar.MONTH)
        tvEtiquetaHoy.visibility = if (esHoyVisible) View.VISIBLE else View.GONE
    }

    private fun construirGrid() {
        val celdas = mutableListOf<DiaCelda>()
        val hoy = Calendar.getInstance()

        val primerDia = cursor.clone() as Calendar
        primerDia.set(Calendar.DAY_OF_MONTH, 1)
        var dow = primerDia.get(Calendar.DAY_OF_WEEK) - 2
        if (dow < 0) dow = 6

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

        rvEventosDia.adapter = EventoAdapter(eventos) { evento ->
            val sheet = EventDetalleBottomSheet.newInstance(evento)
            sheet.show(parentFragmentManager, "EventDetalle")
        }
    }

    // ── WEEK VIEW ──────────────────────────────────────────────────────────

    private fun construirVistaSemana() {
        val ctx = requireContext()
        val hourHeightPx = dpToPx(HOUR_HEIGHT_DP)

        // Find Monday of the week containing diaSeleccionado
        val lunes = diaSeleccionado.clone() as Calendar
        val dow = lunes.get(Calendar.DAY_OF_WEEK)
        val delta = if (dow == Calendar.SUNDAY) -6 else -(dow - Calendar.MONDAY)
        lunes.add(Calendar.DAY_OF_MONTH, delta)

        val hoy = Calendar.getInstance()

        // Build day columns header
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
                val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                layoutParams = lp
            }

            val tvAbrev = TextView(ctx).apply {
                text = DIAS_ABREV[i]
                textSize = 11f
                setTextColor(ContextCompat.getColor(ctx, R.color.muted))
                gravity = Gravity.CENTER
            }

            val tvNum = TextView(ctx).apply {
                text = dayCal.get(Calendar.DAY_OF_MONTH).toString()
                textSize = 15f
                gravity = Gravity.CENTER
                val circleDp = 32
                val circlePx = dpToPx(circleDp)
                val lp = LinearLayout.LayoutParams(circlePx, circlePx)
                lp.topMargin = dpToPx(2)
                layoutParams = lp
                when {
                    esHoy -> {
                        setTextColor(Color.WHITE)
                        textSize = 15f
                        setTypeface(typeface, android.graphics.Typeface.BOLD)
                        background = circleDrawable(ContextCompat.getColor(ctx, R.color.brand))
                    }
                    esSel -> {
                        setTextColor(ContextCompat.getColor(ctx, R.color.brand))
                        background = circleDrawable(ContextCompat.getColor(ctx, R.color.brand_50))
                    }
                    else -> {
                        setTextColor(ContextCompat.getColor(ctx, R.color.ink))
                    }
                }
            }

            dayCol.addView(tvAbrev)
            dayCol.addView(tvNum)

            val finalI = i
            val finalDayCal = dayCal.clone() as Calendar
            dayCol.setOnClickListener {
                diaSeleccionado = finalDayCal
                construirVistaSemana()
            }

            layoutDayColumns.addView(dayCol)
        }

        // Build hour rows
        layoutHourRows.removeAllViews()
        for (h in HORA_INICIO..HORA_FIN) {
            val row = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, hourHeightPx)
                layoutParams = lp
            }

            val tvHour = TextView(ctx).apply {
                text = String.format("%02d:00", h)
                textSize = 10f
                setTextColor(ContextCompat.getColor(ctx, R.color.muted))
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                val lp = LinearLayout.LayoutParams(dpToPx(48), LinearLayout.LayoutParams.MATCH_PARENT)
                layoutParams = lp
                setPadding(dpToPx(4), dpToPx(4), dpToPx(4), 0)
            }

            val divider = View(ctx).apply {
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1))
                lp.topMargin = 0
                layoutParams = lp
                setBackgroundColor(ContextCompat.getColor(ctx, R.color.line))
            }

            val rowContent = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                layoutParams = lp
            }
            rowContent.addView(divider)

            row.addView(tvHour)
            row.addView(rowContent)
            layoutHourRows.addView(row)
        }

        // Build event columns
        layoutEventColumns.removeAllViews()
        val totalHours = HORA_FIN - HORA_INICIO + 1
        val totalHeightPx = totalHours * hourHeightPx

        for (i in 0..6) {
            val dayCal = lunes.clone() as Calendar
            dayCal.add(Calendar.DAY_OF_MONTH, i)

            val dayEventos = eventosEnDia(
                dayCal.get(Calendar.DAY_OF_MONTH),
                dayCal.get(Calendar.MONTH),
                dayCal.get(Calendar.YEAR)
            )

            val colFrame = FrameLayout(ctx).apply {
                val lp = LinearLayout.LayoutParams(0, totalHeightPx, 1f)
                layoutParams = lp
            }

            dayEventos.forEachIndexed { idx, evento ->
                val (topPx, heightPx) = calcEventPositionPx(evento, hourHeightPx)
                val color = COLORES_EVENTOS[idx % COLORES_EVENTOS.size]

                val eventView = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    val lp = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        maxOf(heightPx, dpToPx(24))
                    )
                    lp.topMargin = topPx
                    lp.marginStart = dpToPx(2)
                    lp.marginEnd = dpToPx(2)
                    layoutParams = lp
                    setBackgroundColor(Color.argb(30, Color.red(color), Color.green(color), Color.blue(color)))
                }

                val colorBar = View(ctx).apply {
                    val lp = LinearLayout.LayoutParams(dpToPx(3), LinearLayout.LayoutParams.MATCH_PARENT)
                    layoutParams = lp
                    setBackgroundColor(color)
                }

                val tvTitle = TextView(ctx).apply {
                    text = evento.titulo
                    textSize = 10f
                    setTextColor(color)
                    setPadding(dpToPx(3), dpToPx(2), dpToPx(2), dpToPx(2))
                    maxLines = 2
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams = lp
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

        // Header
        val hoy = Calendar.getInstance()
        val esHoy = diaSeleccionado.get(Calendar.DAY_OF_MONTH) == hoy.get(Calendar.DAY_OF_MONTH) &&
                diaSeleccionado.get(Calendar.MONTH) == hoy.get(Calendar.MONTH) &&
                diaSeleccionado.get(Calendar.YEAR) == hoy.get(Calendar.YEAR)

        val dow = diaSeleccionado.get(Calendar.DAY_OF_WEEK)
        val nombreDia = arrayOf("Domingo","Lunes","Martes","Miércoles","Jueves","Viernes","Sábado")[dow - 1]
        val nombreMes = MESES_GEN[diaSeleccionado.get(Calendar.MONTH)]
        val prefix = if (esHoy) "Hoy · " else ""
        tvDayViewTitle.text = "${prefix}${diaSeleccionado.get(Calendar.DAY_OF_MONTH)} $nombreMes $nombreDia"

        val eventosDelDia = eventosEnDia(
            diaSeleccionado.get(Calendar.DAY_OF_MONTH),
            diaSeleccionado.get(Calendar.MONTH),
            diaSeleccionado.get(Calendar.YEAR)
        )

        // Build hour rows
        layoutDayHourRows.removeAllViews()
        for (h in HORA_INICIO..HORA_FIN) {
            val row = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, hourHeightPx)
                layoutParams = lp
            }

            val tvHour = TextView(ctx).apply {
                text = String.format("%02d:00", h)
                textSize = 11f
                setTextColor(ContextCompat.getColor(ctx, R.color.muted))
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                val lp = LinearLayout.LayoutParams(dpToPx(52), LinearLayout.LayoutParams.MATCH_PARENT)
                layoutParams = lp
                setPadding(dpToPx(8), dpToPx(6), dpToPx(4), 0)
            }

            val lineContainer = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                layoutParams = lp
            }
            val line = View(ctx).apply {
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1))
                layoutParams = lp
                setBackgroundColor(ContextCompat.getColor(ctx, R.color.line))
            }
            lineContainer.addView(line)

            row.addView(tvHour)
            row.addView(lineContainer)
            layoutDayHourRows.addView(row)
        }

        // Build event overlays
        layoutDayEvents.removeAllViews()
        val totalHeightPx = (HORA_FIN - HORA_INICIO + 1) * hourHeightPx

        // Set height of event overlay frame
        val frameParams = layoutDayEvents.layoutParams
        if (frameParams != null) {
            frameParams.height = totalHeightPx
            layoutDayEvents.layoutParams = frameParams
        }

        if (eventosDelDia.isEmpty()) {
            layoutDayEmpty.visibility = View.VISIBLE
        } else {
            layoutDayEmpty.visibility = View.GONE

            eventosDelDia.forEachIndexed { idx, evento ->
                val (topPx, heightPx) = calcEventPositionPx(evento, hourHeightPx)
                val color = COLORES_EVENTOS[idx % COLORES_EVENTOS.size]

                val eventView = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    val lp = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        maxOf(heightPx, dpToPx(40))
                    )
                    lp.topMargin = topPx
                    lp.marginStart = dpToPx(56)
                    lp.marginEnd = dpToPx(12)
                    layoutParams = lp

                    val bgDrawable = android.graphics.drawable.GradientDrawable().apply {
                        setColor(Color.argb(25, Color.red(color), Color.green(color), Color.blue(color)))
                        cornerRadius = dpToPx(12).toFloat()
                    }
                    background = bgDrawable
                    setPadding(dpToPx(2), dpToPx(6), dpToPx(8), dpToPx(6))
                }

                val colorBar = View(ctx).apply {
                    val lp = LinearLayout.LayoutParams(dpToPx(4), LinearLayout.LayoutParams.MATCH_PARENT)
                    lp.marginEnd = dpToPx(8)
                    layoutParams = lp
                    val bg = android.graphics.drawable.GradientDrawable().apply {
                        setColor(color)
                        cornerRadius = dpToPx(4).toFloat()
                    }
                    background = bg
                }

                val infoLayout = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    layoutParams = lp
                }

                val tvTitle = TextView(ctx).apply {
                    text = evento.titulo
                    textSize = 13f
                    setTypeface(typeface, android.graphics.Typeface.BOLD)
                    setTextColor(color)
                    maxLines = 2
                }

                val hora = evento.fechaInicio.substringAfter(" ", "").trim()
                val horaFin = evento.fechaFin.substringAfter(" ", "").trim()
                val tvHora = TextView(ctx).apply {
                    text = if (hora.isNotEmpty() && horaFin.isNotEmpty()) "$hora – $horaFin" else hora
                    textSize = 11f
                    setTextColor(ContextCompat.getColor(ctx, R.color.muted))
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

    private fun circleDrawable(color: Int): android.graphics.drawable.Drawable {
        return android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.OVAL
            setColor(color)
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

        viewMes.visibility = if (vista == "mes") View.VISIBLE else View.GONE
        viewSemana.visibility = if (vista == "semana") View.VISIBLE else View.GONE
        viewDia.visibility = if (vista == "día") View.VISIBLE else View.GONE

        actualizarUI()
    }
}
