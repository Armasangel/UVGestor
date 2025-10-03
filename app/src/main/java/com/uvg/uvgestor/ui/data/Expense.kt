package com.uvg.uvgestor.ui.data

data class Expense(
    val id: Int,
    val title: String,
    val amount: Double,
    val timePeriod: String, // Diario, Semanal, Mensual, Anual
    val category: String, // Comida, Transporte, Ocio
    val date: String
)