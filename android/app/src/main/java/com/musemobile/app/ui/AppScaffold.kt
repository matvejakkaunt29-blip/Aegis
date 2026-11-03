package com.musemobile.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.musemobile.app.auth.MuseAccount
import com.musemobile.app.chat.ChatViewModel
import com.musemobile.app.ui.theme.ThemeMode
import kotlinx.coroutines.launch

enum class AppScreen { HOME, CHAT, SETTINGS }

/**
 * Каркас фазы 1: боковой drawer + экраны.
 * Drawer (композиция из разбора): аватар + имя, новый чат,
 * «Проект», «Новый проект», «История чатов», внизу — поиск,
 * настройки и аккаунты. История в фазе 1 — заглушка-список:
 * реальные чаты хранятся per-account в ChatViewModel, а полная
 * история с поиском — задача фазы 2 вместе с артефактами.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    chatVm: ChatViewModel,
    accounts: List<MuseAccount>,
    activeAccount: MuseAccount?,
    themeMode: ThemeMode,
    onThemeMode: (ThemeMode) -> Unit,
    onSwitchAccount: (String) -> Unit,
    onAddAccount: () -> Unit,
    onLogoutAccount: (String) -> Unit,
    onLogoutAll: () -> Unit
) {
    var screen by remember { mutableStateOf(AppScreen.HOME) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { scope.launch { drawerState.open() } }
    val closeDrawer: () -> Unit = { scope.launch { drawerState.close() } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    Row {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp)) {
                            Box(contentAlignment = androidx.compose.ui.Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text((activeAccount?.displayName?.firstOrNull() ?: "M").toString())
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(activeAccount?.displayName ?: "Muse", style = MaterialTheme.typography.titleMedium)
                            Text(activeAccount?.email ?: "", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { chatVm.onAccountSwitched(); screen = AppScreen.CHAT; closeDrawer() }) {
                        Text("+ Новый чат")
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Проект ▾", style = MaterialTheme.typography.titleSmall)
                    TextButton(onClick = {}) { Text("Новый проект") }
                    Spacer(Modifier.height(8.dp))
                    Text("История чатов", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "В фазе 1 история — текущий чат активного аккаунта. Полная история с поиском — фаза 2.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.weight(1f))
                    Divider()
                    Text("Аккаунты", style = MaterialTheme.typography.titleSmall)
                    LazyColumn(Modifier.weight(1f, fill = false)) {
                        items(accounts) { acc ->
                            TextButton(onClick = { onSwitchAccount(acc.id); closeDrawer() }) {
                                Text((if (acc.isActive) "● " else "") + acc.displayName)
                            }
                        }
                    }
                    TextButton(onClick = onAddAccount) { Text("+ Добавить аккаунт") }
                    activeAccount?.let { acc ->
                        TextButton(onClick = { onLogoutAccount(acc.id); closeDrawer() }) { Text("Выйти из этого аккаунта") }
                    }
                    TextButton(onClick = { screen = AppScreen.SETTINGS; closeDrawer() }) { Text("Настройки") }
                }
            }
        }
    ) {
        when (screen) {
            AppScreen.HOME -> HomeScreen(
                accountName = activeAccount?.displayName ?: "",
                onOpenDrawer = openDrawer,
                onNewChat = { screen = AppScreen.CHAT },
                onQuickAction = { screen = AppScreen.CHAT }
            )
            AppScreen.CHAT -> ChatScreen(viewModel = chatVm, activeAccount = activeAccount, onOpenDrawer = openDrawer)
            AppScreen.SETTINGS -> SettingsScreen(
                accounts = accounts,
                activeAccount = activeAccount,
                themeMode = themeMode,
                onThemeMode = onThemeMode,
                onLogoutAll = onLogoutAll,
                onBack = { screen = AppScreen.HOME }
            )
        }
    }
}
