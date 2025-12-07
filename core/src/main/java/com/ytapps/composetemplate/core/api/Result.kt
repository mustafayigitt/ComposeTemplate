package com.ytapps.composetemplate.core.api

/**
 * A sealed interface representing the result of an operation.
 * 
 * Benefits over sealed class:
 * - No nullable properties in the base type
 * - Type-safe access to data/error
 * - Loading state for UI
 * - Extension functions for convenient handling
 * 
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */
sealed interface Result<out T> {
    
    /**
     * Represents a successful result containing [data].
     */
    data class Success<T>(val data: T) : Result<T>
    
    /**
     * Represents a failed result containing an error [message] and optional [throwable].
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : Result<Nothing>
    
    /**
     * Represents a loading state.
     */
    data object Loading : Result<Nothing>
}

// Extension functions for Result handling

/**
 * Returns the data if this is [Result.Success], otherwise null.
 */
fun <T> Result<T>.getOrNull(): T? = when (this) {
    is Result.Success -> data
    is Result.Error -> null
    is Result.Loading -> null
}

/**
 * Returns the data if this is [Result.Success], otherwise [default].
 */
fun <T> Result<T>.getOrDefault(default: T): T = when (this) {
    is Result.Success -> data
    is Result.Error -> default
    is Result.Loading -> default
}

/**
 * Returns the data if this is [Result.Success], otherwise throws.
 */
fun <T> Result<T>.getOrThrow(): T = when (this) {
    is Result.Success -> data
    is Result.Error -> throw throwable ?: IllegalStateException(message)
    is Result.Loading -> throw IllegalStateException("Result is in Loading state")
}

/**
 * Returns the error message if this is [Result.Error], otherwise null.
 */
fun <T> Result<T>.errorOrNull(): String? = when (this) {
    is Result.Error -> message
    else -> null
}

/**
 * Returns true if this is [Result.Success].
 */
val <T> Result<T>.isSuccess: Boolean get() = this is Result.Success

/**
 * Returns true if this is [Result.Error].
 */
val <T> Result<T>.isError: Boolean get() = this is Result.Error

/**
 * Returns true if this is [Result.Loading].
 */
val <T> Result<T>.isLoading: Boolean get() = this is Result.Loading

/**
 * Transforms the data if this is [Result.Success].
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
    is Result.Loading -> this
}

/**
 * Executes [action] if this is [Result.Success].
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

/**
 * Executes [action] if this is [Result.Error].
 */
inline fun <T> Result<T>.onError(action: (String, Throwable?) -> Unit): Result<T> {
    if (this is Result.Error) action(message, throwable)
    return this
}

/**
 * Executes [action] if this is [Result.Loading].
 */
inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) action()
    return this
}

/**
 * Folds the result into a single value.
 */
inline fun <T, R> Result<T>.fold(
    onSuccess: (T) -> R,
    onError: (String, Throwable?) -> R,
    onLoading: () -> R
): R = when (this) {
    is Result.Success -> onSuccess(data)
    is Result.Error -> onError(message, throwable)
    is Result.Loading -> onLoading()
}
