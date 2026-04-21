package com.example.bocetocalendario1.utilidades

import android.content.Context
import android.content.SharedPreferences
import com.example.bocetocalendario1.datos.modelo.Usuario

class GestorSesion(context: Context) {
    private val PREFERENCIAS_NOMBRE = "sesion_usuario"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFERENCIAS_NOMBRE, Context.MODE_PRIVATE)

    companion object {
        const val CLAVE_ID_USUARIO = "id_usuario"
        const val CLAVE_NOMBRE_USUARIO = "nombre_usuario"

        const val CLAVE_EMAIL = "email_usuario"

        const val CLAVE_CONTRASENA = "contrasena_usuario"

        const val CLAVE_NOTIFICACIONES_ACTIVAS = "notificaciones_activas"

        const val CLAVE_ESTA_LOGUEADO = "esta_logueado"


    }

    fun guardarSesion(usuario: Usuario) {
        val editor = prefs.edit()
        editor.putInt(CLAVE_ID_USUARIO, usuario.id_usuario)
        editor.putString(CLAVE_NOMBRE_USUARIO, usuario.nombre)
        editor.putString(CLAVE_EMAIL, usuario.email)
        editor.putString(CLAVE_CONTRASENA, usuario.contrasena)
        editor.putBoolean(CLAVE_NOTIFICACIONES_ACTIVAS, usuario.notificaciones_activas)
        editor.putBoolean(CLAVE_ESTA_LOGUEADO, true)
        editor.apply()
    }

    fun obtenerIdUsuario(): Int? {
        return prefs.getInt(CLAVE_ID_USUARIO, -1)
    }

    fun obtenerNombreUsuario(): String? {
        return prefs.getString(CLAVE_NOMBRE_USUARIO, "")
    }

    fun obtenerEmail(): String? {
        return prefs.getString(CLAVE_EMAIL, "")
    }

    fun obtenerContrasena(): String? {
        return prefs.getString(CLAVE_CONTRASENA, "")
    }

    fun estanNotificacionesActivas(): Boolean {
        return prefs.getBoolean(CLAVE_NOTIFICACIONES_ACTIVAS, false)
    }

    fun estaLogueado(): Boolean {
        return prefs.getBoolean(CLAVE_ESTA_LOGUEADO, false)
    }

    fun cerrarSesion() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}