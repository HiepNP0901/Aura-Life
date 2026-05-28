# AuraLife

## Project purpose
AuraLife is an Android application that provides movie streaming services.

## Current architecture
The project follows **Clean Architecture** with dependency injection managed by **Hilt**.

### Package structure
```
com.drs.auralife
├── core/              — utilities, constants, helpers
├── data/              — network, Firebase, DTOs, mappers, repository implementations
│   ├── di/            — Hilt modules (NetworkModule, RepositoryModule)
│   ├── firebase/      — Firebase helpers and data access
│   ├── mapper/        — DTO ↔ domain mapping (FilmMapper, FirebaseMapper)
│   ├── model/         — DTOs for API and Firebase
│   └── repository/    — concrete repository implementations
├── domain/            — business logic layer (no platform dependencies)
│   ├── model/         — pure domain entities (Film, FilmDetails, PagedResult, etc.)
│   ├── repository/    — repository interfaces
│   └── usecase/       — business use cases
└── presentation/      — Activities, Fragments, ViewModels, Adapters
    └── viewmodel/     — shared ViewModels (FilmsViewModel, AuthViewModel)
```

### Architecture principles
- `presentation` depends only on `domain` (interfaces + models), not on `data` implementations
- `domain` has no Retrofit, Firebase, or Android dependencies
- `data` implements domain interfaces and handles DTO ↔ domain mapping
- All dependencies wired via Hilt DI modules

## Dependency Injection (Hilt)
- `@HiltAndroidApp` in `AuraLifeApplication`
- `@AndroidEntryPoint` on all Activities and Fragments
- `@HiltViewModel` on ViewModels with `@Inject constructor`
- `@Inject constructor` on use cases and repository implementations

### Hilt modules
- **NetworkModule** — provides OkHttpClient, Retrofit, FilmAPI
- **RepositoryModule** — binds all 7 repository interfaces to their implementations

### Migrated components
- **FilmsViewModel** — StateFlow-based reactive state for latest films, category films, search, film details
- **FilmAdapter** — uses `domain.model.Film` instead of `data.model.film.Movie`
- **EpisodeAdapter** — uses `domain.model.FilmEpisode` instead of `data.model.film.ServerData`
- **FilmDetailsActivity**, **PlayFilmActivity** — observe `filmDetailsState`
- **MainActivity** (search) — observe `searchResultsState` with debounce
- **ExploreDetailsActivity** — observe `categoryFilmsState` + `loadMoreFilmsByCategory`
- **HomeFragment** — observe `latestFilmsState` + `loadMoreLatestFilms`
- **LibraryDetailsActivity**, **HistoryFragment** — use `fetchFilmDetails` (domain callback)
- **ExploreFragment** — use `fetchFilmsByCategory` (domain callback)
- **LoginActivity**, **RegisterActivity** — use `by viewModels()` for `AuthViewModel`

### Domain models added
- `Film`, `FilmDetails`, `FilmEpisode` — enriched domain entities
- `PagedResult<T>` — generic paginated result
- `Category`, `HistoryItem`, `Library`, `PremiumStatus` — domain counterparts of Firebase DTOs

## Still to do (known gaps)
- UI files still call Firebase singleton objects directly (Authentication, Firebase repos) — planned for future migration to domain repository layer
- `UpdateLibraryWorker` creates its own Retrofit/OkHttpClient inline (not using Hilt injection)
- Verify runtime behavior (CDN prefix, episode playback, pagination) with device/emulator testing

## Build and run
1. Open the project in Android Studio.
2. Sync Gradle.
3. Use `./gradlew assembleDebug` to verify the code compiles.
