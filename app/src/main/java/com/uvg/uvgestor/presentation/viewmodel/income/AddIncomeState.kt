package com.uvg.uvgestor.presentation.viewmodel.income

data class AddIncomeUiState(
    val title: String = "",
    val amount: String = "",
    val selectedTimePeriod: String = "",
    val selectedCategory: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val showConfirmationDialog: Boolean = false,
    val currentUserId: String? = null
)