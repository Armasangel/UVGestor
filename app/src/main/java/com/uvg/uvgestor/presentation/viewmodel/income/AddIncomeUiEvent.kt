package com.uvg.uvgestor.presentation.viewmodel.income

sealed class AddIncomeUiEvent {
    data class TitleChanged(val title: String) : AddIncomeUiEvent()
    data class AmountChanged(val amount: String) : AddIncomeUiEvent()
    data class TimePeriodChanged(val period: String) : AddIncomeUiEvent()
    data class CategoryChanged(val category: String) : AddIncomeUiEvent()
    object SaveClicked : AddIncomeUiEvent()
    object ConfirmSave : AddIncomeUiEvent()
    object CancelSave : AddIncomeUiEvent()
    object ErrorDismissed : AddIncomeUiEvent()
    object SaveSuccessHandled : AddIncomeUiEvent()
}