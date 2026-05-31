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
└── presentation/      — Activities, Fragments, ViewModels, Adapters (per-screen packages)
    ├── auth/
    ├── common/
    ├── explore/
    ├── filmdetails/
    ├── history/
    ├── home/
    ├── library/
    ├── payment/
    ├── playfilm/
    └── start/
```

### Architecture principles
- `presentation` depends only on `domain` (interfaces + models), not on `data` implementations
- `domain` has no Retrofit, Firebase, or Android dependencies
- `data` implements domain interfaces and handles DTO ↔ domain mapping
- All dependencies wired via Hilt DI modules
- Each screen has its own ViewModel and Adapter — no shared mega-ViewModels
- UI state exposed via `StateFlow` and collected with `repeatOnLifecycle`

## Dependency Injection (Hilt)
- `@HiltAndroidApp` in `AuraLifeApplication`
- `@AndroidEntryPoint` on all Activities and Fragments
- `@HiltViewModel` on ViewModels with `@Inject constructor`
- `@Inject constructor` on use cases and repository implementations
- `@Inject field` injection for AuthRepository in Activities that need sync checks

### Hilt modules
- **NetworkModule** — provides OkHttpClient, Retrofit, FilmAPI
- **RepositoryModule** — binds all 7+ repository interfaces to their implementations

### Per-screen ViewModels
| Screen                | ViewModel                  | Responsibility                         |
|-----------------------|----------------------------|----------------------------------------|
| Home                  | `HomeViewModel`            | Banners + latest films with pagination |
| Explore               | `ExploreViewModel`         | Categories + category film rows        |
| Explore Detail        | `ExploreDetailViewModel`   | Category film grid with pagination     |
| Search (MainActivity) | `SearchViewModel`          | Debounced search results               |
| Film Details          | `FilmDetailsViewModel`     | Film detail + episode data             |
| Play Film             | (via FilmDetailsViewModel) | Playback + episode navigation          |
| Login                 | `AuthViewModel`            | Login/register                         |
| Library               | `LibraryViewModel`         | Library CRUD                           |
| History               | `HistoryViewModel`         | Watch history                          |
| Premium               | `PremiumViewModel`         | Premium status + purchase              |
| Main                  | `MainViewModel`            | Auth state, avatar, premium status     |

### Per-screen Adapters
Shared `FilmAdapter` replaced with dedicated adapters:
- `HomeFilmAdapter` — vertical cards in grid
- `ExploreFilmAdapter` / `CategoryFilmAdapter` — vertical cards in grid/horizontal row
- `SearchFilmAdapter` — horizontal cards for search results
- `LibraryFilmAdapter` — horizontal cards with long-press remove
- `HistoryFilmAdapter` — horizontal cards with long-press delete

## Build and run
1. Open the project in Android Studio.
2. Sync Gradle.
3. Use `./gradlew assembleDebug` to verify the code compiles.
