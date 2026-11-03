package com.musemobile.app.ui

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.musemobile.app.chat.Artifact

/**
 * Фаза 2: карточка артефакта в ленте и отдельный просмотр в WebView.
 * WebView — системный, JavaScript включён только для локального HTML
 * артефакта (контент генерируется в приложении, внешние URL не грузятся).
 */
@Composable
fun ArtifactCard(artifact: Artifact, onClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(Modifier.padding(12.dp)) {
            Text("📄", modifier = Modifier.padding(end = 8.dp))
            Column(Modifier.weight(1f)) {
                Text(artifact.title, style = MaterialTheme.typography.titleSmall)
                Text(artifact.type.label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = onClick) { Text("Открыть ›") }
        }
    }
}

@Composable
fun ArtifactScreen(artifact: Artifact, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
            TextButton(onClick = onBack) { Text("‹ Назад") }
            Text(artifact.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text(artifact.type.label, style = MaterialTheme.typography.bodySmall)
        }
        Divider(color = MaterialTheme.colorScheme.outline)
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    loadDataWithBaseURL(null, artifact.html, "text/html", "UTF-8", null)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AgentStepsRow(steps: List<com.musemobile.app.chat.AgentStep>) {
    if (steps.isEmpty()) return
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        steps.forEach { step ->
            Row {
                Text(step.tool.icon, modifier = Modifier.padding(end = 8.dp))
                Text(step.tool.label + ": " + step.text, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.weight(1f))
                Text(if (step.status == com.musemobile.app.chat.StepStatus.DONE) "✓" else "⏳", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
