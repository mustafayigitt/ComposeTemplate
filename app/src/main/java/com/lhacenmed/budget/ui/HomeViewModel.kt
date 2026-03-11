package com.lhacenmed.budget.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lhacenmed.budget.data.model.BudgetContribution
import com.lhacenmed.budget.data.model.SpendingItem
import com.lhacenmed.budget.data.repository.SpendingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
data class HomeUiState(
    val days: List<String> = emptyList(),
    val selectedDay: String = LocalDate.now().toString(),
    val items: List<SpendingItem> = emptyList(),
    val contributions: List<BudgetContribution> = emptyList(),
    val totalBudget: Float = 0f,
    val totalSpent: Float = 0f,
    val currentUserName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val remaining get() = totalBudget - totalSpent
}

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SpendingRepository,
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    init {
        loadCurrentUser()
        refresh()
    }

    private fun loadCurrentUser() {
        val user = supabase.auth.currentUserOrNull() ?: return
        val name = user.userMetadata
            ?.get("display_name")
            ?.jsonPrimitive?.content
            ?.takeIf { it.isNotBlank() }
            ?: user.email?.substringBefore("@")
            ?: "Me"
        _state.update { it.copy(currentUserName = name) }
    }

    // Load days, budget, and contributions in parallel
    fun refresh() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        runCatching {
            coroutineScope {
                val daysDeferred = async { repository.getDays() }
                val contributionsDeferred = async { repository.getContributions() }
                val allSpendingDeferred = async { repository.getAllSpending() }
                Triple(daysDeferred.await(), contributionsDeferred.await(), allSpendingDeferred.await())
            }
        }.onSuccess { (days, contributions, allSpending) ->
            val today = LocalDate.now().toString()
            val allDays = if (today in days) days else listOf(today) + days
            val totalBudget = contributions.sumOf { it.amount.toDouble() }.toFloat()
            val totalSpent = allSpending.sumOf { it.price.toDouble() }.toFloat()
            _state.update {
                it.copy(
                    days = allDays,
                    contributions = contributions,
                    totalBudget = totalBudget,
                    totalSpent = totalSpent,
                    isLoading = false
                )
            }
            loadItemsForDay(_state.value.selectedDay)
        }.onFailure { e ->
            _state.update { it.copy(error = e.message, isLoading = false) }
        }
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

    fun addItem(name: String, quantity: String, price: Float, description: String?) = viewModelScope.launch {
        val item = SpendingItem(
            date = _state.value.selectedDay,
            shopper = _state.value.currentUserName,
            name = name,
            quantity = quantity,
            price = price,
            description = description?.takeIf { it.isNotBlank() }
        )
        runCatching { repository.addItem(item) }
            .onSuccess { refresh() }
            .onFailure { e -> _state.update { it.copy(error = e.message) } }
    }

    fun addContribution(amount: Float) = viewModelScope.launch {
        val contribution = BudgetContribution(
            contributor = _state.value.currentUserName,
            amount = amount
        )
        runCatching { repository.addContribution(contribution) }
            .onSuccess { refresh() }
            .onFailure { e -> _state.update { it.copy(error = e.message) } }
    }

    fun deleteItem(id: String) = viewModelScope.launch {
        runCatching { repository.deleteItem(id) }
            .onSuccess { refresh() }
    }
}
