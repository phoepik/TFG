package com.example.bocetocalendario1.adaptadores

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.models.Evento
import java.util.Calendar

// Elemento de la lista: header de día o evento
sealed class EventoListItem {
    data class Header(
        val dia: String,
        val nombreDia: String,
        val nombreMes: String,
        val esHoy: Boolean
    ) : EventoListItem()
    data class EventoItem(val evento: Evento) : EventoListItem()
}

class EventoAdapter(
    eventos: List<Evento>,
    private val onClick: (Evento) -> Unit,
    private val nombresCalendario: Map<Int, String> = emptyMap()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<EventoListItem> = agruparPorDia(eventos)

    // Colors matching the 6 group gradients: brand, magenta, green, purple, orange, teal
    private val CAT_COLORS = listOf(
        "#0B5FFF", "#E94B7B", "#22C55E", "#8B5CF6", "#F97316", "#14B8A6"
    )

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_EVENT = 1

        fun agruparPorDia(eventos: List<Evento>): List<EventoListItem> {
            if (eventos.isEmpty()) return emptyList()
            val grupos = eventos
                .sortedBy { it.fechaInicio }
                .groupBy { extraerFecha(it.fechaInicio) }
            val result = mutableListOf<EventoListItem>()
            grupos.forEach { (fecha, evts) ->
                result.add(parsearHeader(fecha))
                evts.forEach { result.add(EventoListItem.EventoItem(it)) }
            }
            return result
        }

        private fun extraerFecha(fechaInicio: String): String =
            fechaInicio.substringBefore(" ").trim()

        private fun parsearHeader(fecha: String): EventoListItem.Header {
            return try {
                val partes = fecha.split("/")
                if (partes.size < 3) return EventoListItem.Header(fecha, "", "", false)
                val dia = partes[0].toInt()
                val mes = partes[1].toInt()
                val anio = partes[2].toInt()

                val cal = Calendar.getInstance()
                cal.set(anio, mes - 1, dia)

                val hoy = Calendar.getInstance()
                val esHoy = dia == hoy.get(Calendar.DAY_OF_MONTH)
                        && mes - 1 == hoy.get(Calendar.MONTH)
                        && anio == hoy.get(Calendar.YEAR)

                val nombreDia = arrayOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")[cal.get(Calendar.DAY_OF_WEEK) - 1]
                val nombreMes = arrayOf("enero", "febrero", "marzo", "abril", "mayo", "junio",
                    "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")[mes - 1]

                EventoListItem.Header(dia.toString(), nombreDia, nombreMes, esHoy)
            } catch (e: Exception) {
                EventoListItem.Header(fecha, "", "", false)
            }
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDayNumber: TextView = view.findViewById(R.id.tvHeaderDayNumber)
        val tvWeekday: TextView = view.findViewById(R.id.tvHeaderWeekday)
        val tvMonth: TextView = view.findViewById(R.id.tvHeaderMonth)
        val tvHoyChip: TextView = view.findViewById(R.id.tvHeaderHoyChip)
    }

    class EventoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewColorBar: View = view.findViewById(R.id.viewColorBar)
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvHoraInicio: TextView = view.findViewById(R.id.tvHoraInicio)
        val tvUbicacion: TextView = view.findViewById(R.id.tvUbicacion)
        val tvGrupoChip: TextView = view.findViewById(R.id.tvGrupoChip)
    }

    override fun getItemViewType(position: Int) = when (items[position]) {
        is EventoListItem.Header -> TYPE_HEADER
        is EventoListItem.EventoItem -> TYPE_EVENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_evento_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_evento, parent, false)
            EventoViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is EventoListItem.Header -> {
                val h = holder as HeaderViewHolder
                val brandColor = holder.itemView.context.getColor(R.color.brand)
                val inkColor = holder.itemView.context.getColor(R.color.ink)

                h.tvDayNumber.text = item.dia
                h.tvWeekday.text = item.nombreDia
                h.tvMonth.text = item.nombreMes

                val textColor = if (item.esHoy) brandColor else inkColor
                h.tvDayNumber.setTextColor(textColor)
                h.tvWeekday.setTextColor(textColor)

                h.tvHoyChip.visibility = if (item.esHoy) View.VISIBLE else View.GONE
            }
            is EventoListItem.EventoItem -> {
                val h = holder as EventoViewHolder
                val evento = item.evento

                // Dynamic category color based on calendar id
                val colorHex = CAT_COLORS[evento.idCalendario % CAT_COLORS.size]
                val catColor = try { Color.parseColor(colorHex) } catch (e: Exception) { Color.parseColor("#0B5FFF") }

                // Color bar
                h.viewColorBar.setBackgroundColor(catColor)

                // Title
                h.tvTitulo.text = evento.titulo

                // Time
                val hora = evento.fechaInicio.substringAfter(" ", "").trim()
                h.tvHoraInicio.text = hora

                // Location
                if (!evento.ubicacion.isNullOrBlank()) {
                    h.tvUbicacion.text = "📍 ${evento.ubicacion}"
                    h.tvUbicacion.visibility = View.VISIBLE
                } else {
                    h.tvUbicacion.visibility = View.GONE
                }

                // Group chip
                val calNombre = nombresCalendario[evento.idCalendario]
                    ?: if (evento.idCalendario > 0) "Cal. ${evento.idCalendario}" else "Personal"
                h.tvGrupoChip.text = "📅 $calNombre"
                h.tvGrupoChip.setTextColor(catColor)

                // Chip background: category color at ~13% opacity
                val chipBg = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 999f
                    setColor(Color.argb(33, Color.red(catColor), Color.green(catColor), Color.blue(catColor)))
                }
                h.tvGrupoChip.background = chipBg

                holder.itemView.setOnClickListener { onClick(evento) }
            }
        }
    }

    override fun getItemCount() = items.size

    fun actualizar(eventos: List<Evento>) {
        items = agruparPorDia(eventos)
        notifyDataSetChanged()
    }
}
