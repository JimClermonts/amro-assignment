# INSTRUCTIONS FOR CLAUDE

## Process Guidelines

- Before you start and with each step, clarify first, ask questions and share your approach before initiating.
- Asking questions include challenging my proposal, architecturally, and library-wise.
- I've made a hello world Android project in the directory.
- When getting library versions, please get latest (=30 January, 2026).
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
- **Poster width**: 80dp (maintains 2:3 ratio with 120dp height).
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
- **App bar title**: "AMRO Movies" (use string resource)

### List Item Display
- **Multiple genres**: Limited to 6 (e.g., "Action, Adventure, Comedy, Drama, Romance, Sci-Fi"), ellipsize with "..." if too long for one line
- **Title truncation**: Truncate with ellipsis if too long (maxLines = 2)
- **Release year**: Show only year from release date

### Detail Screen
- There must be 1 detailview that is launched when clicking the item in the listview.
- **Navigation data**: Pass basic movie data (title, poster) for immediate display while loading full details.
- **Layout stability**: Views should have fixed width and height so that when still loading, the positioning of existing view elements (title, poster) doesn't change after done loading.
- When a user selects a movie from the list, they should be taken to a detail screen that displays more information about the movie.
- These are the items that should be shown: title, tagline, description, budget, link to imdb, image, vote average, revenue, runtime, genre, vote count, status, release date.
- **Budget/Revenue formatting**: Dutch formatting (€1.000.000) using `Locale.forLanguageTag("nl-NL")`
- **IMDB Link**: Opens in external browser
- **Runtime format**: "120 min"
- **Vote average display**: Numeric "7.5 / 10"
- **Back navigation**: App bar with back arrow
- **Loading indicator**: Linear progress bar at top (same as list)

### Screens General
- Each view (MoviesScreen + MovieDetailScreen) should be made using latest Jetpack Compose.
- Each screen must have @Preview @Composable to show how it looks.
- **All user-visible strings must be in strings.xml** (no hardcoded strings in Kotlin files)

### Loading & States
- **Loading indicator**: Linear progress bar at top
- **Empty state text**: "Geen films geladen" (use string resource)

### Theme & Appearance
- **Light mode only** (no dark mode support)
- **Material Design 3** with Reply app color style

---

## Error Handling
- Include basic error handling to manage scenarios like network issues or empty/error API responses.
- Responses should be communicated with a snackbar.
- The snackbar should have specific information per error.
- The snackbar should translate a network error to a user-friendly message in Dutch language.
- **Snackbar auto-dismiss**: 5 seconds (SnackbarDuration.Short)
- **Snackbar retry action**: No retry button
- When the app is used for the first time without internet, an error message should be shown.
- Repositories and viewmodels should not have context object.
- **ErrorHandler should be part of the UI layer** because it needs Context object to get the strings. Domain shouldn't have context references so that unit tests can run on any computer.
- If a user clicks a movie they haven't viewed before and there's no internet, show an error message.

### Dutch Error Messages (in strings.xml)
- **No internet**: "Geen internetverbinding"
- **Server error**: "Probleem aan onze kant, probeer het later opnieuw."
- **Unknown error**: "Er is iets misgegaan."
- **Movie not found**: "Film niet gevonden."

---

## Architecture

### Clean Architecture Layers
- The App should follow Uncle Bob's clean architecture.
- The layers should be: view, viewmodel (ui package), UseCase (domain package), Repository, DataSource (data package), and low level implementation stuff like httpClient (http package).
- **Single module with package-level separation** (no separate Gradle modules).
- DI modules should be in the directories per layer.

### Package Structure
```
nl.amro.android.app/
├── model/                 (All models - Result, DTOs - accessible by all layers)
├── di/                    (app-wide DI: dispatcher qualifiers)
├── http/                  (HttpClient configuration)
├── ui/
│   ├── di/
│   ├── movies/
│   │   ├── list/          (MoviesScreen, MoviesViewModel, MoviesUiState, MovieCard, FilterDropdown, SortDropdown)
│   │   └── detail/        (MovieDetailScreen, MovieDetailViewModel, MovieDetailUiState)
│   ├── util/              (error handling - ErrorHandler)
│   └── common/            (shared UI components: LoadingIndicator, EmptyState)
├── domain/                (usecases directly in domain package, no usecase subdirectory)
│   └── di/
├── data/                  (repository, datasources, entities, mappers)
│   ├── local/             (Room: AmroDatabase, DAOs, entities)
│   ├── remote/            (TmdbApi interface, TmdbApiService implementation)
│   ├── datasource/        (LocalDataSource, RemoteDataSource)
│   ├── repository/        (MoviesRepository interface, MoviesRepositoryImpl)
│   ├── mapper/            (MovieMapper)
│   └── di/
```

### Naming Conventions
- **UI State classes**: `MoviesUiState`, `MovieDetailUiState` (not `MoviesListUiState`)
- **ViewModel classes**: `MoviesViewModel`, `MovieDetailViewModel` (not `MoviesListViewModel`)
- **Screen composables**: `MoviesScreen`, `MovieDetailScreen`
- **DTO classes**: `MovieDto`, `MovieDetails` (not `MovieDetailsDto`)
- **Model files**: Separate files per model (not combined `TmdbModels.kt`)
- **API service**: `TmdbApi` (interface) + `TmdbApiService` (implementation)

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
Use a generic Result class to pass result/error through all layers (in model package):

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

---

## Technical Specifications

### Libraries & Versions (January 2026)
```toml
[versions]
agp = "9.0.0-rc02"
kotlin = "2.0.21"
ksp = "2.0.21-1.0.28"
coreKtx = "1.17.0"
lifecycleRuntimeKtx = "2.10.0"
activityCompose = "1.8.0"
composeBom = "2026.01.00"
hilt = "2.59"
hiltNavigationCompose = "1.2.0"
room = "2.8.4"
ktor = "3.4.0"
kotlinxSerialization = "1.10.0"
navigationCompose = "2.9.7"
coil = "3.3.0"
mockito = "5.15.2"
mockitoKotlin = "5.4.0"
coroutinesTest = "1.10.1"
```

### Required Dependencies
- **Jetpack Compose**: via BOM 2026.01.00
- **Material Icons Extended**: `androidx.compose.material:material-icons-extended`
- **Dependency Injection**: Hilt 2.59
- **Database**: Room 2.8.4
- **HTTP Client**: Ktor 3.4.0
- **Serialization**: Kotlinx.serialization 1.10.0
- **Image Loading**: Coil 3.3.0 (`coil-compose`, `coil-network-ktor3`)
- **Navigation**: Navigation Compose 2.9.7

### Build Configuration Notes
- Add `android.disallowKotlinSourceSets=false` to gradle.properties (KSP workaround for AGP 9.0)

### SDK Versions (keep existing)
- **minSdk**: 31 (Android 12)
- **targetSdk**: 36
- **compileSdk**: 36

### API Calls
- TMDB's trending endpoint returns 20 items per page.
- To get 100 movies, make **5 API calls in parallel** using `coroutineScope` with `async`.
- **Genres fetch timing**: Only when user opens filter for the first time.
- **API language**: English (en) - original English content

### Images
- **TMDB image size**: w500 (500px wide - large, sharp)
- **Image base URL**: https://image.tmdb.org/t/p/w500
- **Image loading**: Use Coil's `AsyncImage` composable

### Database Schema (Room)
Match the API response structure as much as possible:
- `movies` table (basic info for list: id, title, poster, genre_ids as comma-separated string, popularity, release_date)
- `movie_details` table (full details when user clicks, genres as JSON string)
- `genres` table (cached from genre list API)

---

## Unit Testing
- Unit tests should be provided for domain layer, data layer, util layer.
- These unit tests should not have references to Android packages.
- org.mockito should be used for mocking the classes not under test.
- Test files location: `app/src/test/java/nl/amro/android/app/`
- Use `TestData.kt` object for consistent test data across all tests

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
- Configure API key/Bearer token in HttpClientModule
- Implement 5 parallel API calls for 100 trending movies

### Step 2: Data Layer
- Create DTOs in model package (separate files: MovieDto, MovieDetails, Genre, etc.)
- Create Room entities in data/local/entity
- Create DAOs in data/local/dao
- Create AmroDatabase
- Create MovieMapper for DTO ↔ Entity conversions
- Create LocalDataSource and RemoteDataSource
- Create TmdbApi interface and TmdbApiService implementation
- Create MoviesRepository interface and MoviesRepositoryImpl

### Step 3: Domain Layer
- Create UseCase base classes (SuspendUseCase, FlowUseCase) directly in domain package
- Create all UseCases: GetTrendingMoviesUseCase, GetMovieDetailsUseCase, GetGenresUseCase, GetGenresByIdsUseCase, FilterMoviesUseCase, SortMoviesUseCase
- Define SortOption enum in SortMoviesUseCase file

### Step 4: Unit Tests
- Write tests for all UseCases
- Write tests for MoviesRepository
- Write tests for MovieMapper
- Use TestData.kt for consistent test data

### Step 5: ViewModels
- Create MoviesViewModel with StateFlow<MoviesUiState>
- Create MovieDetailViewModel with StateFlow<MovieDetailUiState>
- ViewModels call UseCases, not repositories directly

### Step 6: Theme
- Follow Material Design 3
- Create Color.kt with full Material 3 color palette
- Create Shape.kt with Material 3 shape scale
- Create Type.kt with complete typography scale
- Create AmroMoviesTheme in Theme.kt (light mode only)

### Step 7: List View
- Create common components: LoadingIndicator, EmptyState
- Create MovieCard component
- Create FilterDropdown component
- Create SortDropdown component
- Create MoviesScreen with all components

### Step 8: List Preview Screenshots
- Make the @Preview screenshots for list view (multiple states: loading, empty, filtered)

### Step 9: Navigation
- Set up NavHost with Navigation Compose
- Make items in the listview clickable
- Navigate to detail screen passing movieId, title, posterPath

### Step 10: Detail View
- Create MovieDetailScreen with all movie details
- Implement ErrorHandler for Dutch error messages

### Step 11: Detail Preview Screenshots
- Generate @Preview screenshots for the detail view (loading, success, error states)

---

## String Resources

All user-visible strings must be defined in `res/values/strings.xml`:

```xml
<resources>
    <string name="app_name">AMRO Movies</string>
    
    <!-- App bar -->
    <string name="app_bar_title">AMRO Movies</string>
    
    <!-- Empty states -->
    <string name="empty_movies">Geen films geladen</string>
    
    <!-- Loading -->
    <string name="loading_genres">Loading genres...</string>
    
    <!-- Filter -->
    <string name="filter">Filter</string>
    
    <!-- Sort options -->
    <string name="sort">Sort</string>
    <string name="sort_popularity_desc">Popularity (high to low)</string>
    <string name="sort_popularity_asc">Popularity (low to high)</string>
    <string name="sort_title_asc">Title (A-Z)</string>
    <string name="sort_title_desc">Title (Z-A)</string>
    <string name="sort_release_date_desc">Release date (newest)</string>
    <string name="sort_release_date_asc">Release date (oldest)</string>
    
    <!-- Detail screen -->
    <string name="navigate_back">Navigate back</string>
    <string name="movie_poster">Movie poster</string>
    <string name="budget_label">Budget</string>
    <string name="revenue_label">Revenue</string>
    <string name="runtime_format">%d min</string>
    <string name="vote_format">%s / 10</string>
    <string name="vote_count_format">(%d votes)</string>
    <string name="view_on_imdb">View on IMDB</string>
    
    <!-- Error messages (Dutch) -->
    <string name="error_no_internet">Geen internetverbinding</string>
    <string name="error_server">Probleem aan onze kant, probeer het later opnieuw.</string>
    <string name="error_unknown">Er is iets misgegaan.</string>
    <string name="error_movie_not_found">Film niet gevonden.</string>
</resources>
```

---

## Specifications Complete

All questions have been answered. Ready to implement!

### Summary of Key Decisions:
- **List**: Vertical cards, 120dp fixed height, 80dp poster width (2:3 ratio)
- **Navigation**: Pass basic movie data (id, title, posterPath) for immediate display
- **Detail layout**: Fixed dimensions for layout stability during loading
- **Filter/Sort**: Ephemeral (reset on app launch)
- **Images**: w500 size from TMDB via Coil AsyncImage
- **Language**: English API responses, Dutch error messages and UI text
- **Theme**: Light mode only, Material Design 3, Reply app style colors
- **Testing**: Mockito for unit tests, Compose Screenshot Testing for visual tests
- **Strings**: All user-visible strings externalized to strings.xml
- **Naming**: MoviesUiState (not MoviesListUiState), MovieDetails (not MovieDetailsDto)
- **Package structure**: UseCases directly in domain package, models in shared model package
