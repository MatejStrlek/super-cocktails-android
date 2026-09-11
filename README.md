# SuperCocktails

An Android app for searching cocktails, built with Kotlin Multiplatform. Pulls data from the CocktailDB API, caches results locally with Room, and follows a clean architecture setup — domain, data, and presentation layers split across the shared KMP module and the Android app module.

## Stack

- **Ktor** — HTTP client for API calls
- **Room** — local cache
- **Koin** — dependency injection
- **ViewModel + UiState** — presentation layer

## Project layout

```
shared/          # KMP module — domain, data, DI (used by all targets)
androidApp/      # Android-specific UI and presentation layer
```

The `shared` module holds everything platform-agnostic: use cases, repository interfaces and implementations, local DB setup, and Koin modules. The `androidApp` module wires up the ViewModels and Compose UI.

## Running the app

```bash
./gradlew :androidApp:assembleDebug
```

## Running tests

```bash
./gradlew :shared:testAndroidHostTest
```
