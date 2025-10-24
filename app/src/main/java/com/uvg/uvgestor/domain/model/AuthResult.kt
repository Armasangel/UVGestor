package com.uvg.uvgestor.domain.model

/**
 * Sealed class representing authentication results
 * Used for handling different authentication states
 */
sealed class AuthResult {
    /**
     * Authentication was successful
     * @param user The authenticated user
     */
    data class Success(val user: User) : AuthResult()
    
    /**
     * Authentication failed with an error
     * @param message Error message describing what went wrong
     */
    data class Error(val message: String) : AuthResult()
    
    /**
     * Authentication is in progress
     */
    object Loading : AuthResult()
}
