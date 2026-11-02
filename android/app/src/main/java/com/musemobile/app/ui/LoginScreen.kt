package com.musemobile.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(onLoginClick: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Center) {
        Text("✦ Muse Mobile", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Muse уже выбран по умолчанию. Войди в Muse-аккаунт — пароль вводится только на странице Muse.")
        Spacer(Modifier.height(20.dp))
        Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth()) {
            Text("Войти в Muse-аккаунт")
        }
        Spacer(Modifier.height(8.dp))
        Text("Можно добавить несколько аккаунтов и переключаться между ними без выхода.", style = MaterialTheme.typography.bodySmall)
    }
}
