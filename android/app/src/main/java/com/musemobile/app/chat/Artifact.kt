package com.musemobile.app.chat

/**
 * Фаза 2: артефакты. Артефакт — это результат работы ассистента,
 * который показывается отдельно от ленты чата, в WebView:
 * HTML-страница, таблица, слайды, диаграмма. Контент — свой,
 * генерируется локально из запроса, а не копируется из Kimi:
 * у Kimi та же идея (WebView + mermaid/markmap/pdf), реализация своя.
 * PDF в фазе 2 — как HTML-предпросмотр, настоящий PDF-рендер — позже.
 */
enum class ArtifactType(val label: String) {
    HTML("HTML-страница"),
    TABLE("Таблица"),
    SLIDES("Слайды"),
    DIAGRAM("Диаграмма"),
    FILE("Файл")
}

data class Artifact(
    val id: String,
    val title: String,
    val type: ArtifactType,
    val html: String
)

enum class AgentTool(val label: String, val icon: String) {
    THINK("Думает", "🧠"),
    SEARCH("Поиск", "🔍"),
    WEB("Браузер", "🌐"),
    FILE("Файл", "📄"),
    CODE("Код", "💻"),
    MEMORY("Память", "🧩"),
    TASK("Задача", "✅"),
    IMAGE("Картинка", "🖼️")
}

enum class StepStatus { RUNNING, DONE, FAILED }

data class AgentStep(val tool: AgentTool, val text: String, val status: StepStatus)

data class ChatHistoryEntry(
    val id: String,
    val title: String,
    val accountId: String,
    val messages: List<com.musemobile.app.provider.ChatMessage>
)

/** Простая локальная генерация артефакта по ключевым словам запроса. */
fun artifactForRequest(request: String): Artifact? {
    val lower = request.lowercase()
    return when {
        "таблиц" in lower -> Artifact(
            "table-1", "Таблица по запросу", ArtifactType.TABLE,
            "<html><body style='background:#111;color:#eee;font-family:sans-serif;padding:16px'>" +
                "<h2>Таблица</h2><table border='1' cellpadding='8'><tr><th>Пункт</th><th>Значение</th></tr>" +
                "<tr><td>Запрос</td><td>${request.take(80)}</td></tr></table></body></html>"
        )
        "слайд" in lower -> Artifact(
            "slides-1", "Слайды по запросу", ArtifactType.SLIDES,
            "<html><body style='background:#111;color:#eee;font-family:sans-serif;padding:16px'>" +
                "<h1>Слайд 1</h1><p>${request.take(120)}</p><hr><h1>Слайд 2</h1><p>Выводы и следующие шаги.</p></body></html>"
        )
        "диаграмм" in lower || "mermaid" in lower -> Artifact(
            "diagram-1", "Диаграмма по запросу", ArtifactType.DIAGRAM,
            "<html><body style='background:#111;color:#eee;font-family:sans-serif;padding:16px'>" +
                "<h2>Диаграмма</h2><pre>Запрос → Анализ → Ответ\n  ↓\nАртефакт</pre></body></html>"
        )
        "артефакт" in lower || "html" in lower -> Artifact(
            "html-1", "HTML-артефакт", ArtifactType.HTML,
            "<html><body style='background:#111;color:#eee;font-family:sans-serif;padding:16px'>" +
                "<h1>Артефакт</h1><p>${request.take(200)}</p></body></html>"
        )
        else -> null
    }
}
