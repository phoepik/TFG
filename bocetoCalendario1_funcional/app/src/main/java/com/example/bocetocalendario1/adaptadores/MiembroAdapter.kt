package com.example.bocetocalendario1.adaptadores

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

    class MiembroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        holder.tvNombreMiembro.text = miembro.nombre
        holder.tvEmailMiembro.text = miembro.email
        
        // true si eres admin
        if (miembro.id == idAdmin) {
            holder.tvRol.visibility = View.VISIBLE
            holder.tvRol.text = "Admin"
        } else {
            holder.tvRol.visibility = View.GONE
        }
    }

    override fun getItemCount() = miembros.size
}
