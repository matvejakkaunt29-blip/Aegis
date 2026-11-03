# Muse Mobile — фазы 1-3 (0.3.0-phase2-3)

Автономный Android APK, без зависимости от ПК пользователя.
Muse — встроенный провайдер по умолчанию, вход — в Muse-аккаунт внутри приложения,
поддерживается несколько аккаунтов одновременно с переключением в drawer.

## Что уже есть (фазы 1-3, по разбору Kimi 3.1.6 через REA)

### Фаза 2: артефакты и агентные шаги
- Артефакты (`Artifact`): HTML, таблица, слайды, диаграмма — карточка в чате и отдельный просмотр в WebView (`ArtifactScreen`)
- Агентные шаги (`AgentStep`): думает, поиск, память — показываются над полем ввода, пока идёт ответ
- История чатов (`ChatViewModel.history`): новый чат сохраняет старый, из drawer его можно открыть заново
- Артефакт генерируется по запросу: «таблица», «слайды», «диаграмма», «артефакт»/«html»

### Фаза 3: проекты, плагины, задачи
- Проекты (`ProjectsScreen`): создание и список, открытие чата из проекта
- Плагины (`PluginsScreen`): установка только после проверки манифеста в `PluginLoader` (без системных android.permission.*), включение/выключение
- Запланированные задачи (`TasksScreen`): название + время, включение/выключение, удаление
- Хранение фаз 2-3 в памяти приложения; Room-персистентность и WorkManager-планирование — следующий шаг после проверки UI

### Фаза 1: база
- `android/` — Kotlin + Jetpack Compose: Gradle, Manifest
- Главный экран (`HomeScreen`): орб, пилюля модели, чипы быстрых действий, пилюля ввода
- Боковой drawer (`AppScaffold`): аватар, новый чат, проекты, история, аккаунты, настройки
- Экран чата (`ChatScreen`): ответы ассистента без пузырей, свои сообщения в карточке
- Настройки (`SettingsScreen`): группы карточками — аккаунт, появление (тёмная/светлая/системная), голос, подписка, персонализация с памятью, помощь, выход
- Экран входа (`LoginScreen`): пароль только на странице Muse, токены только в EncryptedSharedPreferences + Keystore
- Тёмная тема #111111 / карточки #1E1E1E, размеры в dp — Compose сам подгоняет под телефон при запуске
- `AuthManager` — мультиаккаунт
- `MuseProvider` — единый Provider API, токен берётся из активного аккаунта
- `PluginLoader` / `ThemeEngine` — заглушки проверки манифеста, прав и темы из чата
- `docs/ARCHITECTURE.md`, `docs/AUTH.md` — архитектура и вход

Примечание: `docs/design-mockup.html` в локальной копии — старый HTML-макет v6,
он в этот пуш не входит и заменён реальными Compose-экранами фазы 1.

## Сборка APK

### На GitHub (работает уже сейчас)
В репозитории есть `.github/workflows/android.yml`: при пуше в ветку
`muse-mobile` GitHub Actions собирает debug APK и кладёт его в
Actions → Artifacts под именем `muse-mobile-debug-apk`.
Там же гоняются unit-тесты (`:app:testDebugUnitTest`).
Это и есть «тестить APK прямо на GitHub»: ничего локально ставить не надо,
скачиваешь готовый APK из артефакта workflow.

### Локально (SDK уже установлен на этой VM)
- Android SDK: `~/workspace/tools/android-sdk/` — cmdline-tools, platform
  34 (ext12), build-tools 34.0.0, platform-tools
- Gradle 8.7: `~/workspace/tools/gradle-8.7/`, JDK 17: `~/workspace/tools/jdk-17.0.21+9/`
- Сборка: `source ~/workspace/tools/android-sdk/env.sh && cd android && gradle :app:assembleDebug`
- APK после сборки: `android/app/build/outputs/apk/debug/app-debug.apk`
- Важно: локальная сборка упирается в разрешение Muse на прямые
  TCP-соединения для Java (Gradle-демон общается по localhost-сокету).
  Пока в настройках Muse → Permissions → Direct network protocols
  `other_tcp` стоит Deny, локальный Gradle падает с
  «Could not receive a message from the daemon». Как только Ethan
  переключит `other_tcp` в Ask и подтвердит, локальная сборка заработает —
  SDK, Gradle и JDK для неё уже на месте.

## Чего ещё нет (честно)
- Реальный HTTP-запрос к API Muse — в `MuseProvider` заглушка, нужен официальный endpoint.
- Локальный APK пока не собран по причине выше (разрешение other_tcp),
  но GitHub Actions собирает его при каждом пуше.

## Следующие шаги
1. Подключить реальный endpoint Muse и вход через браузер (шаги и артефакты тогда придут из стрима, а не из симуляции).
2. Room-персистентность проектов/задач/истории и WorkManager для задач.
3. Сборка debug-APK и проверка на телефоне.
