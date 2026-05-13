package com.example.bocetocalendario1.activities

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.adaptadores.EventoAdapter
import com.example.bocetocalendario1.models.Evento
import com.example.bocetocalendario1.models.Usuario
import com.example.bocetocalendario1.network.RetrofitClient
import com.example.bocetocalendario1.utilidades.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalleGrupoActivity : AppCompatActivity() {

    private val GROUP_BACKGROUNDS = listOf(
        R.drawable.bg_group_blue, R.drawable.bg_group_magenta, R.drawable.bg_group_green,
        R.drawable.bg_group_purple, R.drawable.bg_group_orange, R.drawable.bg_group_teal
    )
    private val GROUP_COLORS = listOf(
        "#0B5FFF", "#E94B7B", "#22C55E", "#8B5CF6", "#F97316", "#14B8A6"
    )

    private var miembrosCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_grupo)

        val tvNombreGrupo      = findViewById<TextView>(R.id.tvNombreGrupo)
        val tvDescripcion      = findViewById<TextView>(R.id.tvDescripcion)
        val tvMiembrosCount    = findViewById<TextView>(R.id.tvMiembrosCount)
        val tvEventosCount     = findViewById<TextView>(R.id.tvEventosCount)
        val rvEventos          = findViewById<RecyclerView>(R.id.rvMiembros) // RecyclerView bajo "PRÓXIMOS EVENTOS"
        val layoutMiembrosScroll = findViewById<LinearLayout>(R.id.layoutMiembrosScroll)
        val heroFrame          = findViewById<FrameLayout>(R.id.heroFrame)

        val grupoId          = intent.getIntExtra("GRUPO_ID", 0)
        val grupoNombre      = intent.getStringExtra("GRUPO_NOMBRE") ?: "Grupo"
        val grupoDescripcion = intent.getStringExtra("GRUPO_DESCRIPCION") ?: ""
        val grupoIdx         = intent.getIntExtra("GRUPO_IDX", 0)

        tvNombreGrupo.text = grupoNombre
        if (grupoDescripcion.isNotBlank()) tvDescripcion.text = grupoDescripcion
        else tvDescripcion.visibility = android.view.View.GONE

        heroFrame?.setBackgroundResource(GROUP_BACKGROUNDS[grupoIdx % GROUP_BACKGROUNDS.size])

        findViewById<TextView>(R.id.btnBackDetalle).setOnClickListener { finish() }
        findViewById<TextView>(R.id.btnMenuDetalle).setOnClickListener {
            GroupMenuBottomSheet.newInstance(grupoNombre, miembrosCount, grupoIdx, grupoId)
                .show(supportFragmentManager, "GroupMenu")
        }

        rvEventos.layoutManager = LinearLayoutManager(this)

        // ── Cargar miembros con nombres reales (avatares) ──
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val membersResp = RetrofitClient.api.miembrosDeGrupo(grupoId)
                if (membersResp.isSuccessful) {
                    val memberMaps = membersResp.body() ?: emptyList()
                    miembrosCount = memberMaps.size

                    val miembros = memberMaps.mapNotNull { m ->
                        val uid = m["idUsuario"] ?: return@mapNotNull null
                        try {
                            val userResp = RetrofitClient.api.obtenerUsuario(uid)
                            if (userResp.isSuccessful && userResp.body() != null) {
                                val u = userResp.body()!!
                                Usuario(u.idUsuario, u.nombre, u.email, "", u.notificacionesActivas)
                            } else Usuario(uid, "Usuario $uid", "", "", true)
                        } catch (e: Exception) { Usuario(uid, "Usuario $uid", "", "", true) }
                    }

                    withContext(Dispatchers.Main) {
                        tvMiembrosCount.text = "$miembrosCount ${if (miembrosCount == 1) "miembro" else "miembros"}"
                        buildMemberAvatars(layoutMiembrosScroll, miembros, grupoIdx)
                    }
                } else {
                    withContext(Dispatchers.Main) { tvMiembrosCount.text = "0 miembros" }
                }
            } catch (e: Exception) {
                Log.e("DETALLE", "Error cargando miembros: ${e.message}")
                withContext(Dispatchers.Main) { tvMiembrosCount.text = "? miembros" }
            }
        }

        // ── Cargar eventos del grupo ──
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val allEventos = mutableListOf<Evento>()
                val calResp = RetrofitClient.api.obtenerCalendariosDeGrupo(grupoId)
                if (calResp.isSuccessful) {
                    calResp.body()?.forEach { cal ->
                        val idCal = cal.idCalendario ?: return@forEach
                        val evResp = RetrofitClient.api.obtenerEventosDeCalendario(idCal)
                        if (evResp.isSuccessful) {
                            evResp.body()?.forEach { e ->
                                allEventos.add(Evento(
                                    id = e.idEvento ?: 0,
                                    titulo = e.titulo,
                                    descripcion = e.descripcion ?: "",
                                    fechaInicio = e.fechaInicio ?: "",
                                    fechaFin = e.fechaFin ?: "",
                                    ubicacion = e.ubicacion ?: "",
                                    estado = e.estado ?: "PENDIENTE",
                                    idCalendario = e.idCalendario ?: 0
                                ))
                            }
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    tvEventosCount.text = "${allEventos.size} ${if (allEventos.size == 1) "evento" else "eventos"}"
                    if (allEventos.isNotEmpty()) {
                        // Mapa: todos los calendarios de este grupo usan el nombre del grupo
                        val mapaNombres = allEventos.map { it.idCalendario }.distinct()
                            .associateWith { grupoNombre }
                        rvEventos.adapter = EventoAdapter(allEventos, onClick = { evento ->
                            EventDetalleBottomSheet.newInstance(evento)
                                .show(supportFragmentManager, "EventDetalle")
                        }, mapaNombres)
                    }
                }
            } catch (e: Exception) {
                Log.e("DETALLE", "Error cargando eventos: ${e.message}")
            }
        }

        // Add member button
        findViewById<Button>(R.id.btnAnadirMiembro).setOnClickListener {
            InviteBottomSheet.newInstance(grupoNombre, grupoId).show(supportFragmentManager, "Invite")
        }

        // Delete group button
        findViewById<Button>(R.id.btnBorrarGrupo).setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = RetrofitClient.api.borrarGrupo(grupoId)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@DetalleGrupoActivity, "Grupo eliminado", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@DetalleGrupoActivity, "Error al eliminar grupo", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DetalleGrupoActivity, "Error al conectar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun buildMemberAvatars(layout: LinearLayout, miembros: List<Usuario>, grupoIdx: Int) {
        layout.removeAllViews()
        val colorHex = GROUP_COLORS[grupoIdx % GROUP_COLORS.size]
        val color = try { Color.parseColor(colorHex) } catch (e: Exception) { Color.parseColor("#0B5FFF") }

        miembros.take(8).forEach { usuario ->
            val iniciales = usuario.nombre.trim().split(" ").take(2)
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
            addAvatarView(layout, if (iniciales.isNotEmpty()) iniciales else "?", color)
        }

        val size = dpToPx(52)
        val plus = TextView(this).apply {
            text = "+"; textSize = 20f; setTextColor(color); gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(size, size).apply { marginEnd = dpToPx(8) }
            background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setStroke(dpToPx(2), color); setColor(Color.TRANSPARENT)
            }
        }
        layout.addView(plus)
    }

    private fun addAvatarView(parent: LinearLayout, label: String, color: Int) {
        val size = dpToPx(52)
        val av = TextView(this).apply {
            text = label; textSize = 16f; setTextColor(Color.WHITE); gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(size, size).apply { marginEnd = dpToPx(8) }
            background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL; setColor(color)
            }
        }
        parent.addView(av)
    }

    private fun dpToPx(dp: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
    ).toInt()
}