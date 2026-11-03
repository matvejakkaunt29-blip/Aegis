package com.musemobile.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Вход, фаза 1. Тёмный экран, синий орб, одна кнопка входа.
 * Пароль вводится только на официальной странице Muse в системном
 * браузере — здесь пароль не запрашивается и не хранится.
 * После входа токен уходит только в EncryptedSharedPreferences.
 */
@Composable
fun LoginScreen(onLoginClick: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(96.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) { Text("✦", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimary) }
        Spacer(Modifier.height(24.dp))
        Text("Muse Mobile", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Muse уже выбран по умолчанию. Войди в Muse-аккаунт — пароль вводится только на странице Muse.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) { Text("Войти в Muse-аккаунт") }
        Spacer(Modifier.height(12.dp))
        Text(
            "Можно добавить несколько аккаунтов и переключаться между ними без выхода.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
