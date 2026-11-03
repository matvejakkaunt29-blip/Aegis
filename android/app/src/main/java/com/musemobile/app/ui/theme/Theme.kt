package com.musemobile.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Фаза 1, тёмная тема по композиции из разбора Kimi:
 * фон #111111, карточки/группы #1E1E1E с большими скруглениями.
 * Это композиция, а не ассеты Kimi: цвета и формы свои, брендинг Muse.
 * Размеры — в dp, поэтому на любом телефоне Compose сам подгоняет
 * их под физическое разрешение при запуске (вопрос про авто-подгонку
 * из макета v6 здесь решён на уровне платформы, а не HTML).
 */
enum class ThemeMode { DARK, LIGHT, SYSTEM }

private val DarkColors = darkColorScheme(
    primary = Color(0xFF5B8DEF),
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFF111111),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF1E1E1E),
    onBackground = Color(0xFFEDEDED),
    onSurface = Color(0xFFEDEDED),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF2E2E2E)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF2B6FE3),
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFFFFFFF),
    onBackground = Color(0xFF111111),
    onSurface = Color(0xFF111111),
    onSurfaceVariant = Color(0xFF666666),
    outline = Color(0xFFE0E0E0)
)

@Composable
fun MuseMobileTheme(
    mode: ThemeMode = ThemeMode.DARK,
    isSystemDark: Boolean = true,
    content: @Composable () -> Unit
) {
    val dark = when (mode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemDark
    }
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        content = content
    )
}
