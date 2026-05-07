package com.example.bocetocalendario1.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.models.Evento
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar

class EventDetalleBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_EVENTO_ID = "evento_id"
        private const val ARG_TITULO = "titulo"
        private const val ARG_DESCRIPCION = "descripcion"
        private const val ARG_FECHA_INICIO = "fecha_inicio"
        private const val ARG_FECHA_FIN = "fecha_fin"
        private const val ARG_UBICACION = "ubicacion"
        private const val ARG_ESTADO = "estado"
        private const val ARG_ID_CALENDARIO = "id_calendario"

        fun newInstance(evento: Evento): EventDetalleBottomSheet {
            val sheet = EventDetalleBottomSheet()
            val args = Bundle()
            args.putInt(ARG_EVENTO_ID, evento.id)
            args.putString(ARG_TITULO, evento.titulo)
            args.putString(ARG_DESCRIPCION, evento.descripcion)
            args.putString(ARG_FECHA_INICIO, evento.fechaInicio)
            args.putString(ARG_FECHA_FIN, evento.fechaFin)
            args.putString(ARG_UBICACION, evento.ubicacion)
            args.putString(ARG_ESTADO, evento.estado)
            args.putInt(ARG_ID_CALENDARIO, evento.idCalendario)
            sheet.arguments = args
            return sheet
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_evento_detalle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titulo = arguments?.getString(ARG_TITULO) ?: ""
        val descripcion = arguments?.getString(ARG_DESCRIPCION) ?: ""
        val fechaInicio = arguments?.getString(ARG_FECHA_INICIO) ?: ""
        val fechaFin = arguments?.getString(ARG_FECHA_FIN) ?: ""
        val ubicacion = arguments?.getString(ARG_UBICACION) ?: ""
        val estado = arguments?.getString(ARG_ESTADO) ?: "PENDIENTE"
        val idCalendario = arguments?.getInt(ARG_ID_CALENDARIO) ?: 0

        // Set title
        view.findViewById<TextView>(R.id.tvTituloDetalle).text = titulo

        // Group badge
        val badgeColors = listOf("#0B5FFF", "#E94B7B", "#22C55E", "#8B5CF6", "#F97316", "#14B8A6")
        val badgeColor = try {
            android.graphics.Color.parseColor(badgeColors[idCalendario % badgeColors.size])
        } catch (e: Exception) {
            android.graphics.Color.parseColor("#0B5FFF")
        }
        val tvGrupoBadge = view.findViewById<TextView>(R.id.tvGrupoBadge)
        tvGrupoBadge.text = "📅 Calendario ${idCalendario}"
        tvGrupoBadge.setTextColor(badgeColor)

        // Date/time
        val tvFecha = view.findViewById<TextView>(R.id.tvFechaDetalle)
        val tvHora = view.findViewById<TextView>(R.id.tvHoraDetalle)

        tvFecha.text = formatearFechaLarga(fechaInicio)
        val horaInicio = fechaInicio.substringAfter(" ", "").trim()
        val horaFin = fechaFin.substringAfter(" ", "").trim()
        tvHora.text = if (horaInicio.isNotEmpty() && horaFin.isNotEmpty()) {
            "$horaInicio – $horaFin"
        } else if (horaInicio.isNotEmpty()) {
            horaInicio
        } else {
            "Todo el día"
        }

        // Location
        val layoutUbicacion = view.findViewById<LinearLayout>(R.id.layoutUbicacionDetalle)
        val tvUbicacion = view.findViewById<TextView>(R.id.tvUbicacionDetalle)
        if (ubicacion.isNotBlank()) {
            layoutUbicacion.visibility = View.VISIBLE
            tvUbicacion.text = ubicacion
        } else {
            layoutUbicacion.visibility = View.GONE
        }

        // Description
        val layoutDesc = view.findViewById<LinearLayout>(R.id.layoutDescripcionDetalle)
        val tvDesc = view.findViewById<TextView>(R.id.tvDescripcionDetalle)
        if (descripcion.isNotBlank()) {
            layoutDesc.visibility = View.VISIBLE
            tvDesc.text = descripcion
        } else {
            layoutDesc.visibility = View.GONE
        }

        // Close button
        view.findViewById<TextView>(R.id.btnCerrarSheet).setOnClickListener {
            dismiss()
        }

        // Action buttons
        view.findViewById<Button>(R.id.btnEditar).setOnClickListener {
            Toast.makeText(context, "Editar evento (próximamente)", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btnVoy).setOnClickListener {
            Toast.makeText(context, "¡Confirmado! Asistiré a $titulo", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun formatearFechaLarga(fechaInicio: String): String {
        return try {
            val fecha = fechaInicio.substringBefore(" ").trim()
            val partes = fecha.split("/")
            if (partes.size < 3) return fechaInicio
            val dia = partes[0].toInt()
            val mes = partes[1].toInt()
            val anio = partes[2].toInt()
            val cal = Calendar.getInstance()
            cal.set(anio, mes - 1, dia)
            val nombreDia = arrayOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")[cal.get(Calendar.DAY_OF_WEEK) - 1]
            val nombreMes = arrayOf("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")[mes - 1]
            "$nombreDia, $dia de $nombreMes de $anio"
        } catch (e: Exception) { fechaInicio }
    }
}
