package com.example.bocetocalendario1.miniPantalla

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.activities.CrearEventoActivity
import com.example.bocetocalendario1.adaptadores.EventoAdapter
import com.example.bocetocalendario1.models.Evento

class InicioFragment : Fragment() {

    private lateinit var rvEventos: RecyclerView
    private lateinit var btnNuevoEvento: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvEventos = view.findViewById(R.id.rvEventos)
        btnNuevoEvento = view.findViewById(R.id.btnNuevoEvento)

        // RICARDO->>>>>>datos de ejemplo (después se conectará con la BD)
        val eventosEjemplo = listOf(
            Evento(1, "Reunión de equipo", "Revisión semanal del proyecto", "04/01/2026 10:00", "04/01/2026 11:00", "Sala A", "CONFIRMADO", 1),
            Evento(2, "Entrega proyecto", "Fecha límite del trabajo", "10/01/2026 23:59", "10/01/2026 23:59", "Online", "PENDIENTE", 1),
            Evento(3, "Cumpleaños María", "Fiesta sorpresa", "15/01/2026 20:00", "15/01/2026 23:00", "Casa de Ana", "CONFIRMADO", 2)
        )

        // conftimacion de RecyclerView, aquis e abre con mas detealle
        rvEventos.layoutManager = LinearLayoutManager(context)
        rvEventos.adapter = EventoAdapter(eventosEjemplo) { evento ->

        }

        // boton de nuevo evcento
        btnNuevoEvento.setOnClickListener {
            startActivity(Intent(context, CrearEventoActivity::class.java))
        }
    }
}
