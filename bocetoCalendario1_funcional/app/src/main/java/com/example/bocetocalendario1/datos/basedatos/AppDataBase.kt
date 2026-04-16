package com.example.bocetocalendario1.datos.basedatos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bocetocalendario1.datos.dao.AppDao
import com.example.bocetocalendario1.datos.modelo.Usuario
import com.example.bocetocalendario1.datos.modelo.Grupo
import com.example.bocetocalendario1.datos.modelo.UsuarioGrupo
import com.example.bocetocalendario1.datos.modelo.Calendario
import com.example.bocetocalendario1.datos.modelo.Evento
import com.example.bocetocalendario1.datos.modelo.Notificacion

@Database(
    entities = [
        Usuario::class,
        Grupo::class,
        UsuarioGrupo::class,
        Calendario::class,
        Evento::class,
        Notificacion::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "base_calendario"
                )
                    .fallbackToDestructiveMigration() // En desarrollo; en producción usar Migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
