package com.example.bocetocalendario1.adaptadores

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R

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

    class DiaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDia: TextView = view.findViewById(R.id.tvDia)
        val llDots: LinearLayout = view.findViewById(R.id.llDots)
        val cellContainer: LinearLayout = view.findViewById(R.id.cellContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dia_calendario, parent, false)
        return DiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaViewHolder, position: Int) {
        val celda = celdas[position]
        val ctx = holder.itemView.context

        if (celda.dia == 0) {
            holder.tvDia.text = ""
            holder.cellContainer.background = null
            holder.llDots.removeAllViews()
            holder.itemView.setOnClickListener(null)
            return
        }

        holder.tvDia.text = celda.dia.toString()

        // Apply background and text color based on state
        when {
            celda.esHoy -> {
                holder.cellContainer.background =
                    ContextCompat.getDrawable(ctx, R.drawable.bg_cal_cell_today)
                holder.tvDia.setTextColor(Color.WHITE)
            }
            celda.esSeleccionado -> {
                holder.cellContainer.background =
                    ContextCompat.getDrawable(ctx, R.drawable.bg_cal_cell_selected)
                holder.tvDia.setTextColor(ContextCompat.getColor(ctx, R.color.brand))
            }
            !celda.esMesActual -> {
                holder.cellContainer.background = null
                holder.tvDia.setTextColor(ContextCompat.getColor(ctx, R.color.muted_2))
            }
            else -> {
                holder.cellContainer.background = null
                holder.tvDia.setTextColor(ContextCompat.getColor(ctx, R.color.ink))
            }
        }

        // Pip dots for events
        holder.llDots.removeAllViews()
        val density = ctx.resources.displayMetrics.density
        val dotSize = (6 * density).toInt()
        val dotMargin = (2 * density).toInt()

        celda.coloresEventos.take(4).forEach { color ->
            val dotColor = if (celda.esHoy) Color.WHITE else color
            val dot = View(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(dotSize, dotSize).apply {
                    marginEnd = dotMargin
                }
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(dotColor)
                }
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
