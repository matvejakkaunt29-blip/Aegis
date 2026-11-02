# Muse Mobile — каркас (0.1.0-skeleton)

Автономный Android APK, без зависимости от ПК пользователя.
Muse — встроенный провайдер по умолчанию, вход — в Muse-аккаунт внутри приложения,
поддерживается несколько аккаунтов одновременно с переключением вверху чата.

## Что уже есть
- `android/` — Kotlin + Jetpack Compose каркас: Gradle, Manifest
- Экран входа (`LoginScreen`) и экран чата (`ChatScreen`)
- `AuthManager` — мультиаккаунт, токены только в EncryptedSharedPreferences + Keystore
- `MuseProvider` — единый Provider API, токен берётся из активного аккаунта
- `PluginLoader` / `ThemeEngine` — заглушки проверки манифеста, прав и темы из чата
- `docs/design-mockup.html` — макет: вход, чаты, чат, модули/темы
- `docs/ARCHITECTURE.md`, `docs/AUTH.md` — архитектура и вход

## Чего ещё нет (честно)
- Реальный HTTP-запрос к API Muse — в `MuseProvider` заглушка, нужен официальный endpoint.
- Сборка APK здесь не запускалась: на этой VM нет Java/Android SDK.
  Сборка: открыть `android/` в Android Studio, Sync Gradle, Run / Build APK.

## Следующие шаги
1. Подключить реальный endpoint Muse и вход через браузер.
2. Plugin-loader с установкой/откатом и генерацией плагинов в чате.
3. Theme-engine с применением темы без перезапуска.
4. Сборка debug-APK и проверка на телефоне.
