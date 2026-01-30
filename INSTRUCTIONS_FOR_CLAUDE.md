

- I've made a hello world Android project in the directory.


Requirements.

- There must be 1 listview.
- There must be 1 detailview that is launched when clicking the item in the listview.
- The listview must have 100 items.
- the 100 items are this week's top 100 trending movies.
- Each item has a title, image and genre.
- The top right corner of the listview has a filter button. 
- The filter button has a list of genres that can be selected / unselected.
- By default all genres are unselected.
- When all genres are unselected, everything is shown.
- When 1 or more genres are selected, only those genres are shown.
- The top right corner also has a sort button where we can sort based on: popularity, title, release date with descending or ascending.

Detail screen
- When a user selects a movie from the list, they should be taken to a detail screen that
displays more information about the movie.
- These are the items that should be shown: title, tagline, description, budget, link to imdb, image, vote average, revenue, runtime, genre, vote count, status, release date.

Screens
- Each view (MoviesScreen + MovieDetail) should be made using latest Jetpack Compose.
- Each screen must have @Preview @Composable to show how it looks.

Error handling
- Include basic error handling to manage scenarios like network issues or empty/error
API responses. Respnses should be communicated with a snackbar. 
- The snackbar should have specific information per error.
- The snackbar should translate a network error to a user-friendly message in Dutch language.
- When the app is used for the first time without internet, an error message should be shown.
- Repositories and viewmodels should not have context object. Create an injectable ErrorHandler to translate error messages.

Architecture
- The App should follow Uncle Bob's clean architecture.
- The layers should be: view, viewmodel (ui package), UseCase (domain package), Repository, DataSource (data package), and low level implementation stuff like httpClient (util package).
- The directory structure of the source files should be feature driven. 1 feature can be detail and 1 package can be list. 
- There should be a MoviesRepository that gets fast data from the database, and slower but more recent data from the API. 
- The data should be fetched using flows so that the data gets updated as soon as newer data is available.
- Use a generic Success / Error object to pass data through layers from HttpClient to view.
- When there is no internet, show the database.
- When an item is clicked, and the movie detail info is loaded, save into database.
- When an item is clicked, and the movie detail info is already present from database, load this first but fetch the API version directly after this.

Unit testing
- unit tests should be provided for domain layer, data layer, util layer.
- these unit tests should not have references to Android packages.
- org.mockito should be used for mocking the classes not under test.

Visual testing
- Use Compose Preview Screenshot Testing to verify the screens don't change while developing.
https://developer.android.com/studio/preview/compose-screenshot-testing
- These screenshot tests should be in a directory called screenshotTest

Specifications
- Use latest Jetpack compose libraries (latest = january 2026)
- Use Hilt for dependency injection.
- Use Room for storage of the data.
- Use io.ktor.client.HttpClient for api calls

Resources
https://developer.themoviedb.org/docs/getting-started

Get 100 trending movies
https://developer.themoviedb.org/reference/trending-movies

Get Movie details
https://developer.themoviedb.org/reference/movie-details

Get genres
https://developer.themoviedb.org/reference/genre-movie-list

Get images 
https://developer.themoviedb.org/docs/image-basics

https://developer.themoviedb.org/docs/authentication-application

API-leestoegangstoken
eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJkYjNmNWMwNjNmNmY5ZmMzODQ0MWM2Yjg5ZWUxNzIwOSIsIm5iZiI6MTc2OTYwNDE2OS42Mywic3ViIjoiNjk3YTA0NDkxY2Q1MmIyYjVkZWYzYjVkIiwic2NvcGVzIjpbImFwaV9yZWFkIl0sInZlcnNpb24iOjF9.lQIY-kZByuFb97e68KP2Dxe9dlOSO1bKY_eS0hmLbsg

API-sleutel
db3f5c063f6f9fc38441c6b89ee17209


Optional
- Can make separate modules for domain, data, model to enforce the dependencies inwards.

Approach
For each step, compile and run to verify workings.

1. Start with the network layer: add ktor, api key and do the 100 trending movies call.
2. Make models out of the result and pass this to the DataSource layer. 
3. Add the MoviesRepository and implement it like this example:

https://github.com/google/iosched/blob/main/shared/src/main/java/com/google/samples/apps/iosched/shared/data/ConferenceDataRepository.kt

4. Define UseCases following latest UML specifications:

https://www.uml-diagrams.org/use-case-diagrams.html

Make sure every functionality of the app is covered in a UseCase, describing what the functionality does.

Also use UseCase to get of main thread using CoroutineDispatcher:

class AddNewsItemUseCase @Inject constructor(
    private val repository: VersesRepository,
    private val analytics: Analytics,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : SuspendUseCase<NewsItem, Unit>(ioDispatcher) {

    override suspend fun execute(parameters: NewsItem) {
        val result = repository.addNewsItem(parameters)
        if (result is Error) {
            analytics.recordException(result.exception)
        }
    }
}

Use this example for Generifying UseCases:



/**
 * Executes business logic synchronously or asynchronously using Coroutines.
 *
 * The [execute] method of [SuspendUseCase] is a suspend function as opposed to the
 * [UseCase.execute] method of [UseCase].
 */
abstract class SuspendUseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {

    /** Executes the use case asynchronously and returns a [Result].
     *
     * @return a [Result].
     *
     * @param parameters the input parameters to run the use case with
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
            L.d(SuspendUseCase::class.java.name, e.toString(), e)
            Result.Error(e)
        }
    }

    /**
     * Override this to set the code to be executed.
     */
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): R
}

5. Now write all testcases for domain and data layer and proof the solution is ready for front-end development.

5.1 Create the viewModel and make sure it follows MVVM principles where the view can subscribe to a flow that sends updates, and calls UseCases for user inputs.

6. Create the theme. Follow Material Design 3

https://developer.android.com/develop/ui/compose/designsystems/material3

in terms of color and look and feel, copy the Reply app:

https://github.com/android/compose-samples/tree/main/Reply

7. Now make the listview, compile and run to verify. 

8. Now make the @Preview screenshots.

9. Make the items in the listview clickable and launch the detailscreen.

10. fill in all details for the detailview.

11. Generate @Preview screenshots for the detail view.


