# 07 · UI Architecture (Compose & XML Views)

[← 06 Android Architecture](06-android-architecture.md) · [README](../README.md) · **Next:** [08 · XML, Resources & Images →](08-xml-resources.md)

> How we build screens: choosing Compose vs XML Views, holding state correctly, delivering one-time events, and handling every screen state.

---

## 1. Compose or XML Views? — 🟢 Official (Compose is Google's recommended modern toolkit) · 🟣 Team decision per project

**In plain words.** Google recommends **Jetpack Compose** (write UI in Kotlin) for new apps. But "recommended" doesn't mean "always better for every case." Here's how we decide:

| Situation                                   | Recommended                      | Why                                                | When the other wins                      |
|---------------------------------------------|----------------------------------|----------------------------------------------------|------------------------------------------|
| New screen in a Compose-capable app         | **Compose**                      | Less boilerplate, state-driven, Google's direction | —                                        |
| Adding to a large existing XML codebase     | **Match the surrounding screen** | Consistency beats mixing paradigms mid-flow        | Isolated new feature → Compose           |
| Highly dynamic, state-driven UI             | **Compose**                      | Recomposition fits reactive UI                     | —                                        |
| Heavy reliance on a mature View-only widget | **Depends**                      | Interop cost                                       | Widget has no Compose equivalent → Views |
| Wear OS / watch UI                          | **Compose for Wear OS**          | Google's current Wear direction                    | Legacy Wear code → Views                 |

**Team rule (🟣):** within one screen, **don't mix** paradigms. Use interop (`ComposeView` / `AndroidView`) only at deliberate boundaries.

---

## 2. State hoisting — 🟢 Official · 🔴 Mandatory (Compose)

**In plain words:** "hoisting" means a small UI piece doesn't own its own data — the data is passed **in** as a parameter, and changes are sent **out** as a callback. This makes the piece reusable and testable.

### ✅ Recommended — stateless, reusable
```kotlin
/** A pure email input: value comes in, changes go out. Owns no business state. */
@Composable
fun EmailField(email: String, onEmailChange: (String) -> Unit) {
    TextField(value = email, onValueChange = onEmailChange)
}
```
### ❌ Avoid
A leaf composable owning business state with its own `remember { mutableStateOf(...) }` that the ViewModel *also* needs → two sources of truth that drift apart.

**Why.** Stateless composables are reusable, previewable, and testable; a single source of truth (usually the ViewModel) prevents state desync.

---

## 3. One-time events — 🟠 Strong Eng. · 🔴 Mandatory

Render **state**; deliver **events once**. Never model a snackbar or navigation as a boolean in state that you must remember to reset — it re-fires on rotation. Use a `SharedFlow`/`Channel` for events.

**In plain words:** "navigate to next screen" should happen exactly once. If it's a boolean in the state, rotating the phone replays it and you navigate twice.

---

## 4. Lifecycle-aware collection & recomposition — 🟢 Official · 🔴 Mandatory

- Collect with `collectAsStateWithLifecycle()` (Compose) / `repeatOnLifecycle` (Views) — see [05 · Coroutines §5](05-coroutines-concurrency.md).
- Keep composables cheap: no heavy work during composition; use `remember`/`derivedStateOf` to avoid recomputing; give `LazyColumn` items stable `key`s. Details in [18 · Performance](18-performance.md).

**In plain words about recomposition:** Compose re-runs (`recomposes`) your UI functions whenever their inputs change. If a function does heavy work every time it runs, the UI stutters. Keep composables light and let `remember` cache expensive results.

---

## 5. Screen states: loading / success / error / empty — 🟣 Team Standard · 🔴 Mandatory

Every data-backed screen must handle **all four** states. Model them in the sealed UI state so the compiler forces you to render each.

```kotlin
sealed interface OrdersUiState {
    data object Loading : OrdersUiState
    data class Success(val orders: List<OrderUiModel>) : OrdersUiState
    data object Empty : OrdersUiState              // loaded, but nothing to show
    data class Error(val message: String) : OrdersUiState
}
```

**Why.** "Forgot the empty state" and "spinner forever on error" are the most common UX bugs. Making states exhaustive in the type system means the compiler won't let you forget one.

---

## 6. Navigation — 🟢 Official · 🟡 Recommended

Use the Jetpack **Navigation** component (Navigation Compose for Compose UIs). Pass **IDs/arguments**, not big objects; the destination re-fetches from its own ViewModel/repository.

**Why pass IDs, not objects?** Passing large parcelable objects between screens is fragile and wastes memory; passing an `orderId` and letting the next screen load it keeps a single source of truth.

---

**Next:** [08 · XML, Resources & Images →](08-xml-resources.md)
#
#
#
#