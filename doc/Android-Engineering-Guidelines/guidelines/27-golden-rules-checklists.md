# 27 · Golden Rules, Developer Checklists & Official References

[← 26 Exception Process](26-exceptions-process.md) · [README](../README.md)

> The one-page summary: non-negotiable rules, staged checklists, and the official sources that back this rulebook.

---

## 1. Golden Rules

1. Keep code simple; prefer readability over cleverness. ([01](01-engineering-philosophy.md))
2. Follow official Android and Kotlin conventions. ([03](03-kotlin.md), [04](04-naming-conventions.md))
3. **Every abstraction must have a reason** — no gratuitous Managers/Helpers/interfaces/UseCases. ([01](01-engineering-philosophy.md))
4. Never block the main thread. ([18](18-performance.md))
5. Prefer `val` and immutable state. ([03](03-kotlin.md))
6. One exhaustive, immutable UI state; deliver events once. ([06](06-android-architecture.md), [07](07-ui-compose.md))
7. Don't blindly use `!!`; handle null explicitly. ([03](03-kotlin.md))
8. Never `GlobalScope`; scope coroutines to a lifecycle. ([05](05-coroutines-concurrency.md))
9. Collect flows lifecycle-aware. ([05](05-coroutines-concurrency.md))
10. Keep `Context`/`View` out of ViewModels and long-lived singletons. ([06](06-android-architecture.md), [12](12-dependency-injection.md))
11. No hardcoded user-facing strings; use resources and plurals. ([23](23-accessibility-i18n.md))
12. Icons → vector/SVG; banners/ads/large/many images → remote/API; minimize bundled raster. ([08](08-xml-resources.md))
13. Right-size images; never load full-res into small views. ([08](08-xml-resources.md))
14. Repository is the single source of truth; map DTO/entity/domain/UI models. ([06](06-android-architecture.md))
15. Prefer Room over raw SQLite; DataStore over SharedPreferences for new code. ([11](11-database-storage.md))
16. Never silently swallow exceptions; never swallow `CancellationException`. ([13](13-error-handling-logging.md))
17. Never log secrets or PII; strip debug logs from release. ([13](13-error-handling-logging.md))
18. Treat everything shipped in the APK as non-secret; store real secrets securely / server-side. ([14](14-security.md))
19. Enable R8 + resource shrinking; keep rules narrow and commented; preserve mapping files. ([15](15-r8-proguard.md))
20. **Verify 16 KB page-size compatibility** — check native `.so` from SDKs too. ([16](16-16kb-page-size.md))
21. Choose background-work tools by durability; a coroutine is not a durable background job. ([19](19-background-work.md))
22. Don't add a dependency the platform/Kotlin/Jetpack already provides; pin versions. ([17](17-gradle-build.md))
23. Measure before optimizing; no premature optimization. ([18](18-performance.md))
24. Treat accessibility as part of UI quality. ([23](23-accessibility-i18n.md))
25. Fix warnings; don't hide them. ([22](22-static-analysis-quality.md))
26. Don't introduce new deprecated-API usage without a documented reason. ([22](22-static-analysis-quality.md))
27. **A feature working in Debug does not prove it works in Release** — verify release builds. ([17](17-gradle-build.md), [15](15-r8-proguard.md), [16](16-16kb-page-size.md))
28. Small, single-purpose PRs; no dead/commented-out code, no stray TODOs. ([21](21-git-code-review.md), [24](24-code-smells.md))
29. Prefer realistic fakes over heavy mocking; test behavior, not implementation. ([20](20-testing.md))
30. Any deviation must be intentional, documented, justified, and reviewable. ([26](26-exceptions-process.md))
31. Keep code neat and clean: no commented-out code, no dead code, no stray debug logs. ([03](03-kotlin.md), [24](24-code-smells.md))
32. Every non-trivial function has a one-line purpose comment above it. ([03](03-kotlin.md))
33. Keep functions and classes small; split by responsibility when they exceed team thresholds. ([03](03-kotlin.md))
34. One primary type per file — don't stack unrelated classes; extract via OOP. ([03](03-kotlin.md))
35. Proper, meaningful names for every function, class, and file. ([03](03-kotlin.md), [04](04-naming-conventions.md))

---

## 2. Developer Checklists

### 2.1 Before Coding
```text
[ ] I understand the requirement and the affected layer(s) (02, 06)
[ ] I checked whether existing code/components already solve this
[ ] I chose the simplest approach; no abstraction "just in case" (01)
[ ] Storage/background/UI-toolkit decisions made against the decision tables (25)
[ ] No new dependency needed — or it passed the evaluation checklist (17)
```

### 2.2 During Development
```text
[ ] Naming conventions followed (04)
[ ] val/immutable by default; null handled without blind !! (03)
[ ] Each non-trivial function has a one-line purpose comment (03)
[ ] Functions/classes within size thresholds; oversized units split by responsibility (03)
[ ] One primary type per file; no unrelated classes stacked together (03)
[ ] No commented-out code, dead code, or stray debug logs (03, 13)
[ ] Coroutines scoped correctly; dispatchers injected; flows lifecycle-aware (05)
[ ] One exhaustive UI state; events delivered once (06, 07)
[ ] Strings/dimens/colors in resources; RTL-safe attributes (08, 23)
[ ] Icons vector/SVG; banners/large images remote, not bundled (08)
[ ] Errors modeled and mapped; no swallowed exceptions (13)
[ ] No secrets/PII in logs (13); security basics applied (14)
[ ] Accessibility: content descriptions, touch targets, contrast (23)
[ ] No main-thread I/O (18)
```

### 2.3 Before PR
```text
[ ] PR checklist (21) completed
[ ] Lint / ktlint / detekt clean — warnings fixed, not suppressed (22)
[ ] Tests added/updated for changed logic (20)
[ ] No dead/commented-out code, no debug logs, no unticketed TODOs (24)
[ ] Any rule deviation documented per Exception Process (26)
```

### 2.4 Before QA
```text
[ ] Feature verified on a QA/staging build (not just debug) (17)
[ ] All screen states exercised: loading/success/error/empty (07)
[ ] Lifecycle edge cases: rotation + process death ("Don't keep activities") (09)
[ ] Offline/error/timeout paths tested (10)
[ ] Localization + RTL spot-checked (23)
```

### 2.5 Before Release
```text
[ ] Built and fully exercised on a RELEASE build (17)
[ ] R8 minify + resource shrinking enabled; no release-only crashes (15)
[ ] Reflection/serialization/JNI paths verified under R8 (15)
[ ] mapping.txt archived + uploaded to crash reporting/Play (15)
[ ] 16 KB compatibility verified (native libs, alignment, emulator test) (16)
[ ] Signing via secured key / Play App Signing; no secrets committed (14, 17)
[ ] Dependency versions pinned and reviewed (17)
[ ] targetSdk meets current Play requirement (API 36 / Android 16) (README)
```

### 2.6 Production Verification
```text
[ ] R8 shrinking/optimization confirmed in the shipped artifact
[ ] Resource shrinking confirmed
[ ] Mapping files present; crashes symbolicate
[ ] Crash-free rate + ANR rate monitored (Play vitals)
[ ] Startup/performance within budget (Baseline Profiles applied)
[ ] Memory footprint acceptable (Play app-quality signal)
[ ] Security: HTTPS/network config, secure storage verified
[ ] 16 KB page-size compatibility: no Play Console warning
[ ] Native libraries inventory reviewed for this release
[ ] Accessibility pass (TalkBack) done
[ ] Localization complete for shipped locales
[ ] Release build (not debug) is what shipped
```

---

## 3. Official References {#references}

> Primary sources used to ground this rulebook (official Google/Android and Kotlin docs). Community/industry practices are labeled 🟠 in-text and are not sourced here as authority. *Content from these sources was rephrased for compliance with licensing restrictions.*

**Platform & Play requirements**
- Target API level requirements — [developer.android.com/google/play/requirements/target-sdk](https://developer.android.com/google/play/requirements/target-sdk) (API 36 / Android 16). → README, [17](17-gradle-build.md)
- Support 16 KB page sizes — [developer.android.com/guide/practices/page-sizes](https://developer.android.com/guide/practices/page-sizes) (API 35+ requirement; Feb 1, 2027 update deadline; APK Analyzer; ELF alignment). → [16](16-16kb-page-size.md)

**Architecture & UI**
- Guide to app architecture — [developer.android.com/topic/architecture](https://developer.android.com/topic/architecture). → [02](02-project-architecture.md), [06](06-android-architecture.md)
- UI layer & lifecycle-aware collection — [developer.android.com/topic/architecture/ui-layer](https://developer.android.com/topic/architecture/ui-layer). → [05](05-coroutines-concurrency.md), [06](06-android-architecture.md), [07](07-ui-compose.md)
- Jetpack Compose & Compose API guidelines — [developer.android.com/develop/ui/compose](https://developer.android.com/develop/ui/compose). → [04](04-naming-conventions.md), [07](07-ui-compose.md), [18](18-performance.md)

**Kotlin & coroutines**
- Kotlin Coding Conventions — [kotlinlang.org/docs/coding-conventions.html](https://kotlinlang.org/docs/coding-conventions.html). → [03](03-kotlin.md), [04](04-naming-conventions.md)
- Android Kotlin style guide — [developer.android.com/kotlin/style-guide](https://developer.android.com/kotlin/style-guide). → [03](03-kotlin.md), [04](04-naming-conventions.md)
- Coroutines best practices — [developer.android.com/kotlin/coroutines/coroutines-best-practices](https://developer.android.com/kotlin/coroutines/coroutines-best-practices). → [05](05-coroutines-concurrency.md)

**Lifecycle & data**
- Lifecycle, Saved State & process death — [developer.android.com/topic/libraries/architecture/lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle). → [09](09-lifecycle.md)
- Room — [developer.android.com/training/data-storage/room](https://developer.android.com/training/data-storage/room). → [11](11-database-storage.md)
- DataStore — [developer.android.com/topic/libraries/architecture/datastore](https://developer.android.com/topic/libraries/architecture/datastore). → [11](11-database-storage.md)

**Networking, DI, background, performance**
- Connectivity — [developer.android.com/develop/connectivity](https://developer.android.com/develop/connectivity). → [10](10-networking.md)
- Hilt / DI — [developer.android.com/training/dependency-injection/hilt-android](https://developer.android.com/training/dependency-injection/hilt-android). → [12](12-dependency-injection.md)
- Background work / WorkManager — [developer.android.com/develop/background-work](https://developer.android.com/develop/background-work). → [19](19-background-work.md)
- Performance & startup / Baseline Profiles — [developer.android.com/topic/performance](https://developer.android.com/topic/performance). → [18](18-performance.md)

**Security, build, quality**
- Security best practices / Network Security Config / Play Integrity — [developer.android.com/privacy-and-security/security-tips](https://developer.android.com/privacy-and-security/security-tips). → [14](14-security.md)
- R8 shrink/obfuscate/optimize — [developer.android.com/build/shrink-code](https://developer.android.com/build/shrink-code). → [15](15-r8-proguard.md)
- Android Gradle Plugin releases — [developer.android.com/build/releases/gradle-plugin](https://developer.android.com/build/releases/gradle-plugin); Version Catalogs — [docs.gradle.org](https://docs.gradle.org/current/userguide/platforms.html). → [17](17-gradle-build.md)
- Android Lint — [developer.android.com/studio/write/lint](https://developer.android.com/studio/write/lint). → [22](22-static-analysis-quality.md)
- Testing on Android — [developer.android.com/training/testing](https://developer.android.com/training/testing). → [20](20-testing.md)
- Accessibility — [developer.android.com/guide/topics/ui/accessibility](https://developer.android.com/guide/topics/ui/accessibility). → [23](23-accessibility-i18n.md)
- Localization / bidirectional / per-app language — [developer.android.com/guide/topics/resources/localization](https://developer.android.com/guide/topics/resources/localization). → [23](23-accessibility-i18n.md)
- App resources & qualifiers — [developer.android.com/guide/topics/resources](https://developer.android.com/guide/topics/resources/providing-resources). → [08](08-xml-resources.md)
- Vector drawables / reduce app size — [developer.android.com/develop/ui/views/graphics/vector-drawable-resources](https://developer.android.com/develop/ui/views/graphics/vector-drawable-resources), [developer.android.com/topic/performance/reduce-apk-size](https://developer.android.com/topic/performance/reduce-apk-size). → [08](08-xml-resources.md)

*Verify version-specific and Play-policy items against the live docs before each release — see [CONTRIBUTING.md](../CONTRIBUTING.md).*

---

[↑ Back to README](../README.md)
#
#
#
#