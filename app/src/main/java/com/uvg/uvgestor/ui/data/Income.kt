package com.uvg.uvgestor.ui.data

data class Income(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val timePeriod: String, // Diario, Semanal, Mensual, Anual
    val category: String, // Salario, Beca, Trabajo Freelance, Otros
    val date: String
)