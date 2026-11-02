package com.musemobile.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musemobile.app.auth.MuseAccount
import com.musemobile.app.chat.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    accounts: List<MuseAccount>,
    activeAccount: MuseAccount?,
    onSwitchAccount: (String) -> Unit,
    onAddAccount: () -> Unit,
    onLogoutAccount: (String) -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    var input by remember { mutableStateOf("") }
    var showAccounts by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Muse • ${activeAccount?.displayName ?: ""}") },
                actions = { TextButton(onClick = { showAccounts = true }) { Text("Аккаунты ▾") } })
        },
        bottomBar = {
            Row(Modifier.fillMaxWidth().padding(12.dp)) {
                OutlinedTextField(value = input, onValueChange = { input = it },
                    modifier = Modifier.weight(1f), placeholder = { Text("Написать Muse…") })
                Spacer(Modifier.width(8.dp))
                Button(onClick = { if (input.isNotBlank()) { viewModel.send(input); input = "" } }) { Text("↑") }
            }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            items(messages) { msg ->
                Card(Modifier.fillMaxWidth().padding(8.dp)) {
                    Text(msg.text, Modifier.padding(12.dp))
                }
            }
        }
        if (showAccounts) {
            AlertDialog(onDismissRequest = { showAccounts = false },
                title = { Text("Аккаунты") },
                text = {
                    Column {
                        accounts.forEach { acc ->
                            TextButton(onClick = { onSwitchAccount(acc.id); showAccounts = false }) {
                                Text((if (acc.isActive) "● " else "") + acc.displayName + " — " + acc.email)
                            }
                        }
                        TextButton(onClick = onAddAccount) { Text("+ Добавить аккаунт") }
                        activeAccount?.let { TextButton(onClick = { onLogoutAccount(it.id); showAccounts = false }) { Text("Выйти из этого аккаунта") } }
                    }
                },
                confirmButton = { TextButton(onClick = { showAccounts = false }) { Text("Закрыть") } })
        }
    }
}
