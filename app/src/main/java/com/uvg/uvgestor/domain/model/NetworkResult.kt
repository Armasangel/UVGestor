package com.uvg.uvgestor.domain.model


sealed class NetworkResult<out T> {
    /**
     * @param data
     */
    data class Success<T>(val data: T) : NetworkResult<T>()

    /**
     * @param message
     * @param throwable
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : NetworkResult<Nothing>()


    object Loading : NetworkResult<Nothing>()
}
