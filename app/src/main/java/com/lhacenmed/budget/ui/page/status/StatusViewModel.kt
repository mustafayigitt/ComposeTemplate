package com.lhacenmed.budget.ui.page.status

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lhacenmed.budget.data.model.StatusItem
import com.lhacenmed.budget.data.repository.StatusSaverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatusUiState(
    val hasPermission: Boolean   = false,
    val images: List<StatusItem> = emptyList(),
    val videos: List<StatusItem> = emptyList(),
    val isLoading: Boolean       = false,
    val savingUri: Uri?          = null,
    val message: String?         = null,
    val previewItem: StatusItem? = null  // non-null = preview open (image or video)
)

@HiltViewModel
class StatusViewModel @Inject constructor(
    private val repository: StatusSaverRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StatusUiState())
    val state = _state.asStateFlow()

    init { repository.getSavedUri()?.let { load(it) } }

    fun onPermissionGranted(uri: Uri?) {
        if (uri == null) return
        repository.persistUri(uri)
        load(uri)
    }

    fun openPreview(item: StatusItem) = _state.update { it.copy(previewItem = item) }
    fun closePreview()                = _state.update { it.copy(previewItem = null) }

    fun saveStatus(item: StatusItem) = viewModelScope.launch {
        _state.update { it.copy(savingUri = item.uri) }
        val success = repository.saveStatus(item)
        _state.update { it.copy(
            savingUri = null,
            message   = if (success) "Saved to gallery" else "Failed to save"
        )}
    }

    fun clearMessage() = _state.update { it.copy(message = null) }

    private fun load(treeUri: Uri) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, hasPermission = true) }
        val all      = repository.getStatuses(treeUri)
        val noAccess = all.isEmpty() && repository.getSavedUri() == null
        _state.update { it.copy(
            isLoading     = false,
            hasPermission = !noAccess,
            images        = all.filter { s -> !s.isVideo },
            videos        = all.filter { s ->  s.isVideo }
        )}
    }
}
