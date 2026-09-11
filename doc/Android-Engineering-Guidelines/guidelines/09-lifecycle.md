# 09 · Android Lifecycle

[← 08 XML & Resources](08-xml-resources.md) · [README](../README.md) · **Next:** [10 · Networking →](10-networking.md)

> Authority: 🟢 **Official** — [Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle), [Saved state & process death](https://developer.android.com/topic/libraries/architecture/saving-states). Severity 🔴 for the leak/collection rules.

---

## 1. Why lifecycle matters (plain words)

Android can **destroy and recreate** your UI at any moment — when you rotate the phone, change the language, or when the system needs memory (**process death**). If your code assumes the screen lives forever, you get two classic bugs:
- **Leaks** — you hold on to a screen that's already destroyed, so it can't be freed (memory grows, app slows/crashes).
- **Lost state** — the user's typed input vanishes because you kept it in the wrong place.

Lifecycle-awareness is simply writing code that survives these events.

---

## 2. The core lifecycles

```text
ACTIVITY:  onCreate → onStart → onResume → [visible & interactive]
                                        → onPause → onStop → onDestroy

FRAGMENT:  onAttach → onCreate → onCreateView → onViewCreated → onStart → onResume
              onPause → onStop → onDestroyView → onDestroy → onDetach

FRAGMENT VIEW lifecycle (separate!):  onCreateView … onDestroyView
    → inside a Fragment, use viewLifecycleOwner (NOT this) for UI observers

VIEWMODEL:  created once → survives rotation → onCleared() when the owner is truly gone
COMPOSE:    enter composition → recompositions → leave composition (DisposableEffect cleanup)
```

---

## 3. Configuration change vs process death — 🟢 Official · 🔴 Mandatory understanding

**In plain words:** these are two different "the screen got recreated" events, and they need different handling.

| Event                                                 | What survives                                         | What you must do                                                                        |
|-------------------------------------------------------|-------------------------------------------------------|-----------------------------------------------------------------------------------------|
| **Config change** (rotation, locale)                  | The `ViewModel` survives                              | Keep UI state in the ViewModel; don't store it in the Activity                          |
| **Process death** (system kills the backgrounded app) | The `ViewModel` is **gone**; only saved state returns | Save small critical input via `SavedStateHandle`; re-fetch the rest from the repository |

### ✅ Recommended — survives BOTH
```kotlin
class SearchViewModel(private val handle: SavedStateHandle) : ViewModel() {
    /** Survives rotation AND process death because it lives in SavedStateHandle. */
    val query: StateFlow<String> = handle.getStateFlow("query", "")

    fun setQuery(q: String) { handle["query"] = q }
}
```

**Why this matters.** Many "works on my phone" bugs are actually process death. Reproduce it with Developer Options → "Don't keep activities". `ViewModel` handles rotation; `SavedStateHandle` handles process death.

---

## 4. Common lifecycle bugs & the fix — 🔴 Mandatory

| Bug                          | Cause                                                                           | Fix                                                                                             |
|------------------------------|---------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------|
| Leaked Activity/View         | A long-lived object (singleton, static, coroutine) holds a `Context`/`View`     | Use `applicationContext` for long-lived needs; scope coroutines to the lifecycle                |
| Leaked Fragment view         | Observing with `this` instead of `viewLifecycleOwner`; not clearing the binding | Use `viewLifecycleOwner`; null out `ViewBinding` in `onDestroyView`                             |
| Flow collected in background | Collecting without `repeatOnLifecycle`                                          | Use `repeatOnLifecycle` / `collectAsStateWithLifecycle` ([05 §5](05-coroutines-concurrency.md)) |
| Duplicate observers          | Re-subscribing in `onResume` without removing                                   | Subscribe once in the right callback; use lifecycle-scoped collection                           |
| UI reference in ViewModel    | Storing `View`/`Activity` in the ViewModel                                      | Never; emit events instead ([06 §2](06-android-architecture.md))                                |

### ✅ Recommended — clear the Fragment binding
```kotlin
private var _binding: FragmentProfileBinding? = null
private val binding get() = _binding!!   // valid only between onCreateView and onDestroyView

override fun onDestroyView() {
    super.onDestroyView()
    _binding = null   // release the view so it can be garbage-collected
}
```

---

## 5. ViewModel lifecycle — 🟢 Official · 🔴 Mandatory

A `ViewModel` is created for a lifecycle owner (Activity/Fragment/nav graph) and cleared in `onCleared()` when that owner is permanently gone. `viewModelScope` is cancelled automatically; release any other resources in `onCleared()`.

---

**Next:** [10 · Networking →](10-networking.md)
#
#
#
#