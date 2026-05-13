package com.example.bocetocalendario1.utilidades

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordUtils {
    private val FIXED_SALT = byteArrayOf(
        0x54, 0x46, 0x47, 0x43, 0x61, 0x6C, 0x65, 0x6E,
        0x64, 0x61, 0x72, 0x69, 0x6F, 0x41, 0x70, 0x70
    )

    fun hash(password: String): String {
        val rawHash = BCrypt.withDefaults().hash(10, FIXED_SALT, password.toByteArray(Charsets.UTF_8))
        return String(rawHash, Charsets.UTF_8)
    }
}