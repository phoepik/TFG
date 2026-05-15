package com.example.bocetocalendario1.miniPantalla

import android.os.Bundle
import android.util.Log
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
import com.example.bocetocalendario1.datos.modelo.Notificacion
import com.example.bocetocalendario1.network.RetrofitClient
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificacionesFragment : Fragment() {

    private lateinit var rvNotificaciones: RecyclerView
    private lateinit var tvVacio: TextView
    private lateinit var adapter: NotificacionAdapter
    private lateinit var gestorSesion: GestorSesion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_notificaciones, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvNotificaciones = view.findViewById(R.id.rvNotificaciones)
        tvVacio = view.findViewById(R.id.tvVacio)
        gestorSesion = GestorSesion(requireContext())

        adapter = NotificacionAdapter(
            notificaciones = mutableListOf(),
            onAceptar = { notif ->
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        RetrofitClient.api.actualizarInvitacion(
                            notif.id_notificacion,
                            mapOf("estado" to "ACEPTADA")
                        )
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Invitación aceptada", Toast.LENGTH_SHORT).show()
                            cargarNotificaciones()
                        }
                    } catch (e: Exception) {
                        Log.e("NOTIF", "Error aceptando: ${e.message}")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error al aceptar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            onRechazar = { notif ->
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        RetrofitClient.api.actualizarInvitacion(
                            notif.id_notificacion,
                            mapOf("estado" to "RECHAZADA")
                        )
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Invitación rechazada", Toast.LENGTH_SHORT).show()
                            cargarNotificaciones()
                        }
                    } catch (e: Exception) {
                        Log.e("NOTIF", "Error rechazando: ${e.message}")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error al rechazar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            onClick = { notif ->
                if (!notif.leida) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            RetrofitClient.api.marcarLeida(notif.id_notificacion)
                        } catch (_: Exception) {}
                        withContext(Dispatchers.Main) { cargarNotificaciones() }
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
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val resp = RetrofitClient.api.obtenerNotificaciones(idUsuario)
                if (resp.isSuccessful) {
                    val lista = resp.body()?.map { nr ->
                        Notificacion(
                            id_notificacion    = nr.idNotificacion,
                            titulo             = nr.titulo,
                            mensaje            = nr.mensaje,
                            tiempo_anticipacion = nr.tiempoAnticipacion,
                            tipo               = nr.tipo ?: "SISTEMA",
                            id_evento          = nr.idEvento,
                            id_usuario         = nr.idUsuario ?: idUsuario,
                            leida              = nr.leida ?: false,
                            fecha_creacion     = nr.fechaCreacion ?: System.currentTimeMillis(),
                            id_grupo_invitacion = nr.idGrupoInvitacion,
                            estado_invitacion  = nr.estadoInvitacion
                        )
                    } ?: emptyList()

                    withContext(Dispatchers.Main) {
                        adapter.actualizarLista(lista)
                        tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
                        rvNotificaciones.visibility = if (lista.isEmpty()) View.GONE else View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.e("NOTIF", "Error cargando: ${e.message}")
            }
        }
    }
}