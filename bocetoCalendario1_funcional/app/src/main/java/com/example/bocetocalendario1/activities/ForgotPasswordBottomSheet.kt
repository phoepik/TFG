package com.example.bocetocalendario1.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.bocetocalendario1.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ForgotPasswordBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutForm = view.findViewById<LinearLayout>(R.id.layoutFormForgot)
        val layoutSuccess = view.findViewById<LinearLayout>(R.id.layoutSuccessForgot)
        val etEmail = view.findViewById<EditText>(R.id.etEmailForgot)
        val btnEnviar = view.findViewById<Button>(R.id.btnEnviarEnlace)
        val tvEmailConfirm = view.findViewById<TextView>(R.id.tvEmailConfirm)
        val btnEntendido = view.findViewById<Button>(R.id.btnEntendido)

        btnEnviar.isEnabled = false

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s?.toString()?.trim() ?: ""
                btnEnviar.isEnabled = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                btnEnviar.alpha = if (btnEnviar.isEnabled) 1f else 0.5f
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        btnEnviar.alpha = 0.5f

        btnEnviar.setOnClickListener {
            Toast.makeText(context, "Se añadirá próximamente", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        btnEntendido.setOnClickListener {
            dismiss()
        }
    }
}

