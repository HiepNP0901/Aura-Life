# Plan: UI Cleanup

## Current State

The project has Clean Architecture structure (`domain`/`data`/`presentation`) + Hilt DI, but the UI layer largely bypasses it:

- **14/28 presentation files** import Firebase singletons directly
- **8 files** use Firebase DTOs instead of domain models
- **Presentation imports `data.*` ~38 times vs `domain.*` ~14 times** (3:1 ratio)
- **Only `FilmsViewModel`** properly uses domain layer (use cases + domain models)

---

## Phase 1 — Migrate Firebase singletons → domain repository + use cases

### 1.1 Auth (Authentication singleton)

| File | Current | Target |
|---|---|---|
| `AuthViewModel` | `Authentication.login/register(this, ...)` — takes Context | Inject `AuthenticationUseCase` (no Context needed) |
| `LoginActivity` | `Authentication.isLoggedIn.postValue(true)`, `LibraryRepository.recheckLibrary()` | Call `authViewModel.onLoginSuccess()` |
| `MainActivity` | `Authentication.isLoggedIn()`, `.observe()`, `.getEmail()`, `.logout()` | Use `AuthViewModel` StateFlow |

**Steps:**
1. Create `LoginUseCase`, `RegisterUseCase`, `LogoutUseCase`, `GetAuthStateUseCase`;
2. `AuthViewModel` exposes `StateFlow<AuthState>` (loading/success/error);
3. Remove `context` parameter from AuthViewModel methods — move Firebase `Context` dependency to repository impl;
4. `LoginActivity`, `RegisterActivity` observe auth state, validation stays in UI for now.

### 1.2 Library (LibraryRepository singleton)

| File | Current | Target |
|---|---|---|
| `LibraryFragment` | `LibraryRepository.getLibrary{…}`, `Authentication.isLoggedIn()` | `LibraryViewModel` with `GetLibrariesUseCase` |
| `LibraryDetailsActivity` | `LibraryRepository.getLibraryData(…)`, `LibraryRepository.updatePosterUrl(…)` | `LibraryViewModel` with `GetLibraryUseCase`, `UpdatePosterUrlUseCase` |
| `AddToLibrary` (object) | `LibraryRepository.getLibrary/createLibrary/addLibraryData` | `AddToLibraryUseCase` — ViewModel call, UI only calls ViewModel |
| `EditLibrary` (object) | `LibraryRepository.renameLibrary/deleteLibrary/removeFilmFromLibrary` | `EditLibraryUseCase`, `DeleteLibraryUseCase`, `RemoveFilmUseCase` |

**Steps:**
1. Convert `AddToLibrary`, `EditLibrary` from static `object` → injectable classes or ViewModel methods;
2. Create `LibraryViewModel` with relevant use cases;
3. Remove direct `LibraryRepository` calls from all UI files.

### 1.3 History

| File | Current | Target |
|---|---|---|
| `HistoryFragment` | `HistoryRepository.getHistoryData{…}`, `Authentication.isLoggedIn()` | `HistoryViewModel` + `GetHistoryUseCase` |
| `DeleteHistory` (object) | `HistoryRepository.deleteHistory(slug)` | `DeleteHistoryUseCase` |
| `PlayFilmActivity` | `HistoryRepository.getHistoryData/addHistoryData` | `HistoryViewModel.addToHistory()` |

### 1.4 Banner, Category, Premium, Avatar

| File | Current | Target |
|---|---|---|
| `HomeFragment` | `BannerRepository.getBannerData{…}` | `HomeViewModel` + `GetBannersUseCase` |
| `ExploreFragment` | `CategoryRepository.getCategoryData{…}` | `ExploreViewModel` + `GetCategoriesUseCase` |
| `PaymentActivity` | `PremiumRepository.getPremiumStatus/uploadPremium` | `PremiumViewModel` + use cases |
| `MainActivity` | `AvatarRepository.getAvatar/uploadAvatar`, `PremiumRepository.getPremiumStatus` | `MainViewModel` + use cases |

---

## Phase 2 — Replace DTOs with domain models

| File | DTO | Domain model |
|---|---|---|
| `OnboardingActivity`, `OnboardingAdapter` | `data.model.OnboardingItem` | Move to `domain.model.OnboardingItem` or keep as local |
| `PaymentActivity`, `PaymentAdapter` | `data.model.PaymentItem` | Move to `domain.model.PaymentItem` |
| `LibraryDetailsActivity`, `LibraryAdapter` | `data.firebase…Library` | `domain.model.Library` |
| `HistoryFragment` | `data.firebase…History` | `domain.model.HistoryItem` |
| `ExploreDetailsActivity` | `data.firebase…Category` | `domain.model.Category` |

---

## Phase 3 — Move business logic out of UI

### Critical (runtime-impacting)
- **`PlayFilmActivity` lines 268-291**: Premium 5-minute throttle logic — move to `PremiumManager` use case or `PlaybackController` domain class
- **`HomeFragment` lines 87-108**: Banner auto-rotation → banner presenter helper
- **`HistoryFragment` lines 68-109**: Film list cross-referencing + time-difference → `HistoryViewModel`

### Validation
- **`LoginActivity`, `RegisterActivity`** validation orchestration → `Validator` is OK in core layer, but remove from activity into `AuthViewModel`
- **`EditLibrary`, `AddToLibrary`** inline validation → repository use cases

### Data transformation
- **`FilmDetailsActivity`** `getFilmDetailsMap()` → ViewModel computed state
- **`LibraryDetailsActivity`** sorting + `Film` construction → ViewModel

### Hardcoded data
- **`PaymentActivity`** pricing plans → `domain/model/PaymentPlan.kt` or repository
- **Vietnamese strings in code** → `strings.xml` resources

---

## Phase 4 — Restructure `presentation/` directory

### Current structure

```
presentation/
├── auth/              LoginActivity, RegisterActivity, LogoFragment, AuthViewModel
├── explore/           ExploreFragment, ExploreDetailsActivity
├── film/              FilmAdapter, FilmDetailsActivity, PlayFilmActivity (lẫn lộn)
│   ├── details/
│   └── play/
├── history/           HistoryFragment, DeleteHistory (utility)
├── home/              HomeFragment, BannerAdapter
├── library/           LibraryFragment, LibraryDetails, AddToLibrary (utility), EditLibrary (utility)
├── payment/           PaymentActivity, PaymentAdapter
├── start/             Splash, Onboarding, OnboardingAdapter
├── viewmodel/         FilmsViewModel (tách khỏi feature)
├── MainActivity.kt
├── NotificationAdapter.kt    (tràn root)
├── ViewPageAdapter.kt        (tràn root)
```

### Issues
1. **`viewmodel/`** — chỉ chứa 1 file, nên gộp vào `film/`
2. **Root-level files** — `ViewPageAdapter.kt` → `start/`, `NotificationAdapter.kt` → `common/`
3. **`FilmAdapter` dùng chung** — các feature khác import từ `film/`, nên giữ hoặc tách `film/adapter/`
4. **Utility objects** (`AddToLibrary`, `EditLibrary`, `DeleteHistory`) — sẽ biến mất sau P1, tạm thời giữ nguyên

### Target structure

```
presentation/
├── auth/              LoginActivity, RegisterActivity, LogoFragment, AuthViewModel
├── explore/           ExploreFragment, ExploreDetailsActivity
├── film/
│   ├── adapter/       FilmAdapter, EpisodeAdapter (dùng chung)
│   ├── details/       FilmDetailsActivity
│   ├── play/          PlayFilmActivity
│   └── FilmsViewModel.kt
├── history/           HistoryFragment
├── home/              HomeFragment, BannerAdapter
├── library/           LibraryFragment, LibraryDetailsActivity, LibraryAdapter
├── payment/           PaymentActivity, PaymentAdapter
├── start/             SplashActivity, OnboardingActivity, OnboardingAdapter, ViewPageAdapter
├── common/            NotificationAdapter, base classes
└── MainActivity.kt
```

### Steps
1. Xoá `viewmodel/`, move `FilmsViewModel.kt` → `film/`
2. Move `ViewPageAdapter.kt` → `start/`
3. Move `NotificationAdapter.kt` → `common/` (tạo mới)
4. Tạo `film/adapter/`, move `FilmAdapter.kt` + `EpisodeAdapter.kt` vào
5. Cập nhật imports ở tất cả file bị ảnh hưởng

---

## Effort estimate

| Phase | Tasks | Files affected | Est. effort |
|---|---|---|---|
| **P1.1 Auth** | Use cases, AuthViewModel state, remove Context | 4 | 1-2 days |
| **P1.2 Library** | Use cases, LibraryViewModel, convert static objects | 6 | 2-3 days |
| **P1.3 History** | Use cases, HistoryViewModel | 4 | 1 day |
| **P1.4 Others** | Banner, Category, Premium, Avatar use cases | 6 | 2-3 days |
| **P2 DTOs** | Replace with domain models | 8 | 0.5 day |
| **P3 Logic** | Move business logic to use cases / ViewModels | 10+ | 3-5 days |
| **P4 Structure** | Restructure presentation/ directory | ~15 | 0.5 day |
| **Total** | | | **~10-15 days** |

---

## Non-goals (out of scope)
- Feature changes or UI redesign
- Migration to Jetpack Compose
- Module separation (prepared by this cleanup, but done separately)
- Adding tests (though cleanup enables it)

## First actionable step
1. Create `domain/usecase/LoginUseCase.kt` and `domain/usecase/RegisterUseCase.kt`
2. Refactor `AuthViewModel` to use them and expose `StateFlow<AuthState>`
3. Update `LoginActivity`/`RegisterActivity` to observe the new state
