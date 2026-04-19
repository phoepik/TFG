package com.example.bocetocalendario1.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.datos.basedatos.AppDatabase
import com.example.bocetocalendario1.notificaciones.NotificacionService
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnadirMiembroActivity : AppCompatActivity() {

    private lateinit var etIdUsuario: EditText
    private lateinit var btnAnadir: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anadir_miembro)

        val db = AppDatabase.getDatabase(this)
        val gestorSesion = GestorSesion(this)
        val grupoId = intent.getIntExtra("GRUPO_ID", 0)
        val grupoNombre = intent.getStringExtra("GRUPO_NOMBRE") ?: "Grupo"

        etIdUsuario = findViewById(R.id.etIdUsuario)
        btnAnadir = findViewById(R.id.btnAnadir)
        btnCancelar = findViewById(R.id.btnCancelar)

        btnAnadir.setOnClickListener {
            val idUsuarioTexto = etIdUsuario.text.toString()

            if (idUsuarioTexto.isEmpty()) {
                Toast.makeText(this, "Introduce el ID del usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idUsuarioDestino = idUsuarioTexto.toIntOrNull()
            if (idUsuarioDestino == null) {
                Toast.makeText(this, "ID de usuario no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                // Verificar que el usuario existe
                val usuarioDestino = db.appDao().obtenerUsuarioPorId(idUsuarioDestino)
                if (usuarioDestino == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AnadirMiembroActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Verificar que no es ya miembro
                val yaEsMiembro = db.appDao().esMiembroDeGrupo(idUsuarioDestino, grupoId)
                if (yaEsMiembro) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AnadirMiembroActivity, "Ya es miembro del grupo", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Enviar notificación de invitación
                val nombreQuienInvita = gestorSesion.obtenerNombreUsuario() ?: "Alguien"
                NotificacionService.enviarInvitacionGrupo(
                    context = this@AnadirMiembroActivity,
                    idUsuarioDestino = idUsuarioDestino,
                    nombreGrupo = grupoNombre,
                    idGrupo = grupoId,
                    nombreQuienInvita = nombreQuienInvita
                )

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AnadirMiembroActivity, "Invitación enviada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}