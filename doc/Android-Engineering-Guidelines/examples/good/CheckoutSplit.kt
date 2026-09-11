package examples.good

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * GOOD example — the god class from examples/bad/CheckoutGodViewModel.kt,
 * split by responsibility per guideline 03 §12.
 *
 * Five jobs → five focused, independently testable types. The ViewModel now
 * only COORDINATES (~one screen of code). Each helper has one clear job and a
 * one-line purpose comment (03 §10).
 */

// ── job 2: validation ─────────────────────────────────────────────────────
/** Validates address, coupon, and payment for checkout. Pure rules, no Android. */
class CheckoutValidator {
    /** Returns the first validation error, or null when the cart is valid. */
    fun validate(cart: Cart): CheckoutError? = null
}

// ── job 3: pricing ────────────────────────────────────────────────────────
/** Computes subtotal, tax, and discount for a cart. */
class PriceCalculator {
    /** Returns the final payable amount in minor units (e.g. paise/cents). */
    fun total(cart: Cart): Long = 0
}

// ── job 4: UI mapping ─────────────────────────────────────────────────────
/** Maps a domain [Cart] + total into the UI-ready model. */
class CheckoutUiMapper {
    /** Builds the render-ready UI model for the checkout screen. */
    fun toUiModel(cart: Cart, total: Long): CheckoutUiModel = CheckoutUiModel(total)
}

// ── coordinator ───────────────────────────────────────────────────────────
/**
 * Coordinates checkout. Delegates each job to a focused collaborator
 * (jobs 1 and 5 live in the repository and analytics types).
 */
class CheckoutViewModel(
    private val repository: CartRepository,     // job 1: load/cache
    private val validator: CheckoutValidator,   // job 2
    private val calculator: PriceCalculator,    // job 3
    private val uiMapper: CheckoutUiMapper,      // job 4
    private val analytics: CheckoutAnalytics,    // job 5
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Loading)
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    /** Loads the cart and publishes a ready-to-render UI state. */
    fun load() {
        viewModelScope.launch {
            when (val result = repository.getCart()) {
                is NetworkResult.Success -> {
                    val cart = result.data
                    _uiState.value = CheckoutUiState.Ready(uiMapper.toUiModel(cart, calculator.total(cart)))
                }
                is NetworkResult.Failure -> _uiState.value = CheckoutUiState.Error(result.error)
            }
        }
    }
}
