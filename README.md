# SuperCocktails

An Android app for searching cocktails, built with Kotlin Multiplatform. Pulls data from the CocktailDB API, caches results locally with Room, and follows a clean architecture setup — domain, data, and presentation layers split across the shared KMP module and the Android app module.

## Features

- **Recommended cocktails** — personalized list assembled on first launch and cached locally
- **Search** — reactive, debounced search by name with live Room updates
- **Favorites** — star/unstar any cocktail; persisted in Room, reflected instantly via Flow
- **Recent searches** — last 5 search terms saved with DataStore, shown when the search bar is focused with a blank query
- **Detail screen** — full cocktail info including category, alcohol type, ingredients, instructions, and last-updated date

## Stack

- **Ktor** — HTTP client for API calls
- **Room** — local cache with Flow-based queries for reactive UI updates
- **DataStore** — persistent key-value storage for recent searches
- **Koin** — dependency injection
- **Coil** — async image loading
- **ViewModel + StateFlow + sealed UiState** — presentation layer
- **Jetpack Compose** — UI
- **kotlinx.serialization** — JSON parsing and DataStore encoding

## Architecture

```
shared/
├── data/
│   ├── local/          # Room DAO and database
│   ├── remote/         # Ktor API client and DTOs
│   └── repository/     # CocktailRepositoryImpl
├── domain/
│   ├── model/          # Domain models
│   ├── repository/     # Repository interfaces
│   └── usecase/
│       ├── cocktail/   # Search, recommended, get by id, toggle favorite
│       └── search/     # Recent searches (get, add, delete)
└── di/                 # Koin DataModule

androidApp/
├── data/
│   └── preferences/    # RecentSearchRepositoryImpl (DataStore)
├── presentation/
│   ├── list/           # ListScreen, ListViewModel, element composables
│   ├── detail/         # DetailScreen, DetailViewModel
│   └── splash/         # SplashScreen
└── di/                 # AndroidModule (Room, ViewModels, DataStore)
```

The `shared` module holds everything platform-agnostic: use cases, repository interfaces and implementations, Room setup, and Koin modules. The `androidApp` module wires up ViewModels, Compose UI, and Android-specific implementations (DataStore).

## Running the app

```bash
./gradlew :androidApp:assembleDebug
```

## Running tests

```bash
./gradlew :shared:testAndroidHostTest
```
