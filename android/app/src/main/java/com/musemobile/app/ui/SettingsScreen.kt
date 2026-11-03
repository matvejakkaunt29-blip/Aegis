package com.musemobile.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musemobile.app.auth.MuseAccount
import com.musemobile.app.ui.theme.ThemeMode

/**
 * Настройки, фаза 1. Группы тёмных карточек с разделителями и «›»,
 * как в разобранном интерфейсе: аккаунт, уведомления, появление,
 * голос, функции, подписка, персонализация, помощь, выход.
 * Фаза 2/3 сюда добавят: артефакты, плагины, задачи — отдельными
 * группами, а не переделкой этого экрана.
 */
@Composable
fun SettingsScreen(
    accounts: List<MuseAccount>,
    activeAccount: MuseAccount?,
    themeMode: ThemeMode,
    onThemeMode: (ThemeMode) -> Unit,
    onLogoutAll: () -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Row(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                TextButton(onClick = onBack) { Text("‹") }
                Text("Настройки", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            }
        }
        item {
            SettingsGroup("Аккаунт") {
                SettingRow("Безопасность аккаунта", activeAccount?.email ?: "")
                SettingRow("Уведомления", "Включены")
            }
            Spacer(Modifier.height(12.dp))
            SettingsGroup("Появление") {
                SettingRow("Тёмная тема", if (themeMode == ThemeMode.DARK) "Тёмная" else if (themeMode == ThemeMode.LIGHT) "Светлая" else "Системная")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf(ThemeMode.DARK to "Тёмная", ThemeMode.LIGHT to "Светлая", ThemeMode.SYSTEM to "Системная").forEach { (mode, label) ->
                        FilterChip(selected = themeMode == mode, onClick = { onThemeMode(mode) }, label = { Text(label) })
                    }
                }
                SettingRow("Озвучка голосом", "Включена")
                SettingRow("Управление функциями", "")
            }
            Spacer(Modifier.height(12.dp))
            SettingsGroup("Подписка") {
                SettingRow("Мои подписки", "Free")
                SettingRow("Лимит и счета", "")
                SettingRow("Подарочные карты", "")
            }
            Spacer(Modifier.height(12.dp))
            SettingsGroup("Персонализация") {
                SettingRow("Память", "Управление тем, что Muse помнит")
                SettingRow("Настройки чата", "")
            }
            Spacer(Modifier.height(12.dp))
            SettingsGroup("Помощь") {
                SettingRow("Справочный центр", "")
                SettingRow("Отправить отзыв", "")
                SettingRow("О Muse", "")
                SettingRow("Проверить обновления", "Версия 0.3.0-phase2-3")
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onLogoutAll,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface)
            ) { Text("Выйти из всех аккаунтов") }
            Spacer(Modifier.height(8.dp))
            Text(
                "Аккаунтов: ${accounts.size}. Выход из одного аккаунта — из drawer, выход из всех — здесь.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
    Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.fillMaxWidth().padding(4.dp), content = content)
    }
}

@Composable
private fun SettingRow(title: String, value: String) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text(title, modifier = Modifier.weight(1f))
            Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (value.isNotBlank()) {
            Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    Divider(color = MaterialTheme.colorScheme.outline)
}
