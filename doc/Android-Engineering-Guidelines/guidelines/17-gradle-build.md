# 17 · Gradle, Build Configuration, Dependencies & Release Engineering

[← 16 · 16 KB Page-Size](16-16kb-page-size.md) · [README](../README.md) · **Next:** [18 · Performance →](18-performance.md)

> Authority: 🟢 **Official** — [AGP](https://developer.android.com/build), [Version catalogs](https://docs.gradle.org/current/userguide/platforms.html); 🟠/🟣 for engineering + team conventions.

---

# PART A — Gradle & Build

## A.1 Kotlin DSL + Version Catalog — 🟢 Official · 🔴 Mandatory

- Use **Kotlin DSL** (`build.gradle.kts`) for type safety + IDE support.
- Centralize versions in a **Version Catalog** (`gradle/libs.versions.toml`).

### ✅ Recommended
```toml
# gradle/libs.versions.toml
[versions]
kotlin = "2.4.10"
agp = "9.4.0"
retrofit = "..."

[libraries]
retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
```
```kotlin
// module build.gradle.kts
dependencies { implementation(libs.retrofit) }
```

**Why a version catalog?** One source of truth for versions. It stops the same library appearing at three different versions across modules and makes an upgrade a one-line change.

## A.2 Build types & flavors — 🟢 Official · 🟡 Recommended

- Standard build types: **debug**, **release**, and a **staging/QA** type (or flavor) — see Part C.
- Use `buildConfigField`/`resValue` for per-variant config (base URLs, flags). **Never commit secrets** ([14 · Security](14-security.md)).
- Use flavors only for genuinely different product variants; don't multiply flavors needlessly (builds explode combinatorially).

## A.3 Signing — 🟢 Official · 🔴 Mandatory

- Debug builds use the debug keystore. Release builds are signed with a **secured release key** whose credentials are **never committed** (use CI secrets / Play App Signing).
- Enroll in **Play App Signing**.

## A.4 Build performance & reproducibility — 🟠 Strong Eng. · 🟡 Recommended

- Enable Gradle **configuration cache** and **build cache**.
- **No dynamic versions** (`1.2.+`) — pin exact versions for reproducible builds.
- Declare repositories centrally via `dependencyResolutionManagement`.

## A.5 Don't over-engineer the build — 🟠 Strong Eng. · 🟡 Recommended

Avoid elaborate custom Gradle logic and convention-plugin sprawl until the project truly needs it. Complex build logic is the least-reviewed, hardest-to-debug code in most projects.

---

# PART B — Dependency Management

## B.1 The first question before adding any dependency — 🟠 Strong Eng. · 🔴 Mandatory

> **"Can the Android platform, Kotlin stdlib, or Jetpack already do this?"**

Every dependency is code you don't control but must ship, secure, and maintain forever.

## B.2 Evaluation checklist
```text
[ ] Necessity — does platform/Kotlin/Jetpack already solve it?
[ ] Maintenance — active releases, not abandoned
[ ] Security — known CVEs? reputable maintainer? (typosquatting check on the name)
[ ] License — compatible with our distribution
[ ] Transitive deps — what does it drag in?
[ ] APK/AAB size impact — measure it
[ ] Native libraries — does it ship .so files? (16 KB impact → 16)
[ ] Compatibility — minSdk/targetSdk, AGP/Kotlin versions
[ ] Release history — stable, not churny/breaking
[ ] Alternatives — a lighter or first-party option?
```

## B.3 Pin versions; review updates — 🔴 Mandatory
Pin exact versions in the catalog; update deliberately, reading changelogs; run a dependency-vulnerability check in CI.

## B.4 When to say yes / no — 🟡 Recommended
- **Yes:** solves a real, non-trivial problem better than you reasonably could (Retrofit/OkHttp, Room, mature image loader, crash reporter), well-maintained, passes the checklist.
- **No:** tiny utilities you can write in a few lines, one-function libraries, unmaintained packages, or anything dragging in incompatible native code.

---

# PART C — Release Engineering: Debug vs QA vs Release

## C.1 The three builds — 🟠 Strong Eng. · 🔴 Mandatory (verify on release)

> **A feature working in Debug does NOT prove it works in Release.** R8, obfuscation, resource shrinking, network config, and logging all behave differently.

|                    | **Debug**                | **QA / Staging**      | **Release**                                                       |
|--------------------|--------------------------|-----------------------|-------------------------------------------------------------------|
| Purpose            | Fast dev loop            | Realistic testing     | Production                                                        |
| R8 / minify        | Off                      | On (mirror release)   | **On** ([15](15-r8-proguard.md))                                  |
| Resource shrinking | Off                      | On                    | **On**                                                            |
| Logging            | Verbose (no PII/secrets) | Info/warn, no secrets | Errors → crash reporter only ([13](13-error-handling-logging.md)) |
| Signing            | Debug keystore           | Staging/release key   | Secured release key / Play App Signing                            |
| Endpoints          | Dev/test                 | Staging               | Production                                                        |
| Crash reporting    | Optional                 | On                    | **On**, with mapping upload                                       |
| 16 KB check        | —                        | Verify                | **Verify** ([16](16-16kb-page-size.md))                           |

**Team rule (🟣):** QA/staging should **mirror release** (minify on, shrinking on) so R8 and 16 KB issues surface **before** production.

**Why a release-like staging build?** The most expensive bugs are release-only (missing keep rule, stripped resource, obfuscated reflection, incompatible native lib). You can't catch them on debug, and catching them in production is costly. A release-like staging build is where they should die.

---

**Next:** [18 · Performance →](18-performance.md)
#
#
#
#