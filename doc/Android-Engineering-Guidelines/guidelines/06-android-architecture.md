# 06 · Android Architecture (Layer Responsibilities)

[← 05 Coroutines](05-coroutines-concurrency.md) · [README](../README.md) · **Next:** [07 · UI & Compose →](07-ui-compose.md)

> The big-picture layering is in [02 · Project Architecture](02-project-architecture.md). This file says exactly **what each layer does, what it must NOT do, and how data moves between them.**
>
> Authority: 🟢 **Official** — [Guide to app architecture](https://developer.android.com/topic/architecture), applied as 🔴 Mandatory.

---

## 1. UI state and UI events — 🟢 Official · 🔴 Mandatory

**In plain words:**
- **UI state** = one snapshot of everything the screen needs to draw right now (is it loading? what's the data? is there an error?).
- **UI event** = a one-time signal ("navigate to home", "show this snackbar") that should happen once and not repeat when the screen rotates.

### ✅ Recommended
```kotlin
/** Everything the login screen needs to render, in one immutable snapshot. */
data class LoginUiState(
    val email: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null,
)

/** One-time signals the login screen reacts to. */
sealed interface LoginUiEvent {
    data object NavigateHome : LoginUiEvent
    data class ShowMessage(val text: String) : LoginUiEvent
}
```

**Why one state object instead of separate `isLoading` / `error` / `data` fields?** Separate fields drift out of sync and create impossible combinations (loading **and** error at the same time). One state object means every render is a valid, consistent snapshot.

---

## 2. ViewModel responsibilities — 🟢 Official · 🔴 Mandatory

**A ViewModel DOES:** expose `StateFlow` UI state, receive user actions as function calls, call the domain/data layer in `viewModelScope`, turn domain models into UI models, and survive rotation.

**A ViewModel must NOT (🔴):**
- Hold a `Context`, `Activity`, `Fragment`, or `View` — that **leaks** them ([09 · Lifecycle](09-lifecycle.md)).
- **Navigate** itself — it *emits an event*; the UI performs navigation.
- Do network/DB calls directly — that's the data layer's job.

### ❌ Avoid
```kotlin
class ProfileViewModel(private val activity: Activity) : ViewModel()   // leaks the Activity
```

**Why no `Context`/`View`?** A ViewModel outlives the screen during rotation. If it holds the old `Activity`, that `Activity` can't be garbage-collected → memory leak, and eventually a crash.

---

## 3. Repository responsibilities — 🟢 Official · 🔴 Mandatory

The repository is the **single source of truth** for one type of data. It decides local vs remote, applies caching, and exposes **domain models** (never DTOs or entities).

### ✅ Recommended
```kotlin
class UserRepository(
    private val remote: UserRemoteDataSource,
    private val local: UserLocalDataSource,
) {
    /** Observes the user from the local DB — the single source of truth. */
    fun observeUser(id: String): Flow<User> = local.observe(id)

    /** Fetches the latest user from the network and updates local storage. */
    suspend fun refresh(id: String) {
        val dto = remote.fetch(id)     // network model (DTO)
        local.upsert(dto.toEntity())   // persist as a DB entity
    }
}
```

**In plain words:** the rest of the app asks the repository for data and doesn't care whether it came from the network or the database. The repository hides that decision.

---

## 4. Data source responsibilities — 🟢 Official · 🟡 Recommended

Each data source wraps **one** origin (one API, or one database) and maps to domain models. It does **not** coordinate across sources — that's the repository's job.

---

## 5. Mapping between layers — 🟠 Strong Eng. · 🔴 Mandatory (no DTO/entity leaks upward) {#mapping-between-layers}

Keep three model families and convert between them explicitly:

```text
DTO (network)  ──toDomain()──►  Domain model  ──toUiModel()──►  UI model
Entity (Room)  ──toDomain()──►  Domain model
```

**In plain words — three shapes of the same data, for three different jobs:**
- **DTO** = exactly how the server sends it (field names, nesting you don't control).
- **Domain model** = the clean, app-friendly version your logic works with.
- **UI model** = shaped for the screen (formatted strings, flags like `showBadge`).

### ✅ Recommended
```kotlin
data class UserDto(@SerialName("name") val name: String, @SerialName("email_id") val email: String)
data class User(val name: String, val email: String)   // domain

/** Converts the network DTO into the clean domain model. */
fun UserDto.toDomain() = User(name = name, email = email)
```

**Why separate them?** The server format, the database schema, and what the screen needs all change for different reasons. If they're the same class, a backend field rename breaks your database and your UI at once. Mapping is cheap insurance.

**Exception.** For a tiny app/prototype with a stable 1:1 shape, one model across layers is a *documented, deliberate* simplification — expect to split it the moment any layer diverges.

---

## 6. What does NOT belong where (quick reference) — 🔴 Mandatory

| Component           | Must NOT contain                                                |
|---------------------|-----------------------------------------------------------------|
| Activity / Fragment | Business logic, network/DB calls, data mapping                  |
| Composable          | Business logic, direct data access, long-lived state (hoist it) |
| ViewModel           | `Context`/`View`, navigation execution, HTTP/DB calls           |
| Repository          | UI/lifecycle knowledge, UI `Context`                            |
| DAO                 | Business rules (only queries)                                   |
| API service         | Mapping/business logic (only endpoints)                         |

---

**Next:** [07 · UI & Compose →](07-ui-compose.md)
#
#
#
#