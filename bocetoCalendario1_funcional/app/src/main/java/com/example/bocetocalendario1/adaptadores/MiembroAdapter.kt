package com.example.bocetocalendario1.adaptadores

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.models.Usuario

class MiembroAdapter(
    private val miembros: List<Usuario>,
    private val idAdmin: Int
) : RecyclerView.Adapter<MiembroAdapter.MiembroViewHolder>() {

    private val AVATAR_COLORS = listOf(
        "#0B5FFF", "#E94B7B", "#22C55E", "#8B5CF6", "#F97316", "#14B8A6"
    )

    class MiembroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvInicialMiembro: TextView = view.findViewById(R.id.tvInicialMiembro)
        val tvNombreMiembro: TextView = view.findViewById(R.id.tvNombreMiembro)
        val tvEmailMiembro: TextView = view.findViewById(R.id.tvEmailMiembro)
        val tvRol: TextView = view.findViewById(R.id.tvRol)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiembroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_miembro, parent, false)
        return MiembroViewHolder(view)
    }

    override fun onBindViewHolder(holder: MiembroViewHolder, position: Int) {
        val miembro = miembros[position]

        // Avatar with initials and dynamic color
        val initials = miembro.nombre.split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
            .ifEmpty { "U" }
        holder.tvInicialMiembro.text = initials

        val colorHex = AVATAR_COLORS[position % AVATAR_COLORS.size]
        val color = try { Color.parseColor(colorHex) } catch (e: Exception) { Color.parseColor("#0B5FFF") }
        val avatarBg = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
        }
        holder.tvInicialMiembro.background = avatarBg

        holder.tvNombreMiembro.text = miembro.nombre
        holder.tvEmailMiembro.text = miembro.email.ifEmpty { "miembro@grupo.com" }

        // Admin badge
        if (miembro.id == idAdmin) {
            holder.tvRol.visibility = View.VISIBLE
        } else {
            holder.tvRol.visibility = View.GONE
        }
    }

    override fun getItemCount() = miembros.size
}
