package com.musemobile.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musemobile.app.auth.MuseAccount
import com.musemobile.app.chat.ChatViewModel

/**
 * Экран чата, фаза 1. Ответы ассистента — без пузырей, обычным
 * текстом (как в разобранном интерфейсе), сообщения пользователя —
 * в тёмной карточке справа. Поле ввода — та же пилюля, что на главном.
 * Переключение аккаунтов — из drawer, а не из диалога поверх чата:
 * у каждого аккаунта свой чат, при переключении список очищается
 * (см. ChatViewModel.onAccountSwitched).
 */
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    activeAccount: MuseAccount?,
    onOpenDrawer: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    var input by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onOpenDrawer) { Text("☰") }
            Text(
                "Muse • ${activeAccount?.displayName ?: ""}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = { viewModel.onAccountSwitched() }) { Text("+ Новый чат") }
        }
        Divider(color = MaterialTheme.colorScheme.outline)
        LazyColumn(Modifier.weight(1f).padding(horizontal = 16.dp)) {
            items(messages) { msg ->
                if (msg.role == "user") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) { Text(msg.text, Modifier.padding(12.dp)) }
                    }
                } else {
                    Text(msg.text, Modifier.fillMaxWidth().padding(vertical = 10.dp))
                }
            }
        }
        Box(Modifier.padding(16.dp)) {
            ChatInputBar(
                value = input,
                onValueChange = { input = it },
                onSend = { if (input.isNotBlank()) { viewModel.send(input); input = "" } }
            )
        }
    }
}
