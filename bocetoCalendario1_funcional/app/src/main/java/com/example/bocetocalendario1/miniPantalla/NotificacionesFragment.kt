package com.example.bocetocalendario1.miniPantalla

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.adaptadores.NotificacionAdapter
import com.example.bocetocalendario1.models.Notificacion

class NotificacionesFragment : Fragment() {

    private lateinit var rvNotificaciones: RecyclerView

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

        // ejemplos
        val notificacionesEjemplo = listOf(
            Notificacion(1, "Reunión en 15 minutos", "Reunión de equipo comienza pronto", 15, "RECORDATORIO", 1, 1),
            Notificacion(2, "Nueva invitación", "Te han invitado al grupo Amigos", 0, "INVITACION", 1, 0),
            Notificacion(3, "Evento mañana", "Entrega proyecto es mañana", 1440, "RECORDATORIO", 1, 2),
            Notificacion(4, "Actualización", "La app se ha actualizado", 0, "SISTEMA", 1, 0)
        )

        // confirmar RecyclerView
        rvNotificaciones.layoutManager = LinearLayoutManager(context)
        rvNotificaciones.adapter = NotificacionAdapter(notificacionesEjemplo)
    }
}
