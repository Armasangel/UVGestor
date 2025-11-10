package com.uvg.uvgestor.domain.model


data class Guardian(
    val id: String,
    val name: String,
    val email: String,
    val relationship: String, // "Padre", "Madre", "Tutor"
    val hasAccessToFinances: Boolean = true
)