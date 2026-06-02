# AuraLife

Android movie streaming application built with **Clean Architecture** and **multi-module** structure.

## Screenshots

<p align="center">
  <img src="screenshots/home.jpg" width="200" alt="Home" />
  <img src="screenshots/film_details.jpg" width="200" alt="Film Details" />
  <img src="screenshots/player.jpg" width="200" alt="Player" />
  <img src="screenshots/library.jpg" width="200" alt="Library" />
</p>

<p align="center">
  <img src="screenshots/explore.jpg" width="200" alt="Explore" />
  <img src="screenshots/search.jpg" width="200" alt="Search" />
  <img src="screenshots/login.jpg" width="200" alt="Login" />
  <img src="screenshots/payment.jpg" width="200" alt="Payment" />
</p>

## Module structure

```
├── app/                          — DI graph, navigation host, Application class
├── domain/                       — Pure Kotlin, no platform dependencies
│   ├── model/                    — Domain entities (Film, FilmDetails, etc.)
│   ├── repository/               — Repository interfaces returning Result<T>
│   └── usecase/                  — Business use cases
├── data/                         — Repository implementations, datasources
│   ├── datasource/
│   │   ├── remote/api/           — FilmApiDataSource
│   │   └── local/                — FilmLocalDataSource
│   ├── repository/               — Concrete repository impls
│   └── di/RepositoryModule.kt    — Binds impls to interfaces
├── core/
│   ├── common/                   — Utils, validators, DispatcherProvider
│   ├── network/                  — Retrofit, OkHttp, FilmAPI, DTOs
│   ├── firebase/                 — Firebase Auth, Realtime DB data sources
│   ├── database/                 — Room DB, DAOs, entities
│   ├── navigation/               — NavRoutes, AppNavigator
│   └── designsystem/             — Shared resources, themes, custom views
└── feature/                      — Feature modules (each with own nav_graph)
    ├── splash/
    ├── onboarding/
    ├── auth/                     — Login + Register
    ├── home/                     — Banners + latest films
    ├── explore/                  — Categories + detail grid
    ├── film-detail/              — Film details page
    ├── film-player/              — ExoPlayer with premium throttle
    ├── library/                  — Library CRUD
    ├── history/                  — Watch history
    ├── search/                   — Debounced search
    └── payment/                  — Premium purchase
```

## Architecture principles

- **Domain layer** has zero Android/Retrofit/Firebase dependencies
- **Data layer** implements domain interfaces, uses datasource pattern (API + Local)
- **Feature modules** depend on `domain`, `core:designsystem`, `core:navigation`, `core:common`, `data`
- **App module** depends on all features + core, provides DI graph and NavHost
- All repository read methods return `Result<T>` (success/error/loading) from `domain/result/Result.kt`
- Navigation goes through `AppNavigator` (type-safe wrapper over NavController)
- Coroutine dispatchers are injected via `DispatcherProvider` (testable)
- Resources are centralized in `core:designsystem` with `android.nonTransitiveRClass=false`

## Navigation

Each feature owns its own `nav_graph.xml`. The main `nav_graph.xml` in `:app` uses `<include>`:

```xml
<navigation app:startDestination="@id/splash_nav_graph">
    <include app:graph="@navigation/splash_nav_graph" />
    <include app:graph="@navigation/home_nav_graph" />
    ...
</navigation>
```

## Dependency injection (Hilt)

| Module | Provides |
|--------|----------|
| `core:network/di/` | OkHttpClient, Retrofit, FilmAPI |
| `core:firebase/di/` | FirebaseDatabase |
| `core:database/di/` | Room DB, all DAOs |
| `core:common/di/` | DispatcherProvider |
| `data/di/` | Repository bindings (impl → interface) |

## ViewModels

| Feature | ViewModel | Responsibility |
|---------|-----------|----------------|
| splash | SplashViewModel | First-time check, delayed navigation |
| onboarding | OnboardingViewModel | First-time flow completion |
| auth | LoginViewModel / RegisterViewModel | Firebase Auth login/register |
| home | HomeViewModel | Banners + latest films with pagination |
| explore | ExploreViewModel | Categories + film rows |
| explore detail | ExploreDetailViewModel | Category film grid with pagination |
| film-detail | FilmDetailsViewModel | Film info, episodes, add to library |
| film-player | FilmPlayerViewModel | Premium throttle check |
| library | LibraryViewModel | Library CRUD operations |
| library detail | LibraryDetailsViewModel | Library films list |
| history | HistoryViewModel | Watch history |
| search | SearchViewModel | Debounced search |
| payment | PaymentViewModel | Premium status + purchase |
| app | MainViewModel | Auth state, avatar, premium status |

## Build and run

1. Open in Android Studio.
2. Create `secrets.properties` in project root:
   ```properties
   baseUrl=https://your-api-url.com
   ```
3. Build: `./gradlew assembleDebug`
4. Lint: `./gradlew ktlintCheck`
