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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import com.example.bocetocalendario1.datos.modelo.Calendario
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.withContext

class   CrearEventoActivity : AppCompatActivity() {

    private lateinit var etTitulo: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etFechaInicio: EditText
    private lateinit var etFechaFin: EditText
    private lateinit var etUbicacion: EditText
    private lateinit var spinnerEstado: Spinner
    private lateinit var spinnerCalendario: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    private var calendarios: List<Calendario> = emptyList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val db = AppDatabase.getDatabase(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_evento)

        // inicializar las diferenrtes vistas
        etTitulo = findViewById(R.id.etTitulo)
        etDescripcion = findViewById(R.id.etDescripcion)
        etFechaInicio = findViewById(R.id.etFechaInicio)
        etFechaFin = findViewById(R.id.etFechaFin)

        etUbicacion = findViewById(R.id.etUbicacion)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        spinnerCalendario = findViewById(R.id.spinnerCalendario)

        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)

        // CONFIRMACION DE Spinners
        val estados = arrayOf("PENDIENTE", "CONFIRMADO")
        spinnerEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estados)

        val gestor = GestorSesion(this)
        val idUsuario = gestor.obtenerIdUsuario() ?: -1

        lifecycleScope.launch(Dispatchers.IO) {
            val calsBD = if (idUsuario != -1) {
                db.appDao().obtenerCalendariosDeUsuario(idUsuario)
            } else emptyList()

            withContext(Dispatchers.Main) {
                calendarios = calsBD
                val nombresCalendarios = if (calendarios.isEmpty()) {
                    arrayOf("Sin calendario disponible")
                } else {
                    calendarios.map { it.nombre }.toTypedArray()
                }
                spinnerCalendario.adapter = ArrayAdapter(
                    this@CrearEventoActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    nombresCalendarios
                )
            }
        }// Selectores de fecha
        etFechaInicio.setOnClickListener { mostrarDateTimePicker(etFechaInicio) }
        etFechaFin.setOnClickListener { mostrarDateTimePicker(etFechaFin) }

        // boton guardar
        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString()
            val descripcion = etDescripcion.text.toString()
            val fechaInicio = etFechaInicio.text.toString()
            val fechaFin = etFechaFin.text.toString()
            val ubicacion = etUbicacion.text.toString()
            val tipoEstado = spinnerEstado.selectedItem.toString()
            val posicion = spinnerCalendario.selectedItemPosition
            val idCalendario = calendarios[posicion].id_calendario

            if (titulo.isEmpty()) {
                Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (fechaInicio.isEmpty()) {
                Toast.makeText(this, "La fecha de inicio es obligatoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (fechaFin.isEmpty()) {
                Toast.makeText(this, "La fecha de fin es obligatoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (calendarios.isEmpty()) {
                Toast.makeText(this, "No hay calendarios disponibles.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val unEvento = Evento(titulo=titulo, descripcion=descripcion,
                fecha_inicio=fechaInicio, fecha_fin=fechaFin,
                ubicacion=ubicacion, estado=tipoEstado, id_calendario=idCalendario)

            lifecycleScope.launch(Dispatchers.IO) {
                db.appDao().insertarEvento(unEvento)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearEventoActivity, "Evento guardado!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        // boton cancelar
        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun mostrarDateTimePicker(editText: EditText) {
        val calendario = Calendar.getInstance()
        
        DatePickerDialog(this, { _, año, mes, dia ->
            TimePickerDialog(this, { _, hora, minuto ->
                val fecha = String.format("%02d/%02d/%d %02d:%02d", dia, mes + 1, año,hora ,minuto)
                editText.setText(fecha)
            }, calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), true).show()
        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show()
    }
}
