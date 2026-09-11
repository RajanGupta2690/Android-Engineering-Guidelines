# 10 · Networking

[← 09 Lifecycle](09-lifecycle.md) · [README](../../../README.md) · **Next:** [11 · Database & Storage →](11-database-storage.md)

> Authority: 🟠 **Strong Engineering** built on 🟢 [official Android networking](https://developer.android.com/develop/connectivity) + widely used Retrofit/OkHttp.

---

## 1. The stack — 🟣 Team Standard · 🟡 Recommended

- **Retrofit** over **OkHttp** for REST; **kotlinx.serialization** (or Moshi) for JSON.
- Suspend functions for one-shot calls; `Flow` for streams/polling where justified.
- DTOs on the wire; map to domain models in the data layer ([06 §5](06-android-architecture.md)).

### ✅ Recommended
```kotlin
interface OrderApi {
    /** GET a single order by id. Returns the raw network DTO. */
    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: String): OrderDto
}
```

---

## 2. Layered error handling — 🟠 Strong Eng. · 🔴 Mandatory

**In plain words:** the UI should never see a raw `HttpException` or `IOException`. Convert failures into a clean result the ViewModel can render.

```kotlin
sealed interface NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>
    data class Failure(val error: AppError) : NetworkResult<Nothing>
}

enum class AppError { NoConnection, Timeout, Unauthorized, Server, Unknown }
```

Map status codes: `401` → `Unauthorized` (refresh token or log out), `5xx` → `Server`, timeouts / `UnknownHostException` → `Timeout` / `NoConnection`.

**Why.** Screens should show "No internet, retry?" — not a stack trace. Mapping errors once, in the data layer, keeps every screen simple.

---

## 3. Timeouts, interceptors, auth, logging — 🟠 Strong Eng. · 🔴 Mandatory (auth + logging)

- Set explicit connect/read/write timeouts on OkHttp — no infinite hangs.
- Use an **auth interceptor** to attach tokens; an **authenticator** to refresh on `401`.
- **Logging interceptor:** `BODY` level only in debug; `NONE` in release. **Never log tokens/PII** ([13 · Logging](13-error-handling-logging.md), [14 · Security](14-security.md)).
- All traffic over **HTTPS** ([14 · Security](14-security.md)).

---

## 4. Retries & idempotency — do NOT blindly retry — 🟠 Strong Eng. · 🔴 Mandatory

**In plain words:** "idempotent" means doing it twice has the same effect as doing it once. Reading data twice is safe; charging a card twice is not.

- **Safe to retry:** idempotent requests — `GET`, `PUT`, `DELETE`, or any request with an **idempotency key** — on transient failures (timeout, `503`), with **exponential backoff + jitter** and a max attempt cap.
- **Dangerous to retry:** a non-idempotent `POST` (create order, charge card) **without** an idempotency key — a retry can double-charge or duplicate data.

**Why.** Retrying blindly turns one network blip into duplicate side effects. Retry only when the operation is safe to repeat; for writes, prefer a server-provided idempotency key.

---

## 5. Caching, pagination, offline, cancellation — 🟠 Strong Eng. · 🟡 Recommended

- Use OkHttp/HTTP caching for cacheable GETs; use the database as the offline source of truth ([11](11-database-storage.md)).
- Use **Paging 3** for large lists.
- Coroutine cancellation cancels the request automatically when the scope dies — rely on it; don't leak calls.

---

**Next:** [11 · Database & Storage →](11-database-storage.md)
#
#
#
#