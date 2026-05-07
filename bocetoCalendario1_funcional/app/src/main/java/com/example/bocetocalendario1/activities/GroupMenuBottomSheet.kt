package com.example.bocetocalendario1.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.bocetocalendario1.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GroupMenuBottomSheet : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(grupoNombre: String, miembrosCount: Int, grupoIdx: Int): GroupMenuBottomSheet {
            val sheet = GroupMenuBottomSheet()
            val args = Bundle()
            args.putString("grupo_nombre", grupoNombre)
            args.putInt("miembros_count", miembrosCount)
            args.putInt("grupo_idx", grupoIdx)
            sheet.arguments = args
            return sheet
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_group_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nombre = arguments?.getString("grupo_nombre") ?: "Grupo"
        val count = arguments?.getInt("miembros_count") ?: 0
        val idx = arguments?.getInt("grupo_idx") ?: 0

        view.findViewById<TextView>(R.id.tvGroupMenuNombre).text = nombre
        view.findViewById<TextView>(R.id.tvGroupMenuMiembros).text = "$count miembros"

        val groupColors = listOf("#0B5FFF","#E94B7B","#22C55E","#8B5CF6","#F97316","#14B8A6")
        val colorHex = groupColors[idx % groupColors.size]
        val color = try { android.graphics.Color.parseColor(colorHex) } catch (e: Exception) { android.graphics.Color.parseColor("#0B5FFF") }

        val emojiCircle = view.findViewById<View>(R.id.viewGroupMenuEmoji)
        emojiCircle?.background = android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.OVAL
            setColor(android.graphics.Color.argb(40, android.graphics.Color.red(color), android.graphics.Color.green(color), android.graphics.Color.blue(color)))
        }

        view.findViewById<LinearLayout>(R.id.rowSilenciar).setOnClickListener {
            Toast.makeText(context, "Notificaciones silenciadas", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        view.findViewById<LinearLayout>(R.id.rowEditarGrupo).setOnClickListener {
            Toast.makeText(context, "Editar grupo (próximamente)", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        view.findViewById<LinearLayout>(R.id.rowGestionarMiembros).setOnClickListener {
            Toast.makeText(context, "Gestionar miembros (próximamente)", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        view.findViewById<LinearLayout>(R.id.rowSalirGrupo).setOnClickListener {
            Toast.makeText(context, "Has salido del grupo", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
}
