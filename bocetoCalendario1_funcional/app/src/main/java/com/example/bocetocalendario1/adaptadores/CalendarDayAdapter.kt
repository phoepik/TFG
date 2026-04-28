package com.example.bocetocalendario1.adaptadores

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import java.util.Calendar

data class DiaCelda(
    val dia: Int,           // 0 = celda vacía
    val esMesActual: Boolean,
    val esHoy: Boolean,
    val esSeleccionado: Boolean,
    val coloresEventos: List<Int>  // colores de los dots
)

class CalendarDayAdapter(
    private var celdas: List<DiaCelda>,
    private val onDiaClick: (Int) -> Unit  // día (1-31)
) : RecyclerView.Adapter<CalendarDayAdapter.DiaViewHolder>() {

    private val COLORS = listOf(
        Color.parseColor("#0B5FFF"),
        Color.parseColor("#E94B7B"),
        Color.parseColor("#22C55E"),
        Color.parseColor("#8B5CF6"),
        Color.parseColor("#F97316"),
        Color.parseColor("#14B8A6"),
    )

    class DiaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDia: TextView = view.findViewById(R.id.tvDia)
        val llDots: LinearLayout = view.findViewById(R.id.llDots)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dia_calendario, parent, false)
        return DiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaViewHolder, position: Int) {
        val celda = celdas[position]

        if (celda.dia == 0) {
            holder.tvDia.text = ""
            holder.tvDia.background = null
            holder.llDots.removeAllViews()
            holder.itemView.setOnClickListener(null)
            return
        }

        holder.tvDia.text = celda.dia.toString()

        // Fondo del número del día
        val bgDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
        }

        when {
            celda.esHoy -> {
                bgDrawable.setColor(Color.parseColor("#0B5FFF"))
                holder.tvDia.setTextColor(Color.WHITE)
                holder.tvDia.background = bgDrawable
            }
            celda.esSeleccionado -> {
                bgDrawable.setColor(Color.parseColor("#EAF1FF"))
                bgDrawable.setStroke(2, Color.parseColor("#0B5FFF"))
                holder.tvDia.setTextColor(Color.parseColor("#0B5FFF"))
                holder.tvDia.background = bgDrawable
            }
            !celda.esMesActual -> {
                holder.tvDia.setTextColor(Color.parseColor("#98A0BC"))
                holder.tvDia.background = null
            }
            else -> {
                holder.tvDia.setTextColor(Color.parseColor("#0A1330"))
                holder.tvDia.background = null
            }
        }

        // Dots de eventos
        holder.llDots.removeAllViews()
        val colores = celda.coloresEventos.take(3)
        colores.forEach { color ->
            val dot = View(holder.itemView.context).apply {
                val size = (6 * holder.itemView.context.resources.displayMetrics.density).toInt()
                val params = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = (2 * holder.itemView.context.resources.displayMetrics.density).toInt()
                }
                layoutParams = params
                val dotDrawable = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    val dotColor = if (celda.esHoy) Color.WHITE else color
                    setColor(dotColor)
                }
                background = dotDrawable
            }
            holder.llDots.addView(dot)
        }

        holder.itemView.setOnClickListener {
            if (celda.dia > 0) onDiaClick(celda.dia)
        }
    }

    override fun getItemCount() = celdas.size

    fun actualizar(nuevasCeldas: List<DiaCelda>) {
        celdas = nuevasCeldas
        notifyDataSetChanged()
    }
}
