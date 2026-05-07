package com.example.bocetocalendario1.miniPantalla

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.activities.EditProfileBottomSheet
import com.example.bocetocalendario1.activities.LanguageBottomSheet
import com.example.bocetocalendario1.activities.NotificationsBottomSheet
import com.example.bocetocalendario1.activities.PrivacyBottomSheet
import com.example.bocetocalendario1.activities.SettingsBottomSheet
import com.example.bocetocalendario1.activities.SplashActivity
import com.example.bocetocalendario1.network.RetrofitClient
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class PerfilFragment : Fragment() {

    private lateinit var tvNombre: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvIdUsuario: TextView
    private lateinit var btnCerrarSesion: LinearLayout

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
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion)

        tvNombre.text = gestorSesion.obtenerNombreUsuario() ?: ""
        tvEmail.text = gestorSesion.obtenerEmail() ?: ""

        val idUsuario = gestorSesion.obtenerIdUsuario() ?: -1

        // Stats views
        val tvStatEventos = view.findViewById<TextView>(R.id.tvStatEventos)
        val tvStatGrupos = view.findViewById<TextView>(R.id.tvStatGrupos)
        val tvStatProximos = view.findViewById<TextView>(R.id.tvStatProximos)
        val tvInicial = view.findViewById<TextView>(R.id.tvInicialUsuario)

        // Set initials
        val nombre = gestorSesion.obtenerNombreUsuario() ?: ""
        val iniciales = nombre.trim().split(" ").take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
        tvInicial?.text = if (iniciales.isNotEmpty()) iniciales else "U"

        // Edit profile button
        val btnEditarPerfil = view.findViewById<TextView>(R.id.btnEditarPerfil)
        btnEditarPerfil?.setOnClickListener {
            EditProfileBottomSheet().show(parentFragmentManager, "EditProfile")
        }

        // Dark mode switch
        val switchModoOscuro = view.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.switchModoOscuro)
        val sharedPrefs = requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val darkMode = sharedPrefs.getBoolean("dark_mode", false)
        switchModoOscuro?.isChecked = darkMode
        switchModoOscuro?.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Preference rows
        view.findViewById<LinearLayout>(R.id.rowNotificaciones)?.setOnClickListener {
            NotificationsBottomSheet().show(parentFragmentManager, "Notifications")
        }
        view.findViewById<LinearLayout>(R.id.rowIdioma)?.setOnClickListener {
            LanguageBottomSheet().show(parentFragmentManager, "Language")
        }
        view.findViewById<LinearLayout>(R.id.rowPrivacidad)?.setOnClickListener {
            PrivacyBottomSheet().show(parentFragmentManager, "Privacy")
        }
        view.findViewById<LinearLayout>(R.id.rowAjustes)?.setOnClickListener {
            SettingsBottomSheet().show(parentFragmentManager, "Settings")
        }

        // Load counts
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

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val calResp = RetrofitClient.api.obtenerCalendariosDeUsuario(idUsuario)
                    val calendarios = if (calResp.isSuccessful) calResp.body() ?: emptyList() else emptyList()
                    var totalEventos = 0
                    var proximosEventos = 0
                    val hoy = Calendar.getInstance()

                    calendarios.forEach { cal ->
                        val idCal = cal.idCalendario ?: return@forEach
                        val evResp = RetrofitClient.api.obtenerEventosDeCalendario(idCal)
                        if (evResp.isSuccessful) {
                            val events = evResp.body() ?: emptyList()
                            totalEventos += events.size
                            proximosEventos += events.count { ev ->
                                try {
                                    val fecha = ev.fechaInicio?.substringBefore(" ")?.trim() ?: ""
                                    val partes = fecha.split("/")
                                    if (partes.size < 3) return@count false
                                    val cal2 = Calendar.getInstance()
                                    cal2.set(partes[2].toInt(), partes[1].toInt() - 1, partes[0].toInt())
                                    !cal2.before(hoy)
                                } catch (e: Exception) { false }
                            }
                        }
                    }

                    withContext(Dispatchers.Main) {
                        tvStatEventos?.text = totalEventos.toString()
                        tvStatProximos?.text = proximosEventos.toString()
                    }
                } catch (e: Exception) { /* ignore */ }
            }

        }

        btnCerrarSesion.setOnClickListener {
            gestorSesion.cerrarSesion()
            val intent = Intent(context, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
