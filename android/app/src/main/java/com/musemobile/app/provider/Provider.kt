package com.musemobile.app.provider

/** Единый интерфейс для всех провайдеров: Muse по умолчанию + любые другие. */
data class ChatMessage(val role: String, val text: String, val accountId: String = "")

interface Provider {
    val id: String
    val displayName: String
    suspend fun send(messages: List<ChatMessage>, onDelta: (String) -> Unit): String
}
