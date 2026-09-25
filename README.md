# SuperCocktails

An Android app for searching and browsing cocktails, built with Kotlin Multiplatform. Pulls data from the CocktailDB API, caches results locally with Room, and follows a strict clean architecture — data, domain, and presentation layers each depend only on the one below.

## Features

- **Recommended cocktails** — a set of 10 cocktails assembled on first launch from random seed terms, cached, and shown by default before any search is typed
- **Search** — reactive, debounced search by name; `flatMapLatest` ensures a slow stale response can never overwrite a newer query's results
- **Offline-first** — all screens read from Room; background network refresh updates the cache without blocking the UI
- **Favorites** — star/unstar any cocktail from the list or detail screen; persisted as a boolean column in Room, never lost on background re-fetch
- **Recent searches** — last 5 search terms saved in DataStore, shown when the search bar is focused; individually deletable
- **Dark / light / system theme** — switchable at runtime, preference persisted in DataStore
- **Detail screen** — full cocktail info: hero image, category/glass/alcoholic tags, ingredients with measures, instructions, and last-updated date

## Tech stack

| Library | Role |
|---|---|
| **Ktor** | HTTP client for all API calls |
| **Room** | Local SQLite cache; all queries return `Flow` for reactive UI updates |
| **DataStore** (Preferences) | Persistent key-value storage for recent searches and theme preference |
| **Koin** | Dependency injection across both modules |
| **Coil** | Async image loading in Compose |
| **Kotlin Coroutines + Flow** | Async work throughout; `flatMapLatest`, `debounce`, `combine`, `StateFlow` |
| **Jetpack Compose** | Declarative UI; all screens are stateless content composables with `@Preview` |
| **kotlinx.serialization** | JSON parsing for API responses and DataStore encoding |
| **Turbine** | Flow testing; used for asserting one-shot `Channel` events in unit tests |

## Architecture

The project follows MVVM with three strict layers. Each layer depends only on the one below — nothing in Presentation touches the repository directly, and nothing in Data knows the UI exists.

```
Data → Domain → Presentation
```

**Data layer** (`shared/data/`) — Room DAOs and database, Ktor API client and DTOs, `CocktailRepositoryImpl`. Owns its own `CoroutineScope` for background refresh and injects a `CoroutineDispatcher` for testability.

**Domain layer** (`shared/domain/`) — repository interfaces and one use case per operation. Use cases are the only thing ViewModels call.

**Presentation layer** (`androidApp/presentation/`) — one ViewModel per screen, each exposing a sealed `UiState` as `StateFlow`. Compose screens are split into a route composable (owns `koinViewModel()` and state collection) and a stateless content composable (receives plain values and lambdas, has `@Preview`).

```
shared/
├── data/
│   ├── local/          # Room DAO, database, entity
│   ├── remote/         # Ktor client, DTOs, API mapping
│   └── repository/     # CocktailRepositoryImpl
├── domain/
│   ├── model/          # Cocktail, Ingredient, ThemePreference
│   ├── repository/     # CocktailRepository, RecentSearchRepository, ThemeRepository (interfaces)
│   └── usecase/
│       ├── cocktail/   # Search, GetRecommended, GetById, ToggleFavorite
│       ├── search/     # AddRecentSearch, DeleteRecentSearch, GetRecentSearches
│       └── theme/      # GetThemePreference, SetThemePreference
└── di/                 # Koin DataModule (commonMain-safe)

androidApp/
├── data/preferences/   # RecentSearchRepositoryImpl + ThemeRepositoryImpl (DataStore)
├── presentation/
│   ├── list/           # ListScreen, ListViewModel, CocktailRow, ListTopBar, RecentSearchesPanel
│   ├── detail/         # DetailScreen, DetailViewModel
│   ├── splash/         # SplashScreen, SplashViewModel
│   └── state/          # ListUiState, DetailUiState (sealed classes)
├── ui/
│   ├── component/      # LoadingState, EmptyState, ErrorState, RetryButton, TagChip, SearchTextField
│   └── theme/          # MaterialTheme color schemes (light + dark)
└── di/                 # AndroidModule (ViewModels, DataStore, Room)
```

## UI component layer

Reusable composables in `ui/component/` are domain-agnostic — they don't know about `Cocktail` or any screen. Screen-specific sub-composables (`CocktailRow`, `ListTopBar`, `RecentSearchesPanel`) live in `presentation/<screen>/element/` because they depend on domain models or screen wiring.

| Component | What it renders |
|---|---|
| `LoadingState` | Centered spinner + optional message |
| `EmptyState` | SearchOff icon + heading + body text |
| `ErrorState` | Warning icon + heading + message + optional `RetryButton` |
| `RetryButton` | Shared red action button |
| `TagChip` | Pill label; `highlighted = true` for the alcoholic variant |
| `SearchTextField` | Pill-shaped search field with leading icon |

## Testing

Unit tests only — no instrumented or UI tests.

- **`ListViewModelTest`** — 14 tests: recommended state, search results/empty/error, retry, recent searches, `flatMapLatest` stale response, one-shot `Channel` error event via Turbine
- **`DetailViewModelTest`** — 5 tests: Loading → Content/Error transitions, `toggleFavorite` reload
- **`CocktailRepositoryTest`** — 11 tests: interface contract via an in-memory fake — flag preservation on upsert, `toggleFavorite` flip, `hasRecommendedCocktails`
- **`RecommendedCocktailsAssemblerTest`** — dedupe-and-loop-to-10 algorithm

All ViewModel tests use `StandardTestDispatcher` + `advanceUntilIdle()`. Fakes over mocks throughout.

```bash
# Build
./gradlew :androidApp:assembleDebug

# Unit tests
./gradlew :androidApp:test
./gradlew :shared:testAndroidHostTest
```
