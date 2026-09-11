# 05 · Coroutines & Concurrency

[← 04 Naming](04-naming-conventions.md) · [README](../README.md) · **Next:** [06 · Android Architecture →](06-android-architecture.md)

> Authority: 🟢 **Official** — [Kotlin coroutines](https://kotlinlang.org/docs/coroutines-overview.html) + [Android coroutines best practices](https://developer.android.com/kotlin/coroutines/coroutines-best-practices). Most rules here are 🔴 Mandatory because misuse causes leaks, ANRs, and crashes.

---

## 1. What a coroutine is (plain words) and the problem it solves

**In plain words.** A coroutine is a lightweight, cancellable piece of work that can **pause** (suspend) without freezing a thread, and resume later.

**The problem it solves.** Slow work — network calls, reading a database, file I/O — takes time. If you do it on the **main thread** (the thread that draws the screen), the UI freezes. If it freezes for ~5 seconds, Android shows an **ANR** ("Application Not Responding") dialog and can kill your app. Coroutines let you do slow work **off** the main thread and put the result back **on** the main thread, without freezing anything.

**Structured concurrency (plain words).** Every coroutine runs inside a **scope**. When the scope dies (e.g. the screen closes), all its coroutines are cancelled automatically. This is what stops leaks: work tied to a screen dies with the screen.

---

## 2. When to create a coroutine — and when NOT to — 🟠 Strong Eng. · 🔴 Mandatory

> **A coroutine is not "the async keyword." Create one only for real suspending/async work that must be tied to a lifecycle.**

```text
Do I need a coroutine here?
        │
        ├── Am I calling a suspend function or doing I/O off the main thread?
        │        └── No ──► Don't launch a coroutine. Just call the function.
        │
        ├── Is there an existing scope that owns this work's lifetime?
        │        └── Yes ──► launch in THAT scope (viewModelScope / lifecycleScope).
        │
        └── Do I need work that OUTLIVES the UI (survives process death)?
                 └── Yes ──► This is NOT a plain coroutine — use WorkManager
                             (see 19 · Background Work).
```

**❌ Avoid:** launching a coroutine just to call a normal (non-suspend) function, or creating a brand-new scope when `viewModelScope` already exists.

---

## 3. Choose the right scope — 🟢 Official · 🔴 Mandatory

**In plain words:** a "scope" decides *how long* your coroutine is allowed to live.

| Scope                                                 | Lives until…                           | Use for                                         |
|-------------------------------------------------------|----------------------------------------|-------------------------------------------------|
| `viewModelScope`                                      | ViewModel is cleared                   | Almost all screen-driven async work             |
| `lifecycleScope`                                      | The Activity/Fragment is destroyed     | UI-only work in Activity/Fragment               |
| `repeatOnLifecycle(STARTED)`                          | Runs while visible, pauses when hidden | **Collecting flows in the UI**                  |
| Custom `CoroutineScope(SupervisorJob() + dispatcher)` | You manage it                          | App-scoped singletons that must outlive screens |
| `GlobalScope`                                         | The whole process                      | **Never** (see §6)                              |

---

## 4. Dispatchers — which thread the work runs on — 🟢 Official · 🔴 Mandatory

**In plain words:** a "dispatcher" picks the thread pool your code runs on.

- `Dispatchers.Main` — UI updates only.
- `Dispatchers.IO` — network, disk, database (waiting-heavy work).
- `Dispatchers.Default` — CPU-heavy work (parsing, sorting big lists).

**The rule:** the person calling your function shouldn't have to worry about threads. Push the dispatcher choice **down** into the data layer with `withContext`.

### ✅ Recommended — main-safe function, injected dispatcher
```kotlin
class UserRemoteDataSource(
    private val api: UserApi,
    private val io: CoroutineDispatcher,   // injected, not hardcoded
) {
    /** Fetches the user off the main thread; safe to call from anywhere. */
    suspend fun fetch(id: String): UserDto = withContext(io) { api.getUser(id) }
}
```

**Why inject the dispatcher instead of hardcoding `Dispatchers.IO`?** Hardcoding makes tests slow and flaky. Injecting lets a test pass a fast `TestDispatcher` for deterministic results ([20 · Testing](20-testing.md)).

---

## 5. Collecting Flow in the UI safely — 🟢 Official · 🔴 Mandatory

**In plain words:** a `Flow` is a stream of values over time. If you keep listening to it while the screen is in the background, you waste battery and may crash trying to update a screen that isn't there.

### ✅ Recommended (Views)
```kotlin
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiState.collect { render(it) }   // only while the screen is visible
    }
}
```
### ✅ Recommended (Compose)
```kotlin
val state by viewModel.uiState.collectAsStateWithLifecycle()
```
### ❌ Avoid
```kotlin
lifecycleScope.launch { viewModel.uiState.collect { render(it) } }
// keeps collecting in the background → wasted work / crashes
```

**Why.** `repeatOnLifecycle` / `collectAsStateWithLifecycle` automatically stop collecting when the UI isn't visible and restart when it comes back.

---

## 6. StateFlow vs SharedFlow vs cold Flow — 🟢 Official · 🟡 Recommended

| Type         | Hot/Cold | Holds last value?                | Use for                                                                |
|--------------|----------|----------------------------------|------------------------------------------------------------------------|
| Cold `Flow`  | Cold     | No                               | A stream produced when you start collecting (DB query, network stream) |
| `StateFlow`  | Hot      | Yes (always has a current value) | **Screen UI state** — always renderable                                |
| `SharedFlow` | Hot      | Configurable                     | **One-time events** (navigate, show a snackbar)                        |

**Why not LiveData for new code?** (🟣 Team choice) We use **Flow/StateFlow**: it's Kotlin-first, testable without Android, and composes with coroutines. LiveData is still valid and lifecycle-aware out of the box (🟢 official) — keep it in legacy screens; don't mix both in one screen.

---

## 7. Cancellation, exceptions, GlobalScope — 🟢 Official · 🔴 Mandatory

- **Cooperative cancellation:** long loops must check `isActive` or call suspending functions (which are cancellation points). **Never swallow `CancellationException` — rethrow it** (see [13 · Error Handling](13-error-handling-logging.md)).
- **Exceptions:** in a normal scope, one child failing cancels its siblings. Use `SupervisorJob`/`supervisorScope` when children should fail independently. Use a `CoroutineExceptionHandler` only at a top-level scope.
- **`GlobalScope` is banned (🔴):** it's unscoped, so its work leaks past the screen/app and can't be cancelled.

### ❌ Avoid
```kotlin
GlobalScope.launch { syncEverything() }   // leaks, uncancellable, untestable
```

---

## 8. Shared mutable state, races, blocking — 🟠 Strong Eng. · 🔴 Mandatory

- Prefer **confined** mutable state inside one coroutine / one `StateFlow` over shared `var`s guarded by locks.
- If you must share, use `Mutex`/atomics — never assume ordering.
- **Never** call blocking APIs (`Thread.sleep`, blocking I/O, `runBlocking`) on `Dispatchers.Main`.

**Why.** Data races produce bugs that don't reproduce and can't be debugged. Keeping state inside one flow removes the whole problem.

---

**Next:** [06 · Android Architecture →](06-android-architecture.md)
#
#
#
#