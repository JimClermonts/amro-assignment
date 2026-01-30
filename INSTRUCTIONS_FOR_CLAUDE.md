# INSTRUCTIONS FOR CLAUDE

## Process Guidelines

- Before you start and with each step, clarify first, ask questions and share your approach before initiating.
- Asking questions include challenging my proposal, architecturaly, and library-wise.
- I've made a hello world Android project in the directory.
- When getting library versions, please get latest (=30 january, 2026).
- Update this document with the result of our conversation so that this document can generate the correct code at once.

---

## Requirements

### List Screen
- There must be 1 listview.
- The listview must have 100 items.
- The 100 items are this week's top 100 trending movies.
- Each item has a title, image and genre.
- **Vertical list with cards** (Netflix style).
- **Card height**: Fixed 120dp.
- **Poster aspect ratio**: Maintain 2:3 ratio (full poster visible).
- The top right corner of the listview has a filter button. 
- The filter button has a list of genres that can be selected / unselected.
- **Filter/Sort UI**: Simple dropdown menu.
- **Genre filter display**: No movie count (just "Action", not "Action (15)").
- **Filter/Sort persistence**: Reset on each app launch (ephemeral).
- By default all genres are unselected.
- When all genres are unselected, everything is shown ("no filter applied = show all").
- When 1 or more genres are selected, only those genres are shown.
- The top right corner also has a sort button where we can sort based on: popularity, title, release date with descending or ascending.
- **Sort UI**: Combined dropdown with 6 options:
  - "Popularity (high to low)" - default
  - "Popularity (low to high)"
  - "Title (A-Z)"
  - "Title (Z-A)"
  - "Release date (newest)"
  - "Release date (oldest)"
- **Default sort**: Popularity (high to low) - matches API order.
- **No refresh mechanism** - only API call on screen loading.
- **App bar title**: "AMRO Movies"

### List Item Display
- **Multiple genres**: Limited to 2 (e.g., "Action, Adventure")
- **Title truncation**: Truncate with ellipsis if too long

### Detail Screen
- There must be 1 detailview that is launched when clicking the item in the listview.
- **Navigation data**: Pass basic movie data (title, poster) for immediate display while loading full details.
- **Layout stability**: Views should have fixed width and height so that when still loading, the positioning of existing view elements (title, poster) doesn't change after done loading.
- When a user selects a movie from the list, they should be taken to a detail screen that displays more information about the movie.
- These are the items that should be shown: title, tagline, description, budget, link to imdb, image, vote average, revenue, runtime, genre, vote count, status, release date.
- **Budget/Revenue formatting**: Dutch formatting (€1.000.000)
- **IMDB Link**: Opens in external browser
- **Runtime format**: "120 min"
- **Vote average display**: Numeric "7.5 / 10"
- **Back navigation**: App bar with back arrow
- **Loading indicator**: Linear progress bar at top (same as list)

### Screens General
- Each view (MoviesScreen + MovieDetail) should be made using latest Jetpack Compose.
- Each screen must have @Preview @Composable to show how it looks.

### Loading & States
- **Loading indicator**: Linear progress bar at top
- **Empty state text**: "Geen films geladen"

### Theme & Appearance
- **Light mode only** (no dark mode support)

---

## Error Handling
- Include basic error handling to manage scenarios like network issues or empty/error API responses.
- Responses should be communicated with a snackbar.
- The snackbar should have specific information per error.
- The snackbar should translate a network error to a user-friendly message in Dutch language.
- **Snackbar auto-dismiss**: 5 seconds
- **Snackbar retry action**: No retry button
- When the app is used for the first time without internet, an error message should be shown.
- Repositories and viewmodels should not have context object.
- **ErrorHandler should be part of the UI layer** because it needs Context object to get the strings. Domain shouldn't have context references so that unit tests can run on any computer.
- If a user clicks a movie they haven't viewed before and there's no internet, show an error message.

### Dutch Error Messages
- **No internet**: "Geen internetverbinding"
- **Server error**: "Probleem aan onze kant, probeer het later opnieuw."
- **Unknown error**: "Er is iets misgegaan."
- **Movie not found**: "Film niet gevonden."

---

## Architecture

### Clean Architecture Layers
- The App should follow Uncle Bob's clean architecture.
- The layers should be: view, viewmodel (ui package), UseCase (domain package), Repository, DataSource (data package), and low level implementation stuff like httpClient (util package).
- **Single module with package-level separation** (no separate Gradle modules).
- DI modules should be in the directories per layer.

### Package Structure
```
nl.amro.android.app/
├── model/                 (Result class - accessible by all layers)
├── di/                    (app-wide DI: dispatcher qualifiers)
├── http/                  (HttpClient configuration)
├── ui/
│   ├── di/
│   ├── movies/
│   │   ├── list/          (ui, viewmodel)
│   │   └── detail/        (ui, viewmodel)
│   ├── util/              (error handling - ErrorHandler)
│   └── common/            (shared UI components)
├── domain/                (usecases, domain models)
│   └── di/
├── data/                  (repository, datasources)
│   └── di/
```

### Data Flow
- There should be a MoviesRepository that gets fast data from the database, and slower but more recent data from the API.
- The data should be fetched using flows so that the data gets updated as soon as newer data is available.
- **Cache duration**: Always fetch fresh on app launch.
- When there is no internet, show the database.
- When an item is clicked, and the movie detail info is loaded, save into database.
- When an item is clicked, and the movie detail info is already present from database, load this first but fetch the API version directly after this.

### Dispatcher Qualifiers (Hilt)
Create these Hilt qualifiers for coroutine dispatchers:
- `@IoDispatcher` for IO operations
- `@MainDispatcher` for UI
- `@DefaultDispatcher` for CPU-intensive work

### Result Type
Use a generic Result class to pass result/error through all layers:

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

---

## Technical Specifications

### Libraries & Versions (latest = January 2026)
- **Jetpack Compose**: Latest January 2026 version
- **Dependency Injection**: Hilt
- **Database**: Room
- **HTTP Client**: io.ktor.client.HttpClient
- **Serialization**: Kotlinx.serialization
- **Image Loading**: AsyncImage (androidx.compose)
- **Navigation**: Navigation Compose

### SDK Versions (keep existing)
- **minSdk**: 31 (Android 12)
- **targetSdk**: 36
- **compileSdk**: 36

### API Calls
- TMDB's trending endpoint returns 20 items per page.
- To get 100 movies, make **5 API calls in parallel**.
- **Genres fetch timing**: Only when user opens filter for the first time.
- **API language**: English (en) - original English content

### Images
- **TMDB image size**: w500 (500px wide - large, sharp)
- **Image base URL**: https://image.tmdb.org/t/p/w500

### Database Schema (Room)
Match the API response structure as much as possible:
- `movies` table (basic info for list: id, title, poster, genre_ids, popularity, release_date)
- `movie_details` table (full details when user clicks)
- `genres` table (cached from genre list API)

---

## Unit Testing
- Unit tests should be provided for domain layer, data layer, util layer.
- These unit tests should not have references to Android packages.
- org.mockito should be used for mocking the classes not under test.

## Visual Testing
- Use Compose Preview Screenshot Testing to verify the screens don't change while developing.
- https://developer.android.com/studio/preview/compose-screenshot-testing
- These screenshot tests should be in a directory called screenshotTest

---

## API Resources

### TMDB Documentation
- Getting started: https://developer.themoviedb.org/docs/getting-started
- Trending movies: https://developer.themoviedb.org/reference/trending-movies
- Movie details: https://developer.themoviedb.org/reference/movie-details
- Genre list: https://developer.themoviedb.org/reference/genre-movie-list
- Image basics: https://developer.themoviedb.org/docs/image-basics
- Authentication: https://developer.themoviedb.org/docs/authentication-application

### API Credentials
- **API-leestoegangstoken (Bearer Token)**: eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJkYjNmNWMwNjNmNmY5ZmMzODQ0MWM2Yjg5ZWUxNzIwOSIsIm5iZiI6MTc2OTYwNDE2OS42Mywic3ViIjoiNjk3YTA0NDkxY2Q1MmIyYjVkZWYzYjVkIiwic2NvcGVzIjpbImFwaV9yZWFkIl0sInZlcnNpb24iOjF9.lQIY-kZByuFb97e68KP2Dxe9dlOSO1bKY_eS0hmLbsg
- **API-sleutel**: db3f5c063f6f9fc38441c6b89ee17209

---

## Implementation Approach

For each step, compile and run to verify workings.

### Step 1: Network Layer
- Add Ktor with Kotlinx.serialization
- Configure API key/Bearer token
- Implement 5 parallel API calls for 100 trending movies

### Step 2: Data Models
- Create data models matching API response structure
- Pass data to DataSource layer

### Step 3: Repository
Implement MoviesRepository following this pattern:
https://github.com/google/iosched/blob/main/shared/src/main/java/com/google/samples/apps/iosched/shared/data/ConferenceDataRepository.kt

### Step 4: UseCases
Define UseCases following UML specifications:
https://www.uml-diagrams.org/use-case-diagrams.html

Make sure every functionality of the app is covered in a UseCase, describing what the functionality does.

Use UseCase to get off main thread using CoroutineDispatcher:

```kotlin
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
```

Generic UseCase base class:

```kotlin
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
            Result.Error(e)
        }
    }

    /**
     * Override this to set the code to be executed.
     */
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): R
}
```

### Step 5: Unit Tests
Write all testcases for domain and data layer and prove the solution is ready for front-end development.

### Step 5.1: ViewModel
Create the viewModel and make sure it follows MVVM principles where the view can subscribe to a flow that sends updates, and calls UseCases for user inputs.

### Step 6: Theme
Follow Material Design 3:
https://developer.android.com/develop/ui/compose/designsystems/material3

In terms of color and look and feel, copy the Reply app:
https://github.com/android/compose-samples/tree/main/Reply

### Step 7: List View
Make the listview, compile and run to verify.

### Step 8: List Preview Screenshots
Make the @Preview screenshots for list view.

### Step 9: Navigation
Make the items in the listview clickable and launch the detailscreen using Navigation Compose.

### Step 10: Detail View
Fill in all details for the detailview.

### Step 11: Detail Preview Screenshots
Generate @Preview screenshots for the detail view.

---

## Specifications Complete

All questions have been answered. Ready to implement!

### Summary of Key Decisions:
- **List**: Vertical cards, 120dp fixed height, 2:3 poster ratio
- **Navigation**: Pass basic movie data for immediate display
- **Detail layout**: Fixed dimensions for layout stability during loading
- **Filter/Sort**: Ephemeral (reset on app launch)
- **Images**: w500 size from TMDB
- **Language**: English API responses, Dutch error messages
- **Theme**: Light mode only, Material Design 3, Reply app style
- **Testing**: Mockito for unit tests, Compose Screenshot Testing for visual tests

