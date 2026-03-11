package com.lhacenmed.budget.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lhacenmed.budget.data.model.SpendingItem
import com.lhacenmed.budget.data.repository.SpendingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
data class HomeUiState(
    val days: List<String> = emptyList(),
    val selectedDay: String = LocalDate.now().toString(),
    val items: List<SpendingItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SpendingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    init { loadDays() }

    fun loadDays() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        runCatching { repository.getDays() }
            .onSuccess { days ->
                val today = LocalDate.now().toString()
                val allDays = if (today in days) days else listOf(today) + days
                _state.update { it.copy(days = allDays, isLoading = false) }
                loadItemsForDay(_state.value.selectedDay)
            }
            .onFailure { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
    }

    fun selectDay(date: String) {
        _state.update { it.copy(selectedDay = date) }
        loadItemsForDay(date)
    }

    private fun loadItemsForDay(date: String) = viewModelScope.launch {
        runCatching { repository.getItemsForDay(date) }
            .onSuccess { items -> _state.update { it.copy(items = items) } }
            .onFailure { e -> _state.update { it.copy(error = e.message) } }
    }

    fun addItem(shopper: String, name: String, quantity: Float, price: Float) = viewModelScope.launch {
        val item = SpendingItem(
            date = _state.value.selectedDay,
            shopper = shopper,
            name = name,
            quantity = quantity,
            price = price
        )
        runCatching { repository.addItem(item) }
            .onSuccess { loadDays() }
            .onFailure { e -> _state.update { it.copy(error = e.message) } }
    }

    fun deleteItem(id: String) = viewModelScope.launch {
        runCatching { repository.deleteItem(id) }
            .onSuccess { loadItemsForDay(_state.value.selectedDay) }
    }
}
