package com.musemobile.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.musemobile.app.auth.AuthManager
import com.musemobile.app.chat.ChatViewModel
import com.musemobile.app.ui.AppScaffold
import com.musemobile.app.ui.LoginScreen
import com.musemobile.app.ui.theme.MuseMobileTheme
import com.musemobile.app.ui.theme.ThemeMode

/**
 * Фаза 1 (согласована 2026-11-03): главный, drawer, чат, настройки,
 * вход и мульти-аккаунт. Токены — только в EncryptedSharedPreferences,
 * провайдер Muse — по умолчанию, реальный HTTP-эндпоинт Muse всё ещё
 * не подключён (MuseProvider — заглушка), сборка APK здесь невозможна:
 * на этой машине нет Android SDK, нужен Android Studio/Gradle или CI.
 * На GitHub ничего не пушится: по правилу «сначала согласуем» пуш
 * будет только после отдельного «ок» на эту фазу.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = AuthManager(applicationContext)
        val chatVm = ChatViewModel(auth)
        setContent {
            var themeMode by remember { mutableStateOf(ThemeMode.DARK) }
            MuseMobileTheme(mode = themeMode, isSystemDark = isSystemInDarkTheme()) {
                val accounts by auth.accounts.collectAsState()
                if (accounts.isEmpty()) {
                    LoginScreen(
                        onLoginClick = {
                            // Скелет: здесь открывается системный браузер на вход Muse.
                            // После redirect вызывается auth.addAccount(id, name, email, token).
                            auth.addAccount("demo", "Ethan", "demo@muse", "demo-token")
                        }
                    )
                } else {
                    AppScaffold(
                        chatVm = chatVm,
                        accounts = accounts,
                        activeAccount = auth.activeAccount,
                        themeMode = themeMode,
                        onThemeMode = { themeMode = it },
                        onSwitchAccount = { id -> auth.switchAccount(id); chatVm.onAccountSwitched() },
                        onAddAccount = { /* открыть вход Muse для нового аккаунта */ },
                        onLogoutAccount = { id -> auth.removeAccount(id) },
                        onLogoutAll = { auth.logoutAll() }
                    )
                }
            }
        }
    }
}
