package com.example.bocetocalendario1.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.models.Notificacion

class NotificacionAdapter(
    private val notificaciones: List<Notificacion>
) : RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder>() {

    class NotificacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcono: TextView = view.findViewById(R.id.tvIcono)
        val tvTituloNotif: TextView = view.findViewById(R.id.tvTituloNotif)
        val tvMensaje: TextView = view.findViewById(R.id.tvMensaje)
        val tvTipo: TextView = view.findViewById(R.id.tvTipo)
        val tvTiempoAnticipacion: TextView = view.findViewById(R.id.tvTiempoAnticipacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacion, parent, false)
        return NotificacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificacionViewHolder, position: Int) {
        val notif = notificaciones[position]
        holder.tvTituloNotif.text = notif.titulo
        holder.tvMensaje.text = notif.mensaje
        holder.tvTipo.text = notif.tipo
        holder.tvTiempoAnticipacion.text = "⏰ ${notif.tiempoAnticipacion} min antes"
        
        // icono según tipo
        holder.tvIcono.text = when(notif.tipo) {
            "RECORDATORIO" -> "🔔"
            "INVITACION" -> "✉️"
            "SISTEMA" -> "⚙️"
            else -> "🔔"
        }
    }

    override fun getItemCount() = notificaciones.size
}
