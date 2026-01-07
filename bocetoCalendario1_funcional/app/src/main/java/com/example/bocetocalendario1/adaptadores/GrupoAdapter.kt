package com.example.bocetocalendario1.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.models.Grupo

class GrupoAdapter(
    private val grupos: List<Grupo>,
    private val onClick: (Grupo) -> Unit
) : RecyclerView.Adapter<GrupoAdapter.GrupoViewHolder>() {

    class GrupoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreGrupo: TextView = view.findViewById(R.id.tvNombreGrupo)
        val tvDescripcionGrupo: TextView = view.findViewById(R.id.tvDescripcionGrupo)
        val tvMiembros: TextView = view.findViewById(R.id.tvMiembros)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrupoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grupo, parent, false)
        return GrupoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GrupoViewHolder, position: Int) {
        val grupo = grupos[position]
        holder.tvNombreGrupo.text = grupo.nombre
        holder.tvDescripcionGrupo.text = grupo.descripcion
        holder.tvMiembros.text = " ${grupo.miembros} miembros"
        
        holder.itemView.setOnClickListener { onClick(grupo) }
    }

    override fun getItemCount() = grupos.size
}
