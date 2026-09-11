# 04 · Naming Conventions

[← 03 Kotlin](03-kotlin.md) · [README](../README.md) · **Next:** [05 · Coroutines →](05-coroutines-concurrency.md)

> Authority: 🔵 **Kotlin Coding Conventions** + 🟢 **Android Kotlin style guide**, applied as 🔴 **Mandatory** team standard. Case rules are the Kotlin official conventions; the component tables are our team standard.

Good names are the cheapest documentation you'll ever write. A reader who understands a name doesn't need to open the file.

---

## 1. Case rules at a glance — 🔵 Kotlin · 🔴 Mandatory

| Element                                        | Case                                                   | Example                                  |
|------------------------------------------------|--------------------------------------------------------|------------------------------------------|
| Package                                        | all-lowercase, no underscores                          | `com.company.app.feature.login`          |
| Class / interface / object / enum / annotation | UpperCamelCase                                         | `UserRepository`, `PaymentState`         |
| Function / property / local / parameter        | lowerCamelCase                                         | `loadUser()`, `userName`                 |
| `const val` / compile-time constant            | UPPER_SNAKE_CASE                                       | `MAX_RETRIES`, `BASE_URL`                |
| Non-const `val` singleton                      | lowerCamelCase                                         | `val logger = ...`                       |
| Enum entries                                   | UPPER_SNAKE_CASE **or** UpperCamelCase (be consistent) | `ACTIVE` / `Active`                      |
| Type parameters                                | single upper letter or UpperCamelCase                  | `T`, `KeyT`, `Item`                      |
| Test functions                                 | backticked sentences allowed                           | `` `returns error when token expired` `` |

🔵 Kotlin allows enum entries in either case. 🟣 **Team choice:** we use **UPPER_SNAKE_CASE** for grep-ability. Pick one per project; never mix.

---

## 2. Acronyms & abbreviations — 🔵 Kotlin · 🟡 Recommended

Treat acronyms as words in camel case.

- ✅ `jsonParser`, `httpClient`, `UrlBuilder`, `ApiClient`, `parseXml()`, `userId`
- ❌ `jSONParser`, `HTTPClient`, `URLBuilder`, `parseXML()`, `userID`

**Why.** Consistent acronym casing keeps IDE completion, generated names, and grep predictable. `userId` composes cleanly (`userIdList`); `userID` doesn't.

---

## 3. Meaningful, boolean, collection names — 🟠 Strong Eng. · 🟡 Recommended

- **Booleans read as yes/no questions:** `isLoading`, `hasError`, `canRetry`, `shouldRefresh`. Avoid negatives like `isNotReady`.
- **Collections are plural:** `users`, `orderIds`. Singular for one: `user`.
- **No type-encoding / Hungarian:** `strName`, `iCount`, `mUser`, `lstUsers` are forbidden.
- **No meaningless names:** `data`, `info`, `temp`, `obj`, `value2`.

---

## 4. Android component & architecture naming — 🟣 Team Standard · 🔴 Mandatory

| Element           | ✅ Correct                                        | ❌ Incorrect                   | Rule                               | Why                                   |
|-------------------|--------------------------------------------------|-------------------------------|------------------------------------|---------------------------------------|
| Activity          | `LoginActivity`                                  | `Login`, `ActivityLogin`      | `<Feature>Activity`                | Identifies component type at a glance |
| Fragment          | `ProfileFragment`                                | `ProfileFrag`                 | `<Feature>Fragment`                | Consistency, discoverability          |
| ViewModel         | `CheckoutViewModel`                              | `CheckoutVM`                  | `<Feature>ViewModel`               | Matches Jetpack convention            |
| Composable screen | `CheckoutScreen`                                 | `CheckoutView`                | `<Feature>Screen` (noun)           | Composables are UI nouns              |
| Repository        | `OrderRepository`                                | `OrderRepo`, `OrderManager`   | `<Domain>Repository`               | Names the data-layer role             |
| UseCase           | `PlaceOrderUseCase`                              | `OrderInteractor`             | `<Verb><Noun>UseCase`              | Reads as an action                    |
| Data source       | `OrderRemoteDataSource` / `OrderLocalDataSource` | `OrderApiHelper`              | `<Domain><Remote/Local>DataSource` | Names the origin                      |
| Retrofit service  | `OrderApi`                                       | `OrderManager`                | `<Domain>Api`                      | It is the API surface                 |
| Room entity       | `OrderEntity`                                    | `Order` (clashes with domain) | `<Name>Entity`                     | Distinguishes storage model           |
| Room DAO          | `OrderDao`                                       | `OrderDatabaseHelper`         | `<Name>Dao`                        | Standard Room convention              |
| Room database     | `AppDatabase`                                    | `DBHelper`                    | `<Scope>Database`                  | Clear scope                           |
| DTO (network)     | `OrderDto`                                       | `OrderResponse2`              | `<Name>Dto`                        | Distinguishes wire model              |
| Domain model      | `Order`                                          | `OrderModel`, `OrderBean`     | plain noun                         | Domain is the "real" type             |
| UI model          | `OrderUiModel`                                   | `OrderView`                   | `<Name>UiModel`                    | Presentation-shaped data              |
| Mapper            | `OrderDtoMapper` / `toDomain()` ext              | `OrderConverterHelper`        | `<Name>Mapper` or `toX()`          | States direction                      |
| Worker            | `SyncOrdersWorker`                               | `SyncJob`                     | `<Task>Worker`                     | WorkManager convention                |
| Service           | `PlaybackService`                                | `MyService`                   | `<Purpose>Service`                 | Purpose-first                         |
| BroadcastReceiver | `BootReceiver`                                   | `Receiver1`                   | `<Event>Receiver`                  | Names the trigger                     |
| UI state          | `LoginUiState`                                   | `LoginData`                   | `<Feature>UiState`                 | Explicit state holder                 |
| UI event          | `LoginUiEvent`                                   | `LoginActions`                | `<Feature>UiEvent`                 | Explicit event type                   |
| Exception         | `PaymentDeclinedException`                       | `MyError`                     | `<Cause>Exception`                 | Matches JVM convention                |
| Result wrapper    | `NetworkResult`                                  | `Resp`                        | `<Domain>Result`                   | Clear intent                          |

---

## 5. Flow / state / event property naming — 🟠 Strong Eng. · 🟡 Recommended

```kotlin
private val _uiState = MutableStateFlow(LoginUiState())
val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()   // public read-only

val events: SharedFlow<LoginUiEvent>                            // one-time events
```

**Why the underscore.** It's the widely-used Android/Kotlin convention distinguishing the private mutable backing field from the exposed immutable one, preventing accidental external mutation.

---

## 6. Composable function naming — 🟢 Official (Compose API guidelines) · 🔴 Mandatory

- Composables that **emit UI** → **PascalCase nouns**: `UserAvatar()`, `CheckoutScreen()`.
- Composables that **return a value** → normal lowerCamelCase: `rememberScrollState()`.

**Why.** This is Google's documented Compose API guideline; tooling, lint, and readers rely on it.

---

## 7. `Util`, `Manager`, `Helper`, `Handler`, `Common`, `Data` — not banned, but suspect {#util-manager-helper-policy}

🟠 **Strong Eng.** · 🟡 **Recommended**

These names are **not forbidden**, but they're **design smells** more often than not.

**Why suspect.** `Manager`/`Helper`/`Util` describe *nothing* about behavior, so they become magnets for unrelated code → god classes ([24](24-code-smells.md)). They usually mean the author hadn't found the real responsibility yet.

**Name the responsibility instead:**

| Tempting name    | Likely real intent    | Better name                               |
|------------------|-----------------------|-------------------------------------------|
| `UserManager`    | holds/loads user data | `UserRepository` / `SessionStore`         |
| `NetworkHelper`  | builds HTTP calls     | `ApiClient` / a specific `XApi`           |
| `DateUtils`      | formats dates         | `DateFormatter`                           |
| `Common` package | "stuff"               | split into `core:network`, `core:ui`, ... |
| `DataManager`    | caches + persists     | `XRepository` + `XLocalDataSource`        |

**When such a name is genuinely valid.**
- A cohesive, stateless collection of pure functions on one type — `StringExtensions.kt` full of extension functions is fine.
- Framework-mandated names (`AlarmManager`, `WorkManager`) — those are platform types, not yours.

**Exception.** A small, single-purpose `object DateFormatter` with two functions is fine. A `Utils` class with 40 unrelated methods is not.

---

**Next:** [05 · Coroutines & Concurrency →](05-coroutines-concurrency.md)
#
#
#
#