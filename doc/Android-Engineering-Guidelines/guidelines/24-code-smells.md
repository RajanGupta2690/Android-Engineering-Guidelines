# 24 · Code Smells We Do Not Accept + Good vs Bad Examples

[← 23 Accessibility & i18n](23-accessibility-i18n.md) · [README](../README.md) · **Next:** [25 · Decision Tables →](25-decision-tables.md)

> Authority: 🟠 **Strong Engineering**. A "smell" is a warning sign, not always a defect — context decides. Each entry: the problem, a better approach, and the legitimate exception.

---

# PART A — Smells we don't accept

## A.1 God class / God ViewModel / huge Activity or Fragment
**Problem.** One class does everything → impossible to read, test, or change safely.
**Better.** Split by responsibility: extract UseCases, move data logic to repositories, split the screen. Follow the [step-by-step split in 03 §12](03-kotlin.md).
**Exception.** A genuinely cohesive class that's large because the domain is large — but verify it's *one* responsibility.

## A.2 Unnecessary Manager / Helper / Interface / UseCase
**Problem.** Abstractions with no reason ([01 §3.2](01-engineering-philosophy.md)): a `Helper` grab-bag, an interface with one impl and no test/seam need, a pass-through UseCase.
**Better.** Name the responsibility ([04 §7](04-naming-conventions.md)); use the concrete type; add the layer when it earns its place.
**Exception.** DI seams, module boundaries, real second implementations.

## A.3 Huge functions / deep nesting
**Problem.** A 200-line function or 5-level nesting hides logic and bugs.
**Better.** Early returns, extract named functions, `when` on sealed types ([03](03-kotlin.md)).
**Exception.** A flat, sequential setup function can be long without being complex.

## A.4 Global mutable state / excessive singletons
**Problem.** Races, leaks, cross-test bleed, hidden dependencies ([12](12-dependency-injection.md)).
**Better.** Scoped state (StateFlow); DI-scoped singletons for infrastructure only.

## A.5 Blocking main thread / blind `!!` / nested callbacks
**Problem.** Main-thread I/O → ANR ([18](18-performance.md)); `!!` → NPE ([03 §5](03-kotlin.md)); callback pyramids → unreadable async.
**Better.** Coroutines + right dispatcher; safe calls + Elvis; suspend/Flow over callbacks.

## A.6 Duplicated code / copy-paste architecture
**Problem.** Same logic in five places drifts and rots.
**Better.** Extract shared logic once — but only when it's genuinely the same concept. A little duplication beats the wrong abstraction; wait until the pattern is real.

## A.7 Magic numbers / magic strings / hardcoded UI values
**Problem.** `if (status == 3)`, `"premium"` scattered around, `16dp` inline.
**Better.** Named constants/enums; string & dimen resources ([08](08-xml-resources.md), [23](23-accessibility-i18n.md)).
**Exception.** Self-evident values (`0`, `1`, `-1` sentinels with obvious meaning).

## A.8 Giant XML files / excessive layout nesting
**Problem.** Slow inflation, jank, unreadable ([08](08-xml-resources.md)).
**Better.** Flat ConstraintLayout, reusable includes/styles, or Compose.

## A.9 Bundled banners/large images (should be remote)
**Problem.** Bundling banners/ads/large photos bloats every install ([08 · Images](08-xml-resources.md)).
**Better.** Icons → vector/SVG; banners/large/many/changing images → remote/API; minimize bundled raster.

## A.10 Ignored exceptions / broad R8 keep rules / debug-only fixes shipped to release
**Problem.** Empty `catch` hides failures ([13](13-error-handling-logging.md)); `-keep class ** {*;}` disables optimization ([15](15-r8-proguard.md)); a fix that only works in debug ships broken.
**Better.** Handle/log/rethrow; narrow, commented keep rules; verify on release builds ([17 Part C](17-gradle-build.md)).
**Exception.** None — these three are hard rules.

## A.11 Commented-out code / dead code / stray TODOs / debug logs
**Problem.** Rots, confuses, hides intent; debug logs may leak data.
**Better.** Delete it — Git remembers. TODOs need a ticket reference or they don't ship.

---

# PART B — Good vs Bad (realistic)

## B.1 ViewModel — safe, single state

### ❌ Bad
```kotlin
class ProfileViewModel(private val context: Context) : ViewModel() {   // holds Context → leak
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val user = MutableLiveData<User>()                                 // 3 sources → can desync
    fun load() {
        isLoading.value = true
        GlobalScope.launch {                                           // unscoped → leaks
            val u = api.getUser()!!                                    // blind !! + unknown thread
            user.postValue(u)
        }
    }
}
```

### ✅ Recommended
```kotlin
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    /** Loads the user and publishes a single consistent UI state. */
    fun load(id: String) {
        viewModelScope.launch {                                        // scoped, cancellable
            _uiState.value = when (val result = repository.getUser(id)) {
                is NetworkResult.Success -> ProfileUiState.Success(result.data)
                is NetworkResult.Failure -> ProfileUiState.Error(result.error)
            }
        }
    }
}
```
**Why better.** No `Context` leak, scoped coroutine, one consistent state object, no `!!`, errors modeled, dispatcher handled inside the repository.

## B.2 Null handling
```kotlin
val city = user!!.address!!.city!!            // ❌ Bad
val city = user?.address?.city ?: return      // ✅ Recommended
```

## B.3 Retrofit model + mapping
```kotlin
// ❌ Bad — DTO used directly in the UI
data class User(val n: String, val e: String)

// ✅ Recommended
data class UserDto(@SerialName("name") val name: String, @SerialName("email") val email: String)
data class User(val name: String, val email: String)          // domain
fun UserDto.toDomain() = User(name = name, email = email)
```

## B.4 Lifecycle-safe collection
```kotlin
// ❌ Bad — collects in background
lifecycleScope.launch { vm.uiState.collect { render(it) } }

// ✅ Recommended
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) { vm.uiState.collect { render(it) } }
}
```

## B.5 R8 keep rule
```proguard
# ❌ Bad
-keep class ** { *; }

# ✅ Recommended — narrow + justified
# Moshi reads these DTO field names via generated adapters.
-keep class com.company.app.data.dto.** { *; }
```

More runnable examples: [`../examples/good`](../examples/good) and [`../examples/bad`](../examples/bad).

---

**Next:** [25 · Decision Tables →](25-decision-tables.md)
#
#
#
#