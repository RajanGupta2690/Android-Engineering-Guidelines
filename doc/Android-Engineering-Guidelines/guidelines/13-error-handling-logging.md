# 13 · Error Handling & Logging

[← 12 Dependency Injection](12-dependency-injection.md) · [README](../../../README.md) · **Next:** [14 · Security →](14-security.md)

> Authority: 🟠 **Strong Engineering** + 🟢 official privacy/security for logging. Severity 🔴 for "no swallowed exceptions" and "no secrets/PII in logs".

---

# PART A — Error Handling

## A.1 Throw vs `Result` vs sealed error types — 🟠 Strong Eng. · 🟡 Recommended

**In plain words:** pick how you signal a failure based on *what kind* of failure it is.

| Situation                                                   | Approach                                                | Why                                                 |
|-------------------------------------------------------------|---------------------------------------------------------|-----------------------------------------------------|
| Programmer error / broken invariant                         | **Throw** (`require`, `check`, `IllegalStateException`) | It's a bug — fail fast                              |
| Expected, recoverable outcome (network fail, invalid input) | **Return a sealed result**                              | Forces the caller to handle it; visible in the type |
| Single fallible interop call                                | `runCatching` / `Result<T>`                             | Concise, but map to a domain result at the boundary |

### ✅ Recommended — expected failures modeled in the type
```kotlin
sealed interface LoginResult {
    data class Success(val user: User) : LoginResult
    data object InvalidCredentials : LoginResult
    data class Failure(val error: AppError) : LoginResult
}
```

**Why prefer sealed results for expected failures?** Exceptions are invisible in a function signature and easy to forget to catch. A sealed result makes every outcome part of the API the compiler enforces — you *can't* forget the error case.

## A.2 Never silently swallow exceptions — 🔴 Mandatory

### ❌ Avoid
```kotlin
try { risky() } catch (e: Exception) { }   // hides real failures; eats CancellationException
```

**Why this is dangerous.** An empty catch hides bugs and makes debugging impossible. In coroutines it's worse: swallowing `CancellationException` breaks structured concurrency ([05 §7](05-coroutines-concurrency.md)). If you catch, you must **handle, log, or rethrow** — and catch a *specific* exception.

### ✅ Recommended
```kotlin
try {
    api.submit(order)
} catch (e: CancellationException) {
    throw e                                  // never swallow cancellation
} catch (e: IOException) {
    Timber.w(e, "Order submit failed")
    return NetworkResult.Failure(AppError.NoConnection)
}
```

## A.3 Error mapping & user-facing messages — 🟠 Strong Eng. · 🔴 Mandatory

Map low-level errors → domain errors in the data layer; map domain errors → **localized, human** messages in the UI ([23 · i18n](23-accessibility-i18n.md)). Never show raw exception text or stack traces to users.

## A.4 When NOT to catch — 🟡 Recommended

Don't catch what you can't handle. Let genuine programmer errors crash in debug (they surface bugs); rely on the crash reporter in release ([17 · release engineering](17-gradle-build.md)). Catch only where you have a real recovery.

---

# PART B — Logging

## B.1 Use a logging abstraction — 🟠 Strong Eng. · 🟡 Recommended

Use **Timber** (or a wrapper) instead of raw `android.util.Log`. Plant a `DebugTree` in debug and a no-op / crash-reporting tree in release. This controls logging per build type in one place.

## B.2 Never log secrets or PII — 🟢 Official (privacy/security) · 🔴 Mandatory

**Never log:** passwords, tokens, API keys, auth headers, full request/response bodies, personal data (email, phone, address, location), or payment data.

### ❌ Avoid
```kotlin
Timber.d("Login response: $response")   // may contain a token / PII
```
### ✅ Recommended
```kotlin
Timber.d("Login result: success=%b", result is Success)   // no sensitive data
```

**Why.** Logcat can be read by other tooling, is captured in bug reports, and shows up in crash payloads. A logged token is a leaked credential — a security *and* privacy/regulatory issue.

## B.3 What's allowed per build type — 🟣 Team Standard · 🔴 Mandatory

| Build            | Logging policy                                                                         |
|------------------|----------------------------------------------------------------------------------------|
| **Debug**        | Verbose logs OK (still no real PII/secrets); full network logging against test data OK |
| **QA / Staging** | Info/warning; no secrets/PII; limited network logging                                  |
| **Release**      | No debug logs; errors → crash reporting only; **no secrets, no PII, no bodies**        |

R8 can strip debug log calls in release ([15 · R8](15-r8-proguard.md)) — but never rely on that as your only protection against logging secrets.

---

**Next:** [14 · Security →](14-security.md)
#
#
#
#