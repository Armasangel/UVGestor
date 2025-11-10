package com.uvg.uvgestor.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val guardians: List<Guardian> = emptyList(), // AGREGAR ESTA LÍNEA
    val isMinor: Boolean = false // AGREGAR ESTA LÍNEA - para identificar si requiere tutor
)