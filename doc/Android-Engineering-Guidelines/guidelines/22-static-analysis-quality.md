# 22 · Static Analysis, Quality Gates & Deprecated APIs

[← 21 Git & Code Review](21-git-code-review.md) · [README](../README.md) · **Next:** [23 · Accessibility & i18n →](23-accessibility-i18n.md)

> Authority: 🟢 **Official** — [Android Lint](https://developer.android.com/studio/write/lint), [API deprecation]; 🟠 engineering. Severity 🔴 for CI gates.

---

# PART A — Static Analysis & Quality Gates

## A.1 The toolchain — 🟣 Team Standard · 🔴 Mandatory in CI

- **Android Lint** — Android-specific correctness/perf/security/i18n checks.
- **ktlint** — formatting (the Kotlin official style, [03 §1](03-kotlin.md)).
- **detekt** — code smells, complexity, and the size thresholds from [03 §11](03-kotlin.md).
- **Kotlin compiler warnings** — taken seriously.
- CI runs all of these and **fails the build** on violations.

## A.2 Fix warnings — don't hide them — 🟠 Strong Eng. · 🔴 Mandatory

> Fix the cause, not the warning.

### ❌ Avoid
Blanket `@Suppress` / `//noinspection` / disabling a lint rule just to make the build green.

**Why.** Suppressions pile up into blind spots; the next real bug hides among ignored warnings. A codebase that tolerates warnings drifts into a codebase nobody trusts.

## A.3 When suppression is legitimate — 🟡 Recommended

Suppress **narrowly** (one element, not a module), with a **comment justifying it** and ideally a ticket — e.g. a verified lint false positive. This mirrors the [Exception Process](26-exceptions-process.md).

## A.4 Also gate on
```text
[ ] Deprecated API usage flagged (Part B)
[ ] Unused resources / unused dependencies reported
[ ] Build produces no new warnings
[ ] Test suite green
```

---

# PART B — Deprecated APIs

## B.1 Don't introduce new usage of deprecated APIs — 🟠 Strong Eng. · 🔴 Mandatory

New code must not call deprecated APIs unless there's a **documented compatibility reason** (e.g. the replacement needs a higher API level than our `minSdk`).

**In plain words:** "deprecated" means the platform is telling you *this will go away — use the new thing.* Starting new code on the old thing is starting with debt.

## B.2 The migration steps — 🟡 Recommended

1. **Identify the replacement** (the deprecation note names it).
2. **Explain why** the replacement is preferred (behavior, safety, support).
3. **Add compatibility handling** where the replacement needs a higher API — prefer AndroidX compat helpers over hand-rolled `SDK_INT` branches.
4. **Avoid unnecessary version-specific duplication.**

### ✅ Recommended — compat over manual branching
```kotlin
// Prefer AndroidX compat helpers over raw Build.VERSION.SDK_INT branches
ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
```

**Documented exception example:** using a deprecated API guarded by `if (Build.VERSION.SDK_INT < X)` because the replacement only exists on API ≥ X while our `minSdk` is lower — with a comment and a removal plan.

---

**Next:** [23 · Accessibility & i18n →](23-accessibility-i18n.md)
#
#
#
#