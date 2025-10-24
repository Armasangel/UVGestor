package com.uvg.uvgestor.domain.model

/**
 * Domain model representing a user in the system
 * Contains core user information
 * 
 * @property id Unique identifier for the user
 * @property email User's email address
 * @property name User's full name
 * @property createdAt Timestamp when the user was created (milliseconds)
 */
data class User(
    val id: String,
    val email: String,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)
