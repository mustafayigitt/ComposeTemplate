package com.lhacenmed.budget.ui.page.grocery

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lhacenmed.budget.data.local.GroceryItem
import com.lhacenmed.budget.data.repository.GroceryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class GroceryViewModel @Inject constructor(
    private val repository: GroceryRepository,
    private val supabase: SupabaseClient
) : ViewModel() {

    private val userId = supabase.auth.currentUserOrNull()?.id.orEmpty()

    val items: StateFlow<List<GroceryItem>> = repository.getItems(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addItem(name: String) = viewModelScope.launch {
        repository.addItem(userId, name.trim())
    }

    fun toggleItem(item: GroceryItem) = viewModelScope.launch {
        repository.toggleItem(item)
    }

    fun updateItem(id: Int, name: String) = viewModelScope.launch {
        repository.updateName(id, name.trim())
    }

    fun deleteItem(id: Int) = viewModelScope.launch {
        repository.delete(id)
    }
}
