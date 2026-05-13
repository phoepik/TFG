package com.example.bocetocalendario1.activities

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.network.CalendarioResponse
import com.example.bocetocalendario1.network.GrupoResponse
import com.example.bocetocalendario1.network.RetrofitClient
import com.example.bocetocalendario1.utilidades.GestorSesion
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateGroupBottomSheet : BottomSheetDialogFragment() {

    private val emojis = listOf("🎉","🎨","🏡","📚","⚽","🏖️","🍕","🎬","🎵","🚀","💼","✈️")
    private val gradientColors = listOf(
        "#0B5FFF" to "#5B8DFF",
        "#E94B7B" to "#FF8FAB",
        "#22C55E" to "#4ADE80",
        "#8B5CF6" to "#A78BFA",
        "#F97316" to "#FB923C",
        "#14B8A6" to "#2DD4BF"
    )

    private var selectedEmoji    = "🎉"
    private var selectedColorIdx = 0
    private var groupName        = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.bottom_sheet_create_group, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etNombre       = view.findViewById<EditText>(R.id.etGroupName)
        val tvPreviewEmoji = view.findViewById<TextView>(R.id.tvPreviewEmoji)
        val tvPreviewName  = view.findViewById<TextView>(R.id.tvPreviewName)
        val layoutEmojis   = view.findViewById<GridLayout>(R.id.gridEmojis)
        val layoutColors   = view.findViewById<LinearLayout>(R.id.layoutColorSwatches)
        val btnCrear       = view.findViewById<Button>(R.id.btnCrearGrupo)

        tvPreviewEmoji.text = selectedEmoji
        tvPreviewName.text  = "Nombre del grupo"

        // Emoji grid
        emojis.forEach { emoji ->
            val tv = TextView(requireContext()).apply {
                text     = emoji
                textSize = 24f
                gravity  = android.view.Gravity.CENTER
                val sizePx   = (52 * resources.displayMetrics.density).toInt()
                val marginPx = (4  * resources.displayMetrics.density).toInt()
                val lp = GridLayout.LayoutParams()
                lp.width  = sizePx; lp.height = sizePx
                lp.setMargins(marginPx, marginPx, marginPx, marginPx)
                layoutParams = lp
                setOnClickListener { selectedEmoji = emoji; tvPreviewEmoji.text = emoji }
            }
            layoutEmojis.addView(tv)
        }

        // Color swatches
        val density = resources.displayMetrics.density
        gradientColors.forEachIndexed { idx, (c1, c2) ->
            val swatch = View(requireContext()).apply {
                val sizePx = (40 * density).toInt()
                val lp     = LinearLayout.LayoutParams(sizePx, sizePx)
                lp.marginEnd = (10 * density).toInt()
                layoutParams = lp
                background = android.graphics.drawable.GradientDrawable(
                    android.graphics.drawable.GradientDrawable.Orientation.TL_BR,
                    intArrayOf(Color.parseColor(c1), Color.parseColor(c2))
                ).apply { cornerRadius = 20f * density }
                setOnClickListener { selectedColorIdx = idx }
            }
            layoutColors.addView(swatch)
        }

        etNombre.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                groupName          = s?.toString()?.trim() ?: ""
                tvPreviewName.text = if (groupName.isEmpty()) "Nombre del grupo" else groupName
                btnCrear.isEnabled = groupName.isNotEmpty()
                btnCrear.alpha     = if (groupName.isNotEmpty()) 1f else 0.5f
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        btnCrear.isEnabled = false
        btnCrear.alpha     = 0.5f

        btnCrear.setOnClickListener {
            val gestorSesion = GestorSesion(requireContext())
            val idUsuario    = gestorSesion.obtenerIdUsuario() ?: return@setOnClickListener

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val grupoResp = RetrofitClient.api.crearGrupo(
                        GrupoResponse(nombre = groupName, descripcion = selectedEmoji, idAdmin = idUsuario)
                    )

                    if (grupoResp.isSuccessful && grupoResp.body() != null) {
                        val idGrupo = grupoResp.body()!!.idGrupo

                        // Crear calendario grupal automáticamente
                        try {
                            RetrofitClient.api.crearCalendario(
                                CalendarioResponse(
                                    nombre        = groupName,
                                    tipo          = "GRUPAL",
                                    idPropietario = idUsuario,
                                    idGrupo       = idGrupo
                                )
                            )
                        } catch (e: Exception) {
                            Log.e("CREATE_GROUP", "Error creando calendario grupal: ${e.message}")
                        }

                        // Añadir al creador como miembro del grupo
                        try {
                            RetrofitClient.api.anadirMiembro(
                                mapOf("idUsuario" to idUsuario, "idGrupo" to (idGrupo ?: 0))
                            )
                        } catch (e: Exception) {
                            Log.e("CREATE_GROUP", "Error añadiendo admin como miembro: ${e.message}")
                        }

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Grupo \"$groupName\" creado", Toast.LENGTH_SHORT).show()
                            // Notificar a GruposFragment para que recargue la lista
                            parentFragmentManager.setFragmentResult("grupo_creado", android.os.Bundle())
                            dismiss()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error al crear grupo", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("CREATE_GROUP", "Error: ${e.message}")
                        Toast.makeText(context, "Error al conectar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}