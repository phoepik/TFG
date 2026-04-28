package com.example.bocetocalendario1.adaptadores

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.models.Evento

// Elemento de la lista: header de día o evento
sealed class EventoListItem {
    data class Header(val texto: String) : EventoListItem()
    data class EventoItem(val evento: Evento) : EventoListItem()
}

class EventoAdapter(
    eventos: List<Evento>,
    private val onClick: (Evento) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<EventoListItem> = agruparPorDia(eventos)

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
                result.add(EventoListItem.Header(formatearFechaHeader(fecha)))
                evts.forEach { result.add(EventoListItem.EventoItem(it)) }
            }
            return result
        }

        private fun extraerFecha(fechaInicio: String): String =
            fechaInicio.substringBefore(" ").trim()

        private fun formatearFechaHeader(fecha: String): String {
            return try {
                val partes = fecha.split("/")
                if (partes.size < 3) return fecha
                val dia = partes[0].toInt()
                val mes = partes[1].toInt()
                val anio = partes[2].toInt()
                val cal = java.util.Calendar.getInstance()
                cal.set(anio, mes - 1, dia)
                val nombreDia = arrayOf("Dom","Lun","Mar","Mié","Jue","Vie","Sáb")[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1]
                val nombreMes = arrayOf("enero","febrero","marzo","abril","mayo","junio","julio","agosto","septiembre","octubre","noviembre","diciembre")[mes - 1]
                "$nombreDia, $dia de $nombreMes"
            } catch (e: Exception) { fecha }
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHeader: TextView = view.findViewById(R.id.tvHeaderFecha)
    }

    class EventoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvFechaInicio: TextView = view.findViewById(R.id.tvFechaInicio)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val tvUbicacion: TextView = view.findViewById(R.id.tvUbicacion)
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
                (holder as HeaderViewHolder).tvHeader.text = item.texto
            }
            is EventoListItem.EventoItem -> {
                val h = holder as EventoViewHolder
                val evento = item.evento
                h.tvTitulo.text = evento.titulo
                h.tvDescripcion.text = evento.descripcion

                val hora = evento.fechaInicio.substringAfter(" ", "").trim()
                h.tvFechaInicio.text = if (hora.isNotEmpty()) hora else evento.fechaInicio

                h.tvEstado.text = evento.estado
                val ctx = holder.itemView.context
                if (evento.estado == "CONFIRMADO") {
                    h.tvEstado.setTextColor(ctx.getColor(R.color.cat_green))
                    h.tvEstado.background = ctx.getDrawable(R.drawable.bg_chip)
                } else {
                    h.tvEstado.setTextColor(Color.parseColor("#F97316"))
                    h.tvEstado.background = null
                }

                if (!evento.ubicacion.isNullOrBlank()) {
                    h.tvUbicacion.text = "📍 ${evento.ubicacion}"
                    h.tvUbicacion.visibility = View.VISIBLE
                } else {
                    h.tvUbicacion.visibility = View.GONE
                }

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
