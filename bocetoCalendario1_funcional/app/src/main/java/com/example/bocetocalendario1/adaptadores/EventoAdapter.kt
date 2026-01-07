package com.example.bocetocalendario1.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.models.Evento

class EventoAdapter(
    private val eventos: List<Evento>,
    private val onClick: (Evento) -> Unit
) : RecyclerView.Adapter<EventoAdapter.EventoViewHolder>() {

    class EventoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvFechaInicio: TextView = view.findViewById(R.id.tvFechaInicio)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val tvUbicacion: TextView = view.findViewById(R.id.tvUbicacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evento, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = eventos[position]
        holder.tvTitulo.text = evento.titulo
        holder.tvDescripcion.text = evento.descripcion
        holder.tvFechaInicio.text = " ${evento.fechaInicio}"
        holder.tvEstado.text = evento.estado
        holder.tvUbicacion.text = " ${evento.ubicacion}"
        
        // segun su estado
        if (evento.estado == "CONFIRMADO") {
            holder.tvEstado.setTextColor(holder.itemView.context.getColor(R.color.green))
        } else {
            holder.tvEstado.setTextColor(holder.itemView.context.getColor(R.color.orange))
        }
        
        holder.itemView.setOnClickListener { onClick(evento) }
    }

    override fun getItemCount() = eventos.size
}
