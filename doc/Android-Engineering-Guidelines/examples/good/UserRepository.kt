package examples.good

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * GOOD example — follows guidelines 06 (Repository = single source of truth),
 * 10 (Networking: error mapping), and 05 (dispatchers pushed down).
 *
 * Why this is correct:
 * - Exposes DOMAIN models (User), never DTOs/entities (06 §5).
 * - The local DB is the single source of truth; refresh updates it (06 §3).
 * - Network errors are mapped to a domain result, not leaked as exceptions (10 §2).
 * - The IO dispatcher is injected and applied here, so callers stay main-safe (05 §4).
 */
class UserRepository(
    private val remote: UserRemoteDataSource,
    private val local: UserLocalDataSource,
    private val io: CoroutineDispatcher,
) {

    /** Observes the user from local storage — the single source of truth. */
    fun observeUser(id: String): Flow<User> = local.observe(id)

    /** Fetches the user from the network, maps errors, and updates local storage. */
    suspend fun getUser(id: String): NetworkResult<User> = withContext(io) {
        try {
            val dto = remote.fetch(id)          // network DTO
            local.upsert(dto.toEntity())        // persist
            NetworkResult.Success(dto.toDomain())
        } catch (e: IOException) {
            NetworkResult.Failure(AppError.NoConnection)
        }
    }
}

/** Network model — exactly what the server returns. */
data class UserDto(val name: String, val emailId: String)

/** Domain model — the clean type the app works with. */
data class User(val name: String, val email: String)

/** Maps the network DTO into the domain model. */
fun UserDto.toDomain(): User = User(name = name, email = emailId)
