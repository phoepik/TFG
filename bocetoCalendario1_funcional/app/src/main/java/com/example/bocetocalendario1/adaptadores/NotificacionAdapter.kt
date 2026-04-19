package com.example.bocetocalendario1.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.datos.modelo.Notificacion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificacionAdapter(
    private var notificaciones: MutableList<Notificacion>,
    private val onAceptar: ((Notificacion) -> Unit)? = null,
    private val onRechazar: ((Notificacion) -> Unit)? = null,
    private val onClick: ((Notificacion) -> Unit)? = null
) : RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder>() {

    class NotificacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcono: TextView = view.findViewById(R.id.tvIcono)
        val tvTituloNotif: TextView = view.findViewById(R.id.tvTituloNotif)
        val tvMensaje: TextView = view.findViewById(R.id.tvMensaje)
        val tvTipo: TextView = view.findViewById(R.id.tvTipo)
        val tvTiempoAnticipacion: TextView = view.findViewById(R.id.tvTiempoAnticipacion)
        val layoutAcciones: LinearLayout = view.findViewById(R.id.layoutAcciones)
        val btnAceptar: Button = view.findViewById(R.id.btnAceptar)
        val btnRechazar: Button = view.findViewById(R.id.btnRechazar)
        val tvEstadoInvitacion: TextView = view.findViewById(R.id.tvEstadoInvitacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacion, parent, false)
        return NotificacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificacionViewHolder, position: Int) {
        val notif = notificaciones[position]
        holder.tvTituloNotif.text = notif.titulo
        holder.tvMensaje.text = notif.mensaje ?: ""
        holder.tvTipo.text = notif.tipo

        // Icono según tipo
        holder.tvIcono.text = when (notif.tipo) {
            "RECORDATORIO" -> "\uD83D\uDD14" // 🔔
            "INVITACION" -> "✉\uFE0F"
            "SISTEMA" -> "⚙\uFE0F"
            else -> "\uD83D\uDD14"
        }

        // Tiempo anticipación o fecha
        if (notif.tiempo_anticipacion != null && notif.tiempo_anticipacion > 0) {
            holder.tvTiempoAnticipacion.text = "⏰ ${notif.tiempo_anticipacion} min antes"
            holder.tvTiempoAnticipacion.visibility = View.VISIBLE
        } else {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            holder.tvTiempoAnticipacion.text = sdf.format(Date(notif.fecha_creacion))
            holder.tvTiempoAnticipacion.visibility = View.VISIBLE
        }

        // Fondo según leída
        if (!notif.leida) {
            holder.itemView.alpha = 1.0f
        } else {
            holder.itemView.alpha = 0.6f
        }

        // Botones de invitación
        if (notif.tipo == "INVITACION" && notif.estado_invitacion == "PENDIENTE") {
            holder.layoutAcciones.visibility = View.VISIBLE
            holder.tvEstadoInvitacion.visibility = View.GONE

            holder.btnAceptar.setOnClickListener {
                onAceptar?.invoke(notif)
            }
            holder.btnRechazar.setOnClickListener {
                onRechazar?.invoke(notif)
            }
        } else if (notif.tipo == "INVITACION" && notif.estado_invitacion != null) {
            holder.layoutAcciones.visibility = View.GONE
            holder.tvEstadoInvitacion.visibility = View.VISIBLE
            holder.tvEstadoInvitacion.text = when (notif.estado_invitacion) {
                "ACEPTADA" -> "✅ Aceptada"
                "RECHAZADA" -> "❌ Rechazada"
                else -> ""
            }
        } else {
            holder.layoutAcciones.visibility = View.GONE
            holder.tvEstadoInvitacion.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onClick?.invoke(notif)
        }
    }

    override fun getItemCount() = notificaciones.size

    fun actualizarLista(nuevas: List<Notificacion>) {
        notificaciones.clear()
        notificaciones.addAll(nuevas)
        notifyDataSetChanged()
    }
}
