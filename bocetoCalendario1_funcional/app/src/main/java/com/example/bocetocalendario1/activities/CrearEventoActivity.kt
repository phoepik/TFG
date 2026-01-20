package com.example.bocetocalendario1.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bocetocalendario1.R
import java.util.Calendar

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

    override fun onCreate(savedInstanceState: Bundle?) {
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

        val calendarios = arrayOf("Mi calendario (Personal)", "Trabajo DAM (Grupal)", "Familia (Grupal)")
        spinnerCalendario.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, calendarios)
        // Selectores de fecha
        etFechaInicio.setOnClickListener { mostrarDateTimePicker(etFechaInicio) }
        etFechaFin.setOnClickListener { mostrarDateTimePicker(etFechaFin) }

        // boton guardar
        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString()

            if (titulo.isEmpty()) {
                Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // RICARDO->>>>Aqui va el guardado en la BD
            Toast.makeText(this, "Evento guardado!", Toast.LENGTH_SHORT).show()
            finish()
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
