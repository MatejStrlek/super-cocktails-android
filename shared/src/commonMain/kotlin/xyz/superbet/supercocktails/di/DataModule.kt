package xyz.superbet.supercocktails.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import xyz.superbet.supercocktails.data.repository.CocktailRepositoryImpl
import xyz.superbet.supercocktails.domain.repository.CocktailRepository
import xyz.superbet.supercocktails.domain.usecase.cocktail.GetCocktailByIdUseCase
import xyz.superbet.supercocktails.domain.usecase.cocktail.GetRecommendedCocktailsUseCase
import xyz.superbet.supercocktails.domain.usecase.cocktail.LoadRecommendedCocktailsUseCase
import xyz.superbet.supercocktails.domain.usecase.cocktail.SearchCocktailsUseCase
import xyz.superbet.supercocktails.domain.usecase.cocktail.ToggleFavoriteUseCase
import xyz.superbet.supercocktails.domain.usecase.search.AddRecentSearchUseCase
import xyz.superbet.supercocktails.domain.usecase.search.DeleteRecentSearchUseCase
import xyz.superbet.supercocktails.domain.usecase.search.GetRecentSearchesUseCase
import xyz.superbet.supercocktails.domain.usecase.theme.GetThemePreferenceUseCase
import xyz.superbet.supercocktails.domain.usecase.theme.SetThemePreferenceUseCase

val dataModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
    single<CocktailRepository> { CocktailRepositoryImpl(get(), get(), Dispatchers.IO) }
    single { SearchCocktailsUseCase(get()) }
    single { GetRecommendedCocktailsUseCase(get()) }
    single { LoadRecommendedCocktailsUseCase(get()) }
    single { GetCocktailByIdUseCase(get()) }
    single { ToggleFavoriteUseCase(get()) }
    single { GetRecentSearchesUseCase(get()) }
    single { AddRecentSearchUseCase(get()) }
    single { DeleteRecentSearchUseCase(get()) }
    single { GetThemePreferenceUseCase(get()) }
    single { SetThemePreferenceUseCase(get()) }
}