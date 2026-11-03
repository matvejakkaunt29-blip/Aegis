package com.musemobile.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musemobile.app.chat.WorkspaceViewModel
import com.musemobile.app.plugin.PluginManifest

/**
 * Фаза 3: экраны проектов, плагинов и запланированных задач.
 * Проекты — папки для чатов, плагины — модули с манифестом и
 * разрешениями (установка только после проверки), задачи —
 * напоминания, которые ассистент выполняет в заданное время.
 * В фазе 3 списки живут в памяти; WorkManager-планирование
 * реального выполнения задач — следующий шаг после проверки UI.
 */
@Composable
fun ProjectsScreen(vm: WorkspaceViewModel, onBack: () -> Unit, onOpenChat: () -> Unit) {
    val projects by vm.projects.collectAsState()
    var name by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row { TextButton(onClick = onBack) { Text("‹") }; Text("Проекты", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f)) }
        Spacer(Modifier.height(12.dp))
        Row {
            OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.weight(1f), placeholder = { Text("Название проекта") })
            Spacer(Modifier.width(8.dp))
            Button(onClick = { vm.addProject(name); name = "" }) { Text("+") }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn {
            items(projects) { p ->
                Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(Modifier.padding(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text(p.name, style = MaterialTheme.typography.titleSmall)
                            Text("Чаты проекта — в фазе 3 привязываются к текущему чату", style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = onOpenChat) { Text("Открыть ›") }
                    }
                }
            }
            if (projects.isEmpty()) {
                item { Text("Пока нет проектов. Создай первый — сюда будут собираться чаты по теме.", style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

@Composable
fun PluginsScreen(vm: WorkspaceViewModel, onBack: () -> Unit) {
    val plugins by vm.plugins.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row { TextButton(onClick = onBack) { Text("‹") }; Text("Плагины и модули", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f)) }
        Spacer(Modifier.height(12.dp))
        Text(
            "Любой плагин или тему можно сгенерировать в чате. Установка — только после проверки манифеста и прав, с откатом в один тап.",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            vm.installPlugin(PluginManifest(id = "demo-plugin", name = "Демо-плагин", version = "0.1.0", type = "plugin", permissions = listOf("files.app_folder"), tools = emptyList()))
        }) { Text("Установить демо-плагин") }
        Spacer(Modifier.height(12.dp))
        LazyColumn {
            items(plugins) { p ->
                Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(Modifier.padding(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text(p.manifest.name, style = MaterialTheme.typography.titleSmall)
                            Text("v${p.manifest.version} • права: ${p.manifest.permissions.joinToString(", ").ifBlank { "нет" }}", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(checked = p.enabled, onCheckedChange = { vm.togglePlugin(p.manifest.id) })
                    }
                }
            }
            if (plugins.isEmpty()) {
                item { Text("Плагинов пока нет. Сгенерируй первый в чате — например, «сделай плагин для заметок».", style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

@Composable
fun TasksScreen(vm: WorkspaceViewModel, onBack: () -> Unit) {
    val tasks by vm.tasks.collectAsState()
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("09:00") }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row { TextButton(onClick = onBack) { Text("‹") }; Text("Запланированные задачи", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f)) }
        Spacer(Modifier.height(12.dp))
        Row {
            OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.weight(1f), placeholder = { Text("Что сделать?") })
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(value = time, onValueChange = { time = it }, modifier = Modifier.width(90.dp), placeholder = { Text("09:00") })
            Spacer(Modifier.width(8.dp))
            Button(onClick = { vm.addTask(title, time); title = "" }) { Text("+") }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn {
            items(tasks) { t ->
                Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(Modifier.padding(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text(t.title, style = MaterialTheme.typography.titleSmall)
                            Text("Время: ${t.timeText}", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(checked = t.enabled, onCheckedChange = { vm.toggleTask(t.id) })
                        TextButton(onClick = { vm.removeTask(t.id) }) { Text("✕") }
                    }
                }
            }
            if (tasks.isEmpty()) {
                item { Text("Задач пока нет. Запланируй первую — ассистент выполнит её в заданное время.", style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}
