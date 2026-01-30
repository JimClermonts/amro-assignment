package nl.amro.android.app.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import nl.amro.android.app.model.Result

/**
 * Executes business logic synchronously or asynchronously using Coroutines.
 *
 * The [execute] method of [SuspendUseCase] is a suspend function as opposed to the
 * [UseCase.execute] method of [UseCase].
 */
abstract class SuspendUseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {

    /**
     * Executes the use case asynchronously and returns a [Result].
     *
     * @param parameters the input parameters to run the use case with
     * @return a [Result].
     */
    suspend operator fun invoke(parameters: P): Result<R> {
        return try {
            // Moving all use case's executions to the injected dispatcher
            // In production code, this is usually the Default dispatcher (background thread)
            // In tests, this becomes a TestCoroutineDispatcher
            withContext(coroutineDispatcher) {
                execute(parameters).let {
                    Result.Success(it)
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Override this to set the code to be executed.
     */
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): R
}

/**
 * Executes business logic that returns a Flow.
 * Use this for observing data streams from the repository.
 */
abstract class FlowUseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {

    /**
     * Executes the use case and returns a Flow of [Result].
     *
     * @param parameters the input parameters to run the use case with
     * @return a Flow of [Result].
     */
    operator fun invoke(parameters: P): Flow<Result<R>> {
        return execute(parameters)
            .catch { e -> emit(Result.Error(e as Exception)) }
            .flowOn(coroutineDispatcher)
    }

    /**
     * Override this to set the code to be executed.
     */
    protected abstract fun execute(parameters: P): Flow<Result<R>>
}

/**
 * Marker class for use cases that don't need parameters.
 */
object NoParams
