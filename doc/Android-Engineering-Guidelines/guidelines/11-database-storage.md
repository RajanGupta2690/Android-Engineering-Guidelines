# 11 · Database & Local Storage

[← 10 Networking](10-networking.md) · [README](../README.md) · **Next:** [12 · Dependency Injection →](12-dependency-injection.md)

> Authority: 🟢 **Official** — [Room](https://developer.android.com/training/data-storage/room), [DataStore](https://developer.android.com/topic/libraries/architecture/datastore).

---

## 1. Choosing a storage mechanism — 🟢 Official · 🔴 Mandatory

**In plain words:** pick storage by *what kind of data* it is.

| Need                                   | Use                        | Why                                       | Don't use                      |
|----------------------------------------|----------------------------|-------------------------------------------|--------------------------------|
| Structured, queryable, relational data | **Room**                   | Compile-time SQL checks, Flow, migrations | Raw SQLite, files              |
| Key-value settings/preferences         | **Preferences DataStore**  | Async, Flow-based, avoids main-thread I/O | `SharedPreferences` (new code) |
| Typed structured preferences           | **Proto DataStore**        | Type safety + schema                      | SharedPreferences              |
| Large binary / media                   | **Files / app storage**    | A DB isn't for blobs                      | Room BLOB columns              |
| Ephemeral cache                        | **In-memory / HTTP cache** | No persistence needed                     | Persisting everything          |

---

## 2. Room: entities, DAOs, database — 🟢 Official · 🔴 Mandatory (Room over raw SQLite)

**Why Room over raw SQLite?** Room checks your SQL **at compile time** (a typo in a query fails the build, not the app in production), generates the boilerplate, works with coroutines/Flow, and manages migrations. Raw SQLite pushes all of that into fragile runtime strings.

### ✅ Recommended
```kotlin
@Entity(tableName = "orders", indices = [Index("userId")])
data class OrderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val total: Long,
)

@Dao
interface OrderDao {
    /** Observes all orders for a user; emits again whenever the table changes. */
    @Query("SELECT * FROM orders WHERE userId = :userId")
    fun observeForUser(userId: String): Flow<List<OrderEntity>>

    /** Inserts or updates a single order. */
    @Upsert
    suspend fun upsert(order: OrderEntity)

    /** Replaces all of a user's orders in one atomic transaction. */
    @Transaction
    suspend fun replaceAll(userId: String, orders: List<OrderEntity>) { /* ... */ }
}
```

- Expose **`Flow`** for observation, **`suspend`** for one-shots — Room moves work off the main thread.
- Add **indices** on columns you filter/sort by (speeds up queries).
- Wrap multi-step writes in **`@Transaction`** (all-or-nothing).

---

## 3. Room migrations — 🟢 Official · 🔴 Mandatory

**In plain words:** when you change the database schema (add a column, a table), you must tell Room how to upgrade existing users' data — that's a "migration".

- Every schema change needs a migration.
- **Never ship `fallbackToDestructiveMigration()` to production** — it wipes user data. It's for local dev only.
- Test your migrations.

---

## 4. DataStore & migrating off SharedPreferences — 🟢 Official · 🟡 Recommended

`DataStore` is Flow-based and safe to read/write asynchronously. Google provides a `SharedPreferencesMigration` to move existing data over. Use **Preferences DataStore** for simple keys, **Proto DataStore** for a typed schema.

**Why not SharedPreferences for new code?** Its `apply()/commit()` can block, and it has no Flow API — leading to main-thread I/O and race conditions. DataStore fixes both.

---

**Next:** [12 · Dependency Injection →](12-dependency-injection.md)
#
#
#
#