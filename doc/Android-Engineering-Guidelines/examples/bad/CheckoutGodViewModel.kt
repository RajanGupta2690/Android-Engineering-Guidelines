package examples.bad

import android.app.Application
import androidx.lifecycle.AndroidViewModel

/**
 * BAD example — a "god class" doing five jobs in ~520 lines. DO NOT COPY.
 * See guideline 03 §11–§12 for the exact step-by-step way to split this,
 * and examples/good/CheckoutSplit.kt for the result.
 *
 * What's wrong:
 * - Five unrelated responsibilities in one class (loading, validation, pricing,
 *   formatting, analytics) → impossible to test in isolation, constant merge
 *   conflicts, a change to pricing risks the network code (03 §12, 24 §A.1).
 * - AndroidViewModel holding Application for formatting is a smell (06 §2).
 */
class CheckoutGodViewModel(app: Application) : AndroidViewModel(app) {

    private var cart: Cart? = null

    // job 1 — loading (imagine ~90 lines: retrofit call, error handling, caching)
    fun loadCart() { /* ... */ }

    // job 2 — validation (imagine ~110 lines of if/else rules)
    fun validate(): Boolean { /* ... */ return true }

    // job 3 — pricing (imagine ~70 lines of tax/discount math)
    fun calculateTotal(): Long { /* ... */ return 0 }

    // job 4 — UI formatting (imagine ~90 lines)
    fun toUiText(): String { /* ... */ return "" }

    // job 5 — analytics/logging (imagine ~40 lines)
    fun logStep(step: String) { /* ... */ }
}
