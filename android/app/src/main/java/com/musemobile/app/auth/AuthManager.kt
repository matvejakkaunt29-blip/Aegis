package com.musemobile.app.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Мультиаккаунт: несколько Muse-аккаунтов одновременно.
 * Токен каждого аккаунта хранится только в EncryptedSharedPreferences.
 * Пароль здесь никогда не хранится — он вводится только на странице Muse.
 * Active account = аккаунт, от имени которого сейчас работает чат.
 */
data class MuseAccount(
    val id: String,
    val displayName: String,
    val email: String,
    val isActive: Boolean = false
)

class AuthManager(context: Context) {
    private val prefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        EncryptedSharedPreferences.create(
            context, "muse_accounts", masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private val _accounts = MutableStateFlow<List<MuseAccount>>(loadAccounts())
    val accounts: StateFlow<List<MuseAccount>> = _accounts

    val activeAccount: MuseAccount?
        get() = _accounts.value.firstOrNull { it.isActive } ?: _accounts.value.firstOrNull()

    fun isLoggedIn(): Boolean = _accounts.value.isNotEmpty()

    /** Вызывается после успешного входа в браузере: сохраняем токен + аккаунт. */
    fun addAccount(id: String, displayName: String, email: String, token: String) {
        prefs.edit().putString("token_$id", token).apply()
        val updated = _accounts.value.filterNot { it.id == id } +
            MuseAccount(id, displayName, email, isActive = _accounts.value.isEmpty())
        _accounts.value = updated
        saveAccounts(updated)
    }

    fun switchAccount(id: String) {
        val updated = _accounts.value.map { it.copy(isActive = it.id == id) }
        _accounts.value = updated
        saveAccounts(updated)
    }

    fun removeAccount(id: String) {
        prefs.edit().remove("token_$id").apply()
        val remaining = _accounts.value.filterNot { it.id == id }
        _accounts.value = if (remaining.isNotEmpty() && remaining.none { it.isActive }) {
            remaining.mapIndexed { i, a -> a.copy(isActive = i == 0) }
        } else remaining
        saveAccounts(_accounts.value)
    }

    fun tokenFor(accountId: String): String? = prefs.getString("token_$accountId", null)

    fun logoutAll() {
        _accounts.value.forEach { prefs.edit().remove("token_${it.id}").apply() }
        _accounts.value = emptyList()
        saveAccounts(emptyList())
    }

    private fun loadAccounts(): List<MuseAccount> {
        val raw = prefs.getString("accounts_json", null) ?: return emptyList()
        // Скелет: в полной версии — kotlinx-serialization. Здесь храним id|name|email|active по строкам.
        return raw.lines().filter { it.isNotBlank() }.mapNotNull { line ->
            val p = line.split("|")
            if (p.size >= 3) MuseAccount(p[0], p[1], p[2], p.getOrNull(3) == "1") else null
        }
    }

    private fun saveAccounts(list: List<MuseAccount>) {
        val raw = list.joinToString("\n") {
            "${it.id}|${it.displayName}|${it.email}|${if (it.isActive) 1 else 0}"
        }
        prefs.edit().putString("accounts_json", raw).apply()
    }
}
