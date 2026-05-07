package com.example.bocetocalendario1.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.datos.modelo.Grupo

class GrupoAdapter(
    private val grupos: List<Grupo>,
    private val onClick: (Grupo) -> Unit
) : RecyclerView.Adapter<GrupoAdapter.GrupoViewHolder>() {

    // Emojis and gradients matching the design
    private val EMOJIS = listOf("🎉", "🎨", "🏡", "📚", "⚽", "🏖️", "🍕", "🎬", "🎵", "🚀")
    private val BACKGROUNDS = listOf(
        R.drawable.bg_group_magenta,
        R.drawable.bg_group_blue,
        R.drawable.bg_group_green,
        R.drawable.bg_group_purple,
        R.drawable.bg_group_orange,
        R.drawable.bg_group_teal,
    )

    class GrupoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreGrupo: TextView = view.findViewById(R.id.tvNombreGrupo)
        val tvEmojiGrupo: TextView = view.findViewById(R.id.tvEmojiGrupo)
        val tvEventosCount: TextView = view.findViewById(R.id.tvEventosCount)
        val tvMiembrosCount: TextView = view.findViewById(R.id.tvMiembrosCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrupoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grupo, parent, false)
        return GrupoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GrupoViewHolder, position: Int) {
        val grupo = grupos[position]

        holder.tvNombreGrupo.text = grupo.nombre
        holder.itemView.setBackgroundResource(BACKGROUNDS[position % BACKGROUNDS.size])

        // Pick emoji: try to extract from name or use cycle
        val emoji = EMOJIS[position % EMOJIS.size]
        holder.tvEmojiGrupo.text = emoji

        // Show description as subtitle if available, otherwise members count
        val desc = grupo.descripcion
        holder.tvEventosCount.text = if (!desc.isNullOrBlank()) desc else "Ver grupo"
        holder.tvMiembrosCount.text = ""

        holder.itemView.setOnClickListener { onClick(grupo) }
    }

    override fun getItemCount() = grupos.size
}
