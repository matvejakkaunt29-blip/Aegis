package com.musemobile.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.musemobile.app.auth.AuthManager
import com.musemobile.app.chat.ChatViewModel
import com.musemobile.app.ui.ChatScreen
import com.musemobile.app.ui.LoginScreen
import com.musemobile.app.ui.theme.MuseMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = AuthManager(applicationContext)
        val chatVm = ChatViewModel(auth)
        setContent {
            MuseMobileTheme {
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
                    ChatScreen(
                        viewModel = chatVm,
                        accounts = accounts,
                        activeAccount = auth.activeAccount,
                        onSwitchAccount = { id -> auth.switchAccount(id); chatVm.onAccountSwitched() },
                        onAddAccount = { /* открыть вход Muse для нового аккаунта */ },
                        onLogoutAccount = { id -> auth.removeAccount(id) }
                    )
                }
            }
        }
    }
}
