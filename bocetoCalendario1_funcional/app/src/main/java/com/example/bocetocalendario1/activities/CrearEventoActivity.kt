package com.example.bocetocalendario1.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.datos.basedatos.AppDatabase
import com.example.bocetocalendario1.datos.modelo.Evento
import com.example.bocetocalendario1.notificaciones.NotificacionService
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CrearEventoActivity : AppCompatActivity() {

    private lateinit var etTitulo: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etFechaInicio: EditText
    private lateinit var etFechaFin: EditText
    private lateinit var etUbicacion: EditText
    private lateinit var spinnerEstado: Spinner
    private lateinit var spinnerCalendario: Spinner
    private lateinit var spinnerRecordatorio: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    // Almacenar la fecha de inicio en millis para la alarma
    private var fechaInicioMillis: Long = 0L

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val db = AppDatabase.getDatabase(this)
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
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)

        // Spinner de recordatorio (si existe en el layout, si no se ignora)
        /*try {
            spinnerRecordatorio = findViewById(R.id.spinnerRecordatorio)
            val opcionesRecordatorio = arrayOf("Sin recordatorio", "5 minutos antes", "15 minutos antes", "30 minutos antes", "1 hora antes", "1 día antes")
            spinnerRecordatorio.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcionesRecordatorio)
        } catch (_: Exception) {
            // Si no existe el spinner en el layout, se usa 15 min por defecto
        }*/

        val estados = arrayOf("PENDIENTE", "CONFIRMADO")
        spinnerEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estados)

        val calendarios = arrayOf("Mi calendario (Personal)", "Trabajo DAM (Grupal)", "Familia (Grupal)")
        spinnerCalendario.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, calendarios)

        etFechaInicio.setOnClickListener { mostrarDateTimePicker(etFechaInicio, esInicio = true) }
        etFechaFin.setOnClickListener { mostrarDateTimePicker(etFechaFin, esInicio = false) }

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString()
            val descripcion = etDescripcion.text.toString()
            val fechaInicio = etFechaInicio.text.toString()
            val fechaFin = etFechaFin.text.toString()
            val ubicacion = etUbicacion.text.toString()
            val tipoEstado = spinnerEstado.selectedItem.toString()
            val tipoCalendario = spinnerCalendario.selectedItemPosition

            if (titulo.isEmpty()) {
                Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fechaInicio.isEmpty()) {
                Toast.makeText(this, "La fecha de inicio es obligatoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Determinar minutos de anticipación
            val minutosAntes = obtenerMinutosRecordatorio()

            val unEvento = Evento(
                titulo = titulo,
                descripcion = descripcion,
                fecha_inicio = fechaInicio,
                fecha_fin = fechaFin,
                ubicacion = ubicacion,
                estado = tipoEstado,
                id_calendario = tipoCalendario
            )

            lifecycleScope.launch(Dispatchers.IO) {
                val idEvento = db.appDao().insertarEvento(unEvento)
                val idUsuario = gestorSesion.obtenerIdUsuario() ?: -1

                // Programar recordatorio si se ha seleccionado
                if (minutosAntes > 0 && fechaInicioMillis > 0 && idUsuario > 0) {
                    NotificacionService.programarRecordatorioEvento(
                        context = this@CrearEventoActivity,
                        idUsuario = idUsuario,
                        idEvento = idEvento.toInt(),
                        tituloEvento = titulo,
                        descripcionEvento = descripcion.ifEmpty { null },
                        fechaEventoMillis = fechaInicioMillis,
                        minutosAntes = minutosAntes
                    )
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearEventoActivity, "Evento guardado!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun obtenerMinutosRecordatorio(): Int {
        return try {
            when (spinnerRecordatorio.selectedItemPosition) {
                0 -> 0      // Sin recordatorio
                1 -> 5
                2 -> 15
                3 -> 30
                4 -> 60
                5 -> 1440   // 1 día
                else -> 0
            }
        } catch (_: Exception) {
            15 // Por defecto si no existe el spinner
        }
    }

    private fun mostrarDateTimePicker(editText: EditText, esInicio: Boolean) {
        val calendario = Calendar.getInstance()

        DatePickerDialog(this, { _, año, mes, dia ->
            TimePickerDialog(this, { _, hora, minuto ->
                val fecha = String.format("%02d/%02d/%d %02d:%02d", dia, mes + 1, año, hora, minuto)
                editText.setText(fecha)

                if (esInicio) {
                    // Guardar millis para la alarma
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

