package examples.bad

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * BAD example — DO NOT COPY. Violations are listed inline.
 *
 * What's wrong:
 * 1. Holds a Context in the ViewModel → memory leak (06 §2, 09).
 * 2. Three separate LiveData fields → they drift out of sync; you can be
 *    "loading" AND "error" at once (06 §1). Use one sealed UI state.
 * 3. GlobalScope.launch → unscoped work that leaks and can't be cancelled (05 §7).
 * 4. api.getUser()!! → blind !! crashes with NPE if null (03 §5).
 * 5. Network call on an unknown thread; no error modeling (13).
 */
class ProfileViewModel(private val context: Context) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()   // ❌ desyncs with the others
    val error = MutableLiveData<String>()        // ❌
    val user = MutableLiveData<User>()           // ❌

    fun load() {
        isLoading.value = true
        GlobalScope.launch {                     // ❌ leaks, uncancellable
            val u = api.getUser()!!              // ❌ blind !! + no error handling
            user.postValue(u)
        }
    }
}
