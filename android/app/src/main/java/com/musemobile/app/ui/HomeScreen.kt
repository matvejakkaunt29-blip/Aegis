package com.musemobile.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Главный экран, фаза 1. Композиция из разбора Kimi (скриншоты):
 * круглая кнопка меню слева, пилюля модели по центру, кнопка звука
 * справа; по центру — синий орб и приветствие; внизу — чипы быстрых
 * действий и тёмная пилюля ввода. Всё — fillMaxSize, размеры в dp.
 */
@Composable
fun HomeScreen(
    accountName: String,
    onOpenDrawer: () -> Unit,
    onNewChat: () -> Unit,
    onQuickAction: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }
    Column(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Верхняя строка
        Row(
            Modifier.fillMaxWidth().padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenDrawer, modifier = Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface)) {
                Text("☰")
            }
            Spacer(Modifier.weight(1f))
            Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface) {
                Text("Muse Быстрый", Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {}, modifier = Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface)) {
                Text("🔊")
            }
        }
        Spacer(Modifier.height(48.dp))
        // Синий орб
        Box(
            Modifier.size(120.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) { Text("✦", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimary) }
        Spacer(Modifier.height(32.dp))
        Text(
            "Что сделаем сегодня с Muse?",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (accountName.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text("Аккаунт: $accountName", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.weight(1f))
        // Чипы быстрых действий
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
            listOf("Новый проект", "Слайды", "Файлы", "Голос").forEach { chip ->
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    TextButton(onClick = { onQuickAction(chip) }) { Text(chip) }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        // Пилюля ввода
        ChatInputBar(
            value = input,
            onValueChange = { input = it },
            onSend = { if (input.isNotBlank()) { onNewChat(); input = "" } }
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ChatInputBar(value: String, onValueChange: (String) -> Unit, onSend: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) { Text("+") }
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Спросите что угодно…") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surface
                )
            )
            IconButton(onClick = {}) { Text("🎤") }
            Button(onClick = onSend, shape = CircleShape, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(44.dp)) {
                Text("↑")
            }
        }
    }
}
