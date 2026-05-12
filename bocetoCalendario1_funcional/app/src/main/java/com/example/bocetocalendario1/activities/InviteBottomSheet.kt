package com.example.bocetocalendario1.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.bocetocalendario1.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InviteBottomSheet : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(grupoNombre: String): InviteBottomSheet {
            val sheet = InviteBottomSheet()
            val args = Bundle()
            args.putString("grupo_nombre", grupoNombre)
            sheet.arguments = args
            return sheet
        }
    }

    data class PersonSuggestion(val name: String, val email: String, var selected: Boolean = false)

    private val suggestions = emptyList<PersonSuggestion>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_invite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nombre = arguments?.getString("grupo_nombre") ?: "Grupo"
        view.findViewById<TextView>(R.id.tvInviteTitle).text = "Invitar a $nombre"

        val layoutSuggestions = view.findViewById<LinearLayout>(R.id.layoutSuggestions)
        val btnInvitar = view.findViewById<Button>(R.id.btnInvitarPersonas)
        btnInvitar.isEnabled = false
        btnInvitar.alpha = 0.5f

        fun updateButton() {
            val count = suggestions.count { it.selected }
            btnInvitar.isEnabled = count > 0
            btnInvitar.alpha = if (count > 0) 1f else 0.5f
            btnInvitar.text = if (count > 0) "Invitar $count" else "Invitar"
        }

        suggestions.forEach { person ->
            val item = layoutInflater.inflate(R.layout.item_invite_person, layoutSuggestions, false)
            item.findViewById<TextView>(R.id.tvPersonName).text = person.name
            item.findViewById<TextView>(R.id.tvPersonEmail).text = person.email
            val cb = item.findViewById<CheckBox>(R.id.cbPersonSelect)
            cb.isChecked = person.selected

            val avatarBg = item.findViewById<View>(R.id.viewPersonAvatar) as? TextView
            avatarBg?.text = person.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

            item.setOnClickListener {
                person.selected = !person.selected
                cb.isChecked = person.selected
                updateButton()
            }
            cb.setOnCheckedChangeListener { _, checked ->
                person.selected = checked
                updateButton()
            }

            layoutSuggestions.addView(item)
        }

        btnInvitar.setOnClickListener {
            val selected = suggestions.filter { it.selected }.map { it.name }
            Toast.makeText(context, "Invitaciones enviadas a: ${selected.joinToString(", ")}", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        // Email card
        view.findViewById<View>(R.id.cardInviteEmail)?.setOnClickListener {
            Toast.makeText(context, "Invitar por correo (próximamente)", Toast.LENGTH_SHORT).show()
        }
    }
}