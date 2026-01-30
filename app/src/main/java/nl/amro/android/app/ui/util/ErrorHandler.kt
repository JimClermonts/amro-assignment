package nl.amro.android.app.ui.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import nl.amro.android.app.R
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles error translation to user-friendly Dutch messages.
 * Part of the UI layer because it needs Context for string resources.
 */
@Singleton
class ErrorHandler @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    /**
     * Converts an exception to a user-friendly Dutch error message.
     */
    fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is UnknownHostException,
            is SocketTimeoutException -> context.getString(R.string.error_no_internet)
            
            is IOException -> context.getString(R.string.error_no_internet)
            
            is MovieNotFoundException -> context.getString(R.string.error_movie_not_found)
            
            is ServerException -> context.getString(R.string.error_server)
            
            else -> context.getString(R.string.error_unknown)
        }
    }
}

/**
 * Exception thrown when a movie is not found (404).
 */
class MovieNotFoundException(message: String? = null) : Exception(message)

/**
 * Exception thrown for server errors (5xx).
 */
class ServerException(message: String? = null) : Exception(message)
