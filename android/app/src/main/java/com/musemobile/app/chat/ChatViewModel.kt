package com.musemobile.app.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musemobile.app.auth.AuthManager
import com.musemobile.app.provider.ChatMessage
import com.musemobile.app.provider.MuseProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val auth: AuthManager) : ViewModel() {
    private val provider = MuseProvider(auth)
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun send(text: String) {
        val accountId = auth.activeAccount?.id ?: ""
        val userMsg = ChatMessage("user", text, accountId)
        _messages.value = _messages.value + userMsg
        viewModelScope.launch {
            val reply = provider.send(_messages.value) {}
            _messages.value = _messages.value + ChatMessage("assistant", reply, accountId)
        }
    }

    /** При переключении аккаунта чат нового аккаунта начинается отдельно. */
    fun onAccountSwitched() {
        _messages.value = emptyList()
    }
}
