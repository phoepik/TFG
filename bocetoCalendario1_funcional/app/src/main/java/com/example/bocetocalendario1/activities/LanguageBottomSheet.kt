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

class LanguageBottomSheet : BottomSheetDialogFragment() {

    data class Language(val flag: String, val name: String, val code: String)

    private val languages = listOf(
        Language("🇪🇸", "Español", "es"),
        Language("🇬🇧", "English", "en"),
        Language("🇫🇷", "Français", "fr"),
        Language("🇵🇹", "Português", "pt"),
        Language("🇩🇪", "Deutsch", "de"),
        Language("🇮🇹", "Italiano", "it")
    )

    private var selectedCode = "es"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_language, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = view.findViewById<LinearLayout>(R.id.layoutLanguageList)

        languages.forEach { lang ->
            val item = layoutInflater.inflate(R.layout.item_language, container, false)
            item.findViewById<TextView>(R.id.tvLanguageFlag).text = lang.flag
            item.findViewById<TextView>(R.id.tvLanguageName).text = lang.name
            val checkIcon = item.findViewById<TextView>(R.id.tvLanguageCheck)
            checkIcon.visibility = if (lang.code == selectedCode) View.VISIBLE else View.GONE

            item.setOnClickListener {
                selectedCode = lang.code
                // Refresh all items
                for (i in 0 until container.childCount) {
                    val child = container.getChildAt(i)
                    child.findViewById<TextView>(R.id.tvLanguageCheck)?.visibility = View.GONE
                }
                checkIcon.visibility = View.VISIBLE
                Toast.makeText(context, "${lang.name} seleccionado", Toast.LENGTH_SHORT).show()
                dismiss()
            }

            container.addView(item)
        }
    }
}
