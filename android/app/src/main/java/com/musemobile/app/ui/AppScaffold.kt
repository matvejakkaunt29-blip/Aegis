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
import com.musemobile.app.chat.Artifact
import com.musemobile.app.chat.ChatViewModel
import com.musemobile.app.chat.WorkspaceViewModel
import com.musemobile.app.ui.theme.ThemeMode
import kotlinx.coroutines.launch

enum class AppScreen { HOME, CHAT, SETTINGS, ARTIFACT, PROJECTS, PLUGINS, TASKS }

/**
 * Каркас фаз 1-3: боковой drawer + экраны.
 * Drawer: аватар, новый чат, проекты, история (реальная, фазы 2-3,
 * фильтруется по активному аккаунту), плагины, задачи, аккаунты,
 * настройки. Артефакт открывается отдельным экраном из чата.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    chatVm: ChatViewModel,
    workspaceVm: WorkspaceViewModel,
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
    var openArtifact by remember { mutableStateOf<Artifact?>(null) }
    val history by chatVm.history.collectAsState()
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
                    Button(onClick = { chatVm.startNewChat(); screen = AppScreen.CHAT; closeDrawer() }) {
                        Text("+ Новый чат")
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Проект ▾", style = MaterialTheme.typography.titleSmall)
                    TextButton(onClick = { screen = AppScreen.PROJECTS; closeDrawer() }) { Text("Проекты") }
                    TextButton(onClick = { screen = AppScreen.PLUGINS; closeDrawer() }) { Text("Плагины и модули") }
                    TextButton(onClick = { screen = AppScreen.TASKS; closeDrawer() }) { Text("Запланированные задачи") }
                    Spacer(Modifier.height(8.dp))
                    Text("История чатов", style = MaterialTheme.typography.titleSmall)
                    val myHistory = history.filter { it.accountId == (activeAccount?.id ?: "") }
                    if (myHistory.isEmpty()) {
                        Text("История пуста — начни чат, и он появится здесь после нового чата.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        LazyColumn(Modifier.weight(1f, fill = false)) {
                            items(myHistory) { entry ->
                                TextButton(onClick = { chatVm.loadHistory(entry); screen = AppScreen.CHAT; closeDrawer() }) {
                                    Text(entry.title)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Divider()
                    Text("Аккаунты", style = MaterialTheme.typography.titleSmall)
                    accounts.forEach { acc ->
                        TextButton(onClick = { onSwitchAccount(acc.id); closeDrawer() }) {
                            Text((if (acc.isActive) "● " else "") + acc.displayName)
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
            AppScreen.CHAT -> ChatScreen(
                viewModel = chatVm,
                activeAccount = activeAccount,
                onOpenDrawer = openDrawer,
                onArtifactClick = { art -> openArtifact = art; screen = AppScreen.ARTIFACT }
            )
            AppScreen.ARTIFACT -> openArtifact?.let { art ->
                ArtifactScreen(artifact = art, onBack = { screen = AppScreen.CHAT })
            } ?: ChatScreen(viewModel = chatVm, activeAccount = activeAccount, onOpenDrawer = openDrawer, onArtifactClick = {})
            AppScreen.PROJECTS -> ProjectsScreen(vm = workspaceVm, onBack = { screen = AppScreen.HOME }, onOpenChat = { screen = AppScreen.CHAT })
            AppScreen.PLUGINS -> PluginsScreen(vm = workspaceVm, onBack = { screen = AppScreen.HOME })
            AppScreen.TASKS -> TasksScreen(vm = workspaceVm, onBack = { screen = AppScreen.HOME })
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
