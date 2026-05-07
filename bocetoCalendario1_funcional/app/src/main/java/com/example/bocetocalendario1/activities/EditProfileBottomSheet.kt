package com.example.bocetocalendario1.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.utilidades.GestorSesion
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditProfileBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gestor = GestorSesion(requireContext())
        val etNombre = view.findViewById<EditText>(R.id.etEditNombre)
        val etEmail = view.findViewById<EditText>(R.id.etEditEmail)
        val tvAvatar = view.findViewById<TextView>(R.id.tvEditAvatar)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarPerfil)

        val nombre = gestor.obtenerNombreUsuario() ?: ""
        val email = gestor.obtenerEmail() ?: ""

        etNombre.setText(nombre)
        etEmail.setText(email)

        val iniciales = nombre.trim().split(" ").take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
        tvAvatar.text = if (iniciales.isNotEmpty()) iniciales else "U"

        view.findViewById<View>(R.id.btnCambiarFoto)?.setOnClickListener {
            Toast.makeText(context, "Cambiar foto (próximamente)", Toast.LENGTH_SHORT).show()
        }

        btnGuardar.setOnClickListener {
            val newNombre = etNombre.text.toString().trim()
            if (newNombre.isEmpty()) {
                Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
}
