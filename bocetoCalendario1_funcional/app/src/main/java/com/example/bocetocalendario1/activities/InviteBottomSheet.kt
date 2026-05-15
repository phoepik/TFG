package com.example.bocetocalendario1.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.network.NotificacionResponse
import com.example.bocetocalendario1.network.RetrofitClient
import com.example.bocetocalendario1.utilidades.GestorSesion
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InviteBottomSheet : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(grupoNombre: String, grupoId: Int): InviteBottomSheet {
            val sheet = InviteBottomSheet()
            val args = Bundle()
            args.putString("grupo_nombre", grupoNombre)
            args.putInt("grupo_id", grupoId)
            sheet.arguments = args
            return sheet
        }
    }

    data class PersonSuggestion(val id: Int, val name: String, val email: String, var selected: Boolean = false)

    private val suggestions = mutableListOf<PersonSuggestion>()
    private var searchJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.bottom_sheet_invite, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nombre  = arguments?.getString("grupo_nombre") ?: "Grupo"
        val grupoId = arguments?.getInt("grupo_id") ?: 0
        val gestorSesion    = GestorSesion(requireContext())
        val idUsuarioActual = gestorSesion.obtenerIdUsuario() ?: return
        val nombreUsuario   = gestorSesion.obtenerNombreUsuario() ?: "Alguien"

        view.findViewById<TextView>(R.id.tvInviteTitle).text = "Invitar a $nombre"

        val etBuscar          = view.findViewById<EditText>(R.id.etBuscarPersona)
        val layoutSuggestions = view.findViewById<LinearLayout>(R.id.layoutSuggestions)
        val btnInvitar        = view.findViewById<Button>(R.id.btnInvitarPersonas)
        btnInvitar.isEnabled = false
        btnInvitar.alpha     = 0.5f

        fun updateButton() {
            val count = suggestions.count { it.selected }
            btnInvitar.isEnabled = count > 0
            btnInvitar.alpha     = if (count > 0) 1f else 0.5f
            btnInvitar.text      = if (count > 0) "Invitar ($count)" else "Invitar"
        }

        fun mostrarResultados() {
            layoutSuggestions.removeAllViews()
            if (suggestions.isEmpty()) {
                val tv = TextView(context).apply {
                    text = "No se encontraron usuarios"
                    setTextColor(resources.getColor(R.color.muted, null))
                    textSize = 14f
                    setPadding(16, 24, 16, 24)
                }
                layoutSuggestions.addView(tv)
                return
            }
            suggestions.forEach { person ->
                val item = layoutInflater.inflate(R.layout.item_invite_person, layoutSuggestions, false)
                item.findViewById<TextView>(R.id.tvPersonName).text  = person.name
                item.findViewById<TextView>(R.id.tvPersonEmail).text = person.email
                val cb = item.findViewById<CheckBox>(R.id.cbPersonSelect)
                cb.isChecked = person.selected
                (item.findViewById<View>(R.id.viewPersonAvatar) as? TextView)?.text =
                    person.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

                item.setOnClickListener { person.selected = !person.selected; cb.isChecked = person.selected; updateButton() }
                cb.setOnCheckedChangeListener { _, checked -> person.selected = checked; updateButton() }
                layoutSuggestions.addView(item)
            }
        }

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""
                searchJob?.cancel()
                if (query.length < 2) { suggestions.clear(); layoutSuggestions.removeAllViews(); updateButton(); return }
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(400)
                    try {
                        val resp = withContext(Dispatchers.IO) { RetrofitClient.api.buscarUsuarios(query) }
                        if (resp.isSuccessful) {
                            suggestions.clear()
                            resp.body()?.filter { it.idUsuario != idUsuarioActual }
                                ?.forEach { u -> suggestions.add(PersonSuggestion(u.idUsuario, u.nombre, u.email)) }
                            mostrarResultados()
                            updateButton()
                        }
                    } catch (e: Exception) { Log.e("INVITE", "Error buscando: ${e.message}") }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnInvitar.setOnClickListener {
            val seleccionados = suggestions.filter { it.selected }

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                var enviadas = 0
                seleccionados.forEach { person ->
                    try {
                        val notif = NotificacionResponse(
                            titulo = "Invitación a grupo",
                            mensaje = "$nombreUsuario te ha invitado al grupo \"$nombre\"",
                            tipo = "INVITACION",
                            idUsuario = person.id,
                            idGrupoInvitacion = grupoId,
                            estadoInvitacion = "PENDIENTE",
                            fechaCreacion = System.currentTimeMillis()
                        )
                        Log.d("INVITE", "Enviando notificacion a userId=${person.id}, grupoId=$grupoId")
                        val resp = RetrofitClient.api.crearNotificacion(notif)
                        if (resp.isSuccessful) {
                            enviadas++
                            Log.d("INVITE", "OK - notificacion creada para ${person.email}")
                        } else {
                            Log.e("INVITE", "Error ${resp.code()}: ${resp.errorBody()?.string()}")
                        }
                    } catch (e: Exception) {
                        Log.e("INVITE", "Excepcion invitando a ${person.email}: ${e.message}", e)
                    }
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "$enviadas invitación(es) enviada(s)", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }

        view.findViewById<View>(R.id.cardInviteEmail)?.setOnClickListener { etBuscar.requestFocus() }
        etBuscar.requestFocus()
    }
}