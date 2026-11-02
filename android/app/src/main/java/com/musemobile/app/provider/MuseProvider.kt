package com.musemobile.app.provider

import com.musemobile.app.auth.AuthManager

/**
 * Muse — встроенный провайдер по умолчанию.
 * Токен берётся из активного аккаунта AuthManager, в код не зашивается.
 * TODO: подставить официальный endpoint Muse при реализации (см. docs/AUTH.md).
 */
class MuseProvider(private val auth: AuthManager) : Provider {
    override val id = "muse"
    override val displayName = "Muse"

    override suspend fun send(messages: List<ChatMessage>, onDelta: (String) -> Unit): String {
        val account = auth.activeAccount
            ?: return "Сначала войди в Muse-аккаунт."
        val token = auth.tokenFor(account.id)
            ?: return "Сессия аккаунта ${account.displayName} истекла, войди снова."
        // Скелет: здесь будет HTTP-запрос с Authorization: Bearer <token>.
        // Пока возвращаем заглушку, чтобы UI можно было проверить без API.
        val reply = "[Muse • ${account.displayName}] Скелет: ответ API ещё не подключён. " +
            "Последнее сообщение: ${messages.lastOrNull()?.text ?: ""}"
        onDelta(reply)
        return reply
    }
}
