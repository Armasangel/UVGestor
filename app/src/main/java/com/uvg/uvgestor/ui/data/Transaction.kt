package com.uvg.uvgestor.ui.data

data class Transaction(
    val id: Int,
    val title: String,
    val amount: Double,
    val type: String, // ingreso o gasto
    val date: String
)