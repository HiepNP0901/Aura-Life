# AuraLife

## Project purpose
AuraLife is an Android application with current code organization that is not aligned with Clean Architecture. The project needs a refactor so that data access, business logic, and presentation are separated into proper layers.

## Current package structure
- `com.drs.auralife.core` : utilities and shared helpers
- `com.drs.auralife.data` : network, Firebase, models, and even ViewModel logic
- `com.drs.auralife.ui` : presentation components, but currently used as view layer and business logic combined

## Target architecture
Use Clean Architecture with the following top-level packages:
- `com.drs.auralife.core`
  - shared utilities, constants, extensions, platform helpers
- `com.drs.auralife.data`
  - `network` : Retrofit API definitions
  - `firebase` : Firebase helpers and data access
  - `repository` : concrete repository implementations
  - `model.remote` : API / Firebase DTOs
  - `model.local` : local persistence models if needed
  - `mapper` : remote/local -> domain mapping
- `com.drs.auralife.domain`
  - `model` : pure business entities, no Retrofit/Firebase imports
  - `repository` : repository interfaces used by domain and presentation
  - `usecase` : business use cases / interactor classes
- `com.drs.auralife.presentation`
  - Activities, Fragments, Adapters
  - ViewModels / presentation state
  - UI-only logic and state management

## Primary goals for refactor
1. Create a real `domain` layer before moving other code.
2. Move all business logic and use-case logic out of presentation (`presentation` package) and data access classes.
3. Ensure `presentation` only depends on the `domain` layer and interfaces, not on `data` implementations.
4. Move all concrete data access and mapping into `data.repository` and `data.mapper`.
5. Separate API/Firebase DTOs from domain entities.
6. Replace direct repository calls in Activities/Fragments with ViewModel + use case flows.

## Recommended migration plan
1. Create `app/src/main/java/com/drs/auralife/domain` with `model`, `repository`, and `usecase` packages.
2. Define domain entities for core business objects:
   - `Film`, `FilmDetails`, `Category`, `Library`, `HistoryItem`, `PremiumStatus`, etc.
3. Define repository interfaces in `domain.repository` for all data sources:
   - `FilmRepository`, `CategoryRepository`, `LibraryRepository`, `BannerRepository`, `HistoryRepository`, `PremiumRepository`, `AvatarRepository`.
4. Create use cases for actions that application code needs:
   - `GetLatestFilmsUseCase`, `SearchFilmsUseCase`, `GetFilmDetailsUseCase`, `GetCategoryListUseCase`, `AddFilmToLibraryUseCase`, etc.
5. Move `FilmAPI` to `data.network` and keep Retrofit client there.
6. Implement repository classes in `data.repository` that use `data.network` and `data.firebase`.
7. Create mappers in `data.mapper` to convert from remote DTOs to domain models.
8. Rename `ui` package to `presentation` and update package declarations.
9. Move `FilmsViewModel` and any other ViewModel into `presentation.viewmodel`.
10. Refactor existing Activities/Fragments to observe ViewModel state and execute use cases.

## Important quality checks
- No business rules inside `presentation`.
- No Retrofit/Firebase dependencies inside `domain`.
- No ViewModel inside `data`.
- Keep one source of truth per data concept through repository/use case layer.
- Avoid direct `DataSnapshot` logic or Firebase callbacks inside UI classes.

## Build and run
1. Open the project in Android Studio.
2. Sync Gradle.
3. Use `./gradlew assembleDebug` to verify the code compiles.

## Notes for the agent
- The focus is on project structure and dependency direction, not on feature changes.
- Start by establishing the `domain` layer and repository interfaces.
- Then rewire existing code gradually from `data` and `presentation` into the new clean layers.
- Do not remove features; preserve functionality while moving code.
