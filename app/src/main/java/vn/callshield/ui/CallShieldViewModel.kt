package vn.callshield.ui

import android.content.Context
import android.app.role.RoleManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import vn.callshield.data.BlockEntry
import vn.callshield.data.BlocklistRepository
class CallShieldViewModel : ViewModel() {
    private var repository: BlocklistRepository? = null
    var isScreeningEnabled by mutableStateOf(false)
        private set
    fun init(context: Context) {
        if (repository == null) {
            repository = BlocklistRepository(context.applicationContext)
            viewModelScope.launch { repository?.refreshScreeningCache() }
        }
    }
    fun entries(context: Context) = run {
        init(context)
        repository!!.entries.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList<BlockEntry>()
        )
    }
    fun add(context: Context, brand: String, phone: String, note: String, onError: (String?) -> Unit) {
        init(context)
        viewModelScope.launch {
            runCatching { repository!!.add(brand, phone, note) }
                .onSuccess {
                    repository!!.refreshScreeningCache()
                    onError(null)
                }
                .onFailure { onError(it.message ?: "Không thể thêm số điện thoại.") }
        }
    }
    fun toggle(context: Context, id: String, enabled: Boolean) {
        init(context)
        viewModelScope.launch {
            repository!!.toggle(id, enabled)
            repository!!.refreshScreeningCache()
        }
    }
    fun remove(context: Context, id: String) {
        init(context)
        viewModelScope.launch {
            repository!!.remove(id)
            repository!!.refreshScreeningCache()
        }
    }
    fun refreshScreeningRole(context: Context) {
        isScreeningEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val manager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
            manager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        } else {
            false
        }
    }
}