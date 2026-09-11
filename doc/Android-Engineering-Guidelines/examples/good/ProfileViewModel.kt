package examples.good

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * GOOD example — follows guidelines 06 (Android Architecture) and 05 (Coroutines).
 *
 * Why this is correct:
 * - No Context/View held → no leak (06 §2).
 * - Work runs in viewModelScope → scoped & cancellable (05 §3).
 * - One immutable, exhaustive UI state → no impossible states (06 §1, 07 §5).
 * - Errors are modeled, not thrown at the UI (13).
 * - Dispatcher lives in the repository, not here (05 §4).
 */
class ProfileViewModel(
    private val repository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    /** Loads the user for [id] and publishes a single, consistent UI state. */
    fun load(id: String) {
        viewModelScope.launch {
            _uiState.value = when (val result = repository.getUser(id)) {
                is NetworkResult.Success -> ProfileUiState.Success(result.data)
                is NetworkResult.Failure -> ProfileUiState.Error(result.error)
            }
        }
    }
}

/** All the states the profile screen can be in — exhaustive so the UI must handle each. */
sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val user: User) : ProfileUiState
    data class Error(val error: AppError) : ProfileUiState
}
