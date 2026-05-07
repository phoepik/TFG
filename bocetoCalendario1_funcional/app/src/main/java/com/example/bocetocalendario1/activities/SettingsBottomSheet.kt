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

class SettingsBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<LinearLayout>(R.id.rowZonaHoraria)?.setOnClickListener {
            Toast.makeText(context, "Zona horaria (próximamente)", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<LinearLayout>(R.id.rowContactarSoporte)?.setOnClickListener {
            Toast.makeText(context, "Contactar soporte (próximamente)", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<LinearLayout>(R.id.rowTerminos)?.setOnClickListener {
            Toast.makeText(context, "Términos de uso (próximamente)", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<LinearLayout>(R.id.rowPrivacidad)?.setOnClickListener {
            Toast.makeText(context, "Política de privacidad (próximamente)", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<LinearLayout>(R.id.rowAcercaDe)?.setOnClickListener {
            Toast.makeText(context, "CalendarioApp v1.0", Toast.LENGTH_SHORT).show()
        }

        // Week start toggle
        val btnLunes = view.findViewById<TextView>(R.id.btnSemanaLunes)
        val btnDomingo = view.findViewById<TextView>(R.id.btnSemanaDomingo)
        var startsLunes = true

        fun updateWeekToggle() {
            val brandColor = requireContext().getColor(R.color.brand)
            val surfaceColor = requireContext().getColor(R.color.surface)
            val inkColor = requireContext().getColor(R.color.ink)
            val mutedColor = requireContext().getColor(R.color.muted)

            btnLunes?.setTextColor(if (startsLunes) surfaceColor else mutedColor)
            btnLunes?.setBackgroundColor(if (startsLunes) brandColor else android.graphics.Color.TRANSPARENT)

            btnDomingo?.setTextColor(if (!startsLunes) surfaceColor else mutedColor)
            btnDomingo?.setBackgroundColor(if (!startsLunes) brandColor else android.graphics.Color.TRANSPARENT)
        }

        updateWeekToggle()

        btnLunes?.setOnClickListener {
            startsLunes = true
            updateWeekToggle()
        }
        btnDomingo?.setOnClickListener {
            startsLunes = false
            updateWeekToggle()
        }
    }
}
