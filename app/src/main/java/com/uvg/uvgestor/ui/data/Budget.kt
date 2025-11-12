package com.uvg.uvgestor.ui.data

data class Budget(
    val id: Int = 0,
    val monthYear: String, // Formato: "2025-11" para noviembre 2025
    val limitAmount: Double,
    val category: String? = null, // null = presupuesto general, sino por categoría
    val alertAt80Percent: Boolean = true,
    val alertAt100Percent: Boolean = true
)