package com.example.classcash.viewmodels.event

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BudgetViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _expensesList = MutableLiveData<Map<String, Boolean>>()
    val expensesList: LiveData<Map<String, Boolean>> get() = _expensesList

    fun fetchExpensesList(eventId: Int) {
        viewModelScope.launch {
            try {
                val event = eventRepository.getEventById(eventId) // Calling the suspend function
                _expensesList.value = event?.expenses ?: generateDefaultExpensesList()
            } catch (e: Exception) {
                Log.e("BudgetViewModel", "Error fetching expenses list", e)
                _expensesList.value = generateDefaultExpensesList()
            }
        }
    }

    fun computeBudget(event: Event, classBalance: Double): Map<String, Double> {
        val effectiveBalance = classBalance * 0.8 // Use 80% of the class balance
        val predefined = event.expenses.values.any { it }
        val budgetDistribution = mutableMapOf<String, Double>()

        if (predefined) {
            val foodBudget = effectiveBalance * 0.5
            val soundBudget = effectiveBalance * 0.1
            val giftBudget = effectiveBalance * 0.1
            val remainingPercent = 1.0 - (0.5 + 0.1 + 0.1)
            val remainingBudget = effectiveBalance * remainingPercent

            for ((expenseName, isPredefined) in event.expenses) {
                budgetDistribution[expenseName] = when {
                    expenseName.contains("food", ignoreCase = true) -> foodBudget
                    expenseName.contains("sound", ignoreCase = true) -> soundBudget
                    expenseName.contains("gift", ignoreCase = true) -> giftBudget
                    else -> if (isPredefined) remainingBudget / (event.expenses.size - 3) else 0.0
                }
            }
        } else {
            // Default generation
            val foodBudget = effectiveBalance * 0.5
            val otherExpensesBudget = effectiveBalance * 0.2
            val miscBudget = effectiveBalance * 0.1

            budgetDistribution["Food"] = foodBudget
            budgetDistribution["Other"] = otherExpensesBudget
            budgetDistribution["Misc"] = miscBudget
        }

        return budgetDistribution
    }

    fun regenerateBudget(event: Event, classBalance: Double): Map<String, Double> {
        val effectiveBalance = classBalance * 0.8
        val foodBudget = effectiveBalance * (0.4 + Math.random() * 0.15) // Random 40%-55%
        val otherExpensesBudget = effectiveBalance * (0.1 + Math.random() * 0.15) // Random 10%-25%
        val miscBudget = effectiveBalance * (0.01 + Math.random() * 0.14) // Random 1%-15%

        return mapOf(
            "Food" to foodBudget,
            "Other" to otherExpensesBudget,
            "Misc" to miscBudget
        )
    }

    fun deleteBudget(eventId: Int) {
        viewModelScope.launch {
            try {
                val result = eventRepository.deleteEventBudget(eventId) // Calling suspend function
                if (!result) {
                    Log.e("BudgetViewModel", "Failed to delete budget for event ID: $eventId")
                }
            } catch (e: Exception) {
                Log.e("BudgetViewModel", "Error deleting budget for event ID: $eventId", e)
            }
        }
    }

    fun fetchBudgetDetails(eventId: Int, onResult: (Pair<Double, Map<String, Double>>) -> Unit) {
        viewModelScope.launch {
            try {
                val event = eventRepository.getEventById(eventId) // Calling the suspend function
                val budgetDetails = event?.budget ?: Pair(0.0, emptyMap())
                onResult(budgetDetails)
            } catch (e: Exception) {
                Log.e("BudgetViewModel", "Error fetching budget details for event ID: $eventId", e)
                onResult(Pair(0.0, emptyMap()))
            }
        }
    }

    private fun generateDefaultExpensesList(): Map<String, Boolean> {
        return mapOf(
            "Food" to false,
            "Other" to false,
            "Misc" to false
        )
    }
}



