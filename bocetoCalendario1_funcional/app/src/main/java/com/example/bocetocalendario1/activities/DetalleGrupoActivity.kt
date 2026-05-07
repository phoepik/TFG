package com.example.bocetocalendario1.activities

import android.content.Intent
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
import com.example.bocetocalendario1.adaptadores.MiembroAdapter
import com.example.bocetocalendario1.models.Usuario
import com.example.bocetocalendario1.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DetalleGrupoActivity : AppCompatActivity() {

    private val GROUP_BACKGROUNDS = listOf(
        R.drawable.bg_group_blue,
        R.drawable.bg_group_magenta,
        R.drawable.bg_group_green,
        R.drawable.bg_group_purple,
        R.drawable.bg_group_orange,
        R.drawable.bg_group_teal
    )

    private val GROUP_COLORS = listOf(
        "#0B5FFF", "#E94B7B", "#22C55E", "#8B5CF6", "#F97316", "#14B8A6"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_grupo)

        val tvNombreGrupo = findViewById<TextView>(R.id.tvNombreGrupo)
        val tvDescripcion = findViewById<TextView>(R.id.tvDescripcion)
        val tvMiembrosCount = findViewById<TextView>(R.id.tvMiembrosCount)
        val tvEventosCount = findViewById<TextView>(R.id.tvEventosCount)
        val rvMiembros = findViewById<RecyclerView>(R.id.rvMiembros)
        val layoutMiembrosScroll = findViewById<LinearLayout>(R.id.layoutMiembrosScroll)
        val heroFrame = findViewById<FrameLayout>(R.id.heroFrame)

        val grupoId = intent.getIntExtra("GRUPO_ID", 0)
        val grupoNombre = intent.getStringExtra("GRUPO_NOMBRE") ?: "Grupo"
        val grupoDescripcion = intent.getStringExtra("GRUPO_DESCRIPCION") ?: ""
        val grupoIdx = intent.getIntExtra("GRUPO_IDX", 0)

        tvNombreGrupo.text = grupoNombre
        if (grupoDescripcion.isNotBlank()) {
            tvDescripcion.text = grupoDescripcion
        } else {
            tvDescripcion.visibility = android.view.View.GONE
        }

        // Set hero background
        heroFrame?.setBackgroundResource(GROUP_BACKGROUNDS[grupoIdx % GROUP_BACKGROUNDS.size])

        // Back button
        val btnBack = findViewById<TextView>(R.id.btnBackDetalle)
        btnBack.setOnClickListener { finish() }

        // Menu button
        val btnMenu = findViewById<TextView>(R.id.btnMenuDetalle)
        btnMenu.setOnClickListener {
            GroupMenuBottomSheet.newInstance(grupoNombre, 0, grupoIdx)
                .show(supportFragmentManager, "GroupMenu")
        }

        // RecyclerView for events list
        rvMiembros.layoutManager = LinearLayoutManager(this)

        // Load members
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val membersResp = RetrofitClient.api.miembrosDeGrupo(grupoId)
                withContext(Dispatchers.Main) {
                    if (membersResp.isSuccessful) {
                        val memberMaps = membersResp.body() ?: emptyList()
                        val count = memberMaps.size
                        tvMiembrosCount.text = "$count ${if (count == 1) "miembro" else "miembros"}"

                        buildMemberAvatars(layoutMiembrosScroll, memberMaps, grupoIdx)

                        val miembros = memberMaps.mapIndexed { i, m ->
                            val uid = m["idUsuario"] ?: i
                            Usuario(uid, "Usuario $uid", "", "", true)
                        }
                        rvMiembros.adapter = MiembroAdapter(miembros, 1)
                    } else {
                        tvMiembrosCount.text = "0 miembros"
                        buildExampleAvatars(layoutMiembrosScroll, grupoIdx)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("DETALLE", "Error: ${e.message}")
                    tvMiembrosCount.text = "? miembros"
                    buildExampleAvatars(layoutMiembrosScroll, grupoIdx)
                    val ejemplos = listOf(
                        Usuario(1, "Admin", "admin@email.com", "", true),
                        Usuario(2, "Miembro", "m@email.com", "", true)
                    )
                    rvMiembros.adapter = MiembroAdapter(ejemplos, 1)
                }
            }
        }

        // Add member button
        val btnAnadirMiembro = findViewById<Button>(R.id.btnAnadirMiembro)
        btnAnadirMiembro.setOnClickListener {
            InviteBottomSheet.newInstance(grupoNombre).show(supportFragmentManager, "Invite")
        }

        // Delete group button
        val btnBorrarGrupo = findViewById<Button>(R.id.btnBorrarGrupo)
        btnBorrarGrupo.setOnClickListener {
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

    private fun buildMemberAvatars(layout: LinearLayout, memberMaps: List<Map<String, Int>>, grupoIdx: Int) {
        layout.removeAllViews()
        val colorHex = GROUP_COLORS[grupoIdx % GROUP_COLORS.size]
        val color = try { Color.parseColor(colorHex) } catch (e: Exception) { Color.parseColor("#0B5FFF") }

        memberMaps.take(8).forEachIndexed { i, m ->
            val uid = m["idUsuario"] ?: i
            addAvatarView(layout, "U${uid}", color)
        }

        // Invite plus button
        val size = dpToPx(52)
        val plus = TextView(this).apply {
            text = "+"
            textSize = 20f
            setTextColor(color)
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(size, size)
            lp.marginEnd = dpToPx(8)
            layoutParams = lp
            background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setStroke(dpToPx(2), color)
                setColor(Color.TRANSPARENT)
            }
        }
        layout.addView(plus)
    }

    private fun buildExampleAvatars(layout: LinearLayout, grupoIdx: Int) {
        layout.removeAllViews()
        val colorHex = GROUP_COLORS[grupoIdx % GROUP_COLORS.size]
        val color = try { Color.parseColor(colorHex) } catch (e: Exception) { Color.parseColor("#0B5FFF") }
        listOf("A", "B", "C").forEach { addAvatarView(layout, it, color) }
    }

    private fun addAvatarView(parent: LinearLayout, label: String, color: Int) {
        val size = dpToPx(52)
        val av = TextView(this).apply {
            text = label
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(size, size)
            lp.marginEnd = dpToPx(8)
            layoutParams = lp
            background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(color)
            }
        }
        parent.addView(av)
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
        ).toInt()
    }
}
