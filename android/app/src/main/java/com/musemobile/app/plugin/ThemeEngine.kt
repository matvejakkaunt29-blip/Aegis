package com.musemobile.app.plugin

import kotlinx.serialization.Serializable

@Serializable
data class ThemeTokens(
    val primary: String,
    val accent: String,
    val background: String,
    val surface: String,
    val text: String,
    val cornerRadius: Int
)

/** Заглушка: тема из JSON применяется через Material 3 без перезапуска и без пересборки APK. */
object ThemeEngine {
    fun validate(theme: ThemeTokens): Boolean =
        listOf(theme.primary, theme.accent, theme.background, theme.surface, theme.text)
            .all { it.matches(Regex("^#[0-9A-Fa-f]{6}$")) }
}
