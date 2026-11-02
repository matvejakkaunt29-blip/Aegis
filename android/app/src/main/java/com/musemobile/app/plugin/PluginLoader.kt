package com.musemobile.app.plugin

import kotlinx.serialization.Serializable

@Serializable
data class PluginManifest(
    val id: String,
    val name: String,
    val version: String,
    val type: String, // "plugin" или "theme"
    val permissions: List<String> = emptyList(),
    val tools: List<PluginTool> = emptyList()
)

@Serializable
data class PluginTool(val name: String, val description: String)

/**
 * Заглушка следующего шага: проверка манифеста -> проверка прав ->
 * предпросмотр -> установка -> откат. Код из чата не исполняется
 * без проверки манифеста и прав.
 */
object PluginLoader {
    fun validate(manifest: PluginManifest): Boolean {
        if (manifest.id.isBlank() || manifest.name.isBlank()) return false
        // Разрешены только права внутри приложения, никаких системных без явного разрешения Android.
        val forbidden = manifest.permissions.filter { it.startsWith("android.permission.") }
        return forbidden.isEmpty()
    }
}
