package com.example.bocetocalendario1.miniPantalla

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.activities.LoginActivity
import com.example.bocetocalendario1.datos.basedatos.AppDatabase
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilFragment : Fragment() {

    private lateinit var tvNombre: TextView
    private lateinit var tvEmail: TextView
    private lateinit var switchNotificaciones: Switch
    private lateinit var btnCerrarSesion: Button

    private lateinit var tvIdUsuario: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gestorSesion = GestorSesion(this.requireContext())
        val db = AppDatabase.getDatabase(requireContext())
        tvNombre = view.findViewById(R.id.tvNombre)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvIdUsuario = view.findViewById(R.id.tvIdUsuario)
        switchNotificaciones = view.findViewById(R.id.switchNotificaciones)
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion)

        //Coger usuario de la base de datos y rellenar campos
        // datos de ejemplo
        tvNombre.text = gestorSesion.obtenerNombreUsuario()
        tvEmail.text = gestorSesion.obtenerEmail()
        tvIdUsuario.text = gestorSesion.obtenerIdUsuario().toString()

        val idUsuario = gestorSesion.obtenerIdUsuario() ?: -1

        // Ocultar el switch hasta tener el valor real
        switchNotificaciones.visibility = View.INVISIBLE

        if (idUsuario != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                val usuario = db.appDao().verificarUsuario(gestorSesion.obtenerEmail() ?: "")
                withContext(Dispatchers.Main) {
                    // Poner el valor correcto antes de activar el listener
                    switchNotificaciones.isChecked = usuario?.notificaciones_activas ?: false
                    switchNotificaciones.visibility = View.VISIBLE

                    // el listener se registra aquí, cuando el valor ya está puesto
                    switchNotificaciones.setOnCheckedChangeListener { _, isChecked ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            db.appDao().actualizarNotificaciones(idUsuario, isChecked)
                            withContext(Dispatchers.Main) {
                                val mensaje = if (isChecked) "Notificaciones activadas" else "Notificaciones desactivadas"
                                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

        // cerrar sesion
        btnCerrarSesion.setOnClickListener {
            gestorSesion.cerrarSesion()

            // accion de volver al login
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()

        }
    }
}
