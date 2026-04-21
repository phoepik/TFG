package com.example.bocetocalendario1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.bocetocalendario1.miniPantalla.GruposFragment
import com.example.bocetocalendario1.miniPantalla.InicioFragment
import com.example.bocetocalendario1.miniPantalla.NotificacionesFragment
import com.example.bocetocalendario1.miniPantalla.PerfilFragment
import com.example.bocetocalendario1.notificaciones.canales.NotificacionesManagerCanales
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    //Santiago BUenos dias 
    // Lanzador para pedir permiso de notificaciones (Android 13+)
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            // No se necesita acción especial; si deniega, simplemente no verá push
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Crear canales de notificación
        NotificacionesManagerCanales.createAll(this)

        // Pedir permiso POST_NOTIFICATIONS en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Determinar qué tab abrir (puede venir de una notificación push)
        val abrirTab = intent.getStringExtra("abrir_tab")

        if (savedInstanceState == null) {
            when (abrirTab) {
                "notificaciones" -> {
                    cargarFragment(NotificacionesFragment())
                    bottomNavigation.selectedItemId = R.id.nav_notificaciones
                }
                else -> {
                    cargarFragment(InicioFragment())
                }
            }
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    cargarFragment(InicioFragment())
                    true
                }
                R.id.nav_grupos -> {
                    cargarFragment(GruposFragment())
                    true
                }
                R.id.nav_notificaciones -> {
                    cargarFragment(NotificacionesFragment())
                    true
                }
                R.id.nav_perfil -> {
                    cargarFragment(PerfilFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun cargarFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
