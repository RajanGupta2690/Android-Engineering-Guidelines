# 25 · Decision Tables

[← 24 Code Smells](24-code-smells.md) · [README](../../../README.md) · **Next:** [26 · Exception Process →](26-exceptions-process.md)

> A quick "which should I use?" reference. When two approaches both work, this table gives the default and the reason. Detailed rationale lives in each linked file.

---

## Master decision table

| Situation               | Option A            | Option B                | Recommended                           | Why                                | When A                                     | When B                         |
|-------------------------|---------------------|-------------------------|---------------------------------------|------------------------------------|--------------------------------------------|--------------------------------|
| Persistence (key-value) | SharedPreferences   | DataStore               | **DataStore**                         | Async, Flow-based                  | Legacy only                                | New code                       |
| Structured data         | Raw SQLite          | Room                    | **Room**                              | Compile-time SQL, migrations       | Never                                      | Always                         |
| Local storage type      | Room                | DataStore               | **Depends**                           | Relational vs key-value            | Queryable data                             | Simple settings                |
| Background work         | Lifecycle coroutine | WorkManager             | **Depends**                           | Durability                         | Short, screen-tied                         | Survives process death         |
| UI toolkit              | XML Views           | Compose                 | **Compose for new UI**                | Google direction, less boilerplate | XML-first codebase                         | New/isolated UI                |
| DI                      | Manual              | Hilt                    | **Depends on scale**                  | Complexity vs benefit              | Tiny app                                   | Multi-feature app              |
| Image: icon/logo        | Vector (SVG)        | Raster                  | **Vector**                            | Scales, tiny                       | —                                          | Never for icons                |
| Image: banner/ad/photo  | Bundle raster       | Remote/API              | **Remote/API**                        | Keeps app small, updatable         | Small+essential+offline                    | Large/many/changing            |
| Raster format           | WebP                | PNG/JPEG                | **Measure (WebP-first)**              | Content-dependent                  | Usually WebP                               | When genuinely better          |
| State stream            | LiveData            | StateFlow               | **StateFlow (new code)**              | Kotlin-first, testable             | Legacy screens                             | New code                       |
| One-time events         | State boolean       | SharedFlow/Channel      | **SharedFlow**                        | Delivered once                     | Never                                      | Always for events              |
| Error signaling         | Throw               | Sealed result           | **Sealed result (expected failures)** | Compiler-enforced                  | Programmer errors                          | Recoverable outcomes           |
| Test double             | Mock                | Fake                    | **Fake (your interfaces)**            | Behavior over assumptions          | Single boundary                            | Realistic behavior             |
| Domain layer            | Skip UseCase        | Add UseCase             | **Depends**                           | Real logic present?                | Simple pass-through                        | Cross-source/reused            |
| Singleton               | Hand-rolled object  | DI-scoped               | **DI-scoped**                         | Testable, managed                  | Never for mutable state                    | Expensive/shared infra         |
| Cert pinning            | Off                 | On                      | **Off unless high-value**             | Operational risk                   | Most apps                                  | Finance/health + rotation plan |
| Class > 500 lines       | Keep as-is          | Split by responsibility | **Split**                             | Readable, testable                 | Documented single-responsibility exception | Multiple jobs                  |

---

## Table index (where the detail lives)

- Architecture layers → [02](02-project-architecture.md), [06](06-android-architecture.md)
- UseCase decision → [02](02-project-architecture.md)
- Compose vs Views → [07](07-ui-compose.md)
- Image type/format/bundling → [08 · Images](08-xml-resources.md)
- Storage choice → [11](11-database-storage.md)
- DI choice → [12](12-dependency-injection.md)
- Singleton appropriateness → [12](12-dependency-injection.md#singleton-policy)
- Error approach → [13](13-error-handling-logging.md)
- Background work → [19](19-background-work.md)
- Test doubles → [20](20-testing.md)
- Class size / splitting → [03 §11–§12](03-kotlin.md)

---

**Next:** [26 · Exception Process →](26-exceptions-process.md)
#
#
#
#