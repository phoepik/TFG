package com.example.bocetocalendario1.miniPantalla

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.adaptadores.NotificacionAdapter
import com.example.bocetocalendario1.datos.basedatos.AppDatabase
import com.example.bocetocalendario1.notificaciones.NotificacionService
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificacionesFragment : Fragment() {

    private lateinit var rvNotificaciones: RecyclerView
    private lateinit var tvVacio: TextView
    private lateinit var adapter: NotificacionAdapter
    private lateinit var db: AppDatabase
    private lateinit var gestorSesion: GestorSesion

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notificaciones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvNotificaciones = view.findViewById(R.id.rvNotificaciones)
        tvVacio = view.findViewById(R.id.tvVacio)
        db = AppDatabase.getDatabase(requireContext())
        gestorSesion = GestorSesion(requireContext())

        adapter = NotificacionAdapter(
            notificaciones = mutableListOf(),
            onAceptar = { notif ->
                lifecycleScope.launch(Dispatchers.IO) {
                    NotificacionService.aceptarInvitacion(requireContext(), notif)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Invitación aceptada", Toast.LENGTH_SHORT).show()
                        cargarNotificaciones()
                    }
                }
            },
            onRechazar = { notif ->
                lifecycleScope.launch(Dispatchers.IO) {
                    NotificacionService.rechazarInvitacion(requireContext(), notif)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Invitación rechazada", Toast.LENGTH_SHORT).show()
                        cargarNotificaciones()
                    }
                }
            },
            onClick = { notif ->
                if (!notif.leida) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        db.appDao().marcarComoLeida(notif.id_notificacion)
                        withContext(Dispatchers.Main) {
                            cargarNotificaciones()
                        }
                    }
                }
            }
        )

        rvNotificaciones.layoutManager = LinearLayoutManager(context)
        rvNotificaciones.adapter = adapter

        cargarNotificaciones()
    }

    override fun onResume() {
        super.onResume()
        cargarNotificaciones()
    }

    private fun cargarNotificaciones() {
        val idUsuario = gestorSesion.obtenerIdUsuario() ?: return
        lifecycleScope.launch(Dispatchers.IO) {
            val lista = db.appDao().obtenerNotificacionesDeUsuario(idUsuario)
            withContext(Dispatchers.Main) {
                adapter.actualizarLista(lista)
                tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
                rvNotificaciones.visibility = if (lista.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }
}

