# 03 · Kotlin Coding Standards & Language Usage

[← 02 Architecture](02-project-architecture.md) · [README](../../../README.md) · **Next:** [04 · Naming →](04-naming-conventions.md)

> Formatting + language idioms + the rules that keep code neat, clean, and small. Naming is big enough for its own file → [04 · Naming](04-naming-conventions.md).
>
> Authority: 🔵 **Kotlin Coding Conventions** (kotlinlang.org) + 🟢 **Android Kotlin style guide**, enforced by ktlint/detekt + Android Studio formatter (see [22 · Static Analysis](22-static-analysis-quality.md)).

---

## 1. Formatting baseline — 🔵 Kotlin · 🔴 Mandatory

- **Indentation:** 4 spaces, no tabs.
- **Line length:** target ~100–120 columns; wrap sensibly.
- **Braces:** K&R (opening brace on the same line).
- **One statement per line.** No `;` terminators.
- **Encoding:** UTF-8; files end with a newline.
- **Imports:** no wildcard imports; no unused imports.

**Why an auto-formatter, not human debate.** Formatting arguments are pure waste. We set the Kotlin official style once, enforce it in CI, and never argue brace placement in review again.

---

## 2. File organization — 🔵 Kotlin · 🟡 Recommended

- Single class/interface per file → name the file after it (`UserRepository.kt`).
- Closely related declarations may share a file with a meaningful name (`NetworkResult.kt` holding the sealed result + helpers).
- Order within a file: properties → init → constructors → public functions → private functions → companion object. Keep related things adjacent.

Avoid dumping unrelated top-level functions into a `Utils.kt` grab-bag (see [naming](04-naming-conventions.md#util-manager-helper-policy)).

---

## 3. Expression bodies and early returns — 🔵 Kotlin · 🟡 Recommended

### ✅ Recommended

```kotlin
fun fullName(user: User): String = "${user.first} ${user.last}"

fun discount(user: User): Int {
    if (!user.isMember) return 0        // early return keeps it flat
    if (user.isNew) return 5
    return 10
}
```

### ❌ Avoid

```kotlin
fun discount(user: User): Int {
    var result: Int
    if (user.isMember) {
        if (user.isNew) { result = 5 } else { result = 10 }
    } else { result = 0 }
    return result
}
```

**Why.** Early returns flatten nesting; expression bodies remove ceremony. Both reduce the state a reader must track. **Exception:** don't force an expression body onto genuinely multi-step logic.

---

## 4. `val` vs `var` — 🔵 Kotlin · 🟡 Recommended

**In plain words.** Both declare a variable. The difference:
- **`val`** = the value is **fixed** once set. You can't reassign it. (read-only)
- **`var`** = the value **can change** later. You can reassign it.

```kotlin
val name = "Rajan"
name = "Amit"   // ❌ compile error — you can't reassign a val

var age = 25
age = 26        // ✅ ok — a var can be reassigned
```

**The rule:** *when a value never changes, always use `val`, not `var`.* Writing `var` for something that never changes is what we avoid.

### ✅ Recommended — value never changes, so `val`
```kotlin
val userName = "Rajan"
println(userName)
```
### ❌ Avoid — `var` used for a value that never changes
```kotlin
var userName = "Rajan"   // nothing ever reassigns this → it should be a val
println(userName)
```

**Why prefer `val`.** Three concrete reasons:
1. **Catches accidental changes.** With `val`, if code later tries to overwrite the value by mistake, the compiler stops you immediately — the bug is caught at compile time, not in production.
2. **Easier to read.** Seeing `val` tells the reader "this won't change, don't track it." Seeing `var` forces them to remember "this might change somewhere below" — extra mental load.
3. **Safer with coroutines/threads.** An immutable (`val`) value can't be changed by two things at once, so it avoids a whole class of race-condition bugs ([05 · Coroutines](05-coroutines-concurrency.md)).

### ✅ Exception — use `var` when the value genuinely must change
```kotlin
// total is recalculated on every loop iteration → it MUST change → var is correct
var total = 0
for (item in cart.items) {
    total += item.price
}
```

**How to apply it in practice:** write `val` first, always. Only switch to `var` if you actually need to reassign the value (and the compiler complains). Don't reach for `var` by default.

---

## 5. Null safety, `!!`, safe calls, Elvis — 🟠 Strong Eng. · 🔴 Mandatory (no blind `!!`)

### ✅ Recommended
```kotlin
val length = name?.length ?: 0            // safe call + Elvis
val user = repository.find(id) ?: return  // Elvis + early return
```
### ❌ Avoid
```kotlin
val length = name!!.length                // crashes with NPE if null
```

**Why.** `!!` throws `NullPointerException` and throws away the compiler's null-safety guarantee. Every `!!` is an unverified claim "this is never null here." Prefer `requireNotNull(x) { "reason" }` — it documents the invariant and gives a message.

**Exception.** `!!`/`requireNotNull` is acceptable on a **proven invariant the type system can't express**, *with a comment stating the invariant* (example in [Exception Process](26-exceptions-process.md)).

---

## 6. Scope functions — 🔵 Kotlin · 🟡 Recommended

| Function | Receiver | Returns       | Typical use                               |
|----------|----------|---------------|-------------------------------------------|
| `let`    | `it`     | lambda result | null-safe transform: `x?.let { }`         |
| `run`    | `this`   | lambda result | compute a value using an object's members |
| `apply`  | `this`   | the object    | configure/build an object                 |
| `also`   | `it`     | the object    | side effects (logging, add to list)       |
| `with`   | `this`   | lambda result | group calls on a non-null object          |

### ✅ Recommended
```kotlin
val intent = Intent(context, DetailActivity::class.java).apply {
    putExtra(EXTRA_ID, id)
    putExtra(EXTRA_SOURCE, "list")
}
user?.let { renderProfile(it) }
```
### ❌ Avoid — nested scope-function soup
```kotlin
user?.let { u -> u.address?.let { a -> a.city?.let { c -> show(c) } } }
// prefer: user?.address?.city?.let(::show)
```

**Why.** Scope functions cut boilerplate, but nesting them destroys readability. Chain safe calls; use one scope function at a time.

---

## 7. Data / sealed / object / enum — 🔵 Kotlin · 🟡 Recommended

- **`data class`** for value-holding types (models, UI state, DTOs) — free `equals`/`hashCode`/`copy`.
- **`sealed interface`/`sealed class`** for closed hierarchies (UI state, results, events) so `when` is exhaustive.
- **`object`** for stateless singletons of behavior (a mapper, a formatter).
- **`enum`** for a fixed set of constants; **sealed** when variants carry different data.

### ✅ Recommended — exhaustive UI state
```kotlin
sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val user: User) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
```

**Why.** Sealed hierarchies make `when` exhaustive without `else`, so adding a state becomes a compile error everywhere it must be handled — the compiler becomes your checklist.

---

## 8. `lateinit` vs `lazy`, collections, `when`, params — 🔵 Kotlin · 🟡 Recommended

- `by lazy { }` — computed once on first access, thread-safe; for expensive read-only values.
- `lateinit var` — set later by framework/DI/lifecycle; only when init-before-use is guaranteed; never for primitives.
- Expose **read-only** `List`/`Map`/`Set`; keep `Mutable*` private.
- Prefer functional transforms (`map`/`filter`) when they read clearly; don't chain a dozen allocating operations on a hot path ([performance](18-performance.md)).
- Prefer exhaustive `when` on sealed/enum (no `else`); use **default + named parameters** over telescoping overloads.

```kotlin
fun showToast(message: String, long: Boolean = false, haptic: Boolean = false)
showToast(message = "Saved", haptic = true)   // named args → unambiguous
```

---

## 9. Comments & KDoc — 🟠 Strong Eng. · 🟡 Recommended

- Comment **why**, not **what**. The code already says what.
- KDoc on public APIs whose contract isn't obvious from the signature.
- **Delete commented-out code.** Git remembers it (see [24 · Code Smells](24-code-smells.md)).

---

## 10. Document every non-trivial function — 🟣 Team Standard · 🔴 Mandatory

Every function that isn't trivially self-explanatory carries a **one-line purpose comment** (KDoc `/** ... */`) directly above it, stating *what it is for*.

### ✅ Recommended
```kotlin
/** Loads the user for [id] and maps failures to a domain error. */
suspend fun getUser(id: String): NetworkResult<User> { ... }

/** Returns true when the cart can be checked out (has items and a valid address). */
fun canCheckout(cart: Cart): Boolean = cart.items.isNotEmpty() && cart.address != null
```
### ❌ Avoid
```kotlin
suspend fun getUser(id: String): NetworkResult<User> { ... }   // intent unclear at a glance

// increment i by one
i++                                                            // redundant "what" comment
```

**Why.** A one-line intent comment lets a reader/reviewer understand a function without decoding its body — the biggest time-saver in maintenance. This is a *purpose* comment (why it exists / what it guarantees), **not** noise that restates the code.

**Exception.** Truly self-evident functions (a trivial getter, an obvious `toString()`, a one-line lambda) don't need it. When in doubt, add the one line — it costs nothing.

---

## 11. Keep functions, classes, and files small — 🟣 Team Standard · 🔴 Mandatory

Small units read faster, test easier, and review cleaner. When a unit grows past the **soft threshold**, treat it as a **signal to split**.

| Unit                      | Soft threshold (review trigger) | Hard ceiling (split unless justified) |
|---------------------------|---------------------------------|---------------------------------------|
| **Function**              | ~40 lines                       | ~60 lines                             |
| **Class / file**          | ~300 lines                      | ~400 lines                            |
| **Function parameters**   | 4                               | 6 (use a param `data class` beyond)   |
| **Nesting depth**         | 3 levels                        | 4 levels (extract / early returns)    |
| **Cyclomatic complexity** | detekt default                  | detekt default                        |

> These are **team thresholds, not laws of physics** ([philosophy §3.2](01-engineering-philosophy.md)). They're configured in **detekt** ([22](22-static-analysis-quality.md)) so the trigger is automatic. Exceeding a number is a prompt to *look*, not an automatic failure — but exceeding a hard ceiling needs a documented exception ([26](26-exceptions-process.md)).

**One primary type per file (🔴).** A file holds **one** public class/interface/major type, named after it. Small tightly-coupled helpers (a sealed sub-hierarchy, a private helper) may share the file. Do **not** stack unrelated classes "upar niche" in one file.

---

## 12. What to do when a class crosses ~300 → 400 → 500+ lines — 🟣 Team Standard · 🔴 Mandatory

Line count is not the enemy — it's a **thermometer**. A high number tells you the class is probably doing **more than one job**. The fix is never "make lines shorter"; it's **separate the jobs**.

```text
Class size       What it usually means           What you MUST do
──────────────────────────────────────────────────────────────────────────────
≤ 300 lines      Healthy                         Nothing. Keep going.
300–400 lines    Warning: watch it               Review responsibilities now.
                                                  Extract if you see > 1 job.
400–500 lines    Over the hard ceiling           Split required unless a documented
                                                  exception (§26) is written.
> 500 lines      Almost always a god class        Stop. Refactor BEFORE adding more.
                 (see 24 · Code Smells)           Split into 2–4 focused types.
```

### The exact step-by-step refactor (follow in order)

1. **List the responsibilities.** Write down every distinct job (load, validate, format, cache, page, log). More than one → it must split.
2. **Group members by responsibility.** Which fields + functions belong to each job? Each cohesive group is a **candidate class**.
3. **Name each group by what it does** ([04 · naming](04-naming-conventions.md)) — `CheckoutValidator`, `PriceCalculator`, `CheckoutUiMapper`. Can't name it? The boundary is wrong; regroup.
4. **Extract each group into its own file/type** (one primary type per file). Use the technique table below.
5. **Wire them back via constructor injection** ([12 · DI](12-dependency-injection.md)) so the original class *coordinates* instead of *implementing*.
6. **Add the one-line purpose comment** on each new class and moved function (§10).
7. **Move the tests with the code** — each extracted class gets its own focused test ([20 · testing](20-testing.md)).

### Which technique to use

| You see...                                            | Technique                            | Plain words                                                                     |
|-------------------------------------------------------|--------------------------------------|---------------------------------------------------------------------------------|
| Related fields + functions doing one sub-job          | **Extract Class**                    | Cut them into a new class; old class holds an instance                          |
| A long function with clear stages                     | **Extract Function**                 | Break into small named functions (§11 function limit)                           |
| Repeated logic across classes                         | **Extract + share** (UseCase/helper) | Pull common logic into one place                                                |
| Optional/variant behavior via `if (type == …)` chains | **Strategy** (interface + impls)     | One interface, one class per variant — only if a real second impl exists (§3.2) |
| Building a complex object step by step                | **Builder / factory**                | Move construction out of the class                                              |

### Worked example — a 500+ line god ViewModel → focused pieces

**❌ Bad (one class doing five jobs, ~520 lines):**
```kotlin
class CheckoutViewModel(app: Application) : AndroidViewModel(app) {
    private var cart: Cart? = null
    fun loadCart() { /* ~90 lines: retrofit call, error handling, caching */ }      // job 1
    fun validate(): Boolean { /* ~110 lines of if/else rules */ }                   // job 2
    fun calculateTotal(): Long { /* ~70 lines of pricing math */ }                  // job 3
    fun toUiText(): CheckoutUiModel { /* ~90 lines of formatting */ }               // job 4
    fun logStep(step: String) { /* ~40 lines */ }                                   // job 5
    // ...plus state and helpers → ~520 lines
}
```
Nobody can safely touch pricing without risking the network code; tests can't isolate anything; two people editing it always conflict.

**✅ Recommended (split by responsibility, each in its own file):**
```kotlin
// File: CheckoutValidator.kt
/** Validates address, coupon, and payment for checkout. Pure rules, no Android. */
class CheckoutValidator {
    /** Returns the first validation error, or null when the cart is valid. */
    fun validate(cart: Cart): CheckoutError? { ... }
}

// File: PriceCalculator.kt
/** Computes subtotal, tax, and discount for a cart. */
class PriceCalculator {
    /** Returns the final payable amount in minor units. */
    fun total(cart: Cart): Long { ... }
}

// File: CheckoutUiMapper.kt
/** Maps a domain [Cart] + totals into the UI-ready [CheckoutUiModel]. */
class CheckoutUiMapper {
    fun toUiModel(cart: Cart, total: Long): CheckoutUiModel { ... }
}

// File: CheckoutViewModel.kt  → now ~80 lines: it only COORDINATES.
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val repository: CartRepository,     // job 1 → data layer
    private val validator: CheckoutValidator,   // job 2
    private val calculator: PriceCalculator,    // job 3
    private val uiMapper: CheckoutUiMapper,      // job 4
    private val analytics: CheckoutAnalytics,    // job 5
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Loading)
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    /** Loads the cart and publishes a ready-to-render UI state. */
    fun load() {
        viewModelScope.launch {
            when (val result = repository.getCart()) {
                is NetworkResult.Success -> {
                    val cart = result.data
                    _uiState.value = CheckoutUiState.Ready(uiMapper.toUiModel(cart, calculator.total(cart)))
                }
                is NetworkResult.Failure -> _uiState.value = CheckoutUiState.Error(result.error)
            }
        }
    }
}
```
**Result:** five ~80–110 line focused classes instead of one 520-line class. Each has one job, one clear name, its own test file, and can change without touching the others.

### Answering the common pushback (so no one has to argue)

| "But…"                                  | Answer (team policy)                                                                                                                                                                                                        |
|-----------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| *"Where is it written? Not necessary."* | **It is written — here, §11–§12.** 🔴 Mandatory, enforced by detekt. "I feel it's fine" isn't an exception; [§26](26-exceptions-process.md) defines the only valid way to deviate.                                          |
| *"Splitting creates too many files."*   | Many small single-job files are **easier** to navigate than one file you must scroll and mentally filter. The IDE finds files instantly; it can't un-tangle a god class for you.                                            |
| *"It works, why refactor?"*             | "Works today" ≠ "safe to change tomorrow." A god class works until two people edit it or a bug hides in it. We optimize for change.                                                                                         |
| *"It's long but all related."*          | Then it's the documented exception (§11 + [§26](26-exceptions-process.md)): one large responsibility. Write the one-line reason in the PR. Can't state it in one sentence without "and"? Not one responsibility — split it. |
| *"I'll clean it later."*                | Later never comes; the class only grows. Split **before** it crosses 500 lines, or **before** the next feature — whichever is first.                                                                                        |
| *"Extraction slows me down now."*       | A little now, or a lot for everyone later. The whole team pays interest on a god class every read, test, and merge.                                                                                                         |

**The one-sentence test (use in review):** *"Can I describe this class's job in one sentence without the word 'and'?"* If you need "and", it has more than one responsibility — split it.

---

## 13. Neat & clean baseline — 🟣 Team Standard · 🔴 Mandatory

- **No commented-out code** in commits — delete it; Git is the history.
- **No dead code**, unused imports, unused parameters, or unreachable branches.
- **No stray debug logs / `println`** ([13 · logging](13-error-handling-logging.md)); no `TODO` without a ticket reference.
- **Consistent formatting** — enforced by ktlint, never hand-argued (§1).
- **Meaningful names** everywhere; one responsibility per unit; proper spacing and grouping.

**Why.** "Clean" isn't cosmetic — commented-out blocks, dead code, and mystery functions actively mislead the next reader and hide real bugs.

---

## 14. Language convention vs team preference

Items marked 🔵 are Kotlin's own conventions (authoritative). Items like "enum entries in UPPER_SNAKE_CASE" ([04](04-naming-conventions.md)) or "prefer `requireNotNull` over `!!`" (§5), and all §10–§13 size/clean rules, are **team preferences** layered on valid Kotlin — we picked one.

---

**Next:** [04 · Naming Conventions →](04-naming-conventions.md)
#
#
#
#