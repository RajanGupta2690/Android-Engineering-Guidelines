# 19 · Background Work

[← 18 Performance](18-performance.md) · [README](../README.md) · **Next:** [20 · Testing →](20-testing.md)

> Authority: 🟢 **Official** — [Guide to background work](https://developer.android.com/develop/background-work), WorkManager, foreground services, background restrictions. Severity 🔴 for the "coroutine ≠ background job" rule.

---

## 1. A coroutine is NOT automatically a background-job solution — 🟢 Official · 🔴 Mandatory understanding

> A coroutine in `viewModelScope`/`lifecycleScope` **dies when the screen/process dies.** It is *not* durable background work.

**In plain words:** if the work must keep going after the user leaves the screen, closes the app, or the system kills the process, a coroutine alone will be cancelled and the work is lost. For that, you need WorkManager.

---

## 2. Decision tree — 🟢 Official · 🔴 Mandatory (choose per this tree)

```text
Need background work?
        │
        ├── Must it survive app close / process death / reboot,
        │   and complete eventually (deferrable)?
        │            └── YES → WorkManager  (constraints, retry/backoff)
        │
        ├── Is it user-visible and long-running right now
        │   (playback, active navigation, ongoing upload)?
        │            └── YES → Foreground Service (+ notification)
        │
        ├── Does it need exact user-facing timing (alarm clock, reminder)?
        │            └── YES → AlarmManager (exact alarms — needs permission/justification)
        │
        └── Is it short work tied to a currently active screen?
                     └── Lifecycle-aware coroutine (viewModelScope / lifecycleScope)
```

---

## 3. The options — 🟢 Official · 🔴 Mandatory

| Tool                    | Use for                                             | Notes                                                                                                                |
|-------------------------|-----------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| **Lifecycle coroutine** | Short work tied to a live screen                    | Cancelled with the scope; not durable                                                                                |
| **WorkManager**         | Deferrable, guaranteed work; sync, upload, periodic | Survives process death & reboot; constraints (network/charging), retry/backoff, chaining; expedited work for urgency |
| **Foreground Service**  | Ongoing, user-visible work                          | Requires a notification + correct foreground-service type; subject to modern restrictions                            |
| **AlarmManager**        | Exact time triggers                                 | Exact alarms are restricted — use only when user-facing timing truly requires it; else prefer inexact/WorkManager    |
| **BroadcastReceiver**   | React to system/app events                          | Keep work tiny; hand long work to WorkManager                                                                        |
| **JobScheduler**        | Low-level scheduling                                | Usually prefer WorkManager, which wraps it                                                                           |

### ✅ Recommended — durable sync with WorkManager
```kotlin
/** Uploads pending orders; retries with backoff; only runs when online. */
class SyncOrdersWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result =
        try { syncOrders(); Result.success() }
        catch (e: IOException) { Result.retry() }
}
```

---

## 4. Respect background execution limits — 🟢 Official · 🔴 Mandatory

Modern Android heavily restricts background execution (background service start limits, Doze, App Standby buckets, foreground-service type requirements, exact-alarm permissions). Design for deferrable work with WorkManager constraints; don't fight the platform with hacks.

---

**Next:** [20 · Testing →](20-testing.md)
#
#
#
#