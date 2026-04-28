package com.example.bocetocalendario1.miniPantalla

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.activities.LoginActivity
import com.example.bocetocalendario1.network.RetrofitClient
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilFragment : Fragment() {

    private lateinit var tvNombre: TextView
    private lateinit var tvEmail: TextView
    private lateinit var switchNotificaciones: Switch
    private lateinit var btnCerrarSesion: Button
    private lateinit var tvIdUsuario: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gestorSesion = GestorSesion(requireContext())

        tvNombre = view.findViewById(R.id.tvNombre)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvIdUsuario = view.findViewById(R.id.tvIdUsuario)
        switchNotificaciones = view.findViewById(R.id.switchNotificaciones)
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion)

        tvNombre.text = gestorSesion.obtenerNombreUsuario()
        tvEmail.text = gestorSesion.obtenerEmail()
        tvIdUsuario.text = gestorSesion.obtenerIdUsuario().toString()

        val idUsuario = gestorSesion.obtenerIdUsuario() ?: -1

        // Stats views
        val tvStatEventos = view.findViewById<TextView>(R.id.tvStatEventos)
        val tvStatGrupos = view.findViewById<TextView>(R.id.tvStatGrupos)
        val tvInicial = view.findViewById<TextView>(R.id.tvInicialUsuario)

        // Set initials
        val nombre = gestorSesion.obtenerNombreUsuario() ?: ""
        val iniciales = nombre.trim().split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
        tvInicial?.text = if (iniciales.isNotEmpty()) iniciales else "U"

        // Load groups count
        if (idUsuario != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val gruposResp = RetrofitClient.api.obtenerGruposDeUsuario(idUsuario)
                    withContext(Dispatchers.Main) {
                        if (gruposResp.isSuccessful) {
                            tvStatGrupos?.text = gruposResp.body()?.size?.toString() ?: "0"
                        }
                    }
                } catch (e: Exception) { /* ignore */ }
            }
        }

        switchNotificaciones.visibility = View.INVISIBLE

        if (idUsuario != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = RetrofitClient.api.obtenerUsuario(idUsuario)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body() != null) {
                            val usuario = response.body()!!
                            switchNotificaciones.isChecked = usuario.notificacionesActivas
                            switchNotificaciones.visibility = View.VISIBLE

                            switchNotificaciones.setOnCheckedChangeListener { _, isChecked ->
                                lifecycleScope.launch(Dispatchers.IO) {
                                    try {
                                        RetrofitClient.api.actualizarNotificaciones(
                                            idUsuario,
                                            mapOf("activas" to isChecked)
                                        )
                                        withContext(Dispatchers.Main) {
                                            val mensaje = if (isChecked) "Notificaciones activadas" else "Notificaciones desactivadas"
                                            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        } else {
                            switchNotificaciones.visibility = View.VISIBLE
                            switchNotificaciones.isChecked = gestorSesion.estanNotificacionesActivas()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("PERFIL", "Error: ${e.message}")
                        switchNotificaciones.visibility = View.VISIBLE
                        switchNotificaciones.isChecked = gestorSesion.estanNotificacionesActivas()
                    }
                }
            }
        }

        btnCerrarSesion.setOnClickListener {
            gestorSesion.cerrarSesion()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        }
    }
}