package com.musemobile.app.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musemobile.app.auth.AuthManager
import com.musemobile.app.provider.ChatMessage
import com.musemobile.app.provider.MuseProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Фаза 2: к сообщениям добавились агентные шаги, артефакты и история.
 * Шаги — это то, что ассистент «делает» перед ответом: думает, ищет,
 * читает файл, проверяет память. Пока провайдер — заглушка, шаги
 * симулируются с короткими задержками, чтобы UI можно было проверить;
 * при подключении реального API они будут приходить из стрима.
 * История: при старте нового чата текущий чат сохраняется, а не теряется.
 */
class ChatViewModel(private val auth: AuthManager) : ViewModel() {
    private val provider = MuseProvider(auth)
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _steps = MutableStateFlow<List<AgentStep>>(emptyList())
    val steps: StateFlow<List<AgentStep>> = _steps

    private val _artifacts = MutableStateFlow<List<Artifact>>(emptyList())
    val artifacts: StateFlow<List<Artifact>> = _artifacts

    private val _history = MutableStateFlow<List<ChatHistoryEntry>>(emptyList())
    val history: StateFlow<List<ChatHistoryEntry>> = _history

    fun send(text: String) {
        val accountId = auth.activeAccount?.id ?: ""
        val userMsg = ChatMessage("user", text, accountId)
        _messages.value = _messages.value + userMsg
        viewModelScope.launch {
            // Агентные шаги: think -> search -> memory, затем ответ.
            val plan = listOf(
                AgentStep(AgentTool.THINK, "Разбираю запрос", StepStatus.RUNNING),
                AgentStep(AgentTool.SEARCH, "Ищу по контексту", StepStatus.RUNNING),
                AgentStep(AgentTool.MEMORY, "Проверяю память", StepStatus.RUNNING)
            )
            _steps.value = plan
            plan.forEachIndexed { i, _ ->
                delay(250)
                _steps.value = _steps.value.mapIndexed { j, s ->
                    if (j == i) s.copy(status = StepStatus.DONE) else s
                }
            }
            val reply = provider.send(_messages.value) {}
            _messages.value = _messages.value + ChatMessage("assistant", reply, accountId)
            artifactForRequest(text)?.let { art ->
                if (_artifacts.value.none { it.id == art.id }) {
                    _artifacts.value = _artifacts.value + art
                }
            }
            _steps.value = emptyList()
        }
    }

    /** Новый чат: старый сохраняется в историю активного аккаунта. */
    fun startNewChat() {
        val current = _messages.value
        val accountId = auth.activeAccount?.id ?: ""
        if (current.isNotEmpty()) {
            val title = current.firstOrNull { it.role == "user" }?.text?.take(40) ?: "Чат"
            _history.value = listOf(
                ChatHistoryEntry(System.currentTimeMillis().toString(), title, accountId, current)
            ) + _history.value
        }
        _messages.value = emptyList()
        _artifacts.value = emptyList()
        _steps.value = emptyList()
    }

    fun loadHistory(entry: ChatHistoryEntry) {
        _messages.value = entry.messages
        _artifacts.value = emptyList()
        _steps.value = emptyList()
    }

    /** При переключении аккаунта чат нового аккаунта начинается отдельно. */
    fun onAccountSwitched() {
        _messages.value = emptyList()
        _artifacts.value = emptyList()
        _steps.value = emptyList()
        // История фильтруется по аккаунту на стороне UI.
    }
}
