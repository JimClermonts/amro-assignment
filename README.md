# AMRO Movies

An Android movie browsing application that displays the top 100 trending movies of the week using The Movie Database (TMDB) API.

## Features

- **Movie List**: Browse 100 trending movies in a Netflix-style vertical card layout
- **Filtering**: Filter movies by genre (multiple selection supported)
- **Sorting**: Sort by popularity, title, or release date (ascending/descending)
- **Movie Details**: View comprehensive movie information including:
  - Title and tagline
  - Overview/description
  - Budget and revenue (Dutch formatting: €1.000.000)
  - Runtime, vote average, vote count
  - Genres and release date
  - Direct link to IMDB
- **Offline Support**: Cached movies available when offline
- **Dutch Language**: Error messages displayed in Dutch

## Architecture

This app follows **Uncle Bob's Clean Architecture** with the following layers:

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer                           │
│  (Compose Screens, ViewModels, UI State)               │
├─────────────────────────────────────────────────────────┤
│                    Domain Layer                         │
│  (UseCases - Business Logic)                           │
├─────────────────────────────────────────────────────────┤
│                     Data Layer                          │
│  (Repository, DataSources, Entities, Mappers)          │
└─────────────────────────────────────────────────────────┘
```

### Package Structure

```
nl.amro.android.app/
├── model/          # Shared models (Result, DTOs)
├── di/             # App-wide DI (dispatcher qualifiers)
├── http/           # HttpClient configuration
├── ui/
│   ├── movies/
│   │   ├── list/   # Movies list screen
│   │   └── detail/ # Movie detail screen
│   ├── common/     # Shared UI components
│   └── theme/      # Material Design 3 theme
├── domain/         # UseCases
└── data/
    ├── local/      # Room database
    ├── remote/     # TMDB API service
    ├── repository/ # Data repository
    └── mapper/     # Entity/DTO mappers
```

## Tech Stack

| Category | Technology |
|----------|------------|
| UI | Jetpack Compose with Material Design 3 |
| Architecture | MVVM + Clean Architecture |
| Dependency Injection | Hilt |
| Networking | Ktor Client |
| Serialization | Kotlinx.serialization |
| Database | Room |
| Image Loading | Coil |
| Navigation | Navigation Compose |
| Async | Kotlin Coroutines + Flow |

## Requirements

- **Minimum SDK**: 31 (Android 12)
- **Target SDK**: 36
- **Compile SDK**: 36

## Building the Project

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on device or emulator (Android 12+)

```bash
./gradlew assembleDebug
```

## Running Tests

```bash
# Unit tests
./gradlew test

# All tests
./gradlew check
```

## API

This app uses [The Movie Database (TMDB) API](https://developer.themoviedb.org/docs/getting-started) for movie data.

### Endpoints Used

- **Trending Movies**: `/trending/movie/week` (5 parallel calls for 100 movies)
- **Movie Details**: `/movie/{movie_id}`
- **Genre List**: `/genre/movie/list`

## Design Decisions

- **Offline-first**: Shows cached data immediately, then updates with fresh API data
- **Light mode only**: Simplified theme following Material Design 3
- **Dutch error messages**: User-friendly error handling in Dutch
- **No refresh mechanism**: Data fetched on app launch only
- **Ephemeral filter/sort**: Settings reset on each app launch

## Screenshots

The app includes Compose Preview screenshots for visual testing. Run screenshot tests with:

```bash
./gradlew validateScreenshotTest
```

## License

This project is part of a coding assignment for AMRO.
