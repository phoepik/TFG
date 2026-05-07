package com.example.bocetocalendario1.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.example.bocetocalendario1.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.switchmaterial.SwitchMaterial

class PrivacyBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_privacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<LinearLayout>(R.id.rowCambiarContrasena)?.setOnClickListener {
            Toast.makeText(context, "Cambiar contraseña (próximamente)", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<LinearLayout>(R.id.rowDatosDescargables)?.setOnClickListener {
            Toast.makeText(context, "Descargar datos (próximamente)", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<LinearLayout>(R.id.rowEliminarCuenta)?.setOnClickListener {
            Toast.makeText(context, "Eliminar cuenta requiere confirmación", Toast.LENGTH_SHORT).show()
        }
    }
}
