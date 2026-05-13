package com.example.bocetocalendario1.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.network.RetrofitClient
import com.example.bocetocalendario1.utilidades.GestorSesion
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class GroupMenuBottomSheet : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(grupoNombre: String, miembrosCount: Int, grupoIdx: Int, grupoId: Int = 0): GroupMenuBottomSheet {
            val sheet = GroupMenuBottomSheet()
            val args = Bundle()
            args.putString("grupo_nombre", grupoNombre)
            args.putInt("miembros_count", miembrosCount)
            args.putInt("grupo_idx", grupoIdx)
            args.putInt("grupo_id", grupoId)
            sheet.arguments = args
            return sheet
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.bottom_sheet_group_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nombre  = arguments?.getString("grupo_nombre") ?: "Grupo"
        val count   = arguments?.getInt("miembros_count") ?: 0
        val idx     = arguments?.getInt("grupo_idx") ?: 0
        val grupoId = arguments?.getInt("grupo_id") ?: 0

        view.findViewById<TextView>(R.id.tvGroupMenuNombre).text = nombre
        view.findViewById<TextView>(R.id.tvGroupMenuMiembros).text = "$count miembros"

        val groupColors = listOf("#0B5FFF","#E94B7B","#22C55E","#8B5CF6","#F97316","#14B8A6")
        val colorHex = groupColors[idx % groupColors.size]
        val color = try { android.graphics.Color.parseColor(colorHex) } catch (e: Exception) { android.graphics.Color.parseColor("#0B5FFF") }

        view.findViewById<View>(R.id.viewGroupMenuEmoji)?.background =
            android.graphics.drawable.GradientDrawable().apply {
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

        // ── Salir del grupo: implementado ──
        view.findViewById<LinearLayout>(R.id.rowSalirGrupo).setOnClickListener {
            val gestorSesion = GestorSesion(requireContext())
            val idUsuario = gestorSesion.obtenerIdUsuario() ?: return@setOnClickListener

            if (grupoId == 0) {
                Toast.makeText(context, "Error: ID de grupo no disponible", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = RetrofitClient.api.eliminarMiembro(idUsuario, grupoId)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Has salido de \"$nombre\"", Toast.LENGTH_SHORT).show()
                            dismiss()
                            activity?.finish()
                        } else {
                            Toast.makeText(context, "Error al salir del grupo", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
