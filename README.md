# Android Engineering — Coding & Development Guidelines

> Our team's practical Android engineering rulebook.
> **Production-grade engineering, without unnecessary over-engineering.**

This is the **modular** version of our Android guidelines. Each topic lives in its own file under [`guidelines/`](doc/Android-Engineering-Guidelines/guidelines) so it's easy to read, link to in a PR, and update in isolation. Real, compilable examples live under [`examples/`](doc/Android-Engineering-Guidelines/examples).

New here? Read [`guidelines/01-engineering-philosophy.md`](doc/Android-Engineering-Guidelines/guidelines/01-engineering-philosophy.md) first, then jump to whatever you're building.

---

## Who this is for

Junior, mid, senior, and lead Android developers on the team. It answers one question directly:

> *"If a developer joins our Android project today, what exact coding, architecture, UI, resource, performance, security, build, testing, and release rules must they follow?"*

You should **not** have to ask "how do I name this file / should I use `val` or `var` / do I need a UseCase / PNG or vector / where does business logic live / what do I do when a class hits 500 lines". Those are all answered inline, at the point the rule appears.

---

## How to read every rule

Each meaningful rule carries two independent labels: an **Authority** label (who says so) and a **Severity** label (how strictly we enforce it).

### Authority labels

| Label                                    | Meaning                                                                  |
|------------------------------------------|--------------------------------------------------------------------------|
| 🟢 **Official Android / Google**         | A documented platform, Google, or Play requirement/recommendation.       |
| 🔵 **Kotlin Standard**                   | An official Kotlin coding convention (kotlinlang.org).                   |
| 🟠 **Strong Engineering Recommendation** | Backed by good production practice; not an official Android requirement. |
| 🟣 **Team Engineering Standard**         | Our own project rule. Never presented as an official Google rule.        |

### Severity labels

| Label              | Meaning                                                                                                                     |
|--------------------|-----------------------------------------------------------------------------------------------------------------------------|
| 🔴 **Mandatory**   | Must be followed unless an approved exception is documented (see [Exception Process](doc/Android-Engineering-Guidelines/guidelines/26-exceptions-process.md)). |
| 🟡 **Recommended** | Strong recommendation, but contextual.                                                                                      |

**Authority ≠ Severity.** An official recommendation can be merely recommended for us; a team standard can be mandatory. Both labels appear together.

### The rule format

Most rules use this shape so the *why* always sits next to the *what*:

> **Recommended → Avoid → Why → Example → Exception**

---

## Currency (verified against official sources — September 2026)

These platform facts change on Google's schedule. **Re-verify before each release** (see [CONTRIBUTING.md](doc/Android-Engineering-Guidelines/CONTRIBUTING.md)).

- **Target API:** New Google Play apps and updates must target **Android 16 (API 36)** or higher. (Wear OS / Automotive OS → API 35; Android TV / XR → API 34.)
- **16 KB page size:** Apps targeting **Android 15 (API 35)+** must support 16 KB memory pages on 64-bit devices. From **February 1, 2027**, non-compliant updates can't be released on Google Play. → [`16-16kb-page-size.md`](doc/Android-Engineering-Guidelines/guidelines/16-16kb-page-size.md)
- **Android Gradle Plugin:** 9.x line (9.4, Sep 2026). AGP 9.0 was a major release.
- **Kotlin:** 2.4.x stable, K2 compiler is default.
- **R8** is the default shrinker/optimizer; standalone ProGuard is legacy.

Full sources: [`27-golden-rules-checklists.md` → References](doc/Android-Engineering-Guidelines/guidelines/27-golden-rules-checklists.md#references).

---

## Table of contents

### `guidelines/`

| #  | File                                                                                                   | Covers                                                                                                              |
|----|--------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| 01 | [engineering-philosophy](doc/Android-Engineering-Guidelines/guidelines/01-engineering-philosophy.md)   | Purpose & scope, authority/classification, "every abstraction needs a reason"                                       |
| 02 | [project-architecture](doc/Android-Engineering-Guidelines/guidelines/02-project-architecture.md)       | Layered architecture, optional UseCase, module structure                                                            |
| 03 | [kotlin](doc/Android-Engineering-Guidelines/guidelines/03-kotlin.md)                                   | Formatting, `val`/`var`, null safety, scope functions, **file/class/function size + how to split a 500-line class** |
| 04 | [naming-conventions](doc/Android-Engineering-Guidelines/guidelines/04-naming-conventions.md)           | Case rules, component/architecture naming, `Manager`/`Helper`/`Util` policy                                         |
| 05 | [coroutines-concurrency](doc/Android-Engineering-Guidelines/guidelines/05-coroutines-concurrency.md)   | Scopes, dispatchers, Flow/StateFlow/SharedFlow, cancellation                                                        |
| 06 | [android-architecture](doc/Android-Engineering-Guidelines/guidelines/06-android-architecture.md)       | UI state/events, ViewModel/Repository/DataSource responsibilities, mapping                                          |
| 07 | [ui-compose](doc/Android-Engineering-Guidelines/guidelines/07-ui-compose.md)                           | Compose vs XML Views, state hoisting, one-time events, screen states, navigation                                    |
| 08 | [xml-resources](doc/Android-Engineering-Guidelines/guidelines/08-xml-resources.md)                     | XML standards, resource naming/qualifiers, **icons & images decision**                                              |
| 09 | [lifecycle](doc/Android-Engineering-Guidelines/guidelines/09-lifecycle.md)                             | Activity/Fragment/ViewModel/Compose lifecycle, config change vs process death, leaks                                |
| 10 | [networking](doc/Android-Engineering-Guidelines/guidelines/10-networking.md)                           | Retrofit/OkHttp, error mapping, retries & idempotency, caching                                                      |
| 11 | [database-storage](doc/Android-Engineering-Guidelines/guidelines/11-database-storage.md)               | Room, DataStore, storage decision table, migrations                                                                 |
| 12 | [dependency-injection](doc/Android-Engineering-Guidelines/guidelines/12-dependency-injection.md)       | Constructor injection, Manual DI vs Hilt, Singleton policy                                                          |
| 13 | [error-handling-logging](doc/Android-Engineering-Guidelines/guidelines/13-error-handling-logging.md)   | Throw vs Result vs sealed errors, no swallowed exceptions, logging per build type                                   |
| 14 | [security](doc/Android-Engineering-Guidelines/guidelines/14-security.md)                               | What is actually secret, secure storage, HTTPS/pinning, components/intents                                          |
| 15 | [r8-proguard](doc/Android-Engineering-Guidelines/guidelines/15-r8-proguard.md)                         | Shrinking/obfuscation, no blanket keep rules, mapping files, release-crash flow                                     |
| 16 | [16kb-page-size](doc/Android-Engineering-Guidelines/guidelines/16-16kb-page-size.md)                   | Memory pages, native `.so`, ELF alignment, APK Analyzer, checklist                                                  |
| 17 | [gradle-build](doc/Android-Engineering-Guidelines/guidelines/17-gradle-build.md)                       | Kotlin DSL, version catalog, build types, signing, dependency management, release engineering                       |
| 18 | [performance](doc/Android-Engineering-Guidelines/guidelines/18-performance.md)                         | Measure-first, main-thread, startup, memory/leaks, lists                                                            |
| 19 | [background-work](doc/Android-Engineering-Guidelines/guidelines/19-background-work.md)                 | Decision tree, WorkManager vs Foreground Service vs AlarmManager, "coroutine ≠ background job"                      |
| 20 | [testing](doc/Android-Engineering-Guidelines/guidelines/20-testing.md)                                 | Test pyramid, mock vs fake, naming/structure                                                                        |
| 21 | [git-code-review](doc/Android-Engineering-Guidelines/guidelines/21-git-code-review.md)                 | Branches, commits, PR size, PR checklist, review culture                                                            |
| 22 | [static-analysis-quality](doc/Android-Engineering-Guidelines/guidelines/22-static-analysis-quality.md) | Lint/ktlint/detekt, fix-don't-hide warnings, deprecated APIs                                                        |
| 23 | [accessibility-i18n](doc/Android-Engineering-Guidelines/guidelines/23-accessibility-i18n.md)           | Content descriptions, touch targets, strings/plurals, RTL, per-app language                                         |
| 24 | [code-smells](doc/Android-Engineering-Guidelines/guidelines/24-code-smells.md)                         | Smells we don't accept + good vs bad examples                                                                       |
| 25 | [decision-tables](doc/Android-Engineering-Guidelines/guidelines/25-decision-tables.md)                 | Master decision table + index                                                                                       |
| 26 | [exceptions-process](doc/Android-Engineering-Guidelines/guidelines/26-exceptions-process.md)           | How to legitimately deviate from a rule                                                                             |
| 27 | [golden-rules-checklists](doc/Android-Engineering-Guidelines/guidelines/27-golden-rules-checklists.md) | Golden rules, staged checklists, official references                                                                |

### `examples/`

- [`examples/good/`](doc/Android-Engineering-Guidelines/examples/good) — reference implementations that follow these guidelines.
- [`examples/bad/`](doc/Android-Engineering-Guidelines/examples/bad) — anti-patterns with an explanation of exactly what's wrong.

### Meta

- [`CONTRIBUTING.md`](doc/Android-Engineering-Guidelines/CONTRIBUTING.md) — how to propose changes, update currency facts, and record rule exceptions.

---

## Core philosophy (the one line)

> **Every abstraction must have a reason.** Prefer the simplest production-appropriate solution. Write code the next developer can understand.

If a rule ever feels wrong for your specific case, that's fine — deviate the *right* way: read the [Exception Process](doc/Android-Engineering-Guidelines/guidelines/26-exceptions-process.md). "I prefer it this way" is never enough; a documented, reviewable technical reason is.

#
#
#
#