package com.example.bocetocalendario1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomNavigation = findViewById(R.id.bottomNavigation)

        // cargar parte inicial
        if (savedInstanceState == null) {
            cargarFragment(InicioFragment())
        }

        // configurar navegacion
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
