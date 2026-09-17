package xyz.superbet.supercoctails.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import xyz.superbet.supercoctails.data.repository.CocktailRepositoryImpl
import xyz.superbet.supercoctails.domain.repository.CocktailRepository
import xyz.superbet.supercoctails.domain.usecase.SearchCocktailsUseCase
import xyz.superbet.supercoctails.domain.usecase.GetRecommendedCocktailsUseCase
import xyz.superbet.supercoctails.domain.usecase.GetCocktailByIdUseCase
import xyz.superbet.supercoctails.domain.usecase.ToggleFavoriteUseCase

val dataModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
    single<CocktailRepository> { CocktailRepositoryImpl(get(), get()) }
    single { SearchCocktailsUseCase(get()) }
    single { GetRecommendedCocktailsUseCase(get()) }
    single { GetCocktailByIdUseCase(get()) }
    single { ToggleFavoriteUseCase(get()) }
}
