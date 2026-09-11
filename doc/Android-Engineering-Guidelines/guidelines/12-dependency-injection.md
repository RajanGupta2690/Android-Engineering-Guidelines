# 12 · Dependency Injection & Singleton Policy

[← 11 Database & Storage](11-database-storage.md) · [README](../../../README.md) · **Next:** [13 · Error Handling & Logging →](13-error-handling-logging.md)

> Authority: 🟢 **Official** — [Hilt / DI](https://developer.android.com/training/dependency-injection). Singleton policy is 🟠 Strong Engineering.

---

# PART A — Dependency Injection

## A.1 What DI is and the problem it solves (plain words)

**Dependency Injection** = an object is *given* its collaborators from outside instead of creating them itself.

**The problem without DI:** classes build their own dependencies (`val api = Retrofit...create()`). That hard-wires them together, makes swapping implementations impossible, and makes unit testing very hard — you can't give the class a fake network.

## A.2 Constructor injection is the baseline — 🟠 Strong Eng. · 🔴 Mandatory

Whatever framework you use, **prefer constructor injection**: list dependencies as constructor parameters.

### ✅ Recommended
```kotlin
class UserRepository(
    private val remote: UserRemoteDataSource,
    private val local: UserLocalDataSource,
)   // in a test, just pass fakes: UserRepository(FakeRemote(), FakeLocal())
```
### ❌ Avoid
Pulling dependencies out of a global singleton inside the class → hidden dependencies, untestable.

## A.3 Manual DI vs Hilt — 🟢 Official (Hilt recommended) · 🟣 Team decision by scale

**What Google recommends:** Hilt. **What we recommend:** start manual for small apps; adopt Hilt when the app grows.

|                   | Manual DI                                                        | Hilt                                                                                            |
|-------------------|------------------------------------------------------------------|-------------------------------------------------------------------------------------------------|
| **Advantages**    | Zero deps, no annotation processing, fully explicit, fast builds | Handles Android lifecycles (Activity/ViewModel/WorkManager), scoping, less boilerplate at scale |
| **Disadvantages** | Wiring grows tedious; you hand-manage scopes                     | Learning curve, build-time cost, feels "magic"                                                  |
| **When to use**   | Small app / few dependencies / single module                     | Multi-feature app, many ViewModels, needs `@HiltViewModel`, WorkManager injection, scoping      |

**Why / when Google's approach still wins:** for any non-trivial production app that will grow, adopt Hilt **early** — retrofitting a hand-rolled graph later is painful.

## A.4 Hilt concepts (concise) — 🟢 Official · 🟡 Recommended

- `@HiltAndroidApp` on `Application`; `@AndroidEntryPoint` on components; `@HiltViewModel` on ViewModels.
- **Module** tells Hilt how to provide a type: `@Provides` (types you build), `@Binds` (bind interface → impl).
- **Scopes** (`@Singleton`, `@ViewModelScoped`, …) control lifetime — match scope to lifecycle; over-scoping to `@Singleton` leaks memory.

## A.5 Don't over-inject — 🟠 Strong Eng. · 🟡 Recommended

Not everything needs injecting. Pure stateless helpers (an `object DateFormatter`) can be referenced directly. Inject things that have real dependencies, need swapping in tests, or have a managed lifecycle.

---

# PART B — Singleton Policy {#singleton-policy}

## B.1 What "Singleton" means and why global state is dangerous (plain words)

A **singleton** = one shared instance for the whole app. The danger isn't "one instance" — it's **global, long-lived, shared, often mutable state**:
- **Leaks:** a singleton holding a `Context`/`View` keeps it alive forever ([09 · Lifecycle](09-lifecycle.md)).
- **Testing:** global state bleeds between tests; you can't isolate.
- **Thread safety:** shared mutable singletons invite races ([05 §8](05-coroutines-concurrency.md)).
- **Hidden dependencies:** code reaching into a singleton hides what it truly needs.

## B.2 Policy — 🔴 Mandatory

- **Prefer DI-scoped singletons** (`@Singleton` provided by Hilt) over hand-rolled `object`/static instances, so the DI graph — not global state — owns lifetime and tests can replace them.
- **No global mutable state.** A singleton may hold **immutable config** or **thread-safe infrastructure**; it must not be a mutable grab-bag other code writes to.
- **Never store `Context`/UI references** in a process-lifetime singleton (use `applicationContext` only when needed).

## B.3 When a singleton is appropriate

| Component                              | ✅/❌ | When to use                                    | When NOT                   |
|----------------------------------------|-----|------------------------------------------------|----------------------------|
| OkHttp/Retrofit client                 | ✅   | Always one (expensive; shares connection pool) | —                          |
| Room database                          | ✅   | Always one app-wide                            | —                          |
| Repository                             | ✅   | Usually (DI `@Singleton`)                      | If it becomes a god object |
| App config / feature flags (immutable) | ✅   | Cheap global read                              | If it turns mutable        |
| In-memory cache                        | ✅   | With a thread-safe impl + eviction             | As an unbounded static map |
| "AppManager" holding mutable app state | ❌   | Almost never                                   | Use scoped state instead   |

**Rule:** never make a singleton just because "there should be only one." Make it because the instance is **expensive to build** or **must be shared** — and manage it through DI.

---

**Next:** [13 · Error Handling & Logging →](13-error-handling-logging.md)
#
#
#
#