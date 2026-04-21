package com.example.bocetocalendario1.network

import retrofit2.Response
import retrofit2.http.*

// ── Data classes para peticiones ──

data class RegistroRequest(
    val nombre: String,
    val email: String,
    val contrasena: String
)

data class LoginRequest(
    val email: String,
    val contrasena: String
)

// ── Data classes para respuestas del servidor ──

data class UsuarioResponse(
    val idUsuario: Int = 0,
    val nombre: String = "",
    val email: String = "",
    val contrasena: String = "",
    val notificacionesActivas: Boolean = true
)

data class EventoResponse(
    val idEvento: Int? = null,
    val titulo: String = "",
    val descripcion: String? = null,
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val ubicacion: String? = null,
    val estado: String? = null,
    val idCalendario: Int? = null
)

data class GrupoResponse(
    val idGrupo: Int? = null,
    val nombre: String = "",
    val descripcion: String? = null,
    val idAdmin: Int? = null
)

data class NotificacionResponse(
    val idNotificacion: Int = 0,
    val titulo: String = "",
    val mensaje: String? = null,
    val tiempoAnticipacion: Int? = null,
    val tipo: String = "",
    val idEvento: Int? = null,
    val idUsuario: Int = 0,
    val leida: Boolean = false,
    val fechaCreacion: Long? = null,
    val idGrupoInvitacion: Int? = null,
    val estadoInvitacion: String? = null
)

// ── Interfaz Retrofit ──

interface CalendarioApi {

    // ── Usuarios ──

    @POST("api/usuarios/registro")
    suspend fun registro(@Body usuario: RegistroRequest): Response<UsuarioResponse>

    @POST("api/usuarios/login")
    suspend fun login(@Body login: LoginRequest): Response<UsuarioResponse>

    @GET("api/usuarios/{id}")
    suspend fun obtenerUsuario(@Path("id") id: Int): Response<UsuarioResponse>

    @PUT("api/usuarios/{id}/notificaciones")
    suspend fun actualizarNotificaciones(
        @Path("id") id: Int,
        @Body body: Map<String, Boolean>
    ): Response<Void>

    // ── Eventos ──

    @POST("api/eventos")
    suspend fun crearEvento(@Body evento: EventoResponse): Response<EventoResponse>

    @GET("api/eventos/{id}")
    suspend fun obtenerEvento(@Path("id") id: Int): Response<EventoResponse>

    @GET("api/eventos/calendario/{id}")
    suspend fun obtenerEventosDeCalendario(@Path("id") id: Int): Response<List<EventoResponse>>

    @PUT("api/eventos/{id}")
    suspend fun actualizarEvento(@Path("id") id: Int, @Body evento: EventoResponse): Response<EventoResponse>

    @DELETE("api/eventos/{id}")
    suspend fun borrarEvento(@Path("id") id: Int): Response<Void>

    // ── Grupos ──

    @POST("api/grupos")
    suspend fun crearGrupo(@Body grupo: GrupoResponse): Response<GrupoResponse>

    @GET("api/grupos/{id}")
    suspend fun obtenerGrupo(@Path("id") id: Int): Response<GrupoResponse>

    @GET("api/grupos/usuario/{id}")
    suspend fun obtenerGruposDeUsuario(@Path("id") id: Int): Response<List<GrupoResponse>>

    @PUT("api/grupos/{id}")
    suspend fun actualizarGrupo(@Path("id") id: Int, @Body grupo: GrupoResponse): Response<GrupoResponse>

    @DELETE("api/grupos/{id}")
    suspend fun borrarGrupo(@Path("id") id: Int): Response<Void>

    // ── Notificaciones ──

    @POST("api/notificaciones")
    suspend fun crearNotificacion(@Body notificacion: NotificacionResponse): Response<NotificacionResponse>

    @GET("api/notificaciones/usuario/{id}")
    suspend fun obtenerNotificaciones(@Path("id") id: Int): Response<List<NotificacionResponse>>

    @GET("api/notificaciones/usuario/{id}/no-leidas")
    suspend fun obtenerNotificacionesNoLeidas(@Path("id") id: Int): Response<List<NotificacionResponse>>

    @GET("api/notificaciones/usuario/{id}/contador")
    suspend fun contarNoLeidas(@Path("id") id: Int): Response<Map<String, Long>>

    @PUT("api/notificaciones/{id}/leida")
    suspend fun marcarLeida(@Path("id") id: Int): Response<Void>

    @PUT("api/notificaciones/usuario/{id}/leer-todas")
    suspend fun marcarTodasLeidas(@Path("id") id: Int): Response<Void>

    @PUT("api/notificaciones/{id}/invitacion")
    suspend fun actualizarInvitacion(
        @Path("id") id: Int,
        @Body estado: Map<String, String>
    ): Response<Void>

    @DELETE("api/notificaciones/{id}")
    suspend fun borrarNotificacion(@Path("id") id: Int): Response<Void>

    // ── Miembros de grupo ──

    @POST("api/usuarios-grupos")
    suspend fun anadirMiembro(@Body body: Map<String, Int>): Response<Void>

    @GET("api/usuarios-grupos/grupo/{id}")
    suspend fun miembrosDeGrupo(@Path("id") id: Int): Response<List<Map<String, Int>>>

    @GET("api/usuarios-grupos/existe")
    suspend fun esMiembro(
        @Query("idUsuario") idUsuario: Int,
        @Query("idGrupo") idGrupo: Int
    ): Response<Map<String, Boolean>>
}
