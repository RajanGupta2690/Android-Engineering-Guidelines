# 18 · Performance Engineering

[← 17 Gradle & Build](17-gradle-build.md) · [README](../../../README.md) · **Next:** [19 · Background Work →](19-background-work.md)

> Authority: 🟢 **Official** — [App performance](https://developer.android.com/topic/performance), [App startup](https://developer.android.com/topic/performance/vitals/launch-time), [Compose performance](https://developer.android.com/develop/ui/compose/performance). Severity 🔴 for main-thread and ANR rules.

---

## 1. Measure first — don't guess — 🟠 Strong Eng. · 🔴 Mandatory

> **Measure → Identify the bottleneck → Optimize → Measure again.**

Use Android Studio Profiler, Perfetto, Macrobenchmark, and Baseline Profiles.

**In plain words:** don't "optimize" code you *think* is slow. Measure to find the actual slow part, fix that, then measure again to confirm. **Premature optimization** — micro-tuning code that isn't hot — adds complexity for no real gain, which is its own form of over-engineering ([01 · Philosophy](01-engineering-philosophy.md)).

---

## 2. Never block the main thread — 🟢 Official · 🔴 Mandatory

The main thread draws the UI ~60 times a second (every ~16 ms). Any I/O, heavy computation, or blocking call there causes **jank** (dropped frames = stutter) or an **ANR** (the system offers to kill your unresponsive app after ~5 s).

**Rule:** all I/O and heavy work goes off-main via coroutines + the right dispatcher ([05 §4](05-coroutines-concurrency.md)). No disk/DB/network on `Dispatchers.Main`.

---

## 3. Startup: cold / warm / hot — 🟢 Official · 🟡 Recommended

| Start type | What it is                        | Keep it fast by                                                                            |
|------------|-----------------------------------|--------------------------------------------------------------------------------------------|
| **Cold**   | Process created from scratch      | Minimal `Application.onCreate` work; lazy init; App Startup library; **Baseline Profiles** |
| **Warm**   | Process alive, activity recreated | Cheap Activity creation                                                                    |
| **Hot**    | Activity resumed                  | Cheap resume path                                                                          |

**In plain words:** don't do network/DB calls or build a huge DI graph eagerly in `Application.onCreate` — every user pays that cost on every cold start.

---

## 4. Memory, leaks, OOM — 🟢 Official · 🔴 Mandatory (no known leaks)

- Don't hold `Context`/`View` in long-lived objects ([09](09-lifecycle.md), [12](12-dependency-injection.md)); use **LeakCanary** in debug.
- Don't load full-resolution bitmaps into small views ([08 · Images](08-xml-resources.md)) — the top cause of OOM.
- Google Play now has an app-quality **memory footprint** signal (announced Aug 2026) — lower memory is a quality metric, not just a nicety.

---

## 5. Lists: RecyclerView & Compose — 🟢 Official · 🟡 Recommended

- **RecyclerView:** use `DiffUtil`/`ListAdapter`, stable IDs, keep `onBindViewHolder` light.
- **Compose:** give `LazyColumn/LazyRow` items stable `key`s, hoist state, avoid unstable parameters, use `remember`/`derivedStateOf`, and defer state reads to cut recomposition. Verify with the Layout Inspector / recomposition counts.

---

## 6. I/O, allocations, battery — 🟠 Strong Eng. · 🟡 Recommended

- Batch disk/network I/O; cache appropriately ([10](10-networking.md), [11](11-database-storage.md)).
- Avoid allocations in hot loops / `onDraw` / bind paths.
- Coalesce background work; respect Doze/App Standby; use WorkManager constraints ([19](19-background-work.md)) to avoid draining battery.

---

**Next:** [19 · Background Work →](19-background-work.md)
#
#
#
#