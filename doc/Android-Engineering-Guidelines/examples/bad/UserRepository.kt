package examples.bad

/**
 * BAD example — DO NOT COPY. Violations listed inline.
 *
 * What's wrong:
 * 1. Returns the raw network DTO to callers → the wire format leaks into the
 *    ViewModel and UI; a backend rename breaks everything (06 §5).
 * 2. No error handling → a network exception crashes the app or bubbles raw
 *    to the UI (10 §2, 13).
 * 3. Hardcoded Dispatchers.IO inside → hard to test (05 §4).
 * 4. Empty catch swallowing the failure (see loadOrThrow) hides bugs (13 §A.2).
 */
class UserRepository(private val api: UserApi) {

    // ❌ returns a DTO, not a domain model
    suspend fun getUser(id: String): UserDto {
        return api.getUser(id)   // ❌ no error mapping; runs on caller's thread
    }

    // ❌ swallows every error silently
    suspend fun loadOrThrow(id: String): UserDto? {
        return try {
            api.getUser(id)
        } catch (e: Exception) {
            null                 // ❌ caller can't tell "no user" from "network died"
        }
    }
}

data class UserDto(val n: String, val e: String)   // ❌ cryptic field names too
