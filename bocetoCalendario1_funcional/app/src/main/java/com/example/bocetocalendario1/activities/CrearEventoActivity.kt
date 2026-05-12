package com.example.bocetocalendario1.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.network.CalendarioResponse
import com.example.bocetocalendario1.network.EventoResponse
import com.example.bocetocalendario1.network.RetrofitClient
import com.example.bocetocalendario1.notificaciones.NotificacionService
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CrearEventoActivity : AppCompatActivity() {

    private lateinit var etTitulo: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etFechaInicio: EditText
    private lateinit var etFechaFin: EditText
    private lateinit var etUbicacion: EditText
    private lateinit var spinnerEstado: Spinner
    private lateinit var spinnerCalendario: Spinner
    private lateinit var spinnerRecordatorio: Spinner
    private lateinit var btnGuardar: TextView
    private lateinit var btnCancelar: TextView

    private var fechaInicioMillis: Long = 0L
    private var calendariosDisponibles: List<CalendarioResponse> = emptyList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val gestorSesion = GestorSesion(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_evento)

        etTitulo = findViewById(R.id.etTitulo)
        etDescripcion = findViewById(R.id.etDescripcion)
        etFechaInicio = findViewById(R.id.etFechaInicio)
        etFechaFin = findViewById(R.id.etFechaFin)
        etUbicacion = findViewById(R.id.etUbicacion)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        spinnerCalendario = findViewById(R.id.spinnerCalendario)
        spinnerRecordatorio = findViewById(R.id.spinnerRecordatorio)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)

        val opcionesRecordatorio = arrayOf("Sin recordatorio", "5 minutos antes", "15 minutos antes", "30 minutos antes", "1 hora antes", "1 día antes")
        spinnerRecordatorio.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcionesRecordatorio)

        val estados = arrayOf("PENDIENTE", "CONFIRMADO")
        spinnerEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estados)

        // Cargar calendarios reales del usuario
        val idUsuario = gestorSesion.obtenerIdUsuario() ?: -1
        if (idUsuario != -1) {
            cargarCalendarios(idUsuario)
        } else {
            spinnerCalendario.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arrayOf("Sin calendario"))
        }

        etFechaInicio.setOnClickListener { mostrarDateTimePicker(etFechaInicio, esInicio = true) }
        etFechaFin.setOnClickListener { mostrarDateTimePicker(etFechaFin, esInicio = false) }

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString()
            val descripcion = etDescripcion.text.toString()
            val fechaInicio = etFechaInicio.text.toString()
            val fechaFin = etFechaFin.text.toString()
            val ubicacion = etUbicacion.text.toString()
            val tipoEstado = spinnerEstado.selectedItem.toString()
            val minutosAntes = obtenerMinutosRecordatorio()

            if (titulo.isEmpty()) {
                Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fechaInicio.isEmpty()) {
                Toast.makeText(this, "La fecha de inicio es obligatoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Usar el ID real del calendario seleccionado
            val idCalendarioReal = if (calendariosDisponibles.isNotEmpty()) {
                calendariosDisponibles.getOrNull(spinnerCalendario.selectedItemPosition)?.idCalendario ?: 0
            } else 0

            val eventoRequest = EventoResponse(
                titulo = titulo,
                descripcion = descripcion,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                ubicacion = ubicacion,
                estado = tipoEstado,
                idCalendario = idCalendarioReal
            )

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = RetrofitClient.api.crearEvento(eventoRequest)

                    if (response.isSuccessful && response.body() != null) {
                        val eventoCreado = response.body()!!
                        val uid = gestorSesion.obtenerIdUsuario() ?: -1

                        if (minutosAntes > 0 && fechaInicioMillis > 0 && uid > 0) {
                            NotificacionService.programarRecordatorioEvento(
                                context = this@CrearEventoActivity,
                                idUsuario = uid,
                                idEvento = eventoCreado.idEvento ?: 0,
                                tituloEvento = titulo,
                                descripcionEvento = descripcion.ifEmpty { null },
                                fechaEventoMillis = fechaInicioMillis,
                                minutosAntes = minutosAntes
                            )
                        }

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@CrearEventoActivity, "¡Evento creado!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@CrearEventoActivity, "Error al guardar evento", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("EVENTO", "Error: ${e.message}")
                        Toast.makeText(this@CrearEventoActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun cargarCalendarios(idUsuario: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val resp = RetrofitClient.api.obtenerCalendariosDeUsuario(idUsuario)
                val lista = if (resp.isSuccessful) resp.body() ?: emptyList() else emptyList()
                withContext(Dispatchers.Main) {
                    calendariosDisponibles = lista
                    val nombres = if (lista.isNotEmpty())
                        lista.map { it.nombre.ifEmpty { "Calendario ${it.idCalendario}" } }.toTypedArray()
                    else
                        arrayOf("Sin calendario")
                    spinnerCalendario.adapter = ArrayAdapter(
                        this@CrearEventoActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        nombres
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    spinnerCalendario.adapter = ArrayAdapter(
                        this@CrearEventoActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        arrayOf("Sin calendario")
                    )
                }
            }
        }
    }

    private fun obtenerMinutosRecordatorio(): Int {
        return when (spinnerRecordatorio.selectedItemPosition) {
            0 -> 0
            1 -> 5
            2 -> 15
            3 -> 30
            4 -> 60
            5 -> 1440
            else -> 0
        }
    }

    private fun mostrarDateTimePicker(editText: EditText, esInicio: Boolean) {
        val calendario = Calendar.getInstance()

        DatePickerDialog(this, { _, año, mes, dia ->
            TimePickerDialog(this, { _, hora, minuto ->
                val fecha = String.format("%02d/%02d/%d %02d:%02d", dia, mes + 1, año, hora, minuto)
                editText.setText(fecha)

                if (esInicio) {
                    val cal = Calendar.getInstance().apply {
                        set(año, mes, dia, hora, minuto, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    fechaInicioMillis = cal.timeInMillis
                }
            }, calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), true).show()
        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show()
    }
}