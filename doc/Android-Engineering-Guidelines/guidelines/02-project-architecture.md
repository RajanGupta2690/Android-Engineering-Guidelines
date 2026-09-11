# 02 · Project Architecture

[← 01 Philosophy](01-engineering-philosophy.md) · [README](../../../README.md) · **Next:** [03 · Kotlin →](03-kotlin.md)

> How the codebase is organized top to bottom: the layers, which way dependencies point, when to add a domain layer, and how to structure modules. Detailed layer *responsibilities* live in [06 · Android Architecture](06-android-architecture.md).

---

## The layered model — 🟢 Official (Guide to app architecture) · 🔴 Mandatory

We follow Google's recommended layering: a **UI layer**, an optional **Domain layer**, and a **Data layer**, with a strict one-way dependency direction.

```text
        ┌───────────────────────────────────────────┐
        │                  UI LAYER                   │
        │  Activity / Fragment / Composable + State   │
        │                    │                        │
        │                    ▼                        │
        │                ViewModel                    │
        └────────────────────┬────────────────────────┘
                             │  depends on ↓ only
        ┌────────────────────▼────────────────────────┐
        │            DOMAIN LAYER (OPTIONAL)           │
        │        UseCase — business rules only         │
        └────────────────────┬────────────────────────┘
                             │  depends on ↓ only
        ┌────────────────────▼────────────────────────┐
        │                 DATA LAYER                   │
        │                Repository                    │
        │        ┌───────────┼───────────┐            │
        │        ▼           ▼           ▼            │
        │   Remote DS     Local DS    Local Storage    │
        │   (Retrofit)     (Room)     (DataStore/File)  │
        └───────────────────────────────────────────┘
```

**The dependency rule (🔴).** Dependencies point **downward only**. UI knows the ViewModel; the ViewModel knows Domain (or Data); the Data layer knows nothing above it. Data-layer types (DTOs, Room entities) must never leak upward into the UI — see [mapping](06-android-architecture.md#mapping-between-layers).

**Why one direction.** A one-way graph means a change in the database can't reach up and break a screen, and you can test the ViewModel without a real network. Two-way dependencies create the tangle that makes large apps unchangeable.

---

## Is the Domain layer (UseCase) mandatory? — No — 🟢 Official (domain layer is optional) · 🟣 Team rule for *when*

Google explicitly makes the domain layer optional. Our rule:

**Use `ViewModel → Repository` directly when:**
- The screen reads/writes one repository with no cross-source rules.
- There's no reusable business logic to share.

**Introduce `ViewModel → UseCase → Repository` when at least one is true:**
- Business logic spans **multiple repositories/sources**.
- The **same non-trivial logic is reused** by two or more ViewModels.
- There are **real business rules/validation** worth testing away from Android.

### ✅ Recommended — a UseCase that earns its place

```kotlin
/** Builds the dashboard by combining user + open orders and applying a reorder rule. */
class GetDashboardUseCase(
    private val userRepository: UserRepository,
    private val ordersRepository: OrdersRepository,
) {
    operator fun invoke(): Flow<Dashboard> =
        combine(userRepository.observeUser(), ordersRepository.observeOpenOrders()) { user, orders ->
            Dashboard(user = user, canReorder = user.isVerified && orders.isNotEmpty())
        }
}
```

### ❌ Avoid — a pass-through UseCase

```kotlin
// Adds a file and a layer but does nothing. Over-engineering.
class GetUserUseCase(private val repo: UserRepository) {
    operator fun invoke() = repo.getUser()   // just call the repo from the ViewModel
}
```

**Why.** A UseCase that only forwards a call is pure indirection: more files, more navigation, zero benefit. Add the layer when it carries logic, not before.

**Exception.** Teams that mandate a domain layer *everywhere* for uniformity accept the pass-through cost on purpose. We don't; we optimize for the reader.

---

## Module structure — 🟣 Team Standard · 🟡 Recommended

Start as a **well-organized single `:app` module** with **package-by-feature**. Split into Gradle modules only for a concrete benefit: enforced boundaries, build parallelization on a large codebase, or independent ownership.

```text
com.company.app
├── core            // shared: network, database, designsystem, common utils
├── feature
│   ├── login        // ui/ + domain/ (optional) + data/
│   ├── dashboard
│   └── profile
└── app             // Application, DI wiring, navigation host
```

**Why not modularize everything on day one?** Module splits are hard to undo, add Gradle/DI ceremony, and slow small teams. Package-by-feature gives most of the readability benefit with none of the wiring cost. Modularize when the pain (build times, tangled ownership) is real and measured.

**How to decide a package's home (quick test):** name the *feature* first (`feature/checkout`), the *layer* second (`ui`, `domain`, `data`). If a class doesn't obviously belong to one feature, it's probably `core`.

---

**Next:** [03 · Kotlin →](03-kotlin.md)
#
#
#
#
