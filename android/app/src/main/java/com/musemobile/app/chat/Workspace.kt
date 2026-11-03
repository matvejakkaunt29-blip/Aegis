package com.musemobile.app.chat

import androidx.lifecycle.ViewModel
import com.musemobile.app.plugin.PluginLoader
import com.musemobile.app.plugin.PluginManifest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Фаза 3: проекты, плагины и запланированные задачи.
 * Хранение в фазе 3 — в памяти приложения (StateFlow), а не в Room:
 * персистентность проектов/задач — следующий шаг после проверки UI.
 * Плагины: установка только после validate() из PluginLoader —
 * манифест без системных android.permission.* и с именем/id.
 */
data class Project(val id: String, val name: String, val description: String = "")
data class PluginItem(val manifest: PluginManifest, val enabled: Boolean)
data class ScheduledTask(val id: String, val title: String, val timeText: String, val enabled: Boolean = true)

class WorkspaceViewModel : ViewModel() {
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    private val _plugins = MutableStateFlow<List<PluginItem>>(emptyList())
    val plugins: StateFlow<List<PluginItem>> = _plugins

    private val _tasks = MutableStateFlow<List<ScheduledTask>>(emptyList())
    val tasks: StateFlow<List<ScheduledTask>> = _tasks

    fun addProject(name: String) {
        if (name.isBlank()) return
        _projects.value = _projects.value + Project(System.currentTimeMillis().toString(), name.trim())
    }

    /** Установка плагина из чата: сначала валидация, потом — в список. */
    fun installPlugin(manifest: PluginManifest): Boolean {
        if (!PluginLoader.validate(manifest)) return false
        if (_plugins.value.any { it.manifest.id == manifest.id }) return false
        _plugins.value = _plugins.value + PluginItem(manifest, enabled = true)
        return true
    }

    fun togglePlugin(id: String) {
        _plugins.value = _plugins.value.map {
            if (it.manifest.id == id) it.copy(enabled = !it.enabled) else it
        }
    }

    fun addTask(title: String, timeText: String) {
        if (title.isBlank()) return
        _tasks.value = _tasks.value + ScheduledTask(System.currentTimeMillis().toString(), title.trim(), timeText.ifBlank { "09:00" })
    }

    fun toggleTask(id: String) {
        _tasks.value = _tasks.value.map {
            if (it.id == id) it.copy(enabled = !it.enabled) else it
        }
    }

    fun removeTask(id: String) {
        _tasks.value = _tasks.value.filterNot { it.id == id }
    }
}
